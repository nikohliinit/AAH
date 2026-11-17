package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShipmentEnterpriseMessageTest {

    private ShipmentDetails buildDetails() {
        ShipmentDetails s = new ShipmentDetails();
        s.setVendorNumber(123);
        s.setVendorName("Acme");
        return s;
    }

    @Test
    void testNoArgsAllArgsAndGettersSetters() {
        ShipmentEnterpriseMessage m1 = new ShipmentEnterpriseMessage();
        assertNull(m1.getShipmentDetails());
        assertNull(m1.getEventType());
        assertNull(m1.getShipmentNumber());

        m1.setShipmentDetails(buildDetails());
        m1.setEventType("CREATE");
        m1.setShipmentNumber(999L);

        assertEquals(123, m1.getShipmentDetails().getVendorNumber());
        assertEquals("CREATE", m1.getEventType());
        assertEquals(999L, m1.getShipmentNumber());

        ShipmentEnterpriseMessage m2 = new ShipmentEnterpriseMessage(buildDetails(), "DELETE", 1000L);
        assertEquals("DELETE", m2.getEventType());
        assertEquals(1000L, m2.getShipmentNumber());
        assertEquals("Acme", m2.getShipmentDetails().getVendorName());
    }

    @Test
    void testEqualsHashCodeToString() {
        ShipmentEnterpriseMessage a = new ShipmentEnterpriseMessage(buildDetails(), "E", 1L);
        ShipmentEnterpriseMessage b = new ShipmentEnterpriseMessage(buildDetails(), "E", 1L);
        ShipmentEnterpriseMessage c = new ShipmentEnterpriseMessage(buildDetails(), "X", 2L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("shipmentNumber=1"));
        assertTrue(ts.contains("eventType=E"));
    }

    @Test
    void testEqualsEdgeCases() {
        ShipmentEnterpriseMessage m = new ShipmentEnterpriseMessage();
        assertEquals(m, m);
        assertNotEquals(null, m);
        assertNotEquals("string", m);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"shipmentDetails\":{\"vendorNumber\":123,\"vendorName\":\"Acme\"},\"eventType\":\"CREATE\",\"shipmentNumber\":77}";

        ObjectMapper mapper = new ObjectMapper();
        ShipmentEnterpriseMessage deserialized = mapper.readValue(json, ShipmentEnterpriseMessage.class);

        assertNotNull(deserialized.getShipmentDetails());
        assertEquals(123, deserialized.getShipmentDetails().getVendorNumber());
        assertEquals("Acme", deserialized.getShipmentDetails().getVendorName());
        assertEquals("CREATE", deserialized.getEventType());
        assertEquals(77L, deserialized.getShipmentNumber());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"eventType\":\"CREATE\""));
        assertTrue(serialized.contains("\"shipmentNumber\":77"));
    }
}
