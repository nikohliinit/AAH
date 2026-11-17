package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestStopAppointmentRequest {

    @JsonProperty("stopAppointment")
    private List<RestStopAppointment> stopAppointment;

}
