package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartonContentListTest {

    private CartonContent buildCartonContent(String trackingNbr, int lineItemNbr) {
        CartonContent cc = new CartonContent();
        cc.setDeliveryTrackingNbr(trackingNbr);
        cc.setOrderLineItemNbr(lineItemNbr);
        cc.setItemPackedQuantity(5);
        cc.setItemSkuUpc(100L);
        cc.setItemScannedSkuUpc(200L);
        return cc;
    }

    @Test
    void testNoArgsConstructor() {
        CartonContentList list = new CartonContentList();
        assertNotNull(list);
        assertNull(list.getCartonContent());
    }

    @Test
    void testAllArgsConstructor() {
        List<CartonContent> contents = Collections.singletonList(buildCartonContent("TRK001", 1));
        CartonContentList list = new CartonContentList(contents);
        assertNotNull(list);
        assertEquals(1, list.getCartonContent().size());
        assertEquals("TRK001", list.getCartonContent().getFirst().getDeliveryTrackingNbr());
    }

    @Test
    void testBuilder() {
        List<CartonContent> contents = Arrays.asList(
                buildCartonContent("TRK001", 1),
                buildCartonContent("TRK002", 2)
        );
        CartonContentList list = CartonContentList.builder()
                .cartonContent(contents)
                .build();

        assertNotNull(list);
        assertEquals(2, list.getCartonContent().size());
    }

    @Test
    void testSetterAndGetter() {
        CartonContentList list = new CartonContentList();
        List<CartonContent> contents = Collections.singletonList(buildCartonContent("TRK100", 3));
        list.setCartonContent(contents);
        assertEquals(1, list.getCartonContent().size());
        assertEquals("TRK100", list.getCartonContent().getFirst().getDeliveryTrackingNbr());
    }

    @Test
    void testEqualsAndHashCode() {
        List<CartonContent> contents = Collections.singletonList(buildCartonContent("TRK001", 1));
        CartonContentList list1 = CartonContentList.builder().cartonContent(contents).build();
        CartonContentList list2 = CartonContentList.builder().cartonContent(contents).build();

        assertEquals(list1, list2);
        assertEquals(list1.hashCode(), list2.hashCode());
    }

    @Test
    void testNotEquals() {
        CartonContentList list1 = CartonContentList.builder()
                .cartonContent(Collections.singletonList(buildCartonContent("TRK001", 1)))
                .build();
        CartonContentList list2 = CartonContentList.builder()
                .cartonContent(Collections.singletonList(buildCartonContent("TRK999", 2)))
                .build();
        assertNotEquals(list1, list2);
    }

    @Test
    void testToString() {
        List<CartonContent> contents = Collections.singletonList(buildCartonContent("TRK001", 1));
        CartonContentList list = CartonContentList.builder().cartonContent(contents).build();

        String toString = list.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("TRK001"));
    }

    @Test
    void testEqualsSameInstance() {
        CartonContentList list = new CartonContentList();
        assertSame(list, list);
    }

    @Test
    void testEqualsNull() {
        CartonContentList list = new CartonContentList();
        assertNotEquals(null, list);
    }

    @Test
    void testEqualsDifferentType() {
        CartonContentList list = new CartonContentList();
        assertNotEquals("string", list);
    }

    @Test
    void testBuilderWithEmptyList() {
        CartonContentList list = CartonContentList.builder().cartonContent(Collections.emptyList()).build();
        assertNotNull(list.getCartonContent());
        assertTrue(list.getCartonContent().isEmpty());
    }
}
