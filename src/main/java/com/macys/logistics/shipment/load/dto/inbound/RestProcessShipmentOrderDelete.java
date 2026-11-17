package com.macys.logistics.shipment.load.dto.inbound;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RestProcessShipmentOrderDelete {

  private List<RestShipmentDelete> shipment;
}
