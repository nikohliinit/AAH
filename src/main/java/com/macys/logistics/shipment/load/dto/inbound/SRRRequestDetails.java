package com.macys.logistics.shipment.load.dto.inbound;

import lombok.Data;

@Data
public class SRRRequestDetails {

  private String labelName;
  private String userInputValue;
  private int labelNumber;
}
