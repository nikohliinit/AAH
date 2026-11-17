package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Audit implements Serializable {
    
    @JsonProperty("source")
    private String source;
    
    @JsonProperty("intgrtnCtlSeqNbr")
    private Long intgrtnCtlSeqNbr;
    
    @JsonProperty("eventTs")
    private String eventTs;
}
