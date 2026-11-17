package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OriginTest {

    @Test
    void testNoArgsAllArgsAndBuilder() {
        // no-args
        Origin o1 = new Origin();
        assertNull(o1.getLocationId());
        assertNull(o1.getLocationName());
        assertNull(o1.getAddressLine());
        assertNull(o1.getState());
        assertNull(o1.getCity());
        assertNull(o1.getZip());

        // setters
        o1.setLocationId("LOC1");
        o1.setLocationName("Origin Name");
        o1.setAddressLine("123 Main");
        o1.setState("NY");
        o1.setCity("NYC");
        o1.setZip("10001");

        assertEquals("LOC1", o1.getLocationId());
        assertEquals("Origin Name", o1.getLocationName());
        assertEquals("123 Main", o1.getAddressLine());
        assertEquals("NY", o1.getState());
        assertEquals("NYC", o1.getCity());
        assertEquals("10001", o1.getZip());

        // all-args
        Origin o2 = new Origin("L2", "Name2", "456 Broadway", "CA", "LA", "90001");
        assertEquals("L2", o2.getLocationId());
        assertEquals("Name2", o2.getLocationName());
        assertEquals("456 Broadway", o2.getAddressLine());
        assertEquals("CA", o2.getState());
        assertEquals("LA", o2.getCity());
        assertEquals("90001", o2.getZip());

        // builder
        Origin o3 = Origin.builder()
                .locationId("L3")
                .locationName("Name3")
                .addressLine("789 Ave")
                .state("TX")
                .city("DAL")
                .zip("75001")
                .build();
        assertEquals("L3", o3.getLocationId());
        assertEquals("Name3", o3.getLocationName());
        assertEquals("789 Ave", o3.getAddressLine());
        assertEquals("TX", o3.getState());
        assertEquals("DAL", o3.getCity());
        assertEquals("75001", o3.getZip());
    }

    @Test
    void testEqualsHashCodeToString() {
        Origin a = Origin.builder().locationId("L1").city("NYC").build();
        Origin b = Origin.builder().locationId("L1").city("NYC").build();
        Origin c = Origin.builder().locationId("L2").city("LA").build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("L1"));
        assertTrue(ts.contains("NYC"));
    }

    @Test
    void testEqualsEdgeCases() {
        Origin o = new Origin();
        assertEquals(o, o);
        assertNotEquals(null, o);
        assertNotEquals("string", o);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"locationId\":\"L9\",\"locationName\":\"Name9\",\"addressLine\":\"1 Test St\",\"state\":\"IL\",\"city\":\"CHI\",\"zip\":\"60007\"}";
        Origin origin = mapper.readValue(json, Origin.class);

        assertEquals("L9", origin.getLocationId());
        assertEquals("Name9", origin.getLocationName());
        assertEquals("1 Test St", origin.getAddressLine());
        assertEquals("IL", origin.getState());
        assertEquals("CHI", origin.getCity());
        assertEquals("60007", origin.getZip());

        String serialized = mapper.writeValueAsString(origin);
        assertTrue(serialized.contains("\"locationId\":\"L9\""));
        assertTrue(serialized.contains("\"city\":\"CHI\""));
        assertTrue(serialized.contains("\"zip\":\"60007\""));
    }
}
