package com.macys.logistics.shipment.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model POJO class for Appointment payload
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentPayload {

  private String appointmentNumber;
  private String appointmentStatus;
  private String appointmentType;
  private String appointmentScheduledArivalTs;
  private String appointmentSealConditionDescription;
  private String appointmentCurrTrailerYardAreaID;
  private String appointmentDestLocNumber;
  private String appointmentCarrierTrailerNumber;
  private String appointmentDispatchFlag;
  private String appointmentLastUpdUserIdV;
  private String appointmentChangeOrigin;
  private Integer appointmentNumberOfCartons;
  private String appointmentFreightType;
  private String appointmentDropFlag;
}
