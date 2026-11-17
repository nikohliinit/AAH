package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CustomerBusinessHours")
public class CustomerBusinessHours {
    @XmlElement(name = "CustomerBusinessHoursTimeZoneOffset")
    private Double customerBusinessHoursTimeZoneOffset;

    public Double getCustomerBusinessHoursTimeZoneOffset() {
        return customerBusinessHoursTimeZoneOffset;
    }
    public void setCustomerBusinessHoursTimeZoneOffset(Double customerBusinessHoursTimeZoneOffset) {
        this.customerBusinessHoursTimeZoneOffset = customerBusinessHoursTimeZoneOffset;
    }
}
