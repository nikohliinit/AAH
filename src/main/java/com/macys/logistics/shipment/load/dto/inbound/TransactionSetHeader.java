package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * ST - Transaction Set Header Segment
 * Represents the header information for an EDI 990 transaction set.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionSetHeader {

    @JsonProperty("transactionSetIdentifierCode")
    private String transactionSetIdentifierCode;

    @JsonProperty("transactionSetControlNumber")
    private String transactionSetControlNumber;
}
