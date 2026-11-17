package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VendorTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        Vendor v = new Vendor();
        assertNull(v.getSupplierNbr());
        assertNull(v.getName());

        v.setSupplierNbr("SUP1");
        v.setName("Vendor Name");

        assertEquals("SUP1", v.getSupplierNbr());
        assertEquals("Vendor Name", v.getName());
    }

    @Test
    void testEqualsHashCodeToString() {
        Vendor v1 = new Vendor();
        v1.setSupplierNbr("A");
        v1.setName("B");

        Vendor v2 = new Vendor();
        v2.setSupplierNbr("A");
        v2.setName("B");

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());

        String ts = v1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("A"));
        assertTrue(ts.contains("B"));
    }

    @Test
    void testEqualsEdgeCases() {
        Vendor v = new Vendor();
        assertEquals(v, v); // same instance
        assertNotEquals(null, v);
        assertNotEquals("string", v);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        Vendor v1 = new Vendor();
        v1.setSupplierNbr("X");
        Vendor v2 = new Vendor();
        v2.setSupplierNbr("Y");
        assertNotEquals(v1, v2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"supplierNbr\":\"S001\",\"name\":\"Vendor Inc\"}";

        ObjectMapper mapper = new ObjectMapper();
        Vendor deserialized = mapper.readValue(json, Vendor.class);

        assertEquals("S001", deserialized.getSupplierNbr());
        assertEquals("Vendor Inc", deserialized.getName());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"supplierNbr\":\"S001\""));
        assertTrue(serialized.contains("\"name\":\"Vendor Inc\""));
    }
}
