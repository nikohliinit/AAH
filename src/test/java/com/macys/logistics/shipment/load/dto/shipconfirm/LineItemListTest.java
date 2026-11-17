package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LineItemListTest {

    private LineItem buildLineItem(int orderLineItemNbr, String lineItemState) {
        LineItem item = new LineItem();
        item.setOrderLineItemNbr(orderLineItemNbr);
        item.setLineItemState(lineItemState);
        item.setFulfillmentLocationNbr(6230);
        item.setItemDepartmentNbr(840);
        item.setItemOriginalQuantity(2);
        item.setItemPickedQuantity(2);
        item.setItemScannedSkuUpc(878878669L);
        item.setItemSkuUpc(878878669L);
        item.setOrderShipmentNbr(1);
        return item;
    }

    @Test
    void testDefaultConstructor() {
        LineItemList lineItemList = new LineItemList();
        assertNotNull(lineItemList);
        assertNull(lineItemList.getLineItem());
    }

    @Test
    void testSetterAndGetter() {
        LineItemList lineItemList = new LineItemList();
        List<LineItem> items = Arrays.asList(
                buildLineItem(1, "SHPSHPD"),
                buildLineItem(2, "SHPSHPD")
        );
        lineItemList.setLineItem(items);

        assertNotNull(lineItemList.getLineItem());
        assertEquals(2, lineItemList.getLineItem().size());
        assertEquals(1, lineItemList.getLineItem().getFirst().getOrderLineItemNbr());
        assertEquals(2, lineItemList.getLineItem().getLast().getOrderLineItemNbr());
    }

    @Test
    void testEqualsAndHashCode() {
        List<LineItem> items = Collections.singletonList(buildLineItem(1, "SHPSHPD"));

        LineItemList list1 = new LineItemList();
        list1.setLineItem(items);

        LineItemList list2 = new LineItemList();
        list2.setLineItem(items);

        assertEquals(list1, list2);
        assertEquals(list1.hashCode(), list2.hashCode());
    }

    @Test
    void testNotEquals() {
        LineItemList list1 = new LineItemList();
        list1.setLineItem(Collections.singletonList(buildLineItem(1, "SHPSHPD")));

        LineItemList list2 = new LineItemList();
        list2.setLineItem(Collections.singletonList(buildLineItem(2, "CANCELLED")));

        assertNotEquals(list1, list2);
    }

    @Test
    void testToString() {
        List<LineItem> items = Collections.singletonList(buildLineItem(1, "SHPSHPD"));
        LineItemList lineItemList = new LineItemList();
        lineItemList.setLineItem(items);

        String toString = lineItemList.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("SHPSHPD"));
    }

    @Test
    void testEqualsSameInstance() {
        LineItemList list1 = new LineItemList();
        list1.setLineItem(Collections.emptyList());
        LineItemList list2 = new LineItemList();
        list2.setLineItem(Collections.emptyList());
        assertEquals(list1, list2);
    }

    @Test
    void testEqualsNull() {
        LineItemList list = new LineItemList();
        assertNotEquals(null, list);
    }

    @Test
    void testEqualsDifferentType() {
        LineItemList list = new LineItemList();
        assertNotEquals("string", list);
    }

    @Test
    void testEmptyLineItemList() {
        LineItemList list = new LineItemList();
        list.setLineItem(Collections.emptyList());
        assertNotNull(list.getLineItem());
        assertTrue(list.getLineItem().isEmpty());
    }
}
