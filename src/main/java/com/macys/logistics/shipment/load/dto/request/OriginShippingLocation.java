package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OriginShippingLocation")
public class OriginShippingLocation {
    @XmlElement(name = "OriginShippingLocationContact")
    private String originShippingLocationContact;

    @XmlElement(name = "OriginShippingLocationTimeZoneOffset")
    private Double originShippingLocationTimeZoneOffset;

    @XmlElement(name = "OriginShippingLocationName")
    private String originShippingLocationName;

    public String getOriginShippingLocationContact() {
        return originShippingLocationContact;
    }
    public void setOriginShippingLocationContact(String originShippingLocationContact) {
        this.originShippingLocationContact = originShippingLocationContact;
    }
    public Double getOriginShippingLocationTimeZoneOffset() {
        return originShippingLocationTimeZoneOffset;
    }
    public void setOriginShippingLocationTimeZoneOffset(Double originShippingLocationTimeZoneOffset) {
        this.originShippingLocationTimeZoneOffset = originShippingLocationTimeZoneOffset;
    }
    public String getOriginShippingLocationName() {
        return originShippingLocationName;
    }
    public void setOriginShippingLocationName(String originShippingLocationName) {
        this.originShippingLocationName = originShippingLocationName;
    }
}
