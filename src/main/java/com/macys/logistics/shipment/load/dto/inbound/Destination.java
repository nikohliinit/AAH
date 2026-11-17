package com.macys.logistics.shipment.load.dto.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination {

  private String locationAbbreviation;
  private int locationNbr;
}
