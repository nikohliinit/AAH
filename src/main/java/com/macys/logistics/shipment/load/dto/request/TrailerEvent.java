package com.macys.logistics.shipment.load.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrailerEvent {

    private String eventType;
    private String requestId;
    private String transactionId;
    private String trailerId;
    private String appointmentId;
    private List<Image> images;
    private String carrierSCAC;
    private String vendor;
    private String readyForUnloadTimestamp;

}
