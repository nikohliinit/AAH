package com.macys.logistics.shipment.load.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class SpecialRateRequest {

    private String specialRateRequestType;

    private String containerNumber;

    private String lastFreeDate;

    private String brokerName;

    private String brokerContactPhoneNumber;

    private String brokerContactName;

    private String requestedDeliveryTime;

    private String trailerNumber;

    private String carrier;

    private String dropLive;

    private String carrierContactName;

    private String requestedDeliveryDate;

    private String carrierContactPhoneNumber;

    private Date inDcDate;
}
