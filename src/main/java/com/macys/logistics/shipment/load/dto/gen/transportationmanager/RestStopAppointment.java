package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestStopAppointment {
    @JsonProperty("appointment")
    private RestAppointment appointment;

    @JsonProperty("stopIDTypeEnumVal")
    private String stopIDTypeEnumVal;

    @JsonProperty("systemLoadID")
    private String systemLoadID;

    @JsonProperty("systemStopID")
    private String systemStopID;

}
