package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FulfillmentOrderTest {

    private LineItemList buildLineItemList() {
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
        LineItemList list = new LineItemList();
        list.setLineItem(Collections.singletonList(item));
        return list;
    }

    private OrderHeader buildOrderHeader() {
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
        return orderHeader;
    }

    private ShipmentList buildShipmentList() {
        Shipment shipment = Shipment.builder()
                .fulfillmentNbr("F04634654")
                .orderShipmentNbr(1)
                .shipmentState("SHPSHPD")
                .fulfillmentLocationNbr(6230)
                .fulfillmentAssociateId("ASSOC001")
                .trailer("TRAILER001")
                .apptNumber("APPT001")
                .build();
        return ShipmentList.builder()
                .shipment(Collections.singletonList(shipment))
                .build();
    }

    @Test
    void testNoArgsConstructor() {
        FulfillmentOrder fulfillmentOrder = new FulfillmentOrder();
        assertNotNull(fulfillmentOrder);
        assertNull(fulfillmentOrder.getLineItemList());
        assertNull(fulfillmentOrder.getOrderHeader());
        assertNull(fulfillmentOrder.getShipmentList());
    }

    @Test
    void testAllArgsConstructor() {
        LineItemList lineItemList = buildLineItemList();
        OrderHeader orderHeader = buildOrderHeader();
        ShipmentList shipmentList = buildShipmentList();

        FulfillmentOrder fulfillmentOrder = new FulfillmentOrder(lineItemList, orderHeader, shipmentList);

        assertNotNull(fulfillmentOrder);
        assertEquals(lineItemList, fulfillmentOrder.getLineItemList());
        assertEquals(orderHeader, fulfillmentOrder.getOrderHeader());
        assertEquals(shipmentList, fulfillmentOrder.getShipmentList());
    }

    @Test
    void testBuilder() {
        LineItemList lineItemList = buildLineItemList();
        OrderHeader orderHeader = buildOrderHeader();
        ShipmentList shipmentList = buildShipmentList();

        FulfillmentOrder fulfillmentOrder = FulfillmentOrder.builder()
                .lineItemList(lineItemList)
                .orderHeader(orderHeader)
                .shipmentList(shipmentList)
                .build();

        assertNotNull(fulfillmentOrder);
        assertEquals(lineItemList, fulfillmentOrder.getLineItemList());
        assertEquals(orderHeader, fulfillmentOrder.getOrderHeader());
        assertEquals(shipmentList, fulfillmentOrder.getShipmentList());
    }

    @Test
    void testGettersAndSetters() {
        LineItemList lineItemList = buildLineItemList();
        OrderHeader orderHeader = buildOrderHeader();
        ShipmentList shipmentList = buildShipmentList();

        FulfillmentOrder fulfillmentOrder = new FulfillmentOrder();
        fulfillmentOrder.setLineItemList(lineItemList);
        fulfillmentOrder.setOrderHeader(orderHeader);
        fulfillmentOrder.setShipmentList(shipmentList);

        assertEquals(lineItemList, fulfillmentOrder.getLineItemList());
        assertEquals(orderHeader, fulfillmentOrder.getOrderHeader());
        assertEquals(shipmentList, fulfillmentOrder.getShipmentList());
    }

    @Test
    void testEqualsAndHashCode() {
        LineItemList lineItemList = buildLineItemList();
        OrderHeader orderHeader = buildOrderHeader();
        ShipmentList shipmentList = buildShipmentList();

        FulfillmentOrder fo1 = FulfillmentOrder.builder()
                .lineItemList(lineItemList)
                .orderHeader(orderHeader)
                .shipmentList(shipmentList)
                .build();

        FulfillmentOrder fo2 = FulfillmentOrder.builder()
                .lineItemList(lineItemList)
                .orderHeader(orderHeader)
                .shipmentList(shipmentList)
                .build();

        assertEquals(fo1, fo2);
        assertEquals(fo1.hashCode(), fo2.hashCode());
    }

    @Test
    void testNotEquals() {
        FulfillmentOrder fo1 = FulfillmentOrder.builder()
                .orderHeader(buildOrderHeader())
                .build();

        FulfillmentOrder fo2 = FulfillmentOrder.builder()
                .shipmentList(buildShipmentList())
                .build();

        assertNotEquals(fo1, fo2);
    }

    @Test
    void testToString() {
        FulfillmentOrder fulfillmentOrder = FulfillmentOrder.builder()
                .orderHeader(buildOrderHeader())
                .build();

        String toString = fulfillmentOrder.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("VNTDO21680"));
    }

    @Test
    void testEqualsSameInstance() {
        FulfillmentOrder fo = new FulfillmentOrder();
        FulfillmentOrder copy = new FulfillmentOrder();
        assertEquals(fo, copy);
    }

    @Test
    void testEqualsNull() {
        FulfillmentOrder fo = new FulfillmentOrder();
        assertNotEquals(null, fo);
    }

    @Test
    void testEqualsDifferentType() {
        FulfillmentOrder fo = new FulfillmentOrder();
        assertNotEquals("string", fo);
    }
}
