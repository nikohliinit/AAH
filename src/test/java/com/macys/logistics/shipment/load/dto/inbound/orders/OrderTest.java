package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderTest {

    private LocationInfo buildLocation(String id, Boolean hub) {
        LocationInfo li = new LocationInfo();
        li.setLocationId(id);
        li.setIsHub(hub);
        return li;
    }

    private TimeWindow buildTimeWindow(String e, String l) {
        TimeWindow tw = new TimeWindow();
        tw.setEarliest(e);
        tw.setLatest(l);
        return tw;
    }

    private Charges buildCharges() {
        Charges c = new Charges();
        c.setRevenue(new BigDecimal("10.50"));
        c.setCurrency("USD");
        return c;
    }

    private Carrier buildCarrier() {
        Carrier c = new Carrier();
        c.setScac("SCAC");
        return c;
    }

    private Vendor buildVendor() {
        Vendor v = new Vendor();
        v.setSupplierNbr("V1");
        v.setName("Vendor");
        return v;
    }

    private Commodity buildCommodity() {
        Commodity c = new Commodity();
        c.setCode("C1");
        c.setDescription("Desc");
        return c;
    }

    private LineItem buildLineItem() {
        LineItem li = new LineItem();
        li.setPieces(1);
        li.setWeightLb(new BigDecimal("2.50"));
        li.setVolumeCf(new BigDecimal("3.50"));
        return li;
    }

    private Reference buildReference() {
        Reference r = new Reference();
        r.setType("BOL");
        r.setValue("B1");
        return r;
    }

    private Order buildFullOrder() {
        Order o = new Order();
        o.setOrderId("O1");
        o.setOrigin(buildLocation("L1", true));
        o.setDestination(buildLocation("L2", false));
        o.setPickupWindow(buildTimeWindow("E1", "L1"));
        o.setDeliveryWindow(buildTimeWindow("E2", "L2"));
        o.setCharges(buildCharges());
        o.setCarrier(buildCarrier());
        o.setVendor(buildVendor());
        o.setCommodity(buildCommodity());
        o.setLineItems(List.of(buildLineItem()));
        o.setReferences(List.of(buildReference()));
        o.setProfiles(Arrays.asList("P1", "P2"));
        o.setCustomerCode("CC");
        o.setFreightTerms("PPD");
        o.setShipProject("SP1");
        return o;
    }

    @Test
    void testDefaultConstructor() {
        Order o = new Order();
        assertNull(o.getOrderId());
        assertNull(o.getOrigin());
        assertNull(o.getDestination());
        assertNull(o.getPickupWindow());
        assertNull(o.getDeliveryWindow());
        assertNull(o.getCharges());
        assertNull(o.getCarrier());
        assertNull(o.getVendor());
        assertNull(o.getCommodity());
        assertNull(o.getLineItems());
        assertNull(o.getReferences());
        assertNull(o.getProfiles());
        assertNull(o.getCustomerCode());
        assertNull(o.getFreightTerms());
        assertNull(o.getShipProject());
    }

    @Test
    void testGettersAndSetters() {
        Order full = buildFullOrder();

        assertEquals("O1", full.getOrderId());
        assertEquals("L1", full.getOrigin().getLocationId());
        assertEquals(Boolean.TRUE, full.getOrigin().getIsHub());
        assertEquals("L2", full.getDestination().getLocationId());
        assertEquals(Boolean.FALSE, full.getDestination().getIsHub());
        assertEquals("E1", full.getPickupWindow().getEarliest());
        assertEquals("L1", full.getPickupWindow().getLatest());
        assertEquals("E2", full.getDeliveryWindow().getEarliest());
        assertEquals("L2", full.getDeliveryWindow().getLatest());
        assertEquals(new BigDecimal("10.50"), full.getCharges().getRevenue());
        assertEquals("USD", full.getCharges().getCurrency());
        assertEquals("SCAC", full.getCarrier().getScac());
        assertEquals("V1", full.getVendor().getSupplierNbr());
        assertEquals("Vendor", full.getVendor().getName());
        assertEquals("C1", full.getCommodity().getCode());
        assertEquals(1, full.getLineItems().size());
        assertEquals(1, full.getLineItems().getFirst().getPieces());
        assertEquals(1, full.getReferences().size());
        assertEquals("BOL", full.getReferences().getFirst().getType());
        assertEquals(Arrays.asList("P1", "P2"), full.getProfiles());
        assertEquals("CC", full.getCustomerCode());
        assertEquals("PPD", full.getFreightTerms());
        assertEquals("SP1", full.getShipProject());
    }

    @Test
    void testEqualsHashCodeToString() {
        Order o1 = buildFullOrder();
        Order o2 = buildFullOrder();

        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());

        String ts = o1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("O1"));
        assertTrue(ts.contains("SCAC"));
        assertTrue(ts.contains("PPD"));
        assertTrue(ts.contains("SP1"));
    }

    @Test
    void testEqualsEdgeCasesAndInequality() {
        Order o = new Order();
        Order copy = new Order();
        assertEquals(o, copy);
        assertNotEquals(null, o);
        assertNotEquals("string", o);

        Order o1 = buildFullOrder();
        Order o2 = buildFullOrder();
        o2.setOrderId("DIFFERENT");
        assertNotEquals(o1, o2);
    }

    @Test
    void testJacksonDeserializationCoreFields() throws JsonProcessingException {
        String json = """
                {
                  "orderId":"O1",
                  "origin":{"locationId":"L1","isHub":true},
                  "destination":{"locationId":"L2","isHub":false},
                  "pickupWindow":{"earliest":"E1","latest":"L1"},
                  "deliveryWindow":{"earliest":"E2","latest":"L2"},
                  "charges":{"revenue":10.5,"currency":"USD"},
                  "carrier":{"scac":"SCAC"},
                  "vendor":{"supplierNbr":"V1","name":"Vendor"},
                  "commodity":{"code":"C1","description":"Desc"},
                  "lineItems":[{"pieces":1,"weightLb":2.5,"volumeCf":3.5}],
                  "references":[{"type":"BOL","value":"B1"}],
                  "profiles":["P1","P2"],
                  "customerCode":"CC",
                  "freightTerms":"PPD",
                  "shipProject":"SP1"
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        Order deserialized = mapper.readValue(json, Order.class);

        assertEquals("O1", deserialized.getOrderId());
        assertEquals("L1", deserialized.getOrigin().getLocationId());
        assertEquals(Boolean.TRUE, deserialized.getOrigin().getIsHub());
        assertEquals("L2", deserialized.getDestination().getLocationId());
        assertEquals(Boolean.FALSE, deserialized.getDestination().getIsHub());
        assertEquals("E1", deserialized.getPickupWindow().getEarliest());
        assertEquals("L1", deserialized.getPickupWindow().getLatest());
        assertEquals("E2", deserialized.getDeliveryWindow().getEarliest());
        assertEquals("L2", deserialized.getDeliveryWindow().getLatest());
        assertEquals(new BigDecimal("10.5"), deserialized.getCharges().getRevenue());
        assertEquals("USD", deserialized.getCharges().getCurrency());
        assertEquals("SCAC", deserialized.getCarrier().getScac());
        assertEquals("V1", deserialized.getVendor().getSupplierNbr());
        assertEquals("Vendor", deserialized.getVendor().getName());
        assertEquals("C1", deserialized.getCommodity().getCode());
    }

    @Test
    void testJacksonDeserializationCollectionsAndSerialization() throws JsonProcessingException {
        String json = """
                {
                  "orderId":"O1",
                  "lineItems":[{"pieces":1,"weightLb":2.5,"volumeCf":3.5}],
                  "references":[{"type":"BOL","value":"B1"}],
                  "profiles":["P1","P2"],
                  "customerCode":"CC",
                  "freightTerms":"PPD",
                  "shipProject":"SP1"
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        Order deserialized = mapper.readValue(json, Order.class);

        assertEquals(1, deserialized.getLineItems().size());
        assertEquals(1, deserialized.getLineItems().getFirst().getPieces());
        assertEquals(new BigDecimal("2.5"), deserialized.getLineItems().getFirst().getWeightLb());
        assertEquals(new BigDecimal("3.5"), deserialized.getLineItems().getFirst().getVolumeCf());
        assertEquals(1, deserialized.getReferences().size());
        assertEquals("BOL", deserialized.getReferences().getFirst().getType());
        assertEquals(Arrays.asList("P1", "P2"), deserialized.getProfiles());
        assertEquals("CC", deserialized.getCustomerCode());
        assertEquals("PPD", deserialized.getFreightTerms());
        assertEquals("SP1", deserialized.getShipProject());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"orderId\":\"O1\""));
        assertTrue(serialized.contains("\"lineItems\""));
        assertTrue(serialized.contains("\"freightTerms\":\"PPD\""));
    }
}
