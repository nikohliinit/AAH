package com.macys.logistics.shipment.load.dto.shipconfirm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShipConfirmPayloadTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private FulfillmentOrder buildFulfillmentOrder() {
        LineItem item = new LineItem();
        item.setFulfillmentLocationNbr(6230);
        item.setItemDepartmentNbr(840);
        item.setItemOriginalQuantity(2);
        item.setItemPickedQuantity(2);
        item.setItemScannedSkuUpc(878878669L);
        item.setItemSkuUpc(878878669L);
        item.setLineItemState("SHPSHPD");
        item.setOrderLineItemNbr(1);
        item.setOrderShipmentNbr(1);

        LineItemList lineItemList = new LineItemList();
        lineItemList.setLineItem(Collections.singletonList(item));

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setOrderId("VNTDO21680");
        orderHeader.setSourceApplicationId("MAWM");
        orderHeader.setDeviceName("MAWM");
        orderHeader.setDeviceType("Desktop");
        orderHeader.setSourceApplicationFunctionType("SHP");
        orderHeader.setMessageCreateTimestamp("2024-02-02T12:10:21.272Z");
        orderHeader.setMessageCreateUtcTimestamp("2024-02-02T17:10:21.272Z");
        orderHeader.setMessageUniversalUniqueId("aef48a6b-e1ee-4b11-ad29-c500a565413f");
        orderHeader.setSequenceEpochNbr(1706893821272L);
        orderHeader.setEventType("SHPSHPD");
        orderHeader.setEventTypeDescription("SHP");
        orderHeader.setEventStatus("61");
        orderHeader.setEventTimestamp("2024-02-02T12:10:21.272Z");
        orderHeader.setEventUtcTimestamp("2024-02-02T17:10:21.272Z");

        Shipment shipment = Shipment.builder()
                .fulfillmentNbr("F04634654")
                .orderShipmentNbr(1)
                .shipmentState("SHPSHPD")
                .fulfillmentLocationNbr(6230)
                .fulfillmentAssociateId("ASSOC001")
                .trailer("TRAILER001")
                .apptNumber("APPT001")
                .build();

        ShipmentList shipmentList = ShipmentList.builder()
                .shipment(Collections.singletonList(shipment))
                .build();

        return FulfillmentOrder.builder()
                .lineItemList(lineItemList)
                .orderHeader(orderHeader)
                .shipmentList(shipmentList)
                .build();
    }

    @Test
    void testNoArgsConstructor() {
        ShipConfirmPayload payload = new ShipConfirmPayload();
        assertNotNull(payload);
        assertNull(payload.getFulfillmentOrder());
    }

    @Test
    void testAllArgsConstructor() {
        FulfillmentOrder fulfillmentOrder = buildFulfillmentOrder();
        ShipConfirmPayload payload = new ShipConfirmPayload(fulfillmentOrder);
        assertNotNull(payload);
        assertEquals(fulfillmentOrder, payload.getFulfillmentOrder());
    }

    @Test
    void testBuilder() {
        FulfillmentOrder fulfillmentOrder = buildFulfillmentOrder();
        ShipConfirmPayload payload = ShipConfirmPayload.builder()
                .fulfillmentOrder(fulfillmentOrder)
                .build();

        assertNotNull(payload);
        assertEquals(fulfillmentOrder, payload.getFulfillmentOrder());
    }

    @Test
    void testGetterAndSetter() {
        FulfillmentOrder fulfillmentOrder = buildFulfillmentOrder();
        ShipConfirmPayload payload = new ShipConfirmPayload();
        payload.setFulfillmentOrder(fulfillmentOrder);

        assertEquals(fulfillmentOrder, payload.getFulfillmentOrder());
    }

    @Test
    void testEqualsAndHashCode() {
        FulfillmentOrder fulfillmentOrder = buildFulfillmentOrder();
        ShipConfirmPayload payload1 = ShipConfirmPayload.builder().fulfillmentOrder(fulfillmentOrder).build();
        ShipConfirmPayload payload2 = ShipConfirmPayload.builder().fulfillmentOrder(fulfillmentOrder).build();

        assertEquals(payload1, payload2);
        assertEquals(payload1.hashCode(), payload2.hashCode());
    }

    @Test
    void testNotEquals() {
        FulfillmentOrder fo1 = buildFulfillmentOrder();
        FulfillmentOrder fo2 = buildFulfillmentOrder();
        fo2.getOrderHeader().setOrderId("DIFFERENT_ORDER");

        ShipConfirmPayload payload1 = ShipConfirmPayload.builder().fulfillmentOrder(fo1).build();
        ShipConfirmPayload payload2 = ShipConfirmPayload.builder().fulfillmentOrder(fo2).build();

        assertNotEquals(payload1, payload2);
    }

    @Test
    void testToString() {
        FulfillmentOrder fulfillmentOrder = buildFulfillmentOrder();
        ShipConfirmPayload payload = ShipConfirmPayload.builder().fulfillmentOrder(fulfillmentOrder).build();

        String toString = payload.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("VNTDO21680"));
    }

    @Test
    void testEqualsSameInstance() {
        ShipConfirmPayload payload1 = new ShipConfirmPayload();
        ShipConfirmPayload payload2 = new ShipConfirmPayload();
        assertEquals(payload1, payload2);
    }

    @Test
    void testEqualsNull() {
        ShipConfirmPayload payload = new ShipConfirmPayload();
        assertNotEquals(null, payload);
    }

    @Test
    void testEqualsDifferentType() {
        ShipConfirmPayload payload = new ShipConfirmPayload();
        assertNotEquals("string", payload);
    }

    @Test
    void testJsonPropertyDeserialization() throws Exception {
        String json = "{\"fulfillmentOrder\":{\"orderHeader\":{\"orderId\":\"ORD123\"}}}";
        ShipConfirmPayload payload = objectMapper.readValue(json, ShipConfirmPayload.class);
        assertNotNull(payload);
        assertNotNull(payload.getFulfillmentOrder());
        assertEquals("ORD123", payload.getFulfillmentOrder().getOrderHeader().getOrderId());
    }

    @Test
    void testJsonIgnoreUnknownProperties() throws Exception {
        String json = "{\"fulfillmentOrder\":{\"orderHeader\":{\"orderId\":\"ORD123\"}},\"unknownField\":\"ignoredValue\"}";
        ShipConfirmPayload payload = objectMapper.readValue(json, ShipConfirmPayload.class);
        assertNotNull(payload);
        assertNotNull(payload.getFulfillmentOrder());
    }

    @Test
    void testJsonSerialization() throws Exception {
        FulfillmentOrder fulfillmentOrder = buildFulfillmentOrder();
        ShipConfirmPayload payload = ShipConfirmPayload.builder().fulfillmentOrder(fulfillmentOrder).build();

        String json = objectMapper.writeValueAsString(payload);
        assertNotNull(json);
        assertTrue(json.contains("fulfillmentOrder"));
    }

    @Test
    void testNullFulfillmentOrderEquality() {
        ShipConfirmPayload payload1 = new ShipConfirmPayload();
        ShipConfirmPayload payload2 = new ShipConfirmPayload();
        assertEquals(payload1, payload2);
        assertEquals(payload1.hashCode(), payload2.hashCode());
    }
}
