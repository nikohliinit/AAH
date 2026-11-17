package com.macys.logistics.shipment.load.camel.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.exception.TMSDataValidationException;
import com.macys.logistics.shipment.load.exception.TMSMappingException;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;
@Component
@Log4j2
public class ShipmentEventProcessor implements Processor {
  public static final String EVENT_TYPE = "eventType";
  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public void process(Exchange exchange) throws Exception {
    Long shipmentNumber=null;
    try {
      String request = exchange.getIn().getBody(String.class);
      ShipmentEnterpriseMessage shipment = objectMapper.readValue(request, ShipmentEnterpriseMessage.class);
      shipmentNumber = shipment.getShipmentNumber();
      log.info("Processing shipment event for shipment number: {}", shipmentNumber);
      log.debug("Received shipment event payload: {}", request);

      if (!validateShipmentNumber(shipment)) {
        log.warn("Invalid shipment number {} with shipment details {}", shipmentNumber ,shipment.getShipmentDetails());
        throw new TMSDataValidationException(
                "Invalid shipment number - " + shipmentNumber);
      }else if (StringUtils.isBlank(shipment.getEventType())) {
        log.warn("Invalid shipment eventType {} for shipment number {}", shipment.getEventType(), shipmentNumber);
        throw new TMSDataValidationException(
            "Invalid shipment eventType " + shipment.getEventType() + " for Shipment Number - " + shipmentNumber);
      }  else {
        log.debug("Shipment Enterprise message payload {} ", objectMapper.writeValueAsString(shipment));
        exchange.getIn().setHeader(EVENT_TYPE, shipment.getEventType());
        log.debug("eventType {}", exchange.getIn().getHeader(EVENT_TYPE));
        log.info("FindShipmentEvent process ends {}", shipmentNumber);
      }
    }
    catch (TMSDataValidationException tmsDataValidationException){
      throw tmsDataValidationException;
    }
    catch (Exception e) {
      log.error("Exception while processing shipment number {}. Failure reason {}", shipmentNumber, e.getMessage());
      throw new TMSMappingException("Exception while processing shipment number " + shipmentNumber + ". Failure Reason - " + e.getMessage());
    }
  }
  /**
   * Is Shipment valid for creating shipment
   * @param shipment shipmentMsg
   *
   */
  private boolean validateShipmentNumber(ShipmentEnterpriseMessage shipment) {
    return Optional.ofNullable(shipment)
        .filter(sh -> sh.getShipmentNumber() != null &&  sh.getShipmentNumber() >0)
        .isPresent();
  }
}