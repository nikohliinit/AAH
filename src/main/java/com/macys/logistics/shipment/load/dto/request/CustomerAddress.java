package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CustomerAddress")
public class CustomerAddress {
    @XmlElement(name = "CustomerAddressTimeZoneOffset")
    private Double customerAddressTimeZoneOffset;

    @XmlElement(name = "CustomerAddressStreet")
    private String customerAddressStreet;

    @XmlElement(name = "CustomerAddressCity")
    private String customerAddressCity;

    @XmlElement(name = "CustomerAddressPostalCode")
    private Integer customerAddressPostalCode;

    @XmlElement(name = "CustomerAddressStateCode")
    private String customerAddressStateCode;

    @XmlElement(name = "CustomerAddressCountryCode")
    private String customerAddressCountryCode;

    public Double getCustomerAddressTimeZoneOffset() {
        return customerAddressTimeZoneOffset;
    }
    public void setCustomerAddressTimeZoneOffset(Double customerAddressTimeZoneOffset) {
        this.customerAddressTimeZoneOffset = customerAddressTimeZoneOffset;
    }
    public String getCustomerAddressStreet() {
        return customerAddressStreet;
    }
    public void setCustomerAddressStreet(String customerAddressStreet) {
        this.customerAddressStreet = customerAddressStreet;
    }
    public String getCustomerAddressCity() {
        return customerAddressCity;
    }
    public void setCustomerAddressCity(String customerAddressCity) {
        this.customerAddressCity = customerAddressCity;
    }
    public Integer getCustomerAddressPostalCode() {
        return customerAddressPostalCode;
    }
    public void setCustomerAddressPostalCode(Integer customerAddressPostalCode) {
        this.customerAddressPostalCode = customerAddressPostalCode;
    }
    public String getCustomerAddressStateCode() {
        return customerAddressStateCode;
    }
    public void setCustomerAddressStateCode(String customerAddressStateCode) {
        this.customerAddressStateCode = customerAddressStateCode;
    }
    public String getCustomerAddressCountryCode() {
        return customerAddressCountryCode;
    }
    public void setCustomerAddressCountryCode(String customerAddressCountryCode) {
        this.customerAddressCountryCode = customerAddressCountryCode;
    }
}
