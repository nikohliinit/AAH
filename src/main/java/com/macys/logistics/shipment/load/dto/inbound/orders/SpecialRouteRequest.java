package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpecialRouteRequest implements Serializable {
    
    @JsonProperty("seq")
    private Integer seq;
    
    @JsonProperty("routeType")
    private String routeType;
    
    @JsonProperty("routeValue")
    private String routeValue;
    
    @JsonProperty("priority")
    private Integer priority;
    
    @JsonProperty("userText")
    private String userText;
}
