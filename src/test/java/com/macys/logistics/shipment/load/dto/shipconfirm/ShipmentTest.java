package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShipmentTest {

    private DeliveryTrkList buildDeliveryTrkList() {
        DeliveryTrk trk = new DeliveryTrk();
        trk.setDeliveryTrackingNbr("TRK001");
        trk.setDeliveryCarrierName("UPS");
        trk.setCartonSequenceNbr(1);
        trk.setCartonWeightValue(10.0);
        trk.setCartonNbr("CTN001");
        trk.setFreightChargeAmount(50.0);
        return DeliveryTrkList.builder()
                .deliveryTrk(Collections.singletonList(trk))
                .build();
    }

    private CartonContentList buildCartonContentList() {
        CartonContent cc = new CartonContent();
        cc.setDeliveryTrackingNbr("TRK001");
        cc.setOrderLineItemNbr(1);
        cc.setItemPackedQuantity(5);
        cc.setItemSkuUpc(100L);
        cc.setItemScannedSkuUpc(200L);
        return CartonContentList.builder()
                .cartonContent(Collections.singletonList(cc))
                .build();
    }

    @Test
    void testNoArgsConstructor() {
        Shipment shipment = new Shipment();
        assertNotNull(shipment);
        assertNull(shipment.getFulfillmentNbr());
        assertNull(shipment.getOrderShipmentNbr());
        assertNull(shipment.getShipmentState());
        assertNull(shipment.getFulfillmentLocationNbr());
        assertNull(shipment.getFulfillmentAssociateId());
        assertNull(shipment.getTrailer());
        assertNull(shipment.getApptNumber());
        assertNull(shipment.getDeliveryTrkList());
        assertNull(shipment.getCartonContentList());
    }

    @Test
    void testAllArgsConstructor() {
        DeliveryTrkList deliveryTrkList = buildDeliveryTrkList();
        CartonContentList cartonContentList = buildCartonContentList();

        Shipment shipment = new Shipment(
                "F04634654", 1, "SHPSHPD", 6230,
                "ASSOC001", "TRAILER001", "APPT001",
                deliveryTrkList, cartonContentList
        );

        assertEquals("F04634654", shipment.getFulfillmentNbr());
        assertEquals(1, shipment.getOrderShipmentNbr());
        assertEquals("SHPSHPD", shipment.getShipmentState());
        assertEquals(6230, shipment.getFulfillmentLocationNbr());
        assertEquals("ASSOC001", shipment.getFulfillmentAssociateId());
        assertEquals("TRAILER001", shipment.getTrailer());
        assertEquals("APPT001", shipment.getApptNumber());
        assertEquals(deliveryTrkList, shipment.getDeliveryTrkList());
        assertEquals(cartonContentList, shipment.getCartonContentList());
    }

    @Test
    void testBuilder() {
        DeliveryTrkList deliveryTrkList = buildDeliveryTrkList();
        CartonContentList cartonContentList = buildCartonContentList();

        Shipment shipment = Shipment.builder()
                .fulfillmentNbr("F04634654")
                .orderShipmentNbr(1)
                .shipmentState("SHPSHPD")
                .fulfillmentLocationNbr(6230)
                .fulfillmentAssociateId("ASSOC001")
                .trailer("TRAILER001")
                .apptNumber("APPT001")
                .deliveryTrkList(deliveryTrkList)
                .cartonContentList(cartonContentList)
                .build();

        assertNotNull(shipment);
        assertEquals("F04634654", shipment.getFulfillmentNbr());
        assertEquals(1, shipment.getOrderShipmentNbr());
        assertEquals("SHPSHPD", shipment.getShipmentState());
        assertEquals(6230, shipment.getFulfillmentLocationNbr());
        assertEquals("ASSOC001", shipment.getFulfillmentAssociateId());
        assertEquals("TRAILER001", shipment.getTrailer());
        assertEquals("APPT001", shipment.getApptNumber());
        assertEquals(deliveryTrkList, shipment.getDeliveryTrkList());
        assertEquals(cartonContentList, shipment.getCartonContentList());
    }

    @Test
    void testGettersAndSetters() {
        Shipment shipment = new Shipment();
        DeliveryTrkList deliveryTrkList = buildDeliveryTrkList();
        CartonContentList cartonContentList = buildCartonContentList();

        shipment.setFulfillmentNbr("F04634654");
        shipment.setOrderShipmentNbr(1);
        shipment.setShipmentState("SHPSHPD");
        shipment.setFulfillmentLocationNbr(6230);
        shipment.setFulfillmentAssociateId("ASSOC001");
        shipment.setTrailer("TRAILER001");
        shipment.setApptNumber("APPT001");
        shipment.setDeliveryTrkList(deliveryTrkList);
        shipment.setCartonContentList(cartonContentList);

        assertEquals("F04634654", shipment.getFulfillmentNbr());
        assertEquals(1, shipment.getOrderShipmentNbr());
        assertEquals("SHPSHPD", shipment.getShipmentState());
        assertEquals(6230, shipment.getFulfillmentLocationNbr());
        assertEquals("ASSOC001", shipment.getFulfillmentAssociateId());
        assertEquals("TRAILER001", shipment.getTrailer());
        assertEquals("APPT001", shipment.getApptNumber());
        assertEquals(deliveryTrkList, shipment.getDeliveryTrkList());
        assertEquals(cartonContentList, shipment.getCartonContentList());
    }

    @Test
    void testEqualsAndHashCode() {
        DeliveryTrkList deliveryTrkList = buildDeliveryTrkList();
        CartonContentList cartonContentList = buildCartonContentList();

        Shipment shipment1 = Shipment.builder()
                .fulfillmentNbr("F04634654")
                .orderShipmentNbr(1)
                .shipmentState("SHPSHPD")
                .fulfillmentLocationNbr(6230)
                .fulfillmentAssociateId("ASSOC001")
                .trailer("TRAILER001")
                .apptNumber("APPT001")
                .deliveryTrkList(deliveryTrkList)
                .cartonContentList(cartonContentList)
                .build();

        Shipment shipment2 = Shipment.builder()
                .fulfillmentNbr("F04634654")
                .orderShipmentNbr(1)
                .shipmentState("SHPSHPD")
                .fulfillmentLocationNbr(6230)
                .fulfillmentAssociateId("ASSOC001")
                .trailer("TRAILER001")
                .apptNumber("APPT001")
                .deliveryTrkList(deliveryTrkList)
                .cartonContentList(cartonContentList)
                .build();

        assertEquals(shipment1, shipment2);
        assertEquals(shipment1.hashCode(), shipment2.hashCode());
    }

    @Test
    void testNotEquals() {
        Shipment shipment1 = Shipment.builder().fulfillmentNbr("F04634654").build();
        Shipment shipment2 = Shipment.builder().fulfillmentNbr("F99999999").build();
        assertNotEquals(shipment1, shipment2);
    }

    @Test
    void testToString() {
        Shipment shipment = Shipment.builder()
                .fulfillmentNbr("F04634654")
                .shipmentState("SHPSHPD")
                .build();
        String toString = shipment.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("F04634654"));
        assertTrue(toString.contains("SHPSHPD"));
    }

    @Test
    void testEqualsSameInstance() {
        Shipment shipment1 = new Shipment();
        Shipment shipment2 = new Shipment();
        assertEquals(shipment1, shipment2);
    }

    @Test
    void testEqualsNull() {
        Shipment shipment = new Shipment();
        assertNotEquals(null, shipment);
    }

    @Test
    void testEqualsDifferentType() {
        Shipment shipment = new Shipment();
        assertNotEquals("string", shipment);
    }
}
