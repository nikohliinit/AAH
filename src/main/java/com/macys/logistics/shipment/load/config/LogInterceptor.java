package com.macys.logistics.shipment.load.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Log4j2
public class LogInterceptor implements ClientHttpRequestInterceptor {

    private static final String EXCLUDED_URL = "https://login.microsoftonline.com";
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI requestUri = request.getURI();
        ClientHttpResponse response = execution.execute(request, body);
        if (!requestUri.toString().startsWith(EXCLUDED_URL)) {
            log.info("**********Request URL***********: {}", requestUri);
            log.debug("**********Request Headers***********: {}", filterHeaders(request.getHeaders()));
            log.debug("**********Request body***********: {}", new String(body, StandardCharsets.UTF_8));
            InputStreamReader isr = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8);
            String responseBody = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));
            log.debug("**********Response Body**********: {}", responseBody);
            log.info("**********Response Code**********: {}", response.getStatusCode());
        }
        return response;
    }

    private HttpHeaders filterHeaders(HttpHeaders headers) {
        HttpHeaders filteredHeaders = new HttpHeaders();
        headers.forEach((key, value) -> {
            if (!"Authorization".equalsIgnoreCase(key)) {
                filteredHeaders.put(key, value);
            } else {
                filteredHeaders.put(key, Collections.singletonList("Bearer *****"));
            }
        });
        return filteredHeaders;
    }
}