package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Charges implements Serializable {
    
    @JsonProperty("revenue")
    private BigDecimal revenue;
    
    @JsonProperty("currency")
    private String currency;
}
