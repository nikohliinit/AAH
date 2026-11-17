package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ShipmentLeg")
public class ShipmentLeg {
    @XmlElement(name = "ShipmentLegDestinationCode")
    private String shipmentLegDestinationCode;

    @XmlElement(name = "SystemPickUpStopID")
    private Integer systemPickUpStopID;

    @XmlElement(name = "SystemShipmentLegID")
    private Integer systemShipmentLegID;

    @XmlElement(name = "ShipmentLegDestinationShippingLocation")
    private ShipmentLegDestinationShippingLocation shipmentLegDestinationShippingLocation;

    @XmlElement(name = "ShipmentItinerarySeqNum")
    private Integer shipmentItinerarySeqNum;

    @XmlElement(name = "ShipmentLegOriginCode")
    private String shipmentLegOriginCode;

    @XmlElement(name = "ShipmentLegDestinationEnumVal")
    private String shipmentLegDestinationEnumVal;

    @XmlElement(name = "ShipmentLegOriginShippingLocation")
    private ShipmentLegOriginShippingLocation shipmentLegOriginShippingLocation;

    @XmlElement(name = "ShipmentLegOriginEnumVal")
    private String shipmentLegOriginEnumVal;

    @XmlElement(name = "ShipmentLegDestinationAddress")
    private ShipmentLegDestinationAddress shipmentLegDestinationAddress;

    @XmlElement(name = "SystemDropStopID")
    private Integer systemDropStopID;

    @XmlElement(name = "ShipmentLegOriginAddress")
    private ShipmentLegOriginAddress shipmentLegOriginAddress;

    public String getShipmentLegDestinationCode() {
        return shipmentLegDestinationCode;
    }
    public void setShipmentLegDestinationCode(String shipmentLegDestinationCode) {
        this.shipmentLegDestinationCode = shipmentLegDestinationCode;
    }
    public Integer getSystemPickUpStopID() {
        return systemPickUpStopID;
    }
    public void setSystemPickUpStopID(Integer systemPickUpStopID) {
        this.systemPickUpStopID = systemPickUpStopID;
    }
    public Integer getSystemShipmentLegID() {
        return systemShipmentLegID;
    }
    public void setSystemShipmentLegID(Integer systemShipmentLegID) {
        this.systemShipmentLegID = systemShipmentLegID;
    }
    public ShipmentLegDestinationShippingLocation getShipmentLegDestinationShippingLocation() {
        return shipmentLegDestinationShippingLocation;
    }
    public void setShipmentLegDestinationShippingLocation(ShipmentLegDestinationShippingLocation shipmentLegDestinationShippingLocation) {
        this.shipmentLegDestinationShippingLocation = shipmentLegDestinationShippingLocation;
    }
    public Integer getShipmentItinerarySeqNum() {
        return shipmentItinerarySeqNum;
    }
    public void setShipmentItinerarySeqNum(Integer shipmentItinerarySeqNum) {
        this.shipmentItinerarySeqNum = shipmentItinerarySeqNum;
    }
    public String getShipmentLegOriginCode() {
        return shipmentLegOriginCode;
    }
    public void setShipmentLegOriginCode(String shipmentLegOriginCode) {
        this.shipmentLegOriginCode = shipmentLegOriginCode;
    }
    public String getShipmentLegDestinationEnumVal() {
        return shipmentLegDestinationEnumVal;
    }
    public void setShipmentLegDestinationEnumVal(String shipmentLegDestinationEnumVal) {
        this.shipmentLegDestinationEnumVal = shipmentLegDestinationEnumVal;
    }
    public ShipmentLegOriginShippingLocation getShipmentLegOriginShippingLocation() {
        return shipmentLegOriginShippingLocation;
    }
    public void setShipmentLegOriginShippingLocation(ShipmentLegOriginShippingLocation shipmentLegOriginShippingLocation) {
        this.shipmentLegOriginShippingLocation = shipmentLegOriginShippingLocation;
    }
    public String getShipmentLegOriginEnumVal() {
        return shipmentLegOriginEnumVal;
    }
    public void setShipmentLegOriginEnumVal(String shipmentLegOriginEnumVal) {
        this.shipmentLegOriginEnumVal = shipmentLegOriginEnumVal;
    }
    public ShipmentLegDestinationAddress getShipmentLegDestinationAddress() {
        return shipmentLegDestinationAddress;
    }
    public void setShipmentLegDestinationAddress(ShipmentLegDestinationAddress shipmentLegDestinationAddress) {
        this.shipmentLegDestinationAddress = shipmentLegDestinationAddress;
    }
    public Integer getSystemDropStopID() {
        return systemDropStopID;
    }
    public void setSystemDropStopID(Integer systemDropStopID) {
        this.systemDropStopID = systemDropStopID;
    }
    public ShipmentLegOriginAddress getShipmentLegOriginAddress() {
        return shipmentLegOriginAddress;
    }
    public void setShipmentLegOriginAddress(ShipmentLegOriginAddress shipmentLegOriginAddress) {
        this.shipmentLegOriginAddress = shipmentLegOriginAddress;
    }
}
