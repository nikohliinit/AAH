package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrders {

  private Long poNumber;
  private int divisionNumber;
  private int departmentNumber;
  private String inDcDate;
  private String cancelDate;
  private String poStatus;
  private String poType;
}
