package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderShipmentMessageTest {

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

    private Charges buildCharges(String amount, String currency) {
        Charges c = new Charges();
        c.setRevenue(new BigDecimal(amount));
        c.setCurrency(currency);
        return c;
    }

    private Carrier buildCarrier(String scac) {
        Carrier c = new Carrier();
        c.setScac(scac);
        return c;
    }

    private Vendor buildVendor(String supplier, String name) {
        Vendor v = new Vendor();
        v.setSupplierNbr(supplier);
        v.setName(name);
        return v;
    }

    private Commodity buildCommodity(String code, String desc) {
        Commodity c = new Commodity();
        c.setCode(code);
        c.setDescription(desc);
        return c;
    }

    private LineItem buildLineItem(int pieces, String weight, String volume) {
        LineItem li = new LineItem();
        li.setPieces(pieces);
        li.setWeightLb(new BigDecimal(weight));
        li.setVolumeCf(new BigDecimal(volume));
        return li;
    }

    private Reference buildReference(String type, String value) {
        Reference r = new Reference();
        r.setType(type);
        r.setValue(value);
        return r;
    }

    private Order buildOrder() {
        Order o = new Order();
        o.setOrderId("O1");
        o.setOrigin(buildLocation("L1", true));
        o.setDestination(buildLocation("L2", false));
        o.setPickupWindow(buildTimeWindow("E1", "L1"));
        o.setDeliveryWindow(buildTimeWindow("E2", "L2"));
        o.setCharges(buildCharges("10.50", "USD"));
        o.setCarrier(buildCarrier("SCAC"));
        o.setVendor(buildVendor("V1", "Vendor"));
        o.setCommodity(buildCommodity("C1", "Desc"));
        o.setLineItems(List.of(buildLineItem(1, "2.50", "3.50")));
        o.setReferences(List.of(buildReference("BOL", "B1")));
        o.setProfiles(List.of("P1", "P2"));
        o.setCustomerCode("CC");
        o.setFreightTerms("PPD");
        o.setShipProject("SP1");
        return o;
    }

    private Spec buildSpec(int seq, String type, String value, String desc) {
        Spec s = new Spec();
        s.setSeq(seq);
        s.setType(type);
        s.setValue(value);
        s.setDescription(desc);
        return s;
    }

    private SpecialRouteRequest buildSpecialRoute(int seq, String type, String value, int priority, String text) {
        SpecialRouteRequest srr = new SpecialRouteRequest();
        srr.setSeq(seq);
        srr.setRouteType(type);
        srr.setRouteValue(value);
        srr.setPriority(priority);
        srr.setUserText(text);
        return srr;
    }

    private Note buildNote(int fbNoteSeq, String tmsNoteSeq, String text) {
        Note n = new Note();
        n.setFbNoteSeq(fbNoteSeq);
        n.setTmsNoteSeq(tmsNoteSeq);
        n.setText(text);
        return n;
    }

    private Audit buildAudit(String source, long seq, String ts) {
        Audit a = new Audit();
        a.setSource(source);
        a.setIntgrtnCtlSeqNbr(seq);
        a.setEventTs(ts);
        return a;
    }

    private OrderShipmentMessage buildFullMessage() {
        OrderShipmentMessage m = new OrderShipmentMessage();
        m.setTransCode("TC");
        m.setFbNbr("FB1");
        m.setOrder(buildOrder());
        m.setSpecs(List.of(buildSpec(1, "T", "V", "D")));
        m.setSpecialRouteRequests(List.of(buildSpecialRoute(1, "RT", "RV", 5, "UT")));
        m.setNotes(List.of(buildNote(1, "T", "X")));
        m.setAudit(buildAudit("SRC", 123L, "TS"));
        m.setPreferredAPCarrierCode("AP1");
        m.setSpclRteNbr("SR1");
        m.setPrimaryLoc("PL1");
        m.setVendorName("VN1");
        return m;
    }

    @Test
    void testDefaultConstructorAndGettersSetters() {
        OrderShipmentMessage m = new OrderShipmentMessage();
        assertNull(m.getTransCode());
        assertNull(m.getFbNbr());
        assertNull(m.getOrder());
        assertNull(m.getSpecs());
        assertNull(m.getSpecialRouteRequests());
        assertNull(m.getNotes());
        assertNull(m.getAudit());
        assertNull(m.getPreferredAPCarrierCode());
        assertNull(m.getSpclRteNbr());
        assertNull(m.getPrimaryLoc());
        assertNull(m.getVendorName());

        OrderShipmentMessage full = buildFullMessage();

        assertEquals("TC", full.getTransCode());
        assertEquals("FB1", full.getFbNbr());
        assertNotNull(full.getOrder());
        assertEquals("O1", full.getOrder().getOrderId());
        assertEquals(1, full.getSpecs().size());
        assertEquals(1, full.getSpecialRouteRequests().size());
        assertEquals(1, full.getNotes().size());
        assertNotNull(full.getAudit());
        assertEquals("AP1", full.getPreferredAPCarrierCode());
        assertEquals("SR1", full.getSpclRteNbr());
        assertEquals("PL1", full.getPrimaryLoc());
        assertEquals("VN1", full.getVendorName());
    }

    @Test
    void testEqualsHashCodeToString() {
        OrderShipmentMessage m1 = buildFullMessage();
        OrderShipmentMessage m2 = buildFullMessage();

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());

        String ts = m1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("TC"));
        assertTrue(ts.contains("FB1"));
        assertTrue(ts.contains("AP1"));
        assertTrue(ts.contains("VN1"));
    }

    @Test
    void testEqualsEdgeCases() {
        OrderShipmentMessage m = new OrderShipmentMessage();
        assertEquals(m, m);
        assertNotEquals(null, m);
        assertNotEquals("string", m);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        OrderShipmentMessage m1 = buildFullMessage();
        OrderShipmentMessage m2 = buildFullMessage();
        m2.setFbNbr("DIFF");
        assertNotEquals(m1, m2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = """
            {
              "transCode":"TC",
              "fbNbr":"FB1",
              "order":{"orderId":"O1"},
              "specs":[{"seq":1,"type":"T","value":"V","description":"D"}],
              "specialRouteRequests":[{"seq":1,"routeType":"RT","routeValue":"RV","priority":5,"userText":"UT"}],
              "notes":[{"fbNoteSeq":1,"tmsNoteSeq":"T","text":"X"}],
              "audit":{"source":"SRC","intgrtnCtlSeqNbr":123,"eventTs":"TS"},
              "preferredAPCarrierCode":"AP1",
              "spclRteNbr":"SR1",
              "primaryLoc":"PL1",
              "vendorName":"VN1"
            }
            """;

        ObjectMapper mapper = new ObjectMapper();
        OrderShipmentMessage deserialized = mapper.readValue(json, OrderShipmentMessage.class);

        assertEquals("TC", deserialized.getTransCode());
        assertEquals("FB1", deserialized.getFbNbr());
        assertNotNull(deserialized.getOrder());
        assertEquals("O1", deserialized.getOrder().getOrderId());
        assertEquals(1, deserialized.getSpecs().size());
        assertEquals(1, deserialized.getSpecialRouteRequests().size());
        assertEquals(1, deserialized.getNotes().size());
        assertEquals("SRC", deserialized.getAudit().getSource());
        assertEquals(123L, deserialized.getAudit().getIntgrtnCtlSeqNbr());
        assertEquals("TS", deserialized.getAudit().getEventTs());
        assertEquals("AP1", deserialized.getPreferredAPCarrierCode());
        assertEquals("SR1", deserialized.getSpclRteNbr());
        assertEquals("PL1", deserialized.getPrimaryLoc());
        assertEquals("VN1", deserialized.getVendorName());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"transCode\":\"TC\""));
        assertTrue(serialized.contains("\"fbNbr\":\"FB1\""));
        assertTrue(serialized.contains("\"order\""));
        assertTrue(serialized.contains("\"specs\""));
        assertTrue(serialized.contains("\"specialRouteRequests\""));
        assertTrue(serialized.contains("\"notes\""));
        assertTrue(serialized.contains("\"audit\""));
        assertTrue(serialized.contains("\"preferredAPCarrierCode\":\"AP1\""));
        assertTrue(serialized.contains("\"spclRteNbr\":\"SR1\""));
        assertTrue(serialized.contains("\"primaryLoc\":\"PL1\""));
        assertTrue(serialized.contains("\"vendorName\":\"VN1\""));
    }
}
