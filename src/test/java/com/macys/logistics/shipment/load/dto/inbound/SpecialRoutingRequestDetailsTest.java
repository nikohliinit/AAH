package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpecialRoutingRequestDetailsTest {

    private SRRRequestDetails buildDetail(String name, String val, int num) {
        SRRRequestDetails d = new SRRRequestDetails();
        d.setLabelName(name);
        d.setUserInputValue(val);
        d.setLabelNumber(num);
        return d;
    }

    @Test
    void testGettersSettersAndDefaults() {
        SpecialRoutingRequestDetails s = new SpecialRoutingRequestDetails();
        assertEquals(0, s.getSrrTypeNumber());
        assertNull(s.getSrrTypeDescription());
        assertNull(s.getSrrRequestDetails());
        assertNull(s.getCarrierScac());

        s.setSrrTypeNumber(10);
        s.setSrrTypeDescription("Temp Control");
        s.setSrrRequestDetails(List.of(buildDetail("TEMP", "COLD", 1)));
        s.setCarrierScac("SCAC1");

        assertEquals(10, s.getSrrTypeNumber());
        assertEquals("Temp Control", s.getSrrTypeDescription());
        assertNotNull(s.getSrrRequestDetails());
        assertEquals(1, s.getSrrRequestDetails().size());
        assertEquals("TEMP", s.getSrrRequestDetails().get(0).getLabelName());
        assertEquals("SCAC1", s.getCarrierScac());
    }

    @Test
    void testEqualsHashCodeToString() {
        SpecialRoutingRequestDetails a = new SpecialRoutingRequestDetails();
        a.setSrrTypeNumber(1);
        a.setSrrTypeDescription("D");
        a.setSrrRequestDetails(List.of(buildDetail("L", "V", 2)));
        a.setCarrierScac("S");

        SpecialRoutingRequestDetails b = new SpecialRoutingRequestDetails();
        b.setSrrTypeNumber(1);
        b.setSrrTypeDescription("D");
        b.setSrrRequestDetails(List.of(buildDetail("L", "V", 2)));
        b.setCarrierScac("S");

        SpecialRoutingRequestDetails c = new SpecialRoutingRequestDetails();
        c.setSrrTypeNumber(99);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("srrTypeNumber=1"));
        assertTrue(ts.contains("carrierScac=S"));
    }

    @Test
    void testEqualsEdgeCases() {
        SpecialRoutingRequestDetails s = new SpecialRoutingRequestDetails();
        assertEquals(s, s);
        assertNotEquals(null, s);
        assertNotEquals("string", s);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = """
        {
          "srrTypeNumber":5,
          "srrTypeDescription":"Door Assignment",
          "srrRequestDetails":[{"labelName":"DOOR","userInputValue":"5","labelNumber":10}],
          "carrierScac":"ABCD"
        }""";

        ObjectMapper mapper = new ObjectMapper();
        SpecialRoutingRequestDetails val = mapper.readValue(json, SpecialRoutingRequestDetails.class);

        assertEquals(5, val.getSrrTypeNumber());
        assertEquals("Door Assignment", val.getSrrTypeDescription());
        assertEquals(1, val.getSrrRequestDetails().size());
        assertEquals("DOOR", val.getSrrRequestDetails().get(0).getLabelName());
        assertEquals("ABCD", val.getCarrierScac());

        String serialized = mapper.writeValueAsString(val);
        assertTrue(serialized.contains("\"srrTypeNumber\":5"));
        assertTrue(serialized.contains("\"carrierScac\":\"ABCD\""));
    }
}
