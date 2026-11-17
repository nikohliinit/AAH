package com.macys.logistics.shipment.load.dto.shipconfirm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItem {


    private int fulfillmentLocationNbr;
    private int itemDepartmentNbr;
    private int itemOriginalQuantity;
    private int itemPickedQuantity;
    private long itemScannedSkuUpc;
    private long itemSkuUpc;
    private String lineItemState;
    private int orderLineItemNbr;
    private int orderShipmentNbr;
    private Integer itemAdjustedQuantity;
    private String itemNotInLocationCode;
    private LineItemTaskActvty lineItemTaskActvty;
}
