package com.macys.logistics.shipment.load.dto.shipconfirm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartonContent {

    private String deliveryTrackingNbr;
    private Integer orderLineItemNbr;
    private Integer itemPackedQuantity;
    private Long itemSkuUpc;
    private Long itemScannedSkuUpc;

}
