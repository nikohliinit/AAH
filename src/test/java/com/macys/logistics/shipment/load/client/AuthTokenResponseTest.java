package com.macys.logistics.shipment.load.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenResponseTest {

    @Test
    void testGettersAndSetters() {
        AuthTokenResponse r = new AuthTokenResponse();
        r.setType("Bearer");
        r.setValue("val");
        r.setExpiry("3600");
        r.setAccessToken("tok123");
        r.setErrorDescription("none");

        assertEquals("Bearer", r.getType());
        assertEquals("val", r.getValue());
        assertEquals("3600", r.getExpiry());
        assertEquals("tok123", r.getAccessToken());
        assertEquals("none", r.getErrorDescription());
    }

    @Test
    void testEqualsAndHashCode() {
        AuthTokenResponse a = new AuthTokenResponse();
        a.setType("Bearer");
        a.setValue("v");
        a.setExpiry("60");
        a.setAccessToken("t");
        a.setErrorDescription("e");

        AuthTokenResponse b = new AuthTokenResponse();
        b.setType("Bearer");
        b.setValue("v");
        b.setExpiry("60");
        b.setAccessToken("t");
        b.setErrorDescription("e");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);
        assertNotEquals(null, a);
        assertNotEquals("x", a);

        assertTrue(a.canEqual(b));
        assertFalse(a.canEqual("x"));

        assertEquals(new AuthTokenResponse(), new AuthTokenResponse());
        assertEquals(new AuthTokenResponse().hashCode(), new AuthTokenResponse().hashCode());
    }

    @Test
    void testEqualsFieldBranches() {
        AuthTokenResponse a = new AuthTokenResponse();
        a.setType("Bearer");
        a.setValue("v");
        a.setExpiry("60");
        a.setAccessToken("t");
        a.setErrorDescription("e");

        AuthTokenResponse b = new AuthTokenResponse();
        b.setType("Bearer");
        b.setValue("v");
        b.setExpiry("60");
        b.setAccessToken("t");
        b.setErrorDescription("e");

        // type differs
        b.setType("X");
        assertNotEquals(a, b);
        b.setType(a.getType());
        a.setType(null);
        assertNotEquals(a, b);
        a.setType(b.getType());

        // value differs
        b.setValue("X");
        assertNotEquals(a, b);
        b.setValue(a.getValue());
        a.setValue(null);
        assertNotEquals(a, b);
        a.setValue(b.getValue());

        // expiry differs
        b.setExpiry("X");
        assertNotEquals(a, b);
        b.setExpiry(a.getExpiry());
        a.setExpiry(null);
        assertNotEquals(a, b);
        a.setExpiry(b.getExpiry());

        // accessToken differs
        b.setAccessToken("X");
        assertNotEquals(a, b);
        b.setAccessToken(a.getAccessToken());
        a.setAccessToken(null);
        assertNotEquals(a, b);
        a.setAccessToken(b.getAccessToken());

        // errorDescription differs
        b.setErrorDescription("X");
        assertNotEquals(a, b);
        b.setErrorDescription(a.getErrorDescription());
        a.setErrorDescription(null);
        assertNotEquals(a, b);
        a.setErrorDescription(b.getErrorDescription());
    }

    @Test
    void testToString() {
        AuthTokenResponse r = new AuthTokenResponse();
        r.setType("Bearer");
        r.setValue("v1");
        r.setAccessToken("token");
        String s = r.toString();
        assertNotNull(s);
        assertTrue(s.contains("Bearer"));
        assertTrue(s.contains("token"));
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"type\":\"Bearer\",\"value\":\"v1\",\"expiry\":\"2026-12-31T23:59:59Z\",\"access_token\":\"tk\",\"error_description\":\"err\"}";
        ObjectMapper mapper = new ObjectMapper();
        AuthTokenResponse deserialized = mapper.readValue(json, AuthTokenResponse.class);

        assertEquals("Bearer", deserialized.getType());
        assertEquals("v1", deserialized.getValue());
        assertEquals("2026-12-31T23:59:59Z", deserialized.getExpiry());
        assertEquals("tk", deserialized.getAccessToken());
        assertEquals("err", deserialized.getErrorDescription());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"access_token\":\"tk\""));
        assertTrue(serialized.contains("\"error_description\":\"err\""));
    }
}
