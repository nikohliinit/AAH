package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CarrierTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        Carrier c = new Carrier();
        assertNull(c.getScac());

        c.setScac("ABCD");
        assertEquals("ABCD", c.getScac());
    }

    @Test
    void testEqualsHashCodeToString() {
        Carrier c1 = new Carrier();
        c1.setScac("WXYZ");

        Carrier c2 = new Carrier();
        c2.setScac("WXYZ");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        String ts = c1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("WXYZ"));
    }

    @Test
    void testEqualsEdgeCases() {
        Carrier c = new Carrier();
        assertEquals(c, c); // same instance
        assertNotEquals(null, c);
        assertNotEquals("string", c);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        Carrier c1 = new Carrier();
        c1.setScac("AAAA");
        Carrier c2 = new Carrier();
        c2.setScac("BBBB");

        assertNotEquals(c1, c2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"scac\":\"JKLL\"}";

        ObjectMapper mapper = new ObjectMapper();
        Carrier deserialized = mapper.readValue(json, Carrier.class);

        assertEquals("JKLL", deserialized.getScac());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"scac\":\"JKLL\""));
    }
}
