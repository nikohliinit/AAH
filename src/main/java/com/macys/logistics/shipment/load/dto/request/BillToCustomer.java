package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BillToCustomer")
public class BillToCustomer {
    @XmlElement(name = "CurrentBillToCustomerVersion")
    private CurrentBillToCustomerVersion currentBillToCustomerVersion;

    @XmlElement(name = "BillToCustomerBusinessHours")
    private BillToCustomerBusinessHours billToCustomerBusinessHours;

    public BillToCustomerBusinessHours getBillToCustomerBusinessHours() {
        return billToCustomerBusinessHours;
    }

    public void setBillToCustomerBusinessHours(BillToCustomerBusinessHours billToCustomerBusinessHours) {
        this.billToCustomerBusinessHours = billToCustomerBusinessHours;
    }

    public CurrentBillToCustomerVersion getCurrentBillToCustomerVersion() {
        return currentBillToCustomerVersion;
    }

    public void setCurrentBillToCustomerVersion(CurrentBillToCustomerVersion currentBillToCustomerVersion) {
        this.currentBillToCustomerVersion = currentBillToCustomerVersion;
    }
}
