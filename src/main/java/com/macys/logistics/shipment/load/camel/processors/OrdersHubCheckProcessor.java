package com.macys.logistics.shipment.load.camel.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.orders.OrderShipmentMessage;
import com.macys.logistics.shipment.load.service.OrderService;
import com.macys.logistics.shipment.load.util.Constants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;

import java.util.List;
import java.util.Objects;

@Component
@Log4j2
@ConditionalOnProperty(name = "feature.orders.hub.check.enabled", havingValue = "true", matchIfMissing = false)
public class OrdersHubCheckProcessor implements Processor {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @Autowired
    public OrdersHubCheckProcessor(ObjectMapper objectMapper, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @Value("${orders.hub.ids}")
    private List<String> hubIds;

    private Counter ordersReceivedCounter;
    private Counter hubSkippedCounter;
    private Counter processingCounter;

    @Autowired(required = false)
    public void setMeterRegistry(MeterRegistry meterRegistry) {
        if (Objects.nonNull(meterRegistry)) {
            this.ordersReceivedCounter = Counter.builder("orders.received")
                    .tag(Constants.ORDERS_COMPONENT, Constants.ORDERS_HUB_CHECKER)
                    .description("Total orders received for hub checking")
                    .register(meterRegistry);

            this.hubSkippedCounter = Counter.builder("orders.hub.skipped")
                    .tag(Constants.ORDERS_COMPONENT, Constants.ORDERS_HUB_CHECKER)
                    .description("Orders skipped due to hub destination")
                    .register(meterRegistry);

            this.processingCounter = Counter.builder("orders.processing")
                    .tag(Constants.ORDERS_COMPONENT, Constants.ORDERS_HUB_CHECKER)
                    .description("Orders passed hub check, proceeding to TMS")
                    .register(meterRegistry);
        }
    }

    @Override
    public void process(Exchange exchange) throws TmsLoadProcessException {
        incrementCounter(ordersReceivedCounter);

        String requestBody = exchange.getIn().getBody(String.class);
        OrderShipmentMessage orderMessage;
        try {
            orderMessage = objectMapper.readValue(requestBody, OrderShipmentMessage.class);
        } catch (Exception e) {
            throw new TmsLoadProcessException("Failed to deserialize order message: " + e.getMessage(), e);
        }

        String orderId = extractOrderId(orderMessage);
        String fbNbr = orderMessage.getFbNbr();

        log.info("Processing orders message for hub check - orderId: {}, fbNbr: {}", orderId, fbNbr);

        if (!isValidDestination(orderMessage)) {
            handleInvalidDestination(exchange, orderId, fbNbr);
            return;
        }

        String destinationLocationId = orderMessage.getOrder().getDestination().getLocationId();
        logOrderDetails(orderMessage, fbNbr, orderId, destinationLocationId);

        if (isHubDestination(destinationLocationId)) {
            handleHubOrder(exchange, destinationLocationId, orderId, fbNbr);
        } else {
            handleNonHubOrder(exchange, orderMessage, destinationLocationId, orderId, fbNbr);
        }
    }

    private String extractOrderId(OrderShipmentMessage orderMessage) {
        return Objects.nonNull(orderMessage.getOrder()) ? orderMessage.getOrder().getOrderId()
                : Constants.ORDERS_DEFAULT_ORDER_ID;
    }

    private boolean isValidDestination(OrderShipmentMessage orderMessage) {
        return Objects.nonNull(orderMessage.getOrder())
                && Objects.nonNull(orderMessage.getOrder().getDestination())
                && StringUtils.isNotBlank(orderMessage.getOrder().getDestination().getLocationId());
    }

    private void handleInvalidDestination(Exchange exchange, String orderId, String fbNbr) {
        log.warn("Order has null or empty destination locationId - orderId: {}, fbNbr: {}", orderId, fbNbr);
        exchange.getIn().setHeader(Constants.ORDERS_HUB_DESTINATION, false);
        incrementCounter(processingCounter);
    }

    private void logOrderDetails(OrderShipmentMessage orderMessage, String fbNbr, String orderId,
            String destinationLocationId) {
        String originLocationId = Objects.nonNull(orderMessage.getOrder().getOrigin())
                ? orderMessage.getOrder().getOrigin().getLocationId()
                : null;
        log.info("Order details - fbNbr: {}, orderId: {}, destination: {}, origin: {}",
                fbNbr, orderId, destinationLocationId, originLocationId);
    }

    private boolean isHubDestination(String destinationLocationId) {
        return hubIds.contains(destinationLocationId);
    }

    private void handleHubOrder(Exchange exchange, String destinationLocationId, String orderId, String fbNbr) {
        log.info("Hub destination detected: {}. Skipping JDA TMS integration for order: {}, fbNbr: {}",
                destinationLocationId, orderId, fbNbr);
        log.info("Destination Location is HUB. Cannot call API to integrate TM");

        exchange.getIn().setHeader(Constants.ORDERS_HUB_DESTINATION, true);
        incrementCounter(hubSkippedCounter);
    }

    private void handleNonHubOrder(Exchange exchange, OrderShipmentMessage orderMessage,
                                   String destinationLocationId, String orderId, String fbNbr) throws TmsLoadProcessException {
        log.info("Destination is not a hub: {}. Proceeding with TMS integration for order: {}, fbNbr: {}",
                destinationLocationId, orderId, fbNbr);

        exchange.getIn().setHeader(Constants.ORDERS_HUB_DESTINATION, false);
        exchange.getIn().setBody(orderMessage);

        routeByTransCode(orderMessage, orderId, fbNbr);
        incrementCounter(processingCounter);
    }

    private void routeByTransCode(OrderShipmentMessage orderMessage, String orderId, String fbNbr) throws TmsLoadProcessException {
        String transCode = orderMessage.getTransCode();

        switch (transCode) {
            case Constants.TRANS_CODE_CREATE:
                log.info("Calling createOrder service - transCode: A, orderId: {}, fbNbr: {}", orderId, fbNbr);
                orderService.createOrder(orderMessage);
                break;
            case Constants.TRANS_CODE_UPDATE:
                log.info("Calling updateOrder service - transCode: M, orderId: {}, fbNbr: {}", orderId, fbNbr);
                orderService.updateOrder(orderMessage);
                break;
            case Constants.TRANS_CODE_DELETE:
                log.info("Calling deleteOrder service - transCode: D, orderId: {}, fbNbr: {}", orderId, fbNbr);
                orderService.deleteOrder(orderMessage);
                break;
            default:
                log.error("Invalid transCode: {} for orderId: {}, fbNbr: {}", transCode, orderId, fbNbr);
                throw new TmsLoadProcessException("Invalid transCode for order processing: " + transCode);
        }
    }

    private void incrementCounter(Counter counter) {
        if (Objects.nonNull(counter)) {
            counter.increment();
        }
    }
}
