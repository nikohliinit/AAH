package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Carrier implements Serializable {
    
    @JsonProperty("scac")
    private String scac;
}
