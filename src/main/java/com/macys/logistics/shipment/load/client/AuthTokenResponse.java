package com.macys.logistics.shipment.load.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthTokenResponse {
	private String type;
	private String value;
	private String expiry;
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("error_description")
	private String errorDescription;
}
