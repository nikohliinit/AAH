package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Reference implements Serializable {
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("value")
    private String value;
}
