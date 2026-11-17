package com.macys.logistics.shipment.load.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ShipmentContainer")
public class ShipmentContainer {
    @XmlElement(name = "ContainerShippingInfo")
    private ContainerShippingInfo containerShippingInfo;

    @XmlElement(name = "SystemItemMasterID")
    private Integer systemItemMasterID;

    @XmlElement(name = "CarrierPackagingTypeVal")
    private Integer carrierPackagingTypeVal;

    @XmlElement(name = "ItemPackageLevelCode")
    private Integer itemPackageLevelCode;

    @XmlElement(name = "ContainerDescription")
    private String containerDescription;

    @XmlElement(name = "ContainerHeight")
    private Double containerHeight;

    @XmlElement(name = "ContainerLength")
    private Double containerLength;

    @XmlElement(name = "SplitMethodEnumVal")
    private String splitMethodEnumVal;

    @XmlElement(name = "ContentLevel")
    private Integer contentLevel;

    @XmlElement(name = "Priority")
    private Integer priority;

    @XmlElement(name = "ContainerType")
    private ContainerType containerType;

    @XmlElement(name = "ItemType")
    private Integer itemType;

    @XmlElement(name = "ContainerMarkedForLocationEnumVal")
    private Integer containerMarkedForLocationEnumVal;

    @XmlElement(name = "ContainerQuantity")
    private Double containerQuantity;

    @XmlElement(name = "ContainerWidth")
    private Double containerWidth;

    @XmlElement(name = "IsItemPackagingCommitted")
    private String isItemPackagingCommitted;

    @XmlElement(name = "ContainerId")
    private Integer containerId;

    @XmlElement(name = "StackingRuleEnumVal")
    private Integer stackingRuleEnumVal;

    @XmlElement(name = "ContentQuantity")
    private Double contentQuantity;

    @XmlElement(name = "ContainerRatingUnitType")
    private String containerRatingUnitType;

    @XmlElement(name = "ContainerCommodityCodeDetail")
    private ContainerCommodityCodeDetail containerCommodityCodeDetail;

    @XmlElement(name = "ContainerReferenceNumberList")
    private List<ContainerReferenceNumberList> containerReferenceNumberList;

    @XmlElement(name = "Is3DLoadingRequired")
    private String is3DLoadingRequired;

    public ContainerShippingInfo getContainerShippingInfo() {
        return containerShippingInfo;
    }
    public void setContainerShippingInfo(ContainerShippingInfo containerShippingInfo) {
        this.containerShippingInfo = containerShippingInfo;
    }
    public Integer getSystemItemMasterID() {
        return systemItemMasterID;
    }
    public void setSystemItemMasterID(Integer systemItemMasterID) {
        this.systemItemMasterID = systemItemMasterID;
    }
    public Integer getCarrierPackagingTypeVal() {
        return carrierPackagingTypeVal;
    }
    public void setCarrierPackagingTypeVal(Integer carrierPackagingTypeVal) {
        this.carrierPackagingTypeVal = carrierPackagingTypeVal;
    }
    public Integer getItemPackageLevelCode() {
        return itemPackageLevelCode;
    }
    public void setItemPackageLevelCode(Integer itemPackageLevelCode) {
        this.itemPackageLevelCode = itemPackageLevelCode;
    }
    public String getContainerDescription() {
        return containerDescription;
    }
    public void setContainerDescription(String containerDescription) {
        this.containerDescription = containerDescription;
    }
    public Double getContainerHeight() {
        return containerHeight;
    }
    public void setContainerHeight(Double containerHeight) {
        this.containerHeight = containerHeight;
    }
    public Double getContainerLength() {
        return containerLength;
    }
    public void setContainerLength(Double containerLength) {
        this.containerLength = containerLength;
    }
    public String getSplitMethodEnumVal() {
        return splitMethodEnumVal;
    }
    public void setSplitMethodEnumVal(String splitMethodEnumVal) {
        this.splitMethodEnumVal = splitMethodEnumVal;
    }
    public Integer getContentLevel() {
        return contentLevel;
    }
    public void setContentLevel(Integer contentLevel) {
        this.contentLevel = contentLevel;
    }
    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    public ContainerType getContainerType() {
        return containerType;
    }
    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }
    public Integer getItemType() {
        return itemType;
    }
    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }
    public Integer getContainerMarkedForLocationEnumVal() {
        return containerMarkedForLocationEnumVal;
    }
    public void setContainerMarkedForLocationEnumVal(Integer containerMarkedForLocationEnumVal) {
        this.containerMarkedForLocationEnumVal = containerMarkedForLocationEnumVal;
    }
    public Double getContainerQuantity() {
        return containerQuantity;
    }
    public void setContainerQuantity(Double containerQuantity) {
        this.containerQuantity = containerQuantity;
    }
    public Double getContainerWidth() {
        return containerWidth;
    }
    public void setContainerWidth(Double containerWidth) {
        this.containerWidth = containerWidth;
    }
    public String getIsItemPackagingCommitted() {
        return isItemPackagingCommitted;
    }
    public void setIsItemPackagingCommitted(String isItemPackagingCommitted) {
        this.isItemPackagingCommitted = isItemPackagingCommitted;
    }
    public Integer getContainerId() {
        return containerId;
    }
    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
    }
    public Integer getStackingRuleEnumVal() {
        return stackingRuleEnumVal;
    }
    public void setStackingRuleEnumVal(Integer stackingRuleEnumVal) {
        this.stackingRuleEnumVal = stackingRuleEnumVal;
    }
    public Double getContentQuantity() {
        return contentQuantity;
    }
    public void setContentQuantity(Double contentQuantity) {
        this.contentQuantity = contentQuantity;
    }
    public String getContainerRatingUnitType() {
        return containerRatingUnitType;
    }
    public void setContainerRatingUnitType(String containerRatingUnitType) {
        this.containerRatingUnitType = containerRatingUnitType;
    }
    public ContainerCommodityCodeDetail getContainerCommodityCodeDetail() {
        return containerCommodityCodeDetail;
    }
    public void setContainerCommodityCodeDetail(ContainerCommodityCodeDetail containerCommodityCodeDetail) {
        this.containerCommodityCodeDetail = containerCommodityCodeDetail;
    }
    public List<ContainerReferenceNumberList> getContainerReferenceNumberList() {
        return containerReferenceNumberList;
    }
    public void setContainerReferenceNumberList(List<ContainerReferenceNumberList> containerReferenceNumberList) {
        this.containerReferenceNumberList = containerReferenceNumberList;
    }
    public String getIs3DLoadingRequired() {
        return is3DLoadingRequired;
    }
    public void setIs3DLoadingRequired(String is3DLoadingRequired) {
        this.is3DLoadingRequired = is3DLoadingRequired;
    }
}
