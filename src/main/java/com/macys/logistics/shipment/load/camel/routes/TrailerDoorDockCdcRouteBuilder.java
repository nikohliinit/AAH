package com.macys.logistics.shipment.load.camel.routes;

import com.macys.logistics.shipment.load.camel.processors.TrailerDoorDockCdcEventProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class TrailerDoorDockCdcRouteBuilder extends RouteBuilder {

    @Autowired
    TrailerDoorDockCdcEventProcessor trailerDoorDockCdcEventProcessor;

    @Value("${trailer.door.dock.cdc.route}")
    private String trailerDoorDockCdcRoute;

    @Value("${trailer.door.dock.cdc.attempts}")
    private String retryCount;

    @Value("${trailer.door.dock.cdc.delay}")
    private String retryInterval;

    @Value("${tms.trailer.door.dock.dlq-topic}")
    private String dlqTopic;

    @Value("${pubsub.project-id}")
    private String projectId;



    public static final String METRICS_HELPER_BEAN = "metricsHelper";

    public static final String CLEAR_THREAD_CONTEXT_METHOD = "clearThreadContext";


    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .log(LoggingLevel.ERROR,
                        "Error processing TrailerDoorDockCDCEvent. correlationId=${header.correlationId}, body=${body}, error=${exception.message}")
                .maximumRedeliveries(retryCount)
                .redeliveryDelay(retryInterval)
                .useExponentialBackOff()
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exception.class.getName(), Exception.class);
                    if (cause != null) {
                        exchange.getIn().setHeader("errorType", cause.getClass().getSimpleName());
                        exchange.getIn().setHeader("errorMessage", cause.getMessage());
                    }
                    exchange.getIn().setHeader("sourceSubscription", trailerDoorDockCdcRoute);
                })
                .bean(METRICS_HELPER_BEAN, CLEAR_THREAD_CONTEXT_METHOD)
                .handled(true)
                .end();


        from(trailerDoorDockCdcRoute)
                .routeId("trailerDoorDockCdcRoute")
                .log(LoggingLevel.DEBUG,
                        "Received TrailerDoorDockCDCEvent payload from Pub/Sub: ${body}")
                .convertBodyTo(String.class)
                .log(LoggingLevel.INFO, "Starting TrailerDoorDockCDC processing")
                .process(trailerDoorDockCdcEventProcessor)
                .log(LoggingLevel.INFO, "TrailerDoorDockCDC processing completed");
    }
}
