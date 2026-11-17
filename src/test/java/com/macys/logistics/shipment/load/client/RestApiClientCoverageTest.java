package com.macys.logistics.shipment.load.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestApiClientCoverageTest {

    @InjectMocks
    private RestApiClient restApiClient;

    @Mock
    private RestTemplate restTemplate;

    // ===================== GET =====================

    @Test
    void get_success() {
        String url = "http://example.com/api";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>("body", headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("response", HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class))
                .thenReturn(responseEntity);

        String result = restApiClient.get(url, httpEntity, String.class);

        assertEquals("response", result);
        verify(restTemplate).exchange(url, HttpMethod.GET, httpEntity, String.class);
    }

    @Test
    void get_nullBody() {
        String url = "http://example.com/api";
        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class))
                .thenReturn(responseEntity);

        String result = restApiClient.get(url, httpEntity, String.class);

        assertNull(result);
    }

    // ===================== POST =====================

    @Test
    void post_success() {
        String url = "http://example.com/api";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>("body", headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("posted", HttpStatus.CREATED);
        when(restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class))
                .thenReturn(responseEntity);

        String result = restApiClient.post(url, httpEntity, String.class);

        assertEquals("posted", result);
        verify(restTemplate).exchange(url, HttpMethod.POST, httpEntity, String.class);
    }

    @Test
    void post_nullBody() {
        String url = "http://example.com/api";
        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class))
                .thenReturn(responseEntity);

        String result = restApiClient.post(url, httpEntity, String.class);

        assertNull(result);
    }

    // ===================== PUT =====================

    @Test
    void put_success() {
        String url = "http://example.com/api";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>("body", headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("updated", HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class))
                .thenReturn(responseEntity);

        String result = restApiClient.put(url, httpEntity, String.class);

        assertEquals("updated", result);
        verify(restTemplate).exchange(url, HttpMethod.PUT, httpEntity, String.class);
    }

    @Test
    void put_nullBody() {
        String url = "http://example.com/api";
        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class))
                .thenReturn(responseEntity);

        String result = restApiClient.put(url, httpEntity, String.class);

        assertNull(result);
    }
}
