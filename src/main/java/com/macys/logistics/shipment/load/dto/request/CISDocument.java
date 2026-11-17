package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CISDocument")
public class CISDocument {
    @XmlElement(name = "EventName")
    private String eventName;

    @XmlElement(name = "ReasonCode")
    private String reasonCode;

    @XmlElement(name = "ReasonCodeDescription")
    private String reasonCodeDescription;

    @XmlElement(name = "SystemTimeZoneOffset")
    private Double systemTimeZoneOffset;

    @XmlElement(name = "EventDateTime")
    private String eventDateTime;

    @XmlElement(name = "EventReportedDateTime")
    private String eventReportedDateTime;

    @XmlElement(name = "EventOccurredDateTime")
    private String eventOccurredDateTime;

    @XmlElement(name = "EventOccurredTimeZoneOffset")
    private Double eventOccurredTimeZoneOffset;

    @XmlElement(name = "EventUser")
    private String eventUser;

    @XmlElement(name = "SystemShipmentID")
    private Integer systemShipmentID;

    @XmlElement(name = "SystemShipmentNumber")
    private String systemShipmentNumber;

    @XmlElement(name = "DivisionCode")
    private String divisionCode;

    @XmlElement(name = "LogisticsGroup")
    private String logisticsGroup;

    @XmlElement(name = "OriginCode")
    private String originCode;

    @XmlElement(name = "OriginTypeEnumVal")
    private String originTypeEnumVal;

    @XmlElement(name = "OriginName")
    private String originName;

    @XmlElement(name = "OriginAddress")
    private OriginAddress originAddress;

    @XmlElement(name = "OriginShippingLocation")
    private OriginShippingLocation originShippingLocation;

    @XmlElement(name = "DestinationCode")
    private String destinationCode;

    @XmlElement(name = "DestinationTypeEnumVal")
    private String destinationTypeEnumVal;

    @XmlElement(name = "DestinationName")
    private String destinationName;

    @XmlElement(name = "DestinationAddress")
    private DestinationAddress destinationAddress;

    @XmlElement(name = "DestinationShippingLocation")
    private DestinationShippingLocation destinationShippingLocation;

    @XmlElement(name = "CustomerCode")
    private Integer customerCode;

    @XmlElement(name = "BillToCustomerCode")
    private Integer billToCustomerCode;

    @XmlElement(name = "CSR")
    private Integer csr;

    @XmlElement(name = "ShipmentInfo")
    private ShipmentInfo shipmentInfo;

    @XmlElement(name = "UOM")
    private UOM uom;

    @XmlElement(name = "OnHold")
    private String onHold;

    @XmlElement(name = "Urgent")
    private String urgent;

    @XmlElement(name = "CommodityCode")
    private String commodityCode;

    @XmlElement(name = "FreightTerm")
    private String freightTerm;

    @XmlElement(name = "Customer")
    private Customer customer;

    @XmlElement(name = "BillToCustomer")
    private BillToCustomer billToCustomer;

    @XmlElement(name = "PickupFromDateTime")
    private String pickupFromDateTime;

    @XmlElement(name = "PickupToDateTime")
    private String pickupToDateTime;

    @XmlElement(name = "DeliveryFromDateTime")
    private String deliveryFromDateTime;

    @XmlElement(name = "DeliveryToDateTime")
    private String deliveryToDateTime;

    @XmlElement(name = "ShipmentCreationDateTime")
    private String shipmentCreationDateTime;

    @XmlElement(name = "ShipmentReferenceNumberList")
    private List<ShipmentReferenceNumberList> shipmentReferenceNumberList;

    @XmlElement(name = "ShipmentContainer")
    private ShipmentContainer shipmentContainer;

    @XmlElement(name = "ShipmentLeg")
    private ShipmentLeg shipmentLeg;

    @XmlElement(name = "SplitMethod")
    private String splitMethod;

