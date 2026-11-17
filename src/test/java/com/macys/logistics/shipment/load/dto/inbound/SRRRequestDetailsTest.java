package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SRRRequestDetailsTest {

    @Test
    void testGettersSettersAndDefaults() {
        SRRRequestDetails d = new SRRRequestDetails();
        assertNull(d.getLabelName());
        assertNull(d.getUserInputValue());
        assertEquals(0, d.getLabelNumber());

        d.setLabelName("APPT_WINDOW");
        d.setUserInputValue("08-10");
        d.setLabelNumber(5);

        assertEquals("APPT_WINDOW", d.getLabelName());
        assertEquals("08-10", d.getUserInputValue());
        assertEquals(5, d.getLabelNumber());
    }

    @Test
    void testEqualsHashCodeToString() {
        SRRRequestDetails a = new SRRRequestDetails();
        a.setLabelName("L"); a.setUserInputValue("V"); a.setLabelNumber(1);
        SRRRequestDetails b = new SRRRequestDetails();
        b.setLabelName("L"); b.setUserInputValue("V"); b.setLabelNumber(1);
        SRRRequestDetails c = new SRRRequestDetails();
        c.setLabelName("X");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("L"));
        assertTrue(ts.contains("V"));
        assertTrue(ts.contains("1"));
    }

    @Test
    void testEqualsEdgeCases() {
        SRRRequestDetails d = new SRRRequestDetails();
        assertEquals(d, d);
        assertNotEquals(null, d);
        assertNotEquals("string", d);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"labelName\":\"LBL\",\"userInputValue\":\"VAL\",\"labelNumber\":9}";

        ObjectMapper mapper = new ObjectMapper();
        SRRRequestDetails deserialized = mapper.readValue(json, SRRRequestDetails.class);

        assertEquals("LBL", deserialized.getLabelName());
        assertEquals("VAL", deserialized.getUserInputValue());
        assertEquals(9, deserialized.getLabelNumber());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"labelName\":\"LBL\""));
        assertTrue(serialized.contains("\"userInputValue\":\"VAL\""));
        assertTrue(serialized.contains("\"labelNumber\":9"));
    }
}
