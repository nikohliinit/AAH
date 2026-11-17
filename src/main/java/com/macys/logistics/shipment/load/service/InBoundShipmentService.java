package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;

public interface InBoundShipmentService {

 void createShipment(ShipmentEnterpriseMessage shipmentEnterpriseMessage);
 void deleteShipment(ShipmentEnterpriseMessage shipmentEnterpriseMessage);

}
