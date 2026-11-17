package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DestinationAddress")
public class DestinationAddress {
    @XmlElement(name = "DestinationTimeZoneOffset")
    private Double destinationTimeZoneOffset;

    @XmlElement(name = "DestinationPostalCode")
    private String destinationPostalCode;

    @XmlElement(name = "DestinationCountryCode")
    private String destinationCountryCode;

    @XmlElement(name = "DestinationCity")
    private String destinationCity;

    @XmlElement(name = "DestinationStateCode")
    private String destinationStateCode;

    @XmlElement(name = "DestinationStreet")
    private String destinationStreet;

    public Double getDestinationTimeZoneOffset() {
        return destinationTimeZoneOffset;
    }
    public void setDestinationTimeZoneOffset(Double destinationTimeZoneOffset) {
        this.destinationTimeZoneOffset = destinationTimeZoneOffset;
    }
    public String getDestinationPostalCode() {
        return destinationPostalCode;
    }
    public void setDestinationPostalCode(String destinationPostalCode) {
        this.destinationPostalCode = destinationPostalCode;
    }
    public String getDestinationCountryCode() {
        return destinationCountryCode;
    }
    public void setDestinationCountryCode(String destinationCountryCode) {
        this.destinationCountryCode = destinationCountryCode;
    }
    public String getDestinationCity() {
        return destinationCity;
    }
    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }
    public String getDestinationStateCode() {
        return destinationStateCode;
    }
    public void setDestinationStateCode(String destinationStateCode) {
        this.destinationStateCode = destinationStateCode;
    }
    public String getDestinationStreet() {
        return destinationStreet;
    }
    public void setDestinationStreet(String destinationStreet) {
        this.destinationStreet = destinationStreet;
    }
}
