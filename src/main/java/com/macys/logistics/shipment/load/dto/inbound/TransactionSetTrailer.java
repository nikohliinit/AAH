package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * SE - Transaction Set Trailer Segment
 * Marks the end of the EDI 990 transaction set.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionSetTrailer {

    @JsonProperty("numberOfIncludedSegments")
    private Integer numberOfIncludedSegments;

    @JsonProperty("transactionSetControlNumber")
    private String transactionSetControlNumber;
}
