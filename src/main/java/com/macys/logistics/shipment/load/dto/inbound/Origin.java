package com.macys.logistics.shipment.load.dto.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Origin {

  private String locationId;
  private String locationName;
  private String addressLine;
  private String state;
  private String city;
  private String zip;
}
