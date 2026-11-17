package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShipmentDetailsTest {

    private Origin buildOrigin() {
        Origin o = new Origin();
        o.setLocationId("O1");
        o.setCity("NYC");
        return o;
    }

    private Destination buildDestination() {
        return Destination.builder()
                .locationAbbreviation("ATL")
                .locationNbr(6230)
                .build();
    }

    private PurchaseOrders buildPo() {
        PurchaseOrders po = new PurchaseOrders();
        po.setPoNumber(1L);
        po.setDivisionNumber(71);
        po.setDepartmentNumber(100);
        po.setInDcDate("2026-03-11");
        po.setCancelDate("2026-03-20");
        po.setPoStatus("OPEN");
        return po;
    }

    private SRRRequestDetails buildSrrDetail() {
        SRRRequestDetails s = new SRRRequestDetails();
        s.setLabelName("LBL");
        s.setUserInputValue("VAL");
        s.setLabelNumber(9);
        return s;
    }

    private SpecialRoutingRequestDetails buildSrr() {
        SpecialRoutingRequestDetails d = new SpecialRoutingRequestDetails();
        d.setSrrTypeNumber(1);
        d.setSrrTypeDescription("Desc");
        d.setSrrRequestDetails(List.of(buildSrrDetail()));
        d.setCarrierScac("SCAC");
        return d;
    }

    private ShipmentDetails buildFull() {
        ShipmentDetails s = new ShipmentDetails();
        s.setVendorNumber(123);
        s.setVendorName("Acme");
        s.setVendorDunsNumber(987654321L);
        s.setOrigin(buildOrigin());
        s.setDestination(buildDestination());
        s.setReadyToShipDate("2026-03-10");
        s.setTotalCube(12.5D);
        s.setTotalWeight(100.0D);
        s.setTotalCarton(50L);
        s.setFilledQuantity(40L);
        s.setTotalPallet(5L);
        s.setFreightClassName("CLASS");
        s.setPurchaseOrders(List.of(buildPo()));
        s.setSpecialRoutingRequestDetails(buildSrr());
        s.setCreatedBy("user");
        s.setCommodityDesc("Home");
        return s;
    }

    @Test
    void testDefaultConstructor() {
        ShipmentDetails s = new ShipmentDetails();
        assertEquals(0, s.getVendorNumber());
        assertNull(s.getVendorName());
        assertNull(s.getVendorDunsNumber());
        assertNull(s.getOrigin());
        assertNull(s.getDestination());
        assertNull(s.getReadyToShipDate());
        assertNull(s.getTotalCube());
        assertNull(s.getTotalWeight());
        assertNull(s.getTotalCarton());
        assertNull(s.getFilledQuantity());
        assertNull(s.getTotalPallet());
        assertNull(s.getFreightClassName());
        assertNull(s.getPurchaseOrders());
        assertNull(s.getSpecialRoutingRequestDetails());
        assertNull(s.getCreatedBy());
        assertNull(s.getCommodityDesc());
    }

    @Test
    void testGettersAndSetters() {
        ShipmentDetails full = buildFull();
        assertEquals(123, full.getVendorNumber());
        assertEquals("Acme", full.getVendorName());
        assertEquals(987654321L, full.getVendorDunsNumber());
        assertEquals("O1", full.getOrigin().getLocationId());
        assertEquals("ATL", full.getDestination().getLocationAbbreviation());
        assertEquals(6230, full.getDestination().getLocationNbr());
        assertEquals("2026-03-10", full.getReadyToShipDate());
        assertEquals(12.5D, full.getTotalCube());
        assertEquals(100.0D, full.getTotalWeight());
        assertEquals(50L, full.getTotalCarton());
        assertEquals(40L, full.getFilledQuantity());
        assertEquals(5L, full.getTotalPallet());
        assertEquals("CLASS", full.getFreightClassName());
        assertEquals(1, full.getPurchaseOrders().size());
        assertEquals(1L, full.getPurchaseOrders().getFirst().getPoNumber());
        assertEquals("user", full.getCreatedBy());
        assertEquals("Home", full.getCommodityDesc());
        assertNotNull(full.getSpecialRoutingRequestDetails());
        assertEquals("SCAC", full.getSpecialRoutingRequestDetails().getCarrierScac());
        assertEquals(1, full.getSpecialRoutingRequestDetails().getSrrRequestDetails().size());
    }

    @Test
    void testEqualsHashCodeToString() {
        ShipmentDetails a = buildFull();
        ShipmentDetails b = buildFull();
        ShipmentDetails c = buildFull();
        c.setVendorNumber(999);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("vendorNumber=123"));
        assertTrue(ts.contains("vendorName=Acme"));
    }

    @Test
    void testEqualsEdgeCases() {
        ShipmentDetails s = new ShipmentDetails();
        ShipmentDetails copy = new ShipmentDetails();
        assertEquals(s, copy);
        assertNotEquals(null, s);
        assertNotEquals("string", s);
    }

    @Test
    void testJacksonDeserializationAndUnknownIgnored() throws JsonProcessingException {
        String json = """
        {
          "vendorNumber":123,
          "vendorName":"Acme",
          "vendorDunsNumber":987654321,
          "origin":{"locationId":"O1","city":"NYC"},
          "destination":{"locationAbbreviation":"ATL","locationNbr":6230},
          "readyToShipDate":"2026-03-10",
          "totalCube":12.5,
          "totalWeight":100.0,
          "totalCarton":50,
          "filledQuantity":40,
          "totalPallet":5,
          "freightClassName":"CLASS",
          "purchaseOrders":[{"poNumber":1,"divisionNumber":71,"departmentNumber":100,"inDcDate":"2026-03-11","cancelDate":"2026-03-20","poStatus":"OPEN"}],
          "specialRoutingRequestDetails":{"srrTypeNumber":1,"srrTypeDescription":"Desc","srrRequestDetails":[{"labelName":"LBL","userInputValue":"VAL","labelNumber":9}],"carrierScac":"SCAC"},
          "createdBy":"user",
          "commodityDesc":"Home",
          "unknownField":"ignored"
        }
        """;

        ObjectMapper mapper = new ObjectMapper();
        ShipmentDetails deserialized = mapper.readValue(json, ShipmentDetails.class);

        assertEquals(123, deserialized.getVendorNumber());
        assertEquals("Acme", deserialized.getVendorName());
        assertEquals(987654321L, deserialized.getVendorDunsNumber());
        assertEquals("O1", deserialized.getOrigin().getLocationId());
        assertEquals("ATL", deserialized.getDestination().getLocationAbbreviation());
        assertEquals(6230, deserialized.getDestination().getLocationNbr());
        assertEquals("2026-03-10", deserialized.getReadyToShipDate());
        assertEquals(12.5D, deserialized.getTotalCube());
        assertEquals(100.0D, deserialized.getTotalWeight());
        assertEquals(50L, deserialized.getTotalCarton());
        assertEquals(40L, deserialized.getFilledQuantity());
        assertEquals(5L, deserialized.getTotalPallet());
        assertEquals("CLASS", deserialized.getFreightClassName());
        assertEquals(1, deserialized.getPurchaseOrders().size());
        assertEquals(1L, deserialized.getPurchaseOrders().getFirst().getPoNumber());
        assertEquals("SCAC", deserialized.getSpecialRoutingRequestDetails().getCarrierScac());
        assertEquals("user", deserialized.getCreatedBy());
        assertEquals("Home", deserialized.getCommodityDesc());
        // unknown field is ignored due to @JsonIgnoreProperties(ignoreUnknown = true)
    }

    @Test
    void testJacksonSerialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ShipmentDetails val = buildFull();
        String serialized = mapper.writeValueAsString(val);
        assertTrue(serialized.contains("\"vendorNumber\":123"));
        assertTrue(serialized.contains("\"vendorName\":\"Acme\""));
        assertTrue(serialized.contains("\"destination\""));
        assertTrue(serialized.contains("\"purchaseOrders\""));
        assertTrue(serialized.contains("\"specialRoutingRequestDetails\""));
    }
}
