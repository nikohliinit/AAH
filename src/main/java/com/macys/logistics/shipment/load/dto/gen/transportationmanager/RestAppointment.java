package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestAppointment {

    @JsonProperty("appointmentFromDateTime")
    private String appointmentFromDateTime;

    @JsonProperty("appointmentStatusEnumVal")
    private String appointmentStatusEnumVal;

    @JsonProperty("appointmentToDateTime")
    private String appointmentToDateTime;

}
