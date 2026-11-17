package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CurrentBillToCustomerVersion")
public class CurrentBillToCustomerVersion {
    @XmlElement(name = "BillToCustomerName")
    private String billToCustomerName;

    @XmlElement(name = "BillToCustomerAddress")
    private BillToCustomerAddress billToCustomerAddress;

    public String getBillToCustomerName() {
        return billToCustomerName;
    }
    public void setBillToCustomerName(String billToCustomerName) {
        this.billToCustomerName = billToCustomerName;
    }
    public BillToCustomerAddress getBillToCustomerAddress() {
        return billToCustomerAddress;
    }
    public void setBillToCustomerAddress(BillToCustomerAddress billToCustomerAddress) {
        this.billToCustomerAddress = billToCustomerAddress;
    }
}
