package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReferenceTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        Reference r = new Reference();
        assertNull(r.getType());
        assertNull(r.getValue());

        r.setType("PROJ_ID");
        r.setValue("12345");

        assertEquals("PROJ_ID", r.getType());
        assertEquals("12345", r.getValue());
    }

    @Test
    void testEqualsHashCodeToString() {
        Reference r1 = new Reference();
        r1.setType("A");
        r1.setValue("V");

        Reference r2 = new Reference();
        r2.setType("A");
        r2.setValue("V");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        String ts = r1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("A"));
        assertTrue(ts.contains("V"));
    }

    @Test
    void testEqualsEdgeCases() {
        Reference r = new Reference();
        assertEquals(r, r); // same instance
        assertNotEquals(null, r);
        assertNotEquals("string", r);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        Reference r1 = new Reference();
        r1.setType("X");
        Reference r2 = new Reference();
        r2.setType("Y");
        assertNotEquals(r1, r2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"type\":\"BOL\",\"value\":\"B123\"}";

        ObjectMapper mapper = new ObjectMapper();
        Reference deserialized = mapper.readValue(json, Reference.class);

        assertEquals("BOL", deserialized.getType());
        assertEquals("B123", deserialized.getValue());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"type\":\"BOL\""));
        assertTrue(serialized.contains("\"value\":\"B123\""));
    }
}
