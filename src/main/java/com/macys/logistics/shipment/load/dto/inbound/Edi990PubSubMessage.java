package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * EDI 990 PubSub Message
 * Top-level wrapper for EDI 990 messages received from GCP PubSub.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Edi990PubSubMessage {

    @JsonProperty("messageId")
    private String messageId;

    @JsonProperty("publishTime")
    private String publishTime;

    @JsonProperty("attributes")
    private PubSubAttributes attributes;

    @JsonProperty("data")
    private Edi990Data data;
}
