package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpecialRouteRequestTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        SpecialRouteRequest r = new SpecialRouteRequest();
        assertNull(r.getSeq());
        assertNull(r.getRouteType());
        assertNull(r.getRouteValue());
        assertNull(r.getPriority());
        assertNull(r.getUserText());

        r.setSeq(7);
        r.setRouteType("AP");
        r.setRouteValue("123");
        r.setPriority(1);
        r.setUserText("text");

        assertEquals(7, r.getSeq());
        assertEquals("AP", r.getRouteType());
        assertEquals("123", r.getRouteValue());
        assertEquals(1, r.getPriority());
        assertEquals("text", r.getUserText());
    }

    @Test
    void testEqualsHashCodeToString() {
        SpecialRouteRequest r1 = new SpecialRouteRequest();
        r1.setSeq(1);
        r1.setRouteType("TYPE");
        r1.setRouteValue("VAL");
        r1.setPriority(9);
        r1.setUserText("UT");

        SpecialRouteRequest r2 = new SpecialRouteRequest();
        r2.setSeq(1);
        r2.setRouteType("TYPE");
        r2.setRouteValue("VAL");
        r2.setPriority(9);
        r2.setUserText("UT");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        String ts = r1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("TYPE"));
        assertTrue(ts.contains("VAL"));
        assertTrue(ts.contains("9"));
        assertTrue(ts.contains("UT"));
    }

    @Test
    void testEqualsEdgeCases() {
        SpecialRouteRequest r = new SpecialRouteRequest();
        assertEquals(r, r);
        assertNotEquals(null, r);
        assertNotEquals("string", r);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        SpecialRouteRequest r1 = new SpecialRouteRequest();
        r1.setSeq(1);
        SpecialRouteRequest r2 = new SpecialRouteRequest();
        r2.setSeq(2);
        assertNotEquals(r1, r2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"seq\":3,\"routeType\":\"SPCL\",\"routeValue\":\"777\",\"priority\":2,\"userText\":\"door 5\"}";

        ObjectMapper mapper = new ObjectMapper();
        SpecialRouteRequest deserialized = mapper.readValue(json, SpecialRouteRequest.class);

        assertEquals(3, deserialized.getSeq());
        assertEquals("SPCL", deserialized.getRouteType());
        assertEquals("777", deserialized.getRouteValue());
        assertEquals(2, deserialized.getPriority());
        assertEquals("door 5", deserialized.getUserText());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"seq\":3"));
        assertTrue(serialized.contains("\"routeType\":\"SPCL\""));
        assertTrue(serialized.contains("\"routeValue\":\"777\""));
        assertTrue(serialized.contains("\"priority\":2"));
        assertTrue(serialized.contains("\"userText\":\"door 5\""));
    }
}
