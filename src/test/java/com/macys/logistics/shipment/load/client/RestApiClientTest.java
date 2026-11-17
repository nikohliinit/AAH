package com.macys.logistics.shipment.load.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestApiClientTest {

    private RestApiClient client;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        client = new RestApiClient();
        restTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(client, "restTemplate", restTemplate);
    }

    @Test
    void testGet_success() {
        String url = "http://localhost/get";
        HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());
        String expected = "body-get";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(entity), eq(String.class)))
                .thenReturn(ResponseEntity.ok(expected));

        String actual = client.get(url, entity, String.class);
        assertEquals(expected, actual);
    }

    @Test
    void testPost_success() {
        String url = "http://localhost/post";
        HttpEntity<String> entity = new HttpEntity<>("payload");
        String expected = "body-post";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), eq(entity), eq(String.class)))
                .thenReturn(ResponseEntity.ok(expected));

        String actual = client.post(url, entity, String.class);
        assertEquals(expected, actual);
    }

    @Test
    void testPut_success() {
        String url = "http://localhost/put";
        HttpEntity<String> entity = new HttpEntity<>("payload");
        String expected = "body-put";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.PUT), eq(entity), eq(String.class)))
                .thenReturn(ResponseEntity.ok(expected));

        String actual = client.put(url, entity, String.class);
        assertEquals(expected, actual);
    }
}
