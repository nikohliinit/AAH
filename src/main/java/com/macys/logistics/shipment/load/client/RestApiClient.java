package com.macys.logistics.shipment.load.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class RestApiClient {
	
	@Autowired
	private RestTemplate restTemplate;

	@SuppressWarnings("rawtypes")
	public <T> T get(String url, HttpEntity httpEntity, Class<T> responseType) {
		ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, responseType);
		return responseEntity.getBody();
	}
	
	@SuppressWarnings("rawtypes")
	public <T> T post(String url, HttpEntity httpEntity, Class<T> responseType) {
		ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, responseType);
		return responseEntity.getBody();
	}
	
	@SuppressWarnings("rawtypes")
	public <T> T put(String url, HttpEntity httpEntity, Class<T> responseType) {
		ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, responseType);
		return responseEntity.getBody();
	}

}
