package com.macys.logistics.shipment.load.camel.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.FindEntitiesResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadType;
import com.macys.logistics.shipment.load.dto.shipconfirm.DeliveryTrk;
import com.macys.logistics.shipment.load.dto.shipconfirm.DeliveryTrkList;
import com.macys.logistics.shipment.load.dto.shipconfirm.ShipConfirmPayload;
import com.macys.logistics.shipment.load.exception.ShipConfirmProcessingException;
import com.macys.logistics.shipment.load.exception.TmsLoadValidationException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.service.LoadEnrichmentService;
import com.macys.logistics.shipment.load.util.Constants;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Log4j2
@Component
public class ShipConfirmProcessor implements Processor {

    @Autowired
    private LoadEnrichmentService loadEnrichmentService;

    @Autowired
    LoadMapper loadMapper;

    private ObjectMapper objectMapper;


    @Override
    @Transactional
    public void process(Exchange exchange) {

        ShipConfirmPayload shipConfirmPayload;

        objectMapper = Optional.ofNullable(objectMapper).isPresent() ? objectMapper : new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String updateAppointmentShipConfirmRequest = exchange.getIn().getBody(String.class);

        if (Optional.ofNullable(updateAppointmentShipConfirmRequest).isEmpty()) {
            log.error("Ship confirm message for updating appointment is empty");
            throw new ShipConfirmProcessingException("Received empty appointment ship confirm event");
        }

        try {
            shipConfirmPayload = objectMapper.readValue(updateAppointmentShipConfirmRequest, ShipConfirmPayload.class);

            String apptNumberString = shipConfirmPayload.getFulfillmentOrder().getShipmentList().getShipment().get(0).getApptNumber();
            log.info("Processing ship confirm event for appointment number: {}", apptNumberString);
            log.debug("Received ship confirm payload: {}", shipConfirmPayload);

            FindEntitiesResponse findEntitiesResponse = loadEnrichmentService.fetchLoadId(Arrays.asList(apptNumberString), Constants.EQ);

            validateFindEntitiesResponse(findEntitiesResponse, apptNumberString);

            String loadId = findEntitiesResponse.getEntity().get(0).getStop().get(0).getSystemLoadID();
            //Find load details from TM
            FindEntitiesResponse loadDetails = loadEnrichmentService.fetchLoadDetails(loadId);
            LoadType load = loadMapper.getLoad(loadDetails);

            List<DeliveryTrk> deliveryTrks = shipConfirmPayload.getFulfillmentOrder().getShipmentList().getShipment().stream()
                    .flatMap(shipment -> Optional.ofNullable(shipment.getDeliveryTrkList())
                            .map(DeliveryTrkList::getDeliveryTrk)
                            .orElseGet(Collections::emptyList) // Handle possible null
                            .stream()).toList();
            int cartonSize = deliveryTrks.size();
            log.debug("Size of cartons shipped [cartonSize={}] for [apptNumber={}]", cartonSize, apptNumberString);

            loadEnrichmentService.triggerLoadCompleteForObtmAppointments(loadId, load, apptNumberString, cartonSize);

            log.info("Successfully processed ship confirm event for appointment number: {}", apptNumberString);

        }catch (JsonProcessingException e) {
            log.error("Exception occurred while parsing Ship Confirm Event: {}", e.getMessage());
            throw new ShipConfirmProcessingException("Exception occurred while processing Appointment Event " +e.getMessage());
        }catch (Exception e){
            log.error("Exception occurred while processing Ship Confirm Event: {}", e.getMessage());
            throw e;
        }
    }

    private void validateFindEntitiesResponse(FindEntitiesResponse findEntitiesResponse, String appointmentNumber) {

        boolean isInvalidResponse = Optional.ofNullable(findEntitiesResponse)
                .map(FindEntitiesResponse::getEntity)
                .filter(entities -> !entities.isEmpty())
                .map(entities -> entities.get(0).getStop())
                .filter(stops -> !stops.isEmpty())
                .isEmpty();

        if (isInvalidResponse) {
            log.warn("No load Id found for appointment {}", appointmentNumber);
            throw new TmsLoadValidationException("No load Id found for appointment " + appointmentNumber);
        }
    }

}
