package com.macys.logistics.shipment.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentSealConditionDetail {

    private Integer nbr;

    private Integer sequenceNbr;

    private String sealNbr;

    private Integer locNbr;

    private Timestamp sealRecordedTS;

     private String sealConditionDesc;

    private String sealCommentDesc;

    private String sealCommentCD;

    private Boolean sealAttachF;

    private String sealExaminerEmp;

    private String appointmentSealCnd;
}
