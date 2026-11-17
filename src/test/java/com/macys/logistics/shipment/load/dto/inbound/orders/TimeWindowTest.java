package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TimeWindowTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        TimeWindow tw = new TimeWindow();
        assertNull(tw.getEarliest());
        assertNull(tw.getLatest());

        tw.setEarliest("2026-03-01T10:00:00Z");
        tw.setLatest("2026-03-01T12:00:00Z");

        assertEquals("2026-03-01T10:00:00Z", tw.getEarliest());
        assertEquals("2026-03-01T12:00:00Z", tw.getLatest());
    }

    @Test
    void testEqualsHashCodeToString() {
        TimeWindow t1 = new TimeWindow();
        t1.setEarliest("E");
        t1.setLatest("L");

        TimeWindow t2 = new TimeWindow();
        t2.setEarliest("E");
        t2.setLatest("L");

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());

        String ts = t1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("E"));
        assertTrue(ts.contains("L"));
    }

    @Test
    void testEqualsEdgeCases() {
        TimeWindow t = new TimeWindow();
        assertEquals(t, t);
        assertNotEquals(null, t);
        assertNotEquals("string", t);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        TimeWindow t1 = new TimeWindow();
        t1.setEarliest("A");
        TimeWindow t2 = new TimeWindow();
        t2.setEarliest("B");
        assertNotEquals(t1, t2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"earliest\":\"2026-03-01T10:00:00Z\",\"latest\":\"2026-03-01T12:00:00Z\"}";

        ObjectMapper mapper = new ObjectMapper();
        TimeWindow deserialized = mapper.readValue(json, TimeWindow.class);

        assertEquals("2026-03-01T10:00:00Z", deserialized.getEarliest());
        assertEquals("2026-03-01T12:00:00Z", deserialized.getLatest());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"earliest\":\"2026-03-01T10:00:00Z\""));
        assertTrue(serialized.contains("\"latest\":\"2026-03-01T12:00:00Z\""));
    }
}
