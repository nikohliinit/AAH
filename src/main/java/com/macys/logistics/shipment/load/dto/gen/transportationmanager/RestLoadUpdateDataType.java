package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RestLoadUpdateDataType {

    
    private String systemLoadID;
    private String loadDescription;
    private String loadScheduledDateTime;

}
