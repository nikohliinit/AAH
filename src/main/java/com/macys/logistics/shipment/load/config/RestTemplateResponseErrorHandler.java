package com.macys.logistics.shipment.load.config;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.macys.logistics.shipment.load.exception.NotFoundException;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return (response.getStatusCode().is4xxClientError()
				|| response.getStatusCode().is5xxServerError());
	}

	@Override
	public void handleError(ClientHttpResponse httpResponse) throws IOException {
		// handle CLIENT_ERROR
		if (httpResponse.getStatusCode().is5xxServerError()
				&& httpResponse.getStatusCode().is4xxClientError()) {
			throw new NotFoundException();
		}

	}

}
