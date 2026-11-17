package com.macys.logistics.shipment.load.dto.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShipmentEnterpriseMessage {

  private ShipmentDetails shipmentDetails;
  private String eventType;
  private Long shipmentNumber;
}
