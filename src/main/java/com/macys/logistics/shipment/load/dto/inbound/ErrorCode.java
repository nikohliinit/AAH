package com.macys.logistics.shipment.load.dto.inbound;

import lombok.Getter;

@Getter
public enum ErrorCode {

    Create_Shipment_Exception("3001","Unsuccessful shipment creation in TMS"),
    Delete_Shipment_Exception("3002","Unsuccessful shipment deletion in TMS");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
