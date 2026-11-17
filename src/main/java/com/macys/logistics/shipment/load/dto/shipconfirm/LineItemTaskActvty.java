package com.macys.logistics.shipment.load.dto.shipconfirm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItemTaskActvty {

    private String associateActivityId;
    private String deviceName;
    private String deviceType;
    private Integer pickAttemptCount;
    private String sourceApplicationId;
    private String fulfillmentAssociateId;
}
