package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ShipmentInfo")
public class ShipmentInfo {
    @XmlElement(name = "Skids")
    private Double skids;

    @XmlElement(name = "Volume")
    private Double volume;

    @XmlElement(name = "Weight")
    private Double weight;

    @XmlElement(name = "Pieces")
    private Double pieces;


    public Double getSkids() {
        return skids;
    }
    public void setSkids(Double skids) {
        this.skids = skids;
    }
    public Double getVolume() {
        return volume;
    }
    public void setVolume(Double volume) {
        this.volume = volume;
    }
    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    public Double getPieces() {
        return pieces;
    }
    public void setPieces(Double pieces) {
        this.pieces = pieces;
    }
}
