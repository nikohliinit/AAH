package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TimeWindow implements Serializable {
    
    @JsonProperty("earliest")
    private String earliest;
    
    @JsonProperty("latest")
    private String latest;
}
