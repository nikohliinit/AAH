package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ShipmentLegDestinationShippingLocation")
public class ShipmentLegDestinationShippingLocation {

    @XmlElement(name = "ShipmentLegDestinationShippingLocationTimeZoneOffset")
    private Double shipmentLegDestinationShippingLocationTimeZoneOffset;


    public Double getShipmentLegDestinationShippingLocationTimeZoneOffset() {
        return shipmentLegDestinationShippingLocationTimeZoneOffset;
    }
    public void setShipmentLegDestinationShippingLocationTimeZoneOffset(Double shipmentLegDestinationShippingLocationTimeZoneOffset) {
        this.shipmentLegDestinationShippingLocationTimeZoneOffset = shipmentLegDestinationShippingLocationTimeZoneOffset;
    }
}
