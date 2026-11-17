package com.macys.logistics.shipment.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuditTrailerFeedback {

    private String applicationName;
    private String processName;
    private String actionName;
    private String traceId;
    private String status;
    private String errorCode;
    private Object[] inputArguments;
    private Timestamp occurredAt;
    private String errorMessage;
    private String operationSuccessful;
}
