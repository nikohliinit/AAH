package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ShipmentLegOriginShippingLocation")
public class ShipmentLegOriginShippingLocation {

    @XmlElement(name = "ShipmentLegOriginShippingLocationTimeZoneOffset")
    private Double shipmentLegOriginShippingLocationTimeZoneOffset;

    public Double getShipmentLegOriginShippingLocationTimeZoneOffset() {
        return shipmentLegOriginShippingLocationTimeZoneOffset;
    }
    public void setShipmentLegOriginShippingLocationTimeZoneOffset(Double shipmentLegOriginShippingLocationTimeZoneOffset) {
        this.shipmentLegOriginShippingLocationTimeZoneOffset = shipmentLegOriginShippingLocationTimeZoneOffset;
    }
}
