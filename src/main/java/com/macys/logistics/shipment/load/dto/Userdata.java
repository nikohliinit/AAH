package com.macys.logistics.shipment.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public  class Userdata implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("UUID")
    private String uuid;

}