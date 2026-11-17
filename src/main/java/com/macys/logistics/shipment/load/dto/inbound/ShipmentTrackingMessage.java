package com.macys.logistics.shipment.load.dto.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShipmentTrackingMessage {

    private Integer loadId;
    private String shipmentNumber;
    private String carrierScac;
    private String serviceType;
    private String proNumber;
    private String labelPath;
    private String labelFormat;
}
