package com.macys.logistics.shipment.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Shipment {

    private String shipmentNumber;
    private String customerCode;
    private String logisticsGroupCode;
    private String divisionCode;
    private String systemPlanID;
    private String freightTermsEnumVal;
    private String shipFromLocationTypeEnumVal;
    private String  shipFromLocationCode;
    private String shipToLocationTypeEnumVal;
    private String shipToLocationCode;
    private XMLGregorianCalendar pickupFromDateTime; 
    private XMLGregorianCalendar pickupToDateTime; 
    private XMLGregorianCalendar deliveryFromDateTime; 
    private XMLGregorianCalendar deliveryToDateTime;
    private String commodityCode;
    private Boolean deferAPRatingFlag;
    private Boolean deferARRatingFlag;
    private Boolean useOriginDefaultsFlag;
    private Long departmentNumber;
    private XMLGregorianCalendar shipDate;
    
    private Boolean useDestinationDefaultsFlag;
    private Boolean ignoreReferenceNumbersFlag;
    private List<ReferenceNumberStructure> referenceNumberStructure;
    private Boolean ignoreContainersFlag;

    private String containerId;
    private Boolean ignoreChargeOverridesFlag;

    private Double length;
    private Double width;
    private Double height;
    private Integer qty;
    private Double weight;
    private Long storeLocnNbr;

}
