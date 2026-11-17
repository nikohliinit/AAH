package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShipmentListTest {

    private Shipment buildShipment(String fulfillmentNbr, int orderShipmentNbr) {
        return Shipment.builder()
                .fulfillmentNbr(fulfillmentNbr)
                .orderShipmentNbr(orderShipmentNbr)
                .shipmentState("SHPSHPD")
                .fulfillmentLocationNbr(6230)
                .fulfillmentAssociateId("ASSOC001")
                .trailer("TRAILER001")
                .apptNumber("APPT001")
                .build();
    }

    @Test
    void testNoArgsConstructor() {
        ShipmentList shipmentList = new ShipmentList();
        assertNotNull(shipmentList);
        assertNull(shipmentList.getShipment());
    }

    @Test
    void testAllArgsConstructor() {
        List<Shipment> shipments = Collections.singletonList(buildShipment("F04634654", 1));
        ShipmentList shipmentList = new ShipmentList(shipments);
        assertNotNull(shipmentList);
        assertEquals(1, shipmentList.getShipment().size());
        assertEquals("F04634654", shipmentList.getShipment().getFirst().getFulfillmentNbr());
    }

    @Test
    void testBuilder() {
        List<Shipment> shipments = Arrays.asList(
                buildShipment("F04634654", 1),
                buildShipment("F04634655", 2)
        );
        ShipmentList shipmentList = ShipmentList.builder()
                .shipment(shipments)
                .build();

        assertNotNull(shipmentList);
        assertEquals(2, shipmentList.getShipment().size());
    }

    @Test
    void testSetterAndGetter() {
        ShipmentList shipmentList = new ShipmentList();
        List<Shipment> shipments = Collections.singletonList(buildShipment("F04634654", 1));
        shipmentList.setShipment(shipments);

        assertEquals(1, shipmentList.getShipment().size());
        assertEquals("F04634654", shipmentList.getShipment().getFirst().getFulfillmentNbr());
    }

    @Test
    void testEqualsAndHashCode() {
        List<Shipment> shipments = Collections.singletonList(buildShipment("F04634654", 1));
        ShipmentList list1 = ShipmentList.builder().shipment(shipments).build();
        ShipmentList list2 = ShipmentList.builder().shipment(shipments).build();

        assertEquals(list1, list2);
        assertEquals(list1.hashCode(), list2.hashCode());
    }

    @Test
    void testNotEquals() {
        ShipmentList list1 = ShipmentList.builder()
                .shipment(Collections.singletonList(buildShipment("F04634654", 1)))
                .build();
        ShipmentList list2 = ShipmentList.builder()
                .shipment(Collections.singletonList(buildShipment("F99999999", 2)))
                .build();
        assertNotEquals(list1, list2);
    }

    @Test
    void testToString() {
        List<Shipment> shipments = Collections.singletonList(buildShipment("F04634654", 1));
        ShipmentList shipmentList = ShipmentList.builder().shipment(shipments).build();

        String toString = shipmentList.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("F04634654"));
    }

    @Test
    void testEqualsSameInstance() {
        ShipmentList list1 = new ShipmentList();
        ShipmentList list2 = new ShipmentList();
        assertEquals(list1, list2);
    }

    @Test
    void testEqualsNull() {
        ShipmentList list = new ShipmentList();
        assertNotEquals(null, list);
    }

    @Test
    void testEqualsDifferentType() {
        ShipmentList list = new ShipmentList();
        assertNotEquals("string", list);
    }

    @Test
    void testBuilderWithEmptyList() {
        ShipmentList list = ShipmentList.builder().shipment(Collections.emptyList()).build();
        assertNotNull(list.getShipment());
        assertTrue(list.getShipment().isEmpty());
    }
}
