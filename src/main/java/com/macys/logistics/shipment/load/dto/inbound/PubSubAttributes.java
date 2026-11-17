package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * PubSub Message Attributes
 * Contains metadata for routing and filtering PubSub messages.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PubSubAttributes {

    @JsonProperty("messageType")
    private String messageType;

    @JsonProperty("responseCode")
    private String responseCode;

    @JsonProperty("tradingPartnerId")
    private String tradingPartnerId;

    @JsonProperty("version")
    private String version;
}
