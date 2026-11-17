package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.inbound.ShipmentTrackingMessage;

public interface LTLTrackingMessageService {

    void addProNumberToTM(ShipmentTrackingMessage shipmentTrackingMessage);
}
