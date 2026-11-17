package com.macys.logistics.shipment.load.camel.processors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.exception.TMSDeleteAPIFailureException;
import com.macys.logistics.shipment.load.exception.TMSMappingException;
import com.macys.logistics.shipment.load.service.InBoundShipmentService;
import com.macys.logistics.shipment.load.util.CommonUtil;
import com.macys.logistics.shipment.load.util.Constants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;
@Component
@Log4j2
public class DeleteShipmentProcessor implements Processor {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private InBoundShipmentService inBoundShipmentService;
  @Autowired
  CommonUtil commonUtil;
  @Override
  public void process(Exchange exchange) throws Exception {
    Long shipmentNumber = null;
    try {
     String request = exchange.getIn().getBody(String.class);
      ShipmentEnterpriseMessage shipment = objectMapper.readValue(request,
          ShipmentEnterpriseMessage.class);
      shipmentNumber = shipment.getShipmentNumber();
      log.info("DeleteShipmentProcessor process() started for shipment number: {}", shipmentNumber);
      log.debug("Received delete shipment payload: {}", request);
      inBoundShipmentService.deleteShipment(shipment);
      commonUtil.createAndPublishFeedbackAuditMessage(Constants.SHIPMENT_LOAD_DELETE_SUCCESS_ACTION,request);
      log.info("Shipment deletion process ends for shipment number {}",shipmentNumber);
    }
    catch (TMSDeleteAPIFailureException exception){
      throw exception;
    }
    catch (Exception e) {
      log.error("Error: Exception while deleting shipment number: {}. Failure Reason - {}", shipmentNumber, e.getMessage());
      throw new TMSMappingException("Exception while deleting shipment number - " + shipmentNumber + ". Failure Reason " + e.getMessage(),e);
    }
  }
}
