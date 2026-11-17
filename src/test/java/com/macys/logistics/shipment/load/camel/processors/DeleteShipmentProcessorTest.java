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
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
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
class DeleteShipmentProcessorTest {

  @InjectMocks
  DeleteShipmentProcessor deleteShipmentProcessor;

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
    ReflectionTestUtils.setField(deleteShipmentProcessor, "objectMapper", objectMapper);
    ReflectionTestUtils.setField(deleteShipmentProcessor, "commonUtil", commonUtil);
    when(exchange.getIn()).thenReturn(message);
  }

  @Test
  void test_process_throws_exception() throws Exception {
    ShipmentEnterpriseMessage shipment = new ShipmentEnterpriseMessage();
    shipment.setShipmentNumber(null);
    String request = "invalid";
    when(exchange.getIn().getBody(String.class)).thenReturn(request);
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(shipment);
    String exceptionMessage = "Exception while processing shipment";
    doThrow(new TMSMappingException(exceptionMessage))
        .when(inBoundShipmentService).deleteShipment(shipment);
    TMSMappingException thrown = assertThrows(TMSMappingException.class,
        () -> deleteShipmentProcessor.process(exchange));
    assertEquals("Exception while deleting shipment number - " + shipment.getShipmentNumber() + ". Failure Reason " + exceptionMessage, thrown.getMessage());
  }
  @Test
  void test_process_valid() throws Exception {
    ShipmentEnterpriseMessage shipment = new ShipmentEnterpriseMessage();
    shipment.setShipmentNumber(45576890L);
    String request = "valid";
    when(exchange.getIn().getBody(String.class)).thenReturn(request);
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(shipment);
    doNothing().when(inBoundShipmentService).deleteShipment(shipment);
    deleteShipmentProcessor.process(exchange);
    verify(inBoundShipmentService).deleteShipment(shipment);
  }

}
