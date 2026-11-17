package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpecTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        Spec s = new Spec();
        assertNull(s.getSeq());
        assertNull(s.getType());
        assertNull(s.getValue());
        assertNull(s.getDescription());

        s.setSeq(1);
        s.setType("TEMP");
        s.setValue("COLD");
        s.setDescription("Temperature spec");

        assertEquals(1, s.getSeq());
        assertEquals("TEMP", s.getType());
        assertEquals("COLD", s.getValue());
        assertEquals("Temperature spec", s.getDescription());
    }

    @Test
    void testEqualsHashCodeToString() {
        Spec s1 = new Spec();
        s1.setSeq(1);
        s1.setType("A");
        s1.setValue("B");
        s1.setDescription("D");

        Spec s2 = new Spec();
        s2.setSeq(1);
        s2.setType("A");
        s2.setValue("B");
        s2.setDescription("D");

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());

        String ts = s1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("1"));
        assertTrue(ts.contains("A"));
        assertTrue(ts.contains("B"));
        assertTrue(ts.contains("D"));
    }

    @Test
    void testEqualsEdgeCases() {
        Spec s = new Spec();
        assertEquals(s, s); // same instance
        assertNotEquals(null, s);
        assertNotEquals("string", s);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        Spec s1 = new Spec();
        s1.setSeq(1);
        Spec s2 = new Spec();
        s2.setSeq(2);
        assertNotEquals(s1, s2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"seq\":5,\"type\":\"DIM\",\"value\":\"LTL\",\"description\":\"Dimensional rating\"}";

        ObjectMapper mapper = new ObjectMapper();
        Spec deserialized = mapper.readValue(json, Spec.class);

        assertEquals(5, deserialized.getSeq());
        assertEquals("DIM", deserialized.getType());
        assertEquals("LTL", deserialized.getValue());
        assertEquals("Dimensional rating", deserialized.getDescription());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"seq\":5"));
        assertTrue(serialized.contains("\"type\":\"DIM\""));
        assertTrue(serialized.contains("\"value\":\"LTL\""));
        assertTrue(serialized.contains("\"description\":\"Dimensional rating\""));
    }
}
