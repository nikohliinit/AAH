package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ContainerType")
public class ContainerType {
    @XmlElement(name = "ContainerTypeCode")
    private String containerTypeCode;

    public String getContainerTypeCode() {
        return containerTypeCode;
    }
    public void setContainerTypeCode(String containerTypeCode) {
        this.containerTypeCode = containerTypeCode;
    }
}
