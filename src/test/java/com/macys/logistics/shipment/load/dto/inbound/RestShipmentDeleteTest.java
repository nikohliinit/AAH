package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestShipmentDeleteTest {

    @Test
    void testGettersSettersAndDefaults() {
        RestShipmentDelete d = new RestShipmentDelete();
        assertNull(d.getShipmentNumber());

        d.setShipmentNumber("SHP123");
        assertEquals("SHP123", d.getShipmentNumber());
    }

    @Test
    void testEqualsHashCodeToString() {
        RestShipmentDelete a = new RestShipmentDelete();
        a.setShipmentNumber("ABC");

        RestShipmentDelete b = new RestShipmentDelete();
        b.setShipmentNumber("ABC");

        RestShipmentDelete c = new RestShipmentDelete();
        c.setShipmentNumber("DEF");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("ABC"));
    }

    @Test
    void testEqualsEdgeCases() {
        RestShipmentDelete d = new RestShipmentDelete();
        assertEquals(d, d);
        assertNotEquals(null, d);
        assertNotEquals("string", d);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"shipmentNumber\":\"SN001\"}";
        RestShipmentDelete val = mapper.readValue(json, RestShipmentDelete.class);

        assertEquals("SN001", val.getShipmentNumber());

        String serialized = mapper.writeValueAsString(val);
        assertTrue(serialized.contains("\"shipmentNumber\":\"SN001\""));
    }
}
