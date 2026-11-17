package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LineItemTest {

    private LineItemTaskActvty buildLineItemTaskActvty() {
        LineItemTaskActvty activity = new LineItemTaskActvty();
        activity.setAssociateActivityId("ACT001");
        activity.setDeviceName("MAWM");
        activity.setDeviceType("Desktop");
        activity.setPickAttemptCount(1);
        activity.setSourceApplicationId("MAWM");
        activity.setFulfillmentAssociateId("ASSOC001");
        return activity;
    }

    @Test
    void testDefaultConstructor() {
        LineItem lineItem = new LineItem();
        assertNotNull(lineItem);
        assertEquals(0, lineItem.getFulfillmentLocationNbr());
        assertEquals(0, lineItem.getItemDepartmentNbr());
        assertEquals(0, lineItem.getItemOriginalQuantity());
        assertEquals(0, lineItem.getItemPickedQuantity());
        assertEquals(0L, lineItem.getItemScannedSkuUpc());
        assertEquals(0L, lineItem.getItemSkuUpc());
        assertNull(lineItem.getLineItemState());
        assertEquals(0, lineItem.getOrderLineItemNbr());
        assertEquals(0, lineItem.getOrderShipmentNbr());
        assertNull(lineItem.getItemAdjustedQuantity());
        assertNull(lineItem.getItemNotInLocationCode());
        assertNull(lineItem.getLineItemTaskActvty());
    }

    @Test
    void testGettersAndSetters() {
        LineItem lineItem = new LineItem();
        LineItemTaskActvty activity = buildLineItemTaskActvty();

        lineItem.setFulfillmentLocationNbr(6230);
        lineItem.setItemDepartmentNbr(840);
        lineItem.setItemOriginalQuantity(2);
        lineItem.setItemPickedQuantity(2);
        lineItem.setItemScannedSkuUpc(878878669L);
        lineItem.setItemSkuUpc(878878669L);
        lineItem.setLineItemState("SHPSHPD");
        lineItem.setOrderLineItemNbr(1);
        lineItem.setOrderShipmentNbr(1);
        lineItem.setItemAdjustedQuantity(5);
        lineItem.setItemNotInLocationCode("CODE01");
        lineItem.setLineItemTaskActvty(activity);

        assertEquals(6230, lineItem.getFulfillmentLocationNbr());
        assertEquals(840, lineItem.getItemDepartmentNbr());
        assertEquals(2, lineItem.getItemOriginalQuantity());
        assertEquals(2, lineItem.getItemPickedQuantity());
        assertEquals(878878669L, lineItem.getItemScannedSkuUpc());
        assertEquals(878878669L, lineItem.getItemSkuUpc());
        assertEquals("SHPSHPD", lineItem.getLineItemState());
        assertEquals(1, lineItem.getOrderLineItemNbr());
        assertEquals(1, lineItem.getOrderShipmentNbr());
        assertEquals(5, lineItem.getItemAdjustedQuantity());
        assertEquals("CODE01", lineItem.getItemNotInLocationCode());
        assertEquals(activity, lineItem.getLineItemTaskActvty());
    }

    @Test
    void testEqualsAndHashCode() {
        LineItemTaskActvty activity = buildLineItemTaskActvty();

        LineItem lineItem1 = new LineItem();
        lineItem1.setFulfillmentLocationNbr(6230);
        lineItem1.setItemDepartmentNbr(840);
        lineItem1.setItemOriginalQuantity(2);
        lineItem1.setItemPickedQuantity(2);
        lineItem1.setItemScannedSkuUpc(878878669L);
        lineItem1.setItemSkuUpc(878878669L);
        lineItem1.setLineItemState("SHPSHPD");
        lineItem1.setOrderLineItemNbr(1);
        lineItem1.setOrderShipmentNbr(1);
        lineItem1.setItemAdjustedQuantity(null);
        lineItem1.setItemNotInLocationCode(null);
        lineItem1.setLineItemTaskActvty(activity);

        LineItem lineItem2 = new LineItem();
        lineItem2.setFulfillmentLocationNbr(6230);
        lineItem2.setItemDepartmentNbr(840);
        lineItem2.setItemOriginalQuantity(2);
        lineItem2.setItemPickedQuantity(2);
        lineItem2.setItemScannedSkuUpc(878878669L);
        lineItem2.setItemSkuUpc(878878669L);
        lineItem2.setLineItemState("SHPSHPD");
        lineItem2.setOrderLineItemNbr(1);
        lineItem2.setOrderShipmentNbr(1);
        lineItem2.setItemAdjustedQuantity(null);
        lineItem2.setItemNotInLocationCode(null);
        lineItem2.setLineItemTaskActvty(activity);

        assertEquals(lineItem1, lineItem2);
        assertEquals(lineItem1.hashCode(), lineItem2.hashCode());
    }

    @Test
    void testNotEquals() {
        LineItem lineItem1 = new LineItem();
        lineItem1.setFulfillmentLocationNbr(6230);

        LineItem lineItem2 = new LineItem();
        lineItem2.setFulfillmentLocationNbr(9999);

        assertNotEquals(lineItem1, lineItem2);
    }

    @Test
    void testToString() {
        LineItem lineItem = new LineItem();
        lineItem.setFulfillmentLocationNbr(6230);
        lineItem.setLineItemState("SHPSHPD");

        String toString = lineItem.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("6230"));
        assertTrue(toString.contains("SHPSHPD"));
    }

    @Test
    void testEqualsSameInstance() {
        LineItem lineItem1 = new LineItem();
        lineItem1.setLineItemState("SHPSHPD");
        LineItem lineItem2 = new LineItem();
        lineItem2.setLineItemState("SHPSHPD");
        assertEquals(lineItem1, lineItem2);
    }

    @Test
    void testEqualsNull() {
        LineItem lineItem = new LineItem();
        assertNotEquals(null, lineItem);
    }

    @Test
    void testEqualsDifferentType() {
        LineItem lineItem = new LineItem();
        assertNotEquals("string", lineItem);
    }
}
