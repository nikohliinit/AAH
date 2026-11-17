package com.macys.logistics.shipment.load.camel.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.orders.LocationInfo;
import com.macys.logistics.shipment.load.dto.inbound.orders.Order;
import com.macys.logistics.shipment.load.dto.inbound.orders.OrderShipmentMessage;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.service.OrderService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

@ExtendWith(SpringExtension.class)
class OrdersHubCheckProcessorTest {

    @InjectMocks
    OrdersHubCheckProcessor processor;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    OrderService orderService;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    private static final String JSON_BODY = "{\"transCode\":\"A\"}";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(processor, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(processor, "orderService", orderService);
        ReflectionTestUtils.setField(processor, "hubIds", Arrays.asList("HUB1", "HUB2", "HUB3"));
        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(String.class)).thenReturn(JSON_BODY);
    }

    // ======================== setMeterRegistry tests ========================

    @Test
    void setMeterRegistry_withNonNullRegistry_registersAllCounters() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        processor.setMeterRegistry(registry);

        Counter ordersReceived = (Counter) ReflectionTestUtils.getField(processor, "ordersReceivedCounter");
        Counter hubSkipped = (Counter) ReflectionTestUtils.getField(processor, "hubSkippedCounter");
        Counter processing = (Counter) ReflectionTestUtils.getField(processor, "processingCounter");

        assertNotNull(ordersReceived);
        assertNotNull(hubSkipped);
        assertNotNull(processing);
    }

    @Test
    void setMeterRegistry_withNullRegistry_countersRemainNull() {
        processor.setMeterRegistry(null);

        assertNull(ReflectionTestUtils.getField(processor, "ordersReceivedCounter"));
        assertNull(ReflectionTestUtils.getField(processor, "hubSkippedCounter"));
        assertNull(ReflectionTestUtils.getField(processor, "processingCounter"));
    }

    // ======================== process — null/empty destination branch ========================

    @Test
    void process_nullOrder_setsHubDestinationFalse() throws Exception {
        OrderShipmentMessage msg = buildMessage("A", null, null, null);
        msg.setOrder(null);
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", false);
        verify(orderService, never()).createOrder(msg);
        verify(orderService, never()).updateOrder(msg);
        verify(orderService, never()).deleteOrder(msg);
    }

    @Test
    void process_nullDestination_setsHubDestinationFalse() throws Exception {
        OrderShipmentMessage msg = buildMessage("A", null, null, null);
        msg.getOrder().setDestination(null);
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", false);
        verify(orderService, never()).createOrder(msg);
    }

    @Test
    void process_nullLocationId_setsHubDestinationFalse() throws Exception {
        OrderShipmentMessage msg = buildMessage("A", null, null, null);
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", false);
    }

    @Test
    void process_emptyLocationId_setsHubDestinationFalse() throws Exception {
        OrderShipmentMessage msg = buildMessage("A", "   ", null, null);
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", false);
    }

    // ======================== process — hub destination path ========================

    @Test
    void process_hubDestination_setsHeaderTrue() throws Exception {
        OrderShipmentMessage msg = buildMessage("A", "HUB1", null, "FB001");
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", true);
        verify(orderService, never()).createOrder(msg);
        verify(orderService, never()).updateOrder(msg);
        verify(orderService, never()).deleteOrder(msg);
    }

    @Test
    void process_hubDestination_withCounters_incrementsHubSkipped() throws Exception {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        processor.setMeterRegistry(registry);

        OrderShipmentMessage msg = buildMessage("A", "HUB2", null, "FB002");
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", true);

        Counter ordersReceived = (Counter) ReflectionTestUtils.getField(processor, "ordersReceivedCounter");
        Counter hubSkipped = (Counter) ReflectionTestUtils.getField(processor, "hubSkippedCounter");
        assertNotNull(ordersReceived);
        assertNotNull(hubSkipped);
    }

    // ======================== process — non-hub, transCode routing ========================

    @Test
    void process_nonHub_transCodeA_callsCreateOrder() throws Exception {
        OrderShipmentMessage msg = buildMessage("A", "STORE1", "ORIGIN1", "FB100");
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", false);
        verify(message).setBody(msg);
        verify(orderService).createOrder(msg);
    }

    @Test
    void process_nonHub_transCodeM_callsUpdateOrder() throws Exception {
        OrderShipmentMessage msg = buildMessage("M", "STORE1", "ORIGIN1", "FB101");
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", false);
        verify(orderService).updateOrder(msg);
    }

    @Test
    void process_nonHub_transCodeD_callsDeleteOrder() throws Exception {
        OrderShipmentMessage msg = buildMessage("D", "STORE1", "ORIGIN1", "FB102");
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", false);
        verify(orderService).deleteOrder(msg);
    }

    @Test
    void process_nonHub_invalidTransCode_throwsTmsLoadProcessException() throws Exception {
        OrderShipmentMessage msg = buildMessage("X", "STORE1", "ORIGIN1", "FB103");
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        TmsLoadProcessException thrown = assertThrows(TmsLoadProcessException.class,
                () -> processor.process(exchange));

        assertTrue(thrown.getMessage().contains("Invalid transCode"));
        assertTrue(thrown.getMessage().contains("X"));
    }

    // ======================== process — origin ternary branch ========================

    @Test
    void process_nonHub_withNullOrigin_proceedsNormally() throws Exception {
        OrderShipmentMessage msg = buildMessage("A", "STORE1", null, "FB104");
        msg.getOrder().setOrigin(null);
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(orderService).createOrder(msg);
    }

    @Test
    void process_nonHub_withOriginSet_proceedsNormally() throws Exception {
        OrderShipmentMessage msg = buildMessage("M", "STORE1", "ORIGIN1", "FB105");
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(orderService).updateOrder(msg);
    }

    // ======================== process — counter null-safety branches ========================

    @Test
    void process_nonHub_withCounters_incrementsReceivedAndProcessing() throws Exception {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        processor.setMeterRegistry(registry);

        OrderShipmentMessage msg = buildMessage("A", "STORE1", "ORIGIN1", "FB106");
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        Counter ordersReceived = (Counter) ReflectionTestUtils.getField(processor, "ordersReceivedCounter");
        Counter processing = (Counter) ReflectionTestUtils.getField(processor, "processingCounter");
        assertNotNull(ordersReceived);
        assertNotNull(processing);
    }

    @Test
    void process_nullDestination_withCounters_incrementsReceivedAndProcessing() throws Exception {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        processor.setMeterRegistry(registry);

        OrderShipmentMessage msg = buildMessage("A", null, null, "FB107");
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        Counter ordersReceived = (Counter) ReflectionTestUtils.getField(processor, "ordersReceivedCounter");
        Counter processing = (Counter) ReflectionTestUtils.getField(processor, "processingCounter");
        assertNotNull(ordersReceived);
        assertNotNull(processing);
    }

    @Test
    void process_nullOrder_withoutCounters_noNPE() throws Exception {
        OrderShipmentMessage msg = new OrderShipmentMessage();
        msg.setTransCode("A");
        msg.setOrder(null);
        when(objectMapper.readValue(anyString(), eq(OrderShipmentMessage.class))).thenReturn(msg);

        processor.process(exchange);

        verify(message).setHeader("HUB_DESTINATION", false);
    }

    // ======================== Helper ========================

    private OrderShipmentMessage buildMessage(String transCode, String destinationLocId,
                                              String originLocId, String fbNbr) {
        OrderShipmentMessage msg = new OrderShipmentMessage();
        msg.setTransCode(transCode);
        msg.setFbNbr(fbNbr);

        Order order = new Order();
        order.setOrderId("ORD-001");

        if (destinationLocId != null) {
            LocationInfo dest = new LocationInfo();
            dest.setLocationId(destinationLocId);
            order.setDestination(dest);
        } else {
            LocationInfo dest = new LocationInfo();
            dest.setLocationId(null);
            order.setDestination(dest);
        }

        if (originLocId != null) {
            LocationInfo origin = new LocationInfo();
            origin.setLocationId(originLocId);
            order.setOrigin(origin);
        }

        msg.setOrder(order);
        return msg;
    }
}
