package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuditTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        Audit audit = new Audit();
        // defaults
        assertNull(audit.getSource());
        assertNull(audit.getIntgrtnCtlSeqNbr());
        assertNull(audit.getEventTs());

        // setters
        audit.setSource("SRC");
        audit.setIntgrtnCtlSeqNbr(123L);
        audit.setEventTs("2026-01-01T00:00:00Z");

        // getters
        assertEquals("SRC", audit.getSource());
        assertEquals(123L, audit.getIntgrtnCtlSeqNbr());
        assertEquals("2026-01-01T00:00:00Z", audit.getEventTs());
    }

    @Test
    void testEqualsHashCodeAndToString() {
        Audit a1 = new Audit();
        a1.setSource("SRC");
        a1.setIntgrtnCtlSeqNbr(123L);
        a1.setEventTs("2026-01-01T00:00:00Z");

        Audit a2 = new Audit();
        a2.setSource("SRC");
        a2.setIntgrtnCtlSeqNbr(123L);
        a2.setEventTs("2026-01-01T00:00:00Z");

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());

        String ts = a1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("SRC"));
        assertTrue(ts.contains("123"));
        assertTrue(ts.contains("2026-01-01T00:00:00Z"));
    }

    @Test
    void testEqualsNullAndDifferentType() {
        Audit a1 = new Audit();
        assertNotEquals(null, a1);
        assertNotEquals("string", a1);
        assertEquals(a1, a1); // same instance
    }

    @Test
    void testNotEqualsWhenDifferent() {
        Audit a1 = new Audit();
        a1.setSource("SRC1");

        Audit a2 = new Audit();
        a2.setSource("SRC2");

        assertNotEquals(a1, a2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"source\":\"SRC\",\"intgrtnCtlSeqNbr\":123,\"eventTs\":\"2026-01-01T00:00:00Z\"}";

        ObjectMapper mapper = new ObjectMapper();
        Audit deserialized = mapper.readValue(json, Audit.class);

        assertEquals("SRC", deserialized.getSource());
        assertEquals(123L, deserialized.getIntgrtnCtlSeqNbr());
        assertEquals("2026-01-01T00:00:00Z", deserialized.getEventTs());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"source\":\"SRC\""));
        assertTrue(serialized.contains("\"intgrtnCtlSeqNbr\":123"));
        assertTrue(serialized.contains("\"eventTs\":\"2026-01-01T00:00:00Z\""));
    }
}
