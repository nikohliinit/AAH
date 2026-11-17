package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestProcessShipmentOrderDeleteTest {

    private RestShipmentDelete build(String num) {
        RestShipmentDelete d = new RestShipmentDelete();
        d.setShipmentNumber(num);
        return d;
    }

    @Test
    void testNoArgsAllArgsAndGettersSetters() {
        RestProcessShipmentOrderDelete d1 = new RestProcessShipmentOrderDelete();
        assertNull(d1.getShipment());

        d1.setShipment(List.of(build("S1"), build("S2")));
        assertNotNull(d1.getShipment());
        assertEquals(2, d1.getShipment().size());
        assertEquals("S1", d1.getShipment().get(0).getShipmentNumber());

        RestProcessShipmentOrderDelete d2 = new RestProcessShipmentOrderDelete(List.of(build("A"), build("B")));
        assertEquals(2, d2.getShipment().size());
        assertEquals("A", d2.getShipment().get(0).getShipmentNumber());
    }

    @Test
    void testEqualsHashCodeToString() {
        RestProcessShipmentOrderDelete a = new RestProcessShipmentOrderDelete(List.of(build("X"), build("Y")));
        RestProcessShipmentOrderDelete b = new RestProcessShipmentOrderDelete(List.of(build("X"), build("Y")));
        RestProcessShipmentOrderDelete c = new RestProcessShipmentOrderDelete(List.of(build("Z")));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("X"));
        assertTrue(ts.contains("Y"));
    }

    @Test
    void testEqualsEdgeCases() {
        RestProcessShipmentOrderDelete d = new RestProcessShipmentOrderDelete();
        assertEquals(d, d);
        assertNotEquals(null, d);
        assertNotEquals("string", d);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"shipment\":[{\"shipmentNumber\":\"S1\"},{\"shipmentNumber\":\"S2\"}]}";

        ObjectMapper mapper = new ObjectMapper();
        RestProcessShipmentOrderDelete deserialized = mapper.readValue(json, RestProcessShipmentOrderDelete.class);

        assertNotNull(deserialized.getShipment());
        assertEquals(2, deserialized.getShipment().size());
        assertEquals("S1", deserialized.getShipment().get(0).getShipmentNumber());
        assertEquals("S2", deserialized.getShipment().get(1).getShipmentNumber());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"shipmentNumber\":\"S1\""));
        assertTrue(serialized.contains("\"shipmentNumber\":\"S2\""));
    }
}
