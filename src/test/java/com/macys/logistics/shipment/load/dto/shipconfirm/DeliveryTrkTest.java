package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeliveryTrkTest {

    @Test
    void testDefaultConstructor() {
        DeliveryTrk deliveryTrk = new DeliveryTrk();
        assertNotNull(deliveryTrk);
        assertNull(deliveryTrk.getCartonSequenceNbr());
        assertNull(deliveryTrk.getCartonWeightValue());
        assertNull(deliveryTrk.getDeliveryTrackingNbr());
        assertNull(deliveryTrk.getDeliveryCarrierName());
        assertNull(deliveryTrk.getCartonNbr());
        assertNull(deliveryTrk.getFreightChargeAmount());
    }

    @Test
    void testGettersAndSetters() {
        DeliveryTrk deliveryTrk = new DeliveryTrk();
        deliveryTrk.setCartonSequenceNbr(1);
        deliveryTrk.setCartonWeightValue(12.5);
        deliveryTrk.setDeliveryTrackingNbr("TRK00001");
        deliveryTrk.setDeliveryCarrierName("UPS");
        deliveryTrk.setCartonNbr("CTN123");
        deliveryTrk.setFreightChargeAmount(99.99);

        assertEquals(1, deliveryTrk.getCartonSequenceNbr());
        assertEquals(12.5, deliveryTrk.getCartonWeightValue());
        assertEquals("TRK00001", deliveryTrk.getDeliveryTrackingNbr());
        assertEquals("UPS", deliveryTrk.getDeliveryCarrierName());
        assertEquals("CTN123", deliveryTrk.getCartonNbr());
        assertEquals(99.99, deliveryTrk.getFreightChargeAmount());
    }

    @Test
    void testEqualsAndHashCode() {
        DeliveryTrk trk1 = new DeliveryTrk();
        trk1.setCartonSequenceNbr(1);
        trk1.setCartonWeightValue(12.5);
        trk1.setDeliveryTrackingNbr("TRK00001");
        trk1.setDeliveryCarrierName("UPS");
        trk1.setCartonNbr("CTN123");
        trk1.setFreightChargeAmount(99.99);

        DeliveryTrk trk2 = new DeliveryTrk();
        trk2.setCartonSequenceNbr(1);
        trk2.setCartonWeightValue(12.5);
        trk2.setDeliveryTrackingNbr("TRK00001");
        trk2.setDeliveryCarrierName("UPS");
        trk2.setCartonNbr("CTN123");
        trk2.setFreightChargeAmount(99.99);

        assertEquals(trk1, trk2);
        assertEquals(trk1.hashCode(), trk2.hashCode());
    }

    @Test
    void testNotEquals() {
        DeliveryTrk trk1 = new DeliveryTrk();
        trk1.setDeliveryTrackingNbr("TRK00001");

        DeliveryTrk trk2 = new DeliveryTrk();
        trk2.setDeliveryTrackingNbr("TRK99999");

        assertNotEquals(trk1, trk2);
    }

    @Test
    void testToString() {
        DeliveryTrk deliveryTrk = new DeliveryTrk();
        deliveryTrk.setDeliveryTrackingNbr("TRK00001");
        deliveryTrk.setDeliveryCarrierName("UPS");

        String toString = deliveryTrk.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("TRK00001"));
        assertTrue(toString.contains("UPS"));
    }

    @Test
    void testEqualsSameInstance() {
        DeliveryTrk trk = new DeliveryTrk();
        trk.setDeliveryTrackingNbr("TRK00001");
        trk.setDeliveryCarrierName("UPS");
        DeliveryTrk copy = new DeliveryTrk();
        copy.setDeliveryTrackingNbr("TRK00001");
        copy.setDeliveryCarrierName("UPS");
        assertEquals(trk, copy);
        assertEquals(trk.hashCode(), copy.hashCode());
    }

    @Test
    void testEqualsNull() {
        DeliveryTrk trk = new DeliveryTrk();
        assertNotEquals(null, trk);
    }

    @Test
    void testEqualsDifferentType() {
        DeliveryTrk trk = new DeliveryTrk();
        assertNotEquals("string", trk);
    }
}
