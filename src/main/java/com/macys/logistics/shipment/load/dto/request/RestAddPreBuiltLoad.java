package com.macys.logistics.shipment.load.dto.request;

import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ApiHeaderType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestShipmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestAddPreBuiltLoad {

    protected ApiHeaderType apiHeader;
    protected List<RestShipmentType> shipment;
    protected RestLoadCreateDataType loadCreateData;
    protected String loadStatus;
    protected Boolean enableLoadLevelNotificationFlag;
    protected String resultContents;
    protected Boolean returnRoutingMessageLogFlag;
}
