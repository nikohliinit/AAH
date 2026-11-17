package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Customer")
public class Customer {
    @XmlElement(name = "CustomerBusinessHours")
    private CustomerBusinessHours customerBusinessHours;

    @XmlElement(name = "POD")
    private String pod;

    @XmlElement(name = "CurrentCustomerVersion")
    private CurrentCustomerVersion currentCustomerVersion;

    public CustomerBusinessHours getCustomerBusinessHours() {
        return customerBusinessHours;
    }
    public void setCustomerBusinessHours(CustomerBusinessHours customerBusinessHours) {
        this.customerBusinessHours = customerBusinessHours;
    }
    public String getPOD() {
        return pod;
    }
    public void setPOD(String pod) {
        this.pod = pod;
    }
    public CurrentCustomerVersion getCurrentCustomerVersion() {
        return currentCustomerVersion;
    }
    public void setCurrentCustomerVersion(CurrentCustomerVersion currentCustomerVersion) {
        this.currentCustomerVersion = currentCustomerVersion;
    }
}