    @XmlElement(name = "NumberRelatedSplitShipments")
    private Integer numberRelatedSplitShipments;

    public ShipmentLeg getShipmentLeg() {
        return shipmentLeg;
    }
    public void setShipmentLeg(ShipmentLeg shipmentLeg) {
        this.shipmentLeg = shipmentLeg;
    }
    public String getDeliveryToDateTime() {
        return deliveryToDateTime;
    }
    public void setDeliveryToDateTime(String deliveryToDateTime) {
        this.deliveryToDateTime = deliveryToDateTime;
    }
    public String getSplitMethod() {
        return splitMethod;
    }
    public void setSplitMethod(String splitMethod) {
        this.splitMethod = splitMethod;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public String getEventDateTime() {
        return eventDateTime;
    }
    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
    public String getDestinationCode() {
        return destinationCode;
    }
    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }
    public Integer getSystemShipmentID() {
        return systemShipmentID;
    }
    public void setSystemShipmentID(Integer systemShipmentID) {
        this.systemShipmentID = systemShipmentID;
    }
    public OriginAddress getOriginAddress() {
        return originAddress;
    }
    public void setOriginAddress(OriginAddress originAddress) {
        this.originAddress = originAddress;
    }
    public ShipmentInfo getShipmentInfo() {
        return shipmentInfo;
    }
    public void setShipmentInfo(ShipmentInfo shipmentInfo) {
        this.shipmentInfo = shipmentInfo;
    }
    public OriginShippingLocation getOriginShippingLocation() {
        return originShippingLocation;
    }
    public void setOriginShippingLocation(OriginShippingLocation originShippingLocation) {
        this.originShippingLocation = originShippingLocation;
    }
    public String getShipmentCreationDateTime() {
        return shipmentCreationDateTime;
    }
    public void setShipmentCreationDateTime(String shipmentCreationDateTime) {
        this.shipmentCreationDateTime = shipmentCreationDateTime;
    }
    public String getDivisionCode() {
        return divisionCode;
    }
    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }
    public String getPickupFromDateTime() {
        return pickupFromDateTime;
    }
    public void setPickupFromDateTime(String pickupFromDateTime) {
        this.pickupFromDateTime = pickupFromDateTime;
    }
    public String getFreightTerm() {
        return freightTerm;
    }
    public void setFreightTerm(String freightTerm) {
        this.freightTerm = freightTerm;
    }
    public String getEventReportedDateTime() {
        return eventReportedDateTime;
    }
    public void setEventReportedDateTime(String eventReportedDateTime) {
        this.eventReportedDateTime = eventReportedDateTime;
    }
    public String getDestinationName() {
        return destinationName;
    }
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
    public String getCommodityCode() {
        return commodityCode;
    }
    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }
    public BillToCustomer getBillToCustomer() {
        return billToCustomer;
    }
    public void setBillToCustomer(BillToCustomer billToCustomer) {
        this.billToCustomer = billToCustomer;
    }
    public ShipmentContainer getShipmentContainer() {
        return shipmentContainer;
    }
    public void setShipmentContainer(ShipmentContainer shipmentContainer) {
        this.shipmentContainer = shipmentContainer;
    }
    public String getDeliveryFromDateTime() {
        return deliveryFromDateTime;
    }
    public void setDeliveryFromDateTime(String deliveryFromDateTime) {
        this.deliveryFromDateTime = deliveryFromDateTime;
    }
    public String getOnHold() {
        return onHold;
    }
    public void setOnHold(String onHold) {
        this.onHold = onHold;
    }
    public Double getEventOccurredTimeZoneOffset() {
        return eventOccurredTimeZoneOffset;
    }
    public void setEventOccurredTimeZoneOffset(Double eventOccurredTimeZoneOffset) {
        this.eventOccurredTimeZoneOffset = eventOccurredTimeZoneOffset;
    }
    public String getLogisticsGroup() {
        return logisticsGroup;
    }
    public void setLogisticsGroup(String logisticsGroup) {
        this.logisticsGroup = logisticsGroup;
    }
    public List<ShipmentReferenceNumberList> getShipmentReferenceNumberList() {
        return shipmentReferenceNumberList;
    }
    public void setShipmentReferenceNumberList(List<ShipmentReferenceNumberList> shipmentReferenceNumberList) {
        this.shipmentReferenceNumberList = shipmentReferenceNumberList;
    }
    public Integer getCSR() {
        return csr;
    }

    public void setCSR(Integer csr) {
        this.csr = csr;
    }
    public String getUrgent() {
        return urgent;
    }
    public void setUrgent(String urgent) {
        this.urgent = urgent;
    }
    public String getPickupToDateTime() {
        return pickupToDateTime;
    }
    public void setPickupToDateTime(String pickupToDateTime) {
        this.pickupToDateTime = pickupToDateTime;
    }
    public Integer getCustomerCode() {
        return customerCode;
    }
    public void setCustomerCode(Integer customerCode) {
        this.customerCode = customerCode;
    }
    public String getOriginCode() {
        return originCode;
    }
    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }
    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public Integer getBillToCustomerCode() {
        return billToCustomerCode;
    }
    public void setBillToCustomerCode(Integer billToCustomerCode) {
        this.billToCustomerCode = billToCustomerCode;
    }
    public String getReasonCode() {
        return reasonCode;
    }
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }
    public String getSystemShipmentNumber() {
        return systemShipmentNumber;
    }
    public void setSystemShipmentNumber(String systemShipmentNumber) {
        this.systemShipmentNumber = systemShipmentNumber;
    }
    public String getDestinationTypeEnumVal() {
        return destinationTypeEnumVal;
    }
    public void setDestinationTypeEnumVal(String destinationTypeEnumVal) {
        this.destinationTypeEnumVal = destinationTypeEnumVal;
    }
    public String getEventOccurredDateTime() {
        return eventOccurredDateTime;
    }
    public void setEventOccurredDateTime(String eventOccurredDateTime) {
        this.eventOccurredDateTime = eventOccurredDateTime;
    }
    public DestinationAddress getDestinationAddress() {
        return destinationAddress;
    }
    public void setDestinationAddress(DestinationAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
    public Integer getNumberRelatedSplitShipments() {
        return numberRelatedSplitShipments;
    }
    public void setNumberRelatedSplitShipments(Integer numberRelatedSplitShipments) {
        this.numberRelatedSplitShipments = numberRelatedSplitShipments;
    }
    public String getOriginName() {
        return originName;
    }
    public void setOriginName(String originName) {
        this.originName = originName;
    }
    public UOM getUOM() {
        return uom;
    }
    public void setUOM(UOM uOM) {
        this.uom = uOM;
    }
    public String getReasonCodeDescription() {
        return reasonCodeDescription;
    }
    public void setReasonCodeDescription(String reasonCodeDescription) {
        this.reasonCodeDescription = reasonCodeDescription;
    }
    public DestinationShippingLocation getDestinationShippingLocation() {
        return destinationShippingLocation;
    }
    public void setDestinationShippingLocation(DestinationShippingLocation destinationShippingLocation) {
        this.destinationShippingLocation = destinationShippingLocation;
    }
    public Double getSystemTimeZoneOffset() {
        return systemTimeZoneOffset;
    }
    public void setSystemTimeZoneOffset(Double systemTimeZoneOffset) {
        this.systemTimeZoneOffset = systemTimeZoneOffset;
    }
    public String getEventUser() {
        return eventUser;
    }
    public void setEventUser(String eventUser) {
        this.eventUser = eventUser;
    }
    public String getOriginTypeEnumVal() {
        return originTypeEnumVal;
    }
    public void setOriginTypeEnumVal(String originTypeEnumVal) {
        this.originTypeEnumVal = originTypeEnumVal;
    }
}
