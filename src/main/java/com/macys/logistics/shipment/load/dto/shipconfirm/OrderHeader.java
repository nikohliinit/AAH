package com.macys.logistics.shipment.load.dto.shipconfirm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderHeader {

    private String orderId;
    private String orderID;
    private String sourceApplicationId;
    private String deviceName;
    private String deviceType;
    private String sourceApplicationFunctionType;
    private String messageCreateTimestamp;
    private String messageCreateUtcTimestamp;
    private String messageUniversalUniqueId;
    private Long sequenceEpochNbr;
    private String eventType;
    private String eventTypeDescription;
    private String eventStatus;
    private String eventTimestamp;
    private String eventUtcTimestamp;
    private String orderType;
    private String orderSubType;
}
