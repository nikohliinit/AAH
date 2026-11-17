package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommodityTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        Commodity c = new Commodity();
        assertNull(c.getCode());
        assertNull(c.getDescription());

        c.setCode("123");
        c.setDescription("Furniture");

        assertEquals("123", c.getCode());
        assertEquals("Furniture", c.getDescription());
    }

    @Test
    void testEqualsHashCodeToString() {
        Commodity c1 = new Commodity();
        c1.setCode("A1");
        c1.setDescription("Desc");

        Commodity c2 = new Commodity();
        c2.setCode("A1");
        c2.setDescription("Desc");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        String ts = c1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("A1"));
        assertTrue(ts.contains("Desc"));
    }

    @Test
    void testEqualsEdgeCases() {
        Commodity c = new Commodity();
        assertEquals(c, c); // same instance
        assertNotEquals(null, c);
        assertNotEquals("string", c);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        Commodity c1 = new Commodity();
        c1.setCode("X");
        Commodity c2 = new Commodity();
        c2.setCode("Y");
        assertNotEquals(c1, c2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"code\":\"789\",\"description\":\"Appliance\"}";

        ObjectMapper mapper = new ObjectMapper();
        Commodity deserialized = mapper.readValue(json, Commodity.class);

        assertEquals("789", deserialized.getCode());
        assertEquals("Appliance", deserialized.getDescription());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"code\":\"789\""));
        assertTrue(serialized.contains("\"description\":\"Appliance\""));
    }
}
