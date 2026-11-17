package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DestinationTest {

    @Test
    void testNoArgsAllArgsAndBuilder() {
        // no-args
        Destination d1 = new Destination();
        assertNull(d1.getLocationAbbreviation());
        assertEquals(0, d1.getLocationNbr());

        d1.setLocationAbbreviation("ATL");
        d1.setLocationNbr(6230);
        assertEquals("ATL", d1.getLocationAbbreviation());
        assertEquals(6230, d1.getLocationNbr());

        // all-args
        Destination d2 = new Destination("SFO", 1001);
        assertEquals("SFO", d2.getLocationAbbreviation());
        assertEquals(1001, d2.getLocationNbr());

        // builder
        Destination d3 = Destination.builder()
                .locationAbbreviation("NYC")
                .locationNbr(2002)
                .build();
        assertEquals("NYC", d3.getLocationAbbreviation());
        assertEquals(2002, d3.getLocationNbr());
    }

    @Test
    void testEqualsHashCodeToString() {
        Destination a = Destination.builder().locationAbbreviation("DAL").locationNbr(111).build();
        Destination b = Destination.builder().locationAbbreviation("DAL").locationNbr(111).build();
        Destination c = Destination.builder().locationAbbreviation("HOU").locationNbr(222).build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("DAL"));
        assertTrue(ts.contains("111"));
    }

    @Test
    void testEqualsEdgeCases() {
        Destination d = new Destination();
        assertEquals(d, d);
        assertNotEquals(null, d);
        assertNotEquals("string", d);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"locationAbbreviation\":\"PHL\",\"locationNbr\":555}";
        Destination dest = mapper.readValue(json, Destination.class);

        assertEquals("PHL", dest.getLocationAbbreviation());
        assertEquals(555, dest.getLocationNbr());

        String serialized = mapper.writeValueAsString(dest);
        assertTrue(serialized.contains("\"locationAbbreviation\":\"PHL\""));
        assertTrue(serialized.contains("\"locationNbr\":555"));
    }
}
