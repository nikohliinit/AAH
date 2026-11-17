package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * B1 - Beginning Segment for Booking or Pick-up/Delivery
 * Contains key information about the tender response including carrier and reservation action code.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BeginningSegment {

    @JsonProperty("standardCarrierAlphaCode")
    private String standardCarrierAlphaCode;

    @JsonProperty("shipmentIdentification")
    private String shipmentIdentification;

    @JsonProperty("date")
    private String date;

    @JsonProperty("reservationActionCode")
    private String reservationActionCode;

    @JsonProperty("reservationDeclinedReasonCode")
    private String reservationDeclinedReasonCode;
}
