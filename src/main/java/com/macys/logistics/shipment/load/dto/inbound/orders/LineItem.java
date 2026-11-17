package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class LineItem implements Serializable {
    
    @JsonProperty("pieces")
    private Integer pieces;
    
    @JsonProperty("weightLb")
    private BigDecimal weightLb;
    
    @JsonProperty("volumeCf")
    private BigDecimal volumeCf;
}
