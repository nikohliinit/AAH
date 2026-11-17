package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Base EDI 990 Data
 * Contains all fields present in the EDI 990 response to a load tender message.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Edi990Data {

    @JsonProperty("transactionSetHeader")
    private TransactionSetHeader transactionSetHeader;

    @JsonProperty("beginningSegment")
    private BeginningSegment beginningSegment;

    @JsonProperty("businessInstructionsAndReferenceNumber")
    private BusinessInstructionsAndReferenceNumber businessInstructionsAndReferenceNumber;

    @JsonProperty("transactionSetTrailer")
    private TransactionSetTrailer transactionSetTrailer;
}
