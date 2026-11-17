package com.macys.logistics.shipment.load.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class Shipment {

    private String eventType;

    private String shipmentNumber;

    private String carrierType;

    private String destinationLocationNbr;

    private Double weight;

    private String weightUnitOfMeasure;

    private Double pieces;

    private Double volume;

    private String volumeUnitOfMeasure;

    private String containerType;

    private String readyToShipTs;

    private String vendorDunsNumber;

    private String vendorName;

    private ShipFromVendorLocation shipFromVendorLocation;

    private String latestInDcDt;

    private Double containerShippingInfoWeight;

    private Double containerShippingInfoVol;

    private Double containerShippingInfoQuantity;

    private String fomPurchaseOrderNumber;

    private String purchaseOrderNumber;

    private String carrierBillOfLadingNbr;

    private PickupWindow pickupWindow;

    private DropWindow dropWindow;

    private List<String> freightBillSpecifications;

    private SpecialRateRequest specialRateRequest;

}