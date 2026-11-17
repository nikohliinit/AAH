package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ContainerShippingInfo")
public class ContainerShippingInfo {
    @XmlElement(name = "ContainerShippingInfoOrderValue")
    private Double containerShippingInfoOrderValue;

    @XmlElement(name = "ContainerShippingInfoVolume")
    private Double containerShippingInfoVolume;

    @XmlElement(name = "ContainerShippingInfoWeight")
    private Double containerShippingInfoWeight;

    public Double getContainerShippingInfoOrderValue() {
        return containerShippingInfoOrderValue;
    }
    public void setContainerShippingInfoOrderValue(Double containerShippingInfoOrderValue) {
        this.containerShippingInfoOrderValue = containerShippingInfoOrderValue;
    }
    public Double getContainerShippingInfoVolume() {
        return containerShippingInfoVolume;
    }
    public void setContainerShippingInfoVolume(Double containerShippingInfoVolume) {
        this.containerShippingInfoVolume = containerShippingInfoVolume;
    }
    public Double getContainerShippingInfoWeight() {
        return containerShippingInfoWeight;
    }
    public void setContainerShippingInfoWeight(Double containerShippingInfoWeight) {
        this.containerShippingInfoWeight = containerShippingInfoWeight;
    }
}
