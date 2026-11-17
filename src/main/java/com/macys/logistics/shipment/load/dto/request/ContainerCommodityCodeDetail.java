package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ContainerCommodityCodeDetail")
public class ContainerCommodityCodeDetail {

    @XmlElement(name = "ContainerCommodityCode")
    private String containerCommodityCode;

    public String getContainerCommodityCode() {
        return containerCommodityCode;
    }
    public void setContainerCommodityCode(String containerCommodityCode) {
        this.containerCommodityCode = containerCommodityCode;
    }
}
