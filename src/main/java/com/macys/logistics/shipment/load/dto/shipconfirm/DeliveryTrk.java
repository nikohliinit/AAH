package com.macys.logistics.shipment.load.dto.shipconfirm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryTrk {

    private Integer cartonSequenceNbr;
    private Double cartonWeightValue;
    private String deliveryTrackingNbr;
    private String deliveryCarrierName;
    private String cartonNbr;
    private Double freightChargeAmount;

}
