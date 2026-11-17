package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class LocationInfo implements Serializable {
    
    @JsonProperty("locationId")
    private String locationId;
    
    @JsonProperty("isHub")
    private Boolean isHub;
}
