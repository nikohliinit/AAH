package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShipmentTrackingMessageTest {

    @Test
    void testNoArgsAllArgsAndGettersSetters() {
        // no-args defaults
        ShipmentTrackingMessage m1 = new ShipmentTrackingMessage();
        assertNull(m1.getLoadId());
        assertNull(m1.getShipmentNumber());
        assertNull(m1.getCarrierScac());
        assertNull(m1.getServiceType());
        assertNull(m1.getProNumber());
        assertNull(m1.getLabelPath());
        assertNull(m1.getLabelFormat());

        // setters
        m1.setLoadId(10);
        m1.setShipmentNumber("SHP1");
        m1.setCarrierScac("ABCD");
        m1.setServiceType("GROUND");
        m1.setProNumber("PRONBR");
        m1.setLabelPath("/tmp/label.pdf");
        m1.setLabelFormat("PDF");

        assertEquals(10, m1.getLoadId());
        assertEquals("SHP1", m1.getShipmentNumber());
        assertEquals("ABCD", m1.getCarrierScac());
        assertEquals("GROUND", m1.getServiceType());
        assertEquals("PRONBR", m1.getProNumber());
        assertEquals("/tmp/label.pdf", m1.getLabelPath());
        assertEquals("PDF", m1.getLabelFormat());

        // all-args
        ShipmentTrackingMessage m2 = new ShipmentTrackingMessage(
                20, "SHP2", "WXYZ", "EXPRESS", "PRO2", "/labels/2.zpl", "ZPL");
        assertEquals(20, m2.getLoadId());
        assertEquals("SHP2", m2.getShipmentNumber());
        assertEquals("WXYZ", m2.getCarrierScac());
        assertEquals("EXPRESS", m2.getServiceType());
        assertEquals("PRO2", m2.getProNumber());
        assertEquals("/labels/2.zpl", m2.getLabelPath());
        assertEquals("ZPL", m2.getLabelFormat());
    }

    @Test
    void testEqualsHashCodeToString() {
        ShipmentTrackingMessage a = new ShipmentTrackingMessage(
                1, "A", "C", "S", "P", "/p.lbl", "PDF");
        ShipmentTrackingMessage b = new ShipmentTrackingMessage(
                1, "A", "C", "S", "P", "/p.lbl", "PDF");
        ShipmentTrackingMessage c = new ShipmentTrackingMessage(
                2, "B", "D", "T", "Q", "/q.lbl", "ZPL");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("A"));
        assertTrue(ts.contains("PDF"));
    }

    @Test
    void testEqualsEdgeCases() {
        ShipmentTrackingMessage m = new ShipmentTrackingMessage();
        assertEquals(m, m);
        assertNotEquals(null, m);
        assertNotEquals("string", m);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"loadId\":99,\"shipmentNumber\":\"SN\",\"carrierScac\":\"SCAC\",\"serviceType\":\"SVC\",\"proNumber\":\"PRO\",\"labelPath\":\"/l\",\"labelFormat\":\"FMT\"}";

        ObjectMapper mapper = new ObjectMapper();
        ShipmentTrackingMessage deserialized = mapper.readValue(json, ShipmentTrackingMessage.class);

        assertEquals(99, deserialized.getLoadId());
        assertEquals("SN", deserialized.getShipmentNumber());
        assertEquals("SCAC", deserialized.getCarrierScac());
        assertEquals("SVC", deserialized.getServiceType());
        assertEquals("PRO", deserialized.getProNumber());
        assertEquals("/l", deserialized.getLabelPath());
        assertEquals("FMT", deserialized.getLabelFormat());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"loadId\":99"));
        assertTrue(serialized.contains("\"shipmentNumber\":\"SN\""));
        assertTrue(serialized.contains("\"labelFormat\":\"FMT\""));
    }
}
