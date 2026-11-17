package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import lombok.Data;

import java.util.List;
@Data
public class ShipmentData {

    private String shipmentNumber;
    private List<ReferenceNumberStructure> referenceNumberStructure;
    private String systemLoadID;
}
