package com.macys.logistics.shipment.load.camel.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.exception.TMSDataValidationException;
import com.macys.logistics.shipment.load.exception.TMSMappingException;
import com.macys.logistics.shipment.load.service.InBoundShipmentService;
import com.macys.logistics.shipment.load.util.CommonUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
class CreateShipmentProcessorTest {

  @InjectMocks
  CreateShipmentProcessor shipmentProcessor;

  @Mock
  InBoundShipmentService inBoundShipmentService;

  @Mock
  Exchange exchange;

  @Mock
  Message message;

  @Mock
  ObjectMapper objectMapper;

  @Mock
  CommonUtil commonUtil;


  @BeforeEach
  void init() {
    ReflectionTestUtils.setField(shipmentProcessor, "objectMapper", objectMapper);
    ReflectionTestUtils.setField(shipmentProcessor, "commonUtility", commonUtil);
    when(exchange.getIn()).thenReturn(message);
  }

  @Test
   void test_process_throws_exception() throws Exception {
    ShipmentEnterpriseMessage shipment = new ShipmentEnterpriseMessage();
    String request = "invalid";
    when(exchange.getIn().getBody(String.class)).thenReturn(request);
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(shipment);
    String exceptionMessage = "Exception while processing shipment ";
    doThrow(new TMSMappingException(exceptionMessage))
        .when(inBoundShipmentService).createShipment(shipment);
    TMSDataValidationException thrown = assertThrows(TMSDataValidationException.class,
        () -> shipmentProcessor.process(exchange));

      assertEquals("Received Invalid ShipmentDetails for shipment number " + shipment.getShipmentNumber(), thrown.getMessage());
  }

  @Test
   void test_process_valid_body() throws Exception {
    ShipmentEnterpriseMessage shipment = DataFactory.getShipmentEnterprisePayload();
    shipment.setShipmentNumber(111111L);
    when(exchange.getIn().getBody(String.class)).thenReturn("valid");
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(shipment);
    doNothing().when(inBoundShipmentService).createShipment(shipment);
    shipmentProcessor.process(exchange);
    verify(inBoundShipmentService).createShipment(shipment);
  }
}
