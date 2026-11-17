package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ShipmentLegOriginAddress")
public class ShipmentLegOriginAddress {
    @XmlElement(name = "ShipmentLegOriginPostalCode")
    private Integer shipmentLegOriginPostalCode;

    @XmlElement(name = "ShipmentLegOriginCountryCode")
    private String shipmentLegOriginCountryCode;

    @XmlElement(name = "ShipmentLegOriginTimeZoneOffset")
    private Double shipmentLegOriginTimeZoneOffset;

    @XmlElement(name = "ShipmentLegOriginCity")
    private String shipmentLegOriginCity;

    @XmlElement(name = "ShipmentLegOriginStreet")
    private String shipmentLegOriginStreet;

    @XmlElement(name = "ShipmentLegOriginStateCode")
    private String shipmentLegOriginStateCode;

    public Integer getShipmentLegOriginPostalCode() {
        return shipmentLegOriginPostalCode;
    }
    public void setShipmentLegOriginPostalCode(Integer shipmentLegOriginPostalCode) {
        this.shipmentLegOriginPostalCode = shipmentLegOriginPostalCode;
    }
    public String getShipmentLegOriginCountryCode() {
        return shipmentLegOriginCountryCode;
    }
    public void setShipmentLegOriginCountryCode(String shipmentLegOriginCountryCode) {
        this.shipmentLegOriginCountryCode = shipmentLegOriginCountryCode;
    }
    public Double getShipmentLegOriginTimeZoneOffset() {
        return shipmentLegOriginTimeZoneOffset;
    }
    public void setShipmentLegOriginTimeZoneOffset(Double shipmentLegOriginTimeZoneOffset) {
        this.shipmentLegOriginTimeZoneOffset = shipmentLegOriginTimeZoneOffset;
    }
    public String getShipmentLegOriginCity() {
        return shipmentLegOriginCity;
    }
    public void setShipmentLegOriginCity(String shipmentLegOriginCity) {
        this.shipmentLegOriginCity = shipmentLegOriginCity;
    }
    public String getShipmentLegOriginStreet() {
        return shipmentLegOriginStreet;
    }
    public void setShipmentLegOriginStreet(String shipmentLegOriginStreet) {
        this.shipmentLegOriginStreet = shipmentLegOriginStreet;
    }
    public String getShipmentLegOriginStateCode() {
        return shipmentLegOriginStateCode;
    }
    public void setShipmentLegOriginStateCode(String shipmentLegOriginStateCode) {
        this.shipmentLegOriginStateCode = shipmentLegOriginStateCode;
    }
}
