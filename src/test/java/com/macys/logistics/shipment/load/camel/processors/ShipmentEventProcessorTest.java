package com.macys.logistics.shipment.load.camel.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.dto.inbound.EventType;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.exception.TMSDataValidationException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ShipmentEventProcessorTest {

  @InjectMocks
  ShipmentEventProcessor shipmentEventProcessor;
  @Mock
  Exchange exchange;

  @Mock
  Message message;

  @Mock
  ObjectMapper objectMapper;

  @BeforeEach
  void init() {
    ReflectionTestUtils.setField(shipmentEventProcessor, "objectMapper", objectMapper);
    when(exchange.getIn()).thenReturn(message);
  }

  @Test
  void test_process_throws_exception() throws Exception {
    ShipmentEnterpriseMessage shipment = new ShipmentEnterpriseMessage();
    shipment.setShipmentNumber(7777L);
    String request = "invalid";
    when(exchange.getIn().getBody(String.class)).thenReturn(request);
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(
        shipment);

    TMSDataValidationException thrown = assertThrows(TMSDataValidationException.class,
        () -> shipmentEventProcessor.process(exchange));
    assertEquals("Invalid shipment eventType "+ shipment.getEventType()+ " for Shipment Number - " + shipment.getShipmentNumber(), thrown.getMessage());
  }

  @Test
  void test_process_valid_body() throws Exception {
    ShipmentEnterpriseMessage shipment = DataFactory.getShipmentEnterprisePayload();
    shipment.setShipmentNumber(777L);
    shipment.setEventType(String.valueOf(EventType.CREATE));
    when(exchange.getIn().getBody(String.class)).thenReturn("valid");
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(
        shipment);
    shipmentEventProcessor.process(exchange);
    verify(exchange, never()).setProperty(
        eq("eventType"), any(String.class));
  }

  @Test
  void test_process_invalid_event_type() throws Exception {
    ShipmentEnterpriseMessage shipment = new ShipmentEnterpriseMessage();
    shipment.setShipmentNumber(778L);
    shipment.setEventType(null);
    when(exchange.getIn().getBody(String.class)).thenReturn("valid");
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(
        shipment);

    TMSDataValidationException thrown = assertThrows(TMSDataValidationException.class,
            () -> shipmentEventProcessor.process(exchange));
    assertEquals("Invalid shipment eventType "+ shipment.getEventType()+ " for Shipment Number - " + shipment.getShipmentNumber(), thrown.getMessage());
  }

  @Test
  void test_process_invalid_event_type2() throws Exception {
    ShipmentEnterpriseMessage shipment = new ShipmentEnterpriseMessage();
    shipment.setShipmentNumber(769L);
    shipment.setEventType("    ");
    when(exchange.getIn().getBody(String.class)).thenReturn("valid");
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(
        shipment);

    TMSDataValidationException thrown = assertThrows(TMSDataValidationException.class,
            () -> shipmentEventProcessor.process(exchange));
    assertEquals("Invalid shipment eventType "+ shipment.getEventType()+ " for Shipment Number - " + shipment.getShipmentNumber(), thrown.getMessage());
  }

  @Test
  void test_process_invalid_shipmentNbr_exception() throws Exception {
    ShipmentEnterpriseMessage shipment = DataFactory.getShipmentEnterprisePayload();
    shipment.setShipmentNumber(null);
    shipment.setEventType(String.valueOf(EventType.DELETE));
    String request = "invalid";
    when(exchange.getIn().getBody(String.class)).thenReturn(request);
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(
        shipment);
    assertThrows(TMSDataValidationException.class, () -> shipmentEventProcessor.process(exchange));
  }

  @Test
  void test_process_invalid_shipmentNbr_exception_2() throws Exception {
    ShipmentEnterpriseMessage shipment = DataFactory.getShipmentEnterprisePayload();
    shipment.setShipmentNumber(-1L);
    shipment.setEventType(String.valueOf(EventType.CREATE));
    String request = "invalid";
    when(exchange.getIn().getBody(String.class)).thenReturn(request);
    when(objectMapper.readValue(anyString(), eq(ShipmentEnterpriseMessage.class))).thenReturn(
        shipment);
    assertThrows(TMSDataValidationException.class, () -> shipmentEventProcessor.process(exchange));
  }
}
