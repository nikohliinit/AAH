package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderHeaderTest {

    @Test
    void testDefaultConstructor() {
        OrderHeader orderHeader = new OrderHeader();
        assertNotNull(orderHeader);
        assertNull(orderHeader.getOrderId());
        assertNull(orderHeader.getSourceApplicationId());
        assertNull(orderHeader.getDeviceName());
        assertNull(orderHeader.getDeviceType());
        assertNull(orderHeader.getSourceApplicationFunctionType());
        assertNull(orderHeader.getMessageCreateTimestamp());
        assertNull(orderHeader.getMessageCreateUtcTimestamp());
        assertNull(orderHeader.getMessageUniversalUniqueId());
        assertNull(orderHeader.getSequenceEpochNbr());
        assertNull(orderHeader.getEventType());
        assertNull(orderHeader.getEventTypeDescription());
        assertNull(orderHeader.getEventStatus());
        assertNull(orderHeader.getEventTimestamp());
        assertNull(orderHeader.getEventUtcTimestamp());
        assertNull(orderHeader.getOrderType());
        assertNull(orderHeader.getOrderSubType());
    }

    @Test
    void testGettersAndSetters() {
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
        orderHeader.setOrderType("STANDARD");
        orderHeader.setOrderSubType("OB");

        assertEquals("VNTDO21680", orderHeader.getOrderId());
        assertEquals("MAWM", orderHeader.getSourceApplicationId());
        assertEquals("MAWM", orderHeader.getDeviceName());
        assertEquals("Desktop", orderHeader.getDeviceType());
        assertEquals("SHP", orderHeader.getSourceApplicationFunctionType());
        assertEquals("2024-02-02T12:10:21.272Z", orderHeader.getMessageCreateTimestamp());
        assertEquals("2024-02-02T17:10:21.272Z", orderHeader.getMessageCreateUtcTimestamp());
        assertEquals("aef48a6b-e1ee-4b11-ad29-c500a565413f", orderHeader.getMessageUniversalUniqueId());
        assertEquals(1706893821272L, orderHeader.getSequenceEpochNbr());
        assertEquals("SHPSHPD", orderHeader.getEventType());
        assertEquals("SHP", orderHeader.getEventTypeDescription());
        assertEquals("61", orderHeader.getEventStatus());
        assertEquals("2024-02-02T12:10:21.272Z", orderHeader.getEventTimestamp());
        assertEquals("2024-02-02T17:10:21.272Z", orderHeader.getEventUtcTimestamp());
        assertEquals("STANDARD", orderHeader.getOrderType());
        assertEquals("OB", orderHeader.getOrderSubType());
    }

    @Test
    void testEqualsAndHashCode() {
        OrderHeader oh1 = new OrderHeader();
        oh1.setOrderId("VNTDO21680");
        oh1.setSourceApplicationId("MAWM");
        oh1.setDeviceName("MAWM");
        oh1.setDeviceType("Desktop");
        oh1.setSourceApplicationFunctionType("SHP");
        oh1.setMessageCreateTimestamp("2024-02-02T12:10:21.272Z");
        oh1.setMessageCreateUtcTimestamp("2024-02-02T17:10:21.272Z");
        oh1.setMessageUniversalUniqueId("aef48a6b");
        oh1.setSequenceEpochNbr(1706893821272L);
        oh1.setEventType("SHPSHPD");
        oh1.setEventTypeDescription("SHP");
        oh1.setEventStatus("61");
        oh1.setEventTimestamp("2024-02-02T12:10:21.272Z");
        oh1.setEventUtcTimestamp("2024-02-02T17:10:21.272Z");
        oh1.setOrderType(null);
        oh1.setOrderSubType(null);

        OrderHeader oh2 = new OrderHeader();
        oh2.setOrderId("VNTDO21680");
        oh2.setSourceApplicationId("MAWM");
        oh2.setDeviceName("MAWM");
        oh2.setDeviceType("Desktop");
        oh2.setSourceApplicationFunctionType("SHP");
        oh2.setMessageCreateTimestamp("2024-02-02T12:10:21.272Z");
        oh2.setMessageCreateUtcTimestamp("2024-02-02T17:10:21.272Z");
        oh2.setMessageUniversalUniqueId("aef48a6b");
        oh2.setSequenceEpochNbr(1706893821272L);
        oh2.setEventType("SHPSHPD");
        oh2.setEventTypeDescription("SHP");
        oh2.setEventStatus("61");
        oh2.setEventTimestamp("2024-02-02T12:10:21.272Z");
        oh2.setEventUtcTimestamp("2024-02-02T17:10:21.272Z");
        oh2.setOrderType(null);
        oh2.setOrderSubType(null);

        assertEquals(oh1, oh2);
        assertEquals(oh1.hashCode(), oh2.hashCode());
    }

    @Test
    void testNotEquals() {
        OrderHeader oh1 = new OrderHeader();
        oh1.setOrderId("VNTDO21680");

        OrderHeader oh2 = new OrderHeader();
        oh2.setOrderId("ORDER99999");

        assertNotEquals(oh1, oh2);
    }

    @Test
    void testToString() {
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setOrderId("VNTDO21680");
        orderHeader.setEventType("SHPSHPD");

        String toString = orderHeader.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("VNTDO21680"));
        assertTrue(toString.contains("SHPSHPD"));
    }

    @Test
    void testEqualsSameInstance() {
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setOrderId("VNTDO21680");
        OrderHeader copy = new OrderHeader();
        copy.setOrderId("VNTDO21680");
        assertEquals(orderHeader, copy);
    }

    @Test
    void testEqualsNull() {
        OrderHeader orderHeader = new OrderHeader();
        assertNotEquals(null, orderHeader);
    }

    @Test
    void testEqualsDifferentType() {
        OrderHeader orderHeader = new OrderHeader();
        assertNotEquals("string", orderHeader);
    }
}
