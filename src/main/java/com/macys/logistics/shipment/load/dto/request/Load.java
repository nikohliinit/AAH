package com.macys.logistics.shipment.load.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Load {

    private String loadStatus;

    private String loadId;

    private String freightTerms;

    private String freightType;

    
    @NotEmpty(message = "carrierCode is mandatory can't be null or empty!!")
    private String carrierCode;
   
    @NotEmpty(message = "carrierType is mandatory can't be null or empty!!")
    private String carrierType;

    private String shipConfirmDate;

    
    @NotEmpty(message = "trailerNumber is mandatory can't be null or empty!!")
    private String trailerNumber;
  

    private String trailerClass;

    private Integer cases;
    
   
    @NotEmpty(message = "stops is mandatory can't be null or empty!!")
    private Stop[] stops;

    private String equipmentType;

}
