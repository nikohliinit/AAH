package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Vendor implements Serializable {
    
    @JsonProperty("supplierNbr")
    private String supplierNbr;
    
    @JsonProperty("name")
    private String name;
}
