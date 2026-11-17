package com.macys.logistics.shipment.load.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.camel.processors.OrdersHubCheckProcessor;
import com.macys.logistics.shipment.load.dto.inbound.orders.OrderShipmentMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Test controller for directly invoking {@link OrdersHubCheckProcessor#process(Exchange)}.
 * Accepts an OrderShipmentMessage JSON payload and runs it through the full
 * hub-check → create/update/delete flow.
 *
 * <p>Enable via property: {@code orders.test.controller.enable=true}</p>
 * <p>Also requires: {@code feature.orders.hub.check.enabled=true}</p>
 *
 * <p>Usage: POST /tms-shipment-load/api/v1/orders/test</p>
 */
@RestController
@RequestMapping("api/v1/orders")
@Slf4j
@ConditionalOnProperty(name = "orders.test.controller.enable", havingValue = "true", matchIfMissing = false)
public class OrderTestController {

    @Autowired(required = false)
    private OrdersHubCheckProcessor ordersHubCheckProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CamelContext camelContext;

    @PostMapping(path = "/test")
    public ResponseEntity<Map<String, Object>> testOrderProcess(@RequestBody String requestBody) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            // Guard: OrdersHubCheckProcessor requires feature.orders.hub.check.enabled=true
            if (ordersHubCheckProcessor == null) {
                result.put("status", "ERROR");
                result.put("message", "OrdersHubCheckProcessor bean is not available. "
                        + "Set feature.orders.hub.check.enabled=true and configure orders.hub.ids");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
            }

            // Deserialize first to extract metadata for the response
            OrderShipmentMessage orderMessage = objectMapper.readValue(requestBody, OrderShipmentMessage.class);
            String orderId = orderMessage.getOrder() != null ? orderMessage.getOrder().getOrderId() : "N/A";
            String transCode = orderMessage.getTransCode();

            log.info("OrderTestController: Received test request - orderId: {}, transCode: {}", orderId, transCode);

            // Build a Camel Exchange to simulate the processor input
            Exchange exchange = new DefaultExchange(camelContext);
            DefaultMessage message = new DefaultMessage(camelContext);
            message.setBody(requestBody);
            exchange.setIn(message);

            // Invoke the processor directly
            ordersHubCheckProcessor.process(exchange);

            // Collect results from the exchange
            boolean isHub = Boolean.TRUE.equals(exchange.getIn().getHeader("HUB_DESTINATION", Boolean.class));
            result.put("status", "SUCCESS");
            result.put("orderId", orderId);
            result.put("transCode", transCode);
            result.put("hubDestination", isHub);

            if (!isHub) {
                result.put("action", getActionDescription(transCode));
            } else {
                result.put("action", "SKIPPED - Hub destination");
            }

            log.info("OrderTestController: Test completed successfully - orderId: {}, hubDestination: {}", orderId, isHub);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("OrderTestController: Validation error", e);
            result.put("status", "VALIDATION_ERROR");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("OrderTestController: Processing error", e);
            result.put("status", "ERROR");
            result.put("message", e.getMessage());
            result.put("exceptionType", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    private String getActionDescription(String transCode) {
        if ("A".equals(transCode)) return "createOrder";
        if ("M".equals(transCode)) return "updateOrder";
        if ("D".equals(transCode)) return "deleteOrder";
        return "UNKNOWN (" + transCode + ")";
    }
}
