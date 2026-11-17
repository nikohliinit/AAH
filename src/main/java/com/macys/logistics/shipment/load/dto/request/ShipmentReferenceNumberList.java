package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ShipmentReferenceNumberList")
public class ShipmentReferenceNumberList {
    @XmlElement(name = "ShipmentReferenceNumberType")
    private String shipmentReferenceNumberType;

    @XmlElement(name = "ShipmentReferenceNumber")
    private String shipmentReferenceNumber;
    public String getShipmentReferenceNumberType() {
        return shipmentReferenceNumberType;
    }
    public void setShipmentReferenceNumberType(String shipmentReferenceNumberType) {
        this.shipmentReferenceNumberType = shipmentReferenceNumberType;
    }
    public String getShipmentReferenceNumber() {
        return shipmentReferenceNumber;
    }
    public void setShipmentReferenceNumber(String shipmentReferenceNumber) {
        this.shipmentReferenceNumber = shipmentReferenceNumber;
    }
}
