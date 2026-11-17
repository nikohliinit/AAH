package com.macys.logistics.shipment.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuditFeedback {
    private String applicationName;
    private String processName;
    private String processAction;
    private String status;
    private String errorCode;
    private String correlationId;
    private Object auditDetail;
    private Timestamp auditCreateTs;
    private Timestamp createdTs;
    private Timestamp updatedTs;
}