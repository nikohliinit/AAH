package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChargesTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        Charges c = new Charges();
        assertNull(c.getRevenue());
        assertNull(c.getCurrency());

        c.setRevenue(new BigDecimal("123.45"));
        c.setCurrency("USD");

        assertEquals(new BigDecimal("123.45"), c.getRevenue());
        assertEquals("USD", c.getCurrency());
    }

    @Test
    void testEqualsHashCodeToString() {
        Charges c1 = new Charges();
        c1.setRevenue(new BigDecimal("10.00"));
        c1.setCurrency("USD");

        Charges c2 = new Charges();
        c2.setRevenue(new BigDecimal("10.00"));
        c2.setCurrency("USD");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        String ts = c1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("10.00"));
        assertTrue(ts.contains("USD"));
    }

    @Test
    void testEqualsEdgeCases() {
        Charges c = new Charges();
        assertEquals(c, c); // same instance
        assertNotEquals(null, c);
        assertNotEquals("string", c);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        Charges c1 = new Charges();
        c1.setRevenue(new BigDecimal("1.00"));

        Charges c2 = new Charges();
        c2.setRevenue(new BigDecimal("2.00"));

        assertNotEquals(c1, c2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"revenue\":99.99,\"currency\":\"EUR\"}";

        ObjectMapper mapper = new ObjectMapper();
        Charges deserialized = mapper.readValue(json, Charges.class);

        assertEquals(new BigDecimal("99.99"), deserialized.getRevenue());
        assertEquals("EUR", deserialized.getCurrency());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"revenue\":99.99"));
        assertTrue(serialized.contains("\"currency\":\"EUR\""));
    }
}
