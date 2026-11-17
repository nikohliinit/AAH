package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * L11 - Business Instructions and Reference Number
 * Contains reference identification for the tender response (only in acceptance messages).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessInstructionsAndReferenceNumber {

    @JsonProperty("referenceIdentification")
    private String referenceIdentification;

    @JsonProperty("referenceIdentificationQualifier")
    private String referenceIdentificationQualifier;
}
