package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "UOM")
public class UOM {

    @XmlElement(name = "WeightUOM")
    private String weightUOM;

    @XmlElement(name = "VolumeUOM")
    private String volumeUOM;

    public String getWeightUOM() {
        return weightUOM;
    }
    public void setWeightUOM(String weightUOM) {
        this.weightUOM = weightUOM;
    }
    public String getVolumeUOM() {
        return volumeUOM;
    }
    public void setVolumeUOM(String volumeUOM) {
        this.volumeUOM = volumeUOM;
    }
}
