package com.macys.logistics.shipment.load.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrailerYardAppointmentInfo {

    @JsonProperty("yardAreaTypNbr")
    private Integer yardAreaTypNbr;

    @JsonProperty("yardAreaTypDesc")
    private String yardAreaTypDesc;

    @JsonProperty("yardAreaId")
    private String yardAreaId;

    @JsonProperty("locNbr")
    private Integer locNbr;

    @JsonProperty("apptNbr")
    private Long apptNbr;
}