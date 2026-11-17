package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BillToCustomerAddress")
public class BillToCustomerAddress {
    @XmlElement(name = "BillToCustomerAddressCity")
    private String billToCustomerAddressCity;

    @XmlElement(name = "BillToCustomerAddressStreet")
    private String billToCustomerAddressStreet;

    @XmlElement(name = "BillToCustomerAddressStateCode")
    private String billToCustomerAddressStateCode;

    @XmlElement(name = "BillToCustomerAddressCountryCode")
    private String billToCustomerAddressCountryCode;

    @XmlElement(name = "BillToCustomerAddressTimeZoneOffset")
    private Double billToCustomerAddressTimeZoneOffset;

    @XmlElement(name = "BillToCustomerAddressPostalCode")
    private Integer billToCustomerAddressPostalCode;

    public String getBillToCustomerAddressCity() {
        return billToCustomerAddressCity;
    }
    public void setBillToCustomerAddressCity(String billToCustomerAddressCity) {
        this.billToCustomerAddressCity = billToCustomerAddressCity;
    }
    public String getBillToCustomerAddressStreet() {
        return billToCustomerAddressStreet;
    }
    public void setBillToCustomerAddressStreet(String billToCustomerAddressStreet) {
        this.billToCustomerAddressStreet = billToCustomerAddressStreet;
    }
    public String getBillToCustomerAddressStateCode() {
        return billToCustomerAddressStateCode;
    }
    public void setBillToCustomerAddressStateCode(String billToCustomerAddressStateCode) {
        this.billToCustomerAddressStateCode = billToCustomerAddressStateCode;
    }
    public String getBillToCustomerAddressCountryCode() {
        return billToCustomerAddressCountryCode;
    }
    public void setBillToCustomerAddressCountryCode(String billToCustomerAddressCountryCode) {
        this.billToCustomerAddressCountryCode = billToCustomerAddressCountryCode;
    }
    public Double getBillToCustomerAddressTimeZoneOffset() {
        return billToCustomerAddressTimeZoneOffset;
    }
    public void setBillToCustomerAddressTimeZoneOffset(Double billToCustomerAddressTimeZoneOffset) {
        this.billToCustomerAddressTimeZoneOffset = billToCustomerAddressTimeZoneOffset;
    }
    public Integer getBillToCustomerAddressPostalCode() {
        return billToCustomerAddressPostalCode;
    }
    public void setBillToCustomerAddressPostalCode(Integer billToCustomerAddressPostalCode) {
        this.billToCustomerAddressPostalCode = billToCustomerAddressPostalCode;
    }
}
