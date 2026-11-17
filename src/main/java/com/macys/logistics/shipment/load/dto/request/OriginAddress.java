package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OriginAddress")
public class OriginAddress {

    @XmlElement(name = "OriginCountryCode")
    private String originCountryCode;

    @XmlElement(name = "OriginPostalCode")
    private Integer originPostalCode;

    @XmlElement(name = "OriginCity")
    private String originCity;

    @XmlElement(name = "OriginStateCode")
    private String originStateCode;

    @XmlElement(name = "OriginStreet")
    private String originStreet;

    @XmlElement(name = "OriginTimeZoneOffset")
    private Double originTimeZoneOffset;

    public String getOriginCountryCode() {
        return originCountryCode;
    }
    public void setOriginCountryCode(String originCountryCode) {
        this.originCountryCode = originCountryCode;
    }
    public Integer getOriginPostalCode() {
        return originPostalCode;
    }
    public void setOriginPostalCode(Integer originPostalCode) {
        this.originPostalCode = originPostalCode;
    }
    public String getOriginCity() {
        return originCity;
    }
    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }
    public String getOriginStateCode() {
        return originStateCode;
    }
    public void setOriginStateCode(String originStateCode) {
        this.originStateCode = originStateCode;
    }
    public String getOriginStreet() {
        return originStreet;
    }
    public void setOriginStreet(String originStreet) {
        this.originStreet = originStreet;
    }
    public Double getOriginTimeZoneOffset() {
        return originTimeZoneOffset;
    }
    public void setOriginTimeZoneOffset(Double originTimeZoneOffset) {
        this.originTimeZoneOffset = originTimeZoneOffset;
    }
}
