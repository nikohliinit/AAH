package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BillToCustomerBusinessHours")
public class BillToCustomerBusinessHours {

    @XmlElement(name = "BillToCustomerBusinessHoursTimeZoneOffset")
    private Double billToCustomerBusinessHoursTimeZoneOffset;

    public Double getBillToCustomerBusinessHoursTimeZoneOffset() {
        return billToCustomerBusinessHoursTimeZoneOffset;
    }
    public void setBillToCustomerBusinessHoursTimeZoneOffset(Double billToCustomerBusinessHoursTimeZoneOffset) {
        this.billToCustomerBusinessHoursTimeZoneOffset = billToCustomerBusinessHoursTimeZoneOffset;
    }
}
