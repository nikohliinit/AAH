package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentDetails {

  private int vendorNumber;
  private String vendorName;
  private Long vendorDunsNumber;
  private Origin origin;
  private Destination destination;
  private String readyToShipDate;
  private Double totalCube;
  private Double totalWeight;
  private Long totalCarton;
  private Long filledQuantity;
  private Long totalPallet;
  private String freightClassName;
  private List<PurchaseOrders> purchaseOrders;
  private SpecialRoutingRequestDetails specialRoutingRequestDetails;
  private String createdBy;
  private String commodityDesc;

}