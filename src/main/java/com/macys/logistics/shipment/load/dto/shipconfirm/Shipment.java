package com.macys.logistics.shipment.load.dto.shipconfirm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shipment {

    private String fulfillmentNbr;
    private Integer orderShipmentNbr;
    private String shipmentState;
    private Integer fulfillmentLocationNbr;
    private String fulfillmentAssociateId;
    private String trailer;
    private String apptNumber;
    private DeliveryTrkList deliveryTrkList;
    private CartonContentList cartonContentList;
}
