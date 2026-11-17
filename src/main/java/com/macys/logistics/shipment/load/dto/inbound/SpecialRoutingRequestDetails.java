package com.macys.logistics.shipment.load.dto.inbound;

import java.util.List;
import lombok.Data;

@Data
public class SpecialRoutingRequestDetails {

  private int srrTypeNumber;
  private String srrTypeDescription;
  private List<SRRRequestDetails> srrRequestDetails;
  private String carrierScac;
}
