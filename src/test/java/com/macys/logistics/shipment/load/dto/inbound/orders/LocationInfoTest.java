package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LocationInfoTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        LocationInfo li = new LocationInfo();
        assertNull(li.getLocationId());
        assertNull(li.getIsHub());

        li.setLocationId("LOC123");
        li.setIsHub(Boolean.TRUE);

        assertEquals("LOC123", li.getLocationId());
        assertTrue(li.getIsHub());
    }

    @Test
    void testEqualsHashCodeToString() {
        LocationInfo l1 = new LocationInfo();
        l1.setLocationId("A");
        l1.setIsHub(Boolean.FALSE);

        LocationInfo l2 = new LocationInfo();
        l2.setLocationId("A");
        l2.setIsHub(Boolean.FALSE);

        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());

        String ts = l1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("A"));
        assertTrue(ts.contains("false"));
    }

    @Test
    void testEqualsEdgeCases() {
        LocationInfo li = new LocationInfo();
        assertEquals(li, li); // same instance
        assertNotEquals(null, li);
        assertNotEquals("string", li);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        LocationInfo l1 = new LocationInfo();
        l1.setLocationId("X");
        LocationInfo l2 = new LocationInfo();
        l2.setLocationId("Y");
        assertNotEquals(l1, l2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"locationId\":\"D001\",\"isHub\":true}";

        ObjectMapper mapper = new ObjectMapper();
        LocationInfo deserialized = mapper.readValue(json, LocationInfo.class);

        assertEquals("D001", deserialized.getLocationId());
        assertEquals(Boolean.TRUE, deserialized.getIsHub());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"locationId\":\"D001\""));
        assertTrue(serialized.contains("\"isHub\":true"));
    }
}
