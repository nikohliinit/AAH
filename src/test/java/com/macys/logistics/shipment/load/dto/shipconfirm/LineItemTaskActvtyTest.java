package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LineItemTaskActvtyTest {

    @Test
    void testDefaultConstructor() {
        LineItemTaskActvty lineItemTaskActvty = new LineItemTaskActvty();
        assertNotNull(lineItemTaskActvty);
        assertNull(lineItemTaskActvty.getAssociateActivityId());
        assertNull(lineItemTaskActvty.getDeviceName());
        assertNull(lineItemTaskActvty.getDeviceType());
        assertNull(lineItemTaskActvty.getPickAttemptCount());
        assertNull(lineItemTaskActvty.getSourceApplicationId());
        assertNull(lineItemTaskActvty.getFulfillmentAssociateId());
    }

    @Test
    void testGettersAndSetters() {
        LineItemTaskActvty activity = new LineItemTaskActvty();
        activity.setAssociateActivityId("ACT001");
        activity.setDeviceName("MAWM");
        activity.setDeviceType("Desktop");
        activity.setPickAttemptCount(3);
        activity.setSourceApplicationId("MAWM");
        activity.setFulfillmentAssociateId("ASSOC001");

        assertEquals("ACT001", activity.getAssociateActivityId());
        assertEquals("MAWM", activity.getDeviceName());
        assertEquals("Desktop", activity.getDeviceType());
        assertEquals(3, activity.getPickAttemptCount());
        assertEquals("MAWM", activity.getSourceApplicationId());
        assertEquals("ASSOC001", activity.getFulfillmentAssociateId());
    }

    @Test
    void testEqualsAndHashCode() {
        LineItemTaskActvty activity1 = new LineItemTaskActvty();
        activity1.setAssociateActivityId("ACT001");
        activity1.setDeviceName("MAWM");
        activity1.setDeviceType("Desktop");
        activity1.setPickAttemptCount(3);
        activity1.setSourceApplicationId("MAWM");
        activity1.setFulfillmentAssociateId("ASSOC001");

        LineItemTaskActvty activity2 = new LineItemTaskActvty();
        activity2.setAssociateActivityId("ACT001");
        activity2.setDeviceName("MAWM");
        activity2.setDeviceType("Desktop");
        activity2.setPickAttemptCount(3);
        activity2.setSourceApplicationId("MAWM");
        activity2.setFulfillmentAssociateId("ASSOC001");

        assertEquals(activity1, activity2);
        assertEquals(activity1.hashCode(), activity2.hashCode());
    }

    @Test
    void testNotEquals() {
        LineItemTaskActvty activity1 = new LineItemTaskActvty();
        activity1.setAssociateActivityId("ACT001");

        LineItemTaskActvty activity2 = new LineItemTaskActvty();
        activity2.setAssociateActivityId("ACT999");

        assertNotEquals(activity1, activity2);
    }

    @Test
    void testToString() {
        LineItemTaskActvty activity = new LineItemTaskActvty();
        activity.setDeviceName("MAWM");
        activity.setSourceApplicationId("MAWM");

        String toString = activity.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("MAWM"));
    }

    @Test
    void testEqualsSameInstance() {
        LineItemTaskActvty activity1 = new LineItemTaskActvty();
        activity1.setDeviceName("MAWM");
        LineItemTaskActvty activity2 = new LineItemTaskActvty();
        activity2.setDeviceName("MAWM");
        assertEquals(activity1, activity2);
    }

    @Test
    void testEqualsNull() {
        LineItemTaskActvty activity = new LineItemTaskActvty();
        assertNotEquals(null, activity);
    }

    @Test
    void testEqualsDifferentType() {
        LineItemTaskActvty activity = new LineItemTaskActvty();
        assertNotEquals("string", activity);
    }

    @Test
    void testNullFieldsStillEqual() {
        LineItemTaskActvty activity1 = new LineItemTaskActvty();
        LineItemTaskActvty activity2 = new LineItemTaskActvty();
        assertEquals(activity1, activity2);
        assertEquals(activity1.hashCode(), activity2.hashCode());
    }
}
