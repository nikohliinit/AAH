package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LineItemTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        LineItem li = new LineItem();
        assertNull(li.getPieces());
        assertNull(li.getWeightLb());
        assertNull(li.getVolumeCf());

        li.setPieces(5);
        li.setWeightLb(new BigDecimal("12.34"));
        li.setVolumeCf(new BigDecimal("1.23"));

        assertEquals(5, li.getPieces());
        assertEquals(new BigDecimal("12.34"), li.getWeightLb());
        assertEquals(new BigDecimal("1.23"), li.getVolumeCf());
    }

    @Test
    void testEqualsHashCodeToString() {
        LineItem l1 = new LineItem();
        l1.setPieces(10);
        l1.setWeightLb(new BigDecimal("100.50"));
        l1.setVolumeCf(new BigDecimal("7.25"));

        LineItem l2 = new LineItem();
        l2.setPieces(10);
        l2.setWeightLb(new BigDecimal("100.50"));
        l2.setVolumeCf(new BigDecimal("7.25"));

        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());

        String ts = l1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("10"));
        assertTrue(ts.contains("100.50"));
        assertTrue(ts.contains("7.25"));
    }

    @Test
    void testEqualsEdgeCases() {
        LineItem li = new LineItem();
        assertEquals(li, li); // same instance
        assertNotEquals(null, li);
        assertNotEquals("string", li);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        LineItem l1 = new LineItem();
        l1.setPieces(1);
        LineItem l2 = new LineItem();
        l2.setPieces(2);
        assertNotEquals(l1, l2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"pieces\":5,\"weightLb\":12.34,\"volumeCf\":1.23}";

        ObjectMapper mapper = new ObjectMapper();
        LineItem deserialized = mapper.readValue(json, LineItem.class);

        assertEquals(5, deserialized.getPieces());
        assertEquals(new BigDecimal("12.34"), deserialized.getWeightLb());
        assertEquals(new BigDecimal("1.23"), deserialized.getVolumeCf());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"pieces\":5"));
        assertTrue(serialized.contains("\"weightLb\":12.34"));
        assertTrue(serialized.contains("\"volumeCf\":1.23"));
    }
}
