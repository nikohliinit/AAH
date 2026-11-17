package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RestProcessShipmentOrderCreate {
    protected ApiHeaderType apiHeader;
    protected List<RestShipmentType> shipment;
    protected Boolean ignoreAllShipmentReferenceNumbersFlag;
    protected Boolean executeAPRatingFlag;
    protected Boolean executeARRatingFlag;
    protected String resultContents;
    protected Boolean validateCarrierServiceEquipmentFlag;

    public List<RestShipmentType> getShipment() {
        if (shipment == null) {
            shipment = new ArrayList<RestShipmentType>();
        }
        return this.shipment;
    }
}
