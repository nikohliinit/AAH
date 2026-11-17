package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DestinationShippingLocation")
public class DestinationShippingLocation {
    @XmlElement(name = "DestinationShippingLocationTimeZoneOffset")
    private Double destinationShippingLocationTimeZoneOffset;

    @XmlElement(name = "DestinationShippingLocationName")
    private String destinationShippingLocationName;

    @XmlElement(name = "DestinationShippingLocationContact")
    private String destinationShippingLocationContact;

    public Double getDestinationShippingLocationTimeZoneOffset() {
        return destinationShippingLocationTimeZoneOffset;
    }
    public void setDestinationShippingLocationTimeZoneOffset(Double destinationShippingLocationTimeZoneOffset) {
        this.destinationShippingLocationTimeZoneOffset = destinationShippingLocationTimeZoneOffset;
    }
    public String getDestinationShippingLocationName() {
        return destinationShippingLocationName;
    }
    public void setDestinationShippingLocationName(String destinationShippingLocationName) {
        this.destinationShippingLocationName = destinationShippingLocationName;
    }
    public String getDestinationShippingLocationContact() {
        return destinationShippingLocationContact;
    }
    public void setDestinationShippingLocationContact(String destinationShippingLocationContact) {
        this.destinationShippingLocationContact = destinationShippingLocationContact;
    }
}
