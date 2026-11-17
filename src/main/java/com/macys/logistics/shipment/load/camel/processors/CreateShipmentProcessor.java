package com.macys.logistics.shipment.load.camel.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.exception.TMSCreateAPIFailureException;
import com.macys.logistics.shipment.load.exception.TMSDataValidationException;
import com.macys.logistics.shipment.load.exception.TMSMappingException;
import com.macys.logistics.shipment.load.service.InBoundShipmentService;
import com.macys.logistics.shipment.load.util.CommonUtil;
import com.macys.logistics.shipment.load.util.Constants;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class CreateShipmentProcessor implements Processor {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private InBoundShipmentService inBoundShipmentService;
  @Autowired
  CommonUtil commonUtility;

  @Override
  public void process(Exchange exchange) throws Exception {
    Long shipmentNumber = null;
    try {
      String request = exchange.getIn().getBody(String.class);
      ShipmentEnterpriseMessage shipment = objectMapper.readValue(request, ShipmentEnterpriseMessage.class);
      shipmentNumber = shipment.getShipmentNumber();

      log.info("Processing create shipment event for shipment number: {}", shipmentNumber);
      log.debug("Received create shipment payload: {}", request);

      if (shipment.getShipmentDetails()==null || StringUtils.isBlank(shipment.getShipmentDetails().toString())) {
        log.error("Received Invalid ShipmentDetails for shipment message {}", shipmentNumber);
        throw new TMSDataValidationException(
            "Received Invalid ShipmentDetails for shipment number " + shipmentNumber);
      }
      inBoundShipmentService.createShipment(shipment);
      commonUtility.createAndPublishFeedbackAuditMessage(Constants.SHIPMENT_LOAD_CREATE_SUCCESS_ACTION,request);
      log.info("Shipment creation process ends for shipment number {}",shipmentNumber);
    }
    catch (TMSDataValidationException | TMSCreateAPIFailureException exception){
      throw exception;
    }
    catch (Exception e) {
      log.error("Exception while creating shipment with shipment Number - {}. Failure Reason - {}", shipmentNumber, e.getMessage());
      throw new TMSMappingException("Exception while creating shipment with shipment Number - " + shipmentNumber + ". Failure Reason - " + e.getMessage());
    }
  }
}
