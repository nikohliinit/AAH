package com.macys.logistics.shipment.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrailerDoorDockCDCEvent {

 @JsonProperty("data")
 private TrlrData data;

 @JsonProperty("metadata")
 private TrailerDoorDockMetaData metadata;

 @JsonProperty("userdata")
 private Userdata userdata;

 @JsonProperty("before")
 private TrlrData before;

 @JsonProperty("typeUUID")
 private String typeUUID;
}
