package com.macys.logistics.shipment.load.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class RestAuthTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("error_description")
        private String errorDescription;
    }

