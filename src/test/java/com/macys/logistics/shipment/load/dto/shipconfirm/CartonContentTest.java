package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartonContentTest {

    @Test
    void testGettersAndSetters() {
        CartonContent cartonContent = new CartonContent();
        cartonContent.setDeliveryTrackingNbr("TRK12345");
        cartonContent.setOrderLineItemNbr(1);
        cartonContent.setItemPackedQuantity(10);
        cartonContent.setItemSkuUpc(9876543210L);
        cartonContent.setItemScannedSkuUpc(1234567890L);

        assertEquals("TRK12345", cartonContent.getDeliveryTrackingNbr());
        assertEquals(1, cartonContent.getOrderLineItemNbr());
        assertEquals(10, cartonContent.getItemPackedQuantity());
        assertEquals(9876543210L, cartonContent.getItemSkuUpc());
        assertEquals(1234567890L, cartonContent.getItemScannedSkuUpc());
    }

    @Test
    void testDefaultConstructor() {
        CartonContent cartonContent = new CartonContent();
        assertNotNull(cartonContent);
        assertNull(cartonContent.getDeliveryTrackingNbr());
        assertNull(cartonContent.getOrderLineItemNbr());
        assertNull(cartonContent.getItemPackedQuantity());
        assertNull(cartonContent.getItemSkuUpc());
        assertNull(cartonContent.getItemScannedSkuUpc());
    }

    @Test
    void testEqualsAndHashCode() {
        CartonContent cartonContent1 = new CartonContent();
        cartonContent1.setDeliveryTrackingNbr("TRK12345");
        cartonContent1.setOrderLineItemNbr(1);
        cartonContent1.setItemPackedQuantity(10);
        cartonContent1.setItemSkuUpc(9876543210L);
        cartonContent1.setItemScannedSkuUpc(1234567890L);

        CartonContent cartonContent2 = new CartonContent();
        cartonContent2.setDeliveryTrackingNbr("TRK12345");
        cartonContent2.setOrderLineItemNbr(1);
        cartonContent2.setItemPackedQuantity(10);
        cartonContent2.setItemSkuUpc(9876543210L);
        cartonContent2.setItemScannedSkuUpc(1234567890L);

        assertEquals(cartonContent1, cartonContent2);
        assertEquals(cartonContent1.hashCode(), cartonContent2.hashCode());
    }

    @Test
    void testNotEquals() {
        CartonContent cartonContent1 = new CartonContent();
        cartonContent1.setDeliveryTrackingNbr("TRK12345");

        CartonContent cartonContent2 = new CartonContent();
        cartonContent2.setDeliveryTrackingNbr("TRK99999");

        assertNotEquals(cartonContent1, cartonContent2);
    }

    @Test
    void testToString() {
        CartonContent cartonContent = new CartonContent();
        cartonContent.setDeliveryTrackingNbr("TRK12345");
        cartonContent.setOrderLineItemNbr(1);

        String toString = cartonContent.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("TRK12345"));
        assertTrue(toString.contains("1"));
    }

    @Test
    void testEqualsSameInstance() {
        CartonContent cartonContent = new CartonContent();
        cartonContent.setDeliveryTrackingNbr("TRK12345");
        assertSame(cartonContent, cartonContent);
    }

    @Test
    void testEqualsNull() {
        CartonContent cartonContent = new CartonContent();
        assertNotEquals(null, cartonContent);
    }

    @Test
    void testEqualsDifferentType() {
        CartonContent cartonContent = new CartonContent();
        assertNotEquals("someString", cartonContent);
    }
}
