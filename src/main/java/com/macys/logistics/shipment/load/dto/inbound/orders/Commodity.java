package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Commodity implements Serializable {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("description")
    private String description;
}
