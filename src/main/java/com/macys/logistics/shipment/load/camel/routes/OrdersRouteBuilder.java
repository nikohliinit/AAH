package com.macys.logistics.shipment.load.camel.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.macys.logistics.shipment.load.camel.processors.OrdersHubCheckProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@ConditionalOnProperty(name = "feature.orders.hub.check.enabled", havingValue = "true", matchIfMissing = false)
public class OrdersRouteBuilder extends RouteBuilder {

    @Value("${orders.consumer.route}")
    private String ordersConsumerRoute;

    @Value("${orders.retry.attempts:3}")
    private int ordersRetryAttempts;

    @Value("${orders.retry.delay:1000}")
    private long ordersRetryDelay;

    private final OrdersHubCheckProcessor ordersHubCheckProcessor;

    @Autowired
    public OrdersRouteBuilder(OrdersHubCheckProcessor ordersHubCheckProcessor) {
        this.ordersHubCheckProcessor = ordersHubCheckProcessor;
    }

    @Override
    public void configure() throws Exception {

        // Exception handling for JSON parsing/validation errors
        onException(JsonProcessingException.class)
                .log("JSON parsing error while processing orders message: ${exception.message}")
                .handled(true)
                .maximumRedeliveries(ordersRetryAttempts)
                .redeliveryDelay(ordersRetryDelay)
                .end();

        // Exception handling for general exceptions
        onException(Exception.class)
                .log("Unexpected error while processing orders message: ${exception.message}")
                .handled(true)
                .maximumRedeliveries(0)
                .end();

        log.info("Configuring Orders Hub Check Route with consumer: {}", ordersConsumerRoute);

        // Main route: consume from GCP Pub/Sub -> hub check processor (calls service
        // directly)
        from(ordersConsumerRoute)
                .routeId("ordersHubCheckRoute")
                .log("Processing orders message from GCP Pub/Sub")
                .process(ordersHubCheckProcessor)
                .log("Order processing completed")
                .end();

        log.info("Orders Hub Check Route configured successfully");
    }
}
