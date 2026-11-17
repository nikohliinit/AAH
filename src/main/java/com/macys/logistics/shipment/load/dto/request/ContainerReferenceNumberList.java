package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ContainerReferenceNumberList")
public class ContainerReferenceNumberList {
    @XmlElement(name = "ContainerReferenceNumberType")
    private String containerReferenceNumberType;

    @XmlElement(name = "ContainerReferenceNumber")
    private String containerReferenceNumber;

    public String getContainerReferenceNumberType() {
        return containerReferenceNumberType;
    }

    public void setContainerReferenceNumberType(String containerReferenceNumberType) {
        this.containerReferenceNumberType = containerReferenceNumberType;
    }

    public String getContainerReferenceNumber() {
        return containerReferenceNumber;
    }

    public void setContainerReferenceNumber(String containerReferenceNumber) {
        this.containerReferenceNumber = containerReferenceNumber;
    }
}
