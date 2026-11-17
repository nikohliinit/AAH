package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ShipmentLegDestinationAddress")
public class ShipmentLegDestinationAddress {
    @XmlElement(name = "ShipmentLegDestinationCountryCode")
    private String shipmentLegDestinationCountryCode;

    @XmlElement(name = "ShipmentLegDestinationStreet")
    private String shipmentLegDestinationStreet;

    @XmlElement(name = "ShipmentLegDestinationCity")
    private String shipmentLegDestinationCity;

    @XmlElement(name = "ShipmentLegDestinationTimeZoneOffset")
    private Double shipmentLegDestinationTimeZoneOffset;

    @XmlElement(name = "ShipmentLegOriginPostalCode")
    private String shipmentLegDestinationPostalCode;

    @XmlElement(name = "ShipmentLegDestinationStateCode")
    private String shipmentLegDestinationStateCode;


    public String getShipmentLegDestinationCountryCode() {
        return shipmentLegDestinationCountryCode;
    }
    public void setShipmentLegDestinationCountryCode(String shipmentLegDestinationCountryCode) {
        this.shipmentLegDestinationCountryCode = shipmentLegDestinationCountryCode;
    }
    public String getShipmentLegDestinationStreet() {
        return shipmentLegDestinationStreet;
    }
    public void setShipmentLegDestinationStreet(String shipmentLegDestinationStreet) {
        this.shipmentLegDestinationStreet = shipmentLegDestinationStreet;
    }
    public String getShipmentLegDestinationCity() {
        return shipmentLegDestinationCity;
    }
    public void setShipmentLegDestinationCity(String shipmentLegDestinationCity) {
        this.shipmentLegDestinationCity = shipmentLegDestinationCity;
    }
    public Double getShipmentLegDestinationTimeZoneOffset() {
        return shipmentLegDestinationTimeZoneOffset;
    }
    public void setShipmentLegDestinationTimeZoneOffset(Double shipmentLegDestinationTimeZoneOffset) {
        this.shipmentLegDestinationTimeZoneOffset = shipmentLegDestinationTimeZoneOffset;
    }
    public String getShipmentLegDestinationPostalCode() {
        return shipmentLegDestinationPostalCode;
    }
    public void setShipmentLegDestinationPostalCode(String shipmentLegDestinationPostalCode) {
        this.shipmentLegDestinationPostalCode = shipmentLegDestinationPostalCode;
    }
    public String getShipmentLegDestinationStateCode() {
        return shipmentLegDestinationStateCode;
    }
    public void setShipmentLegDestinationStateCode(String shipmentLegDestinationStateCode) {
        this.shipmentLegDestinationStateCode = shipmentLegDestinationStateCode;
    }
}
