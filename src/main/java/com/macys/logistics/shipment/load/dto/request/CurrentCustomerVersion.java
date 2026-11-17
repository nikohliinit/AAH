package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CurrentCustomerVersion")
public class CurrentCustomerVersion {
    @XmlElement(name = "CustomerName")
    private String customerName;

    @XmlElement(name = "CustomerAddress")
    private CustomerAddress customerAddress;

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public CustomerAddress getCustomerAddress() {
        return customerAddress;
    }
    public void setCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddress = customerAddress;
    }
}
