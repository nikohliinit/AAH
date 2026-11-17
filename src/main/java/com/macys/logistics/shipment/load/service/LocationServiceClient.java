package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.response.LocationDTO;
import com.macys.logistics.shipment.load.dto.response.YardResponse;

import java.util.Optional;

public interface LocationServiceClient {

    LocationDTO findLocnEntityByLocnAbbr(String locnNbr);

    Optional<YardResponse> getYardInformation(AppointmentPayload apptPayload);
}
