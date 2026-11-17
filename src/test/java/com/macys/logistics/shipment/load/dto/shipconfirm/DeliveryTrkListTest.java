package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeliveryTrkListTest {

    private DeliveryTrk buildDeliveryTrk(String trackingNbr, String carrierName) {
        DeliveryTrk trk = new DeliveryTrk();
        trk.setDeliveryTrackingNbr(trackingNbr);
        trk.setDeliveryCarrierName(carrierName);
        trk.setCartonSequenceNbr(1);
        trk.setCartonWeightValue(10.0);
        trk.setCartonNbr("CTN001");
        trk.setFreightChargeAmount(50.0);
        return trk;
    }

    @Test
    void testNoArgsConstructor() {
        DeliveryTrkList deliveryTrkList = new DeliveryTrkList();
        assertNotNull(deliveryTrkList);
        assertNull(deliveryTrkList.getDeliveryTrk());
    }

    @Test
    void testAllArgsConstructor() {
        List<DeliveryTrk> trks = Collections.singletonList(buildDeliveryTrk("TRK001", "UPS"));
        DeliveryTrkList list = new DeliveryTrkList(trks);
        assertNotNull(list);
        assertEquals(1, list.getDeliveryTrk().size());
        assertEquals("TRK001", list.getDeliveryTrk().getFirst().getDeliveryTrackingNbr());
    }

    @Test
    void testBuilder() {
        List<DeliveryTrk> trks = Arrays.asList(
                buildDeliveryTrk("TRK001", "UPS"),
                buildDeliveryTrk("TRK002", "FedEx")
        );
        DeliveryTrkList list = DeliveryTrkList.builder()
                .deliveryTrk(trks)
                .build();

        assertNotNull(list);
        assertEquals(2, list.getDeliveryTrk().size());
    }

    @Test
    void testSetterAndGetter() {
        DeliveryTrkList list = new DeliveryTrkList();
        List<DeliveryTrk> trks = Collections.singletonList(buildDeliveryTrk("TRK100", "FedEx"));
        list.setDeliveryTrk(trks);
        assertEquals(1, list.getDeliveryTrk().size());
        assertEquals("TRK100", list.getDeliveryTrk().getFirst().getDeliveryTrackingNbr());
    }

    @Test
    void testEqualsAndHashCode() {
        List<DeliveryTrk> trks = Collections.singletonList(buildDeliveryTrk("TRK001", "UPS"));
        DeliveryTrkList list1 = DeliveryTrkList.builder().deliveryTrk(trks).build();
        DeliveryTrkList list2 = DeliveryTrkList.builder().deliveryTrk(trks).build();

        assertEquals(list1, list2);
        assertEquals(list1.hashCode(), list2.hashCode());
    }

    @Test
    void testNotEquals() {
        DeliveryTrkList list1 = DeliveryTrkList.builder()
                .deliveryTrk(Collections.singletonList(buildDeliveryTrk("TRK001", "UPS")))
                .build();
        DeliveryTrkList list2 = DeliveryTrkList.builder()
                .deliveryTrk(Collections.singletonList(buildDeliveryTrk("TRK999", "DHL")))
                .build();
        assertNotEquals(list1, list2);
    }

    @Test
    void testToString() {
        List<DeliveryTrk> trks = Collections.singletonList(buildDeliveryTrk("TRK001", "UPS"));
        DeliveryTrkList list = DeliveryTrkList.builder().deliveryTrk(trks).build();

        String toString = list.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("TRK001"));
    }

    @Test
    void testEqualsSameInstance() {
        DeliveryTrkList list = new DeliveryTrkList();
        assertSame(list, list);
    }

    @Test
    void testEqualsNull() {
        DeliveryTrkList list = new DeliveryTrkList();
        assertNotEquals(null, list);
    }

    @Test
    void testEqualsDifferentType() {
        DeliveryTrkList list = new DeliveryTrkList();
        assertNotEquals("string", list);
    }

    @Test
    void testBuilderWithEmptyList() {
        DeliveryTrkList list = DeliveryTrkList.builder().deliveryTrk(Collections.emptyList()).build();
        assertNotNull(list.getDeliveryTrk());
        assertTrue(list.getDeliveryTrk().isEmpty());
    }
}
