package com.macys.logistics.shipment.load.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestProcessShipmentOrderCreate;
import com.macys.logistics.shipment.load.dto.inbound.RestProcessShipmentOrderDelete;
import com.macys.logistics.shipment.load.dto.inbound.RestShipmentDelete;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.exception.NotFoundException;
import com.macys.logistics.shipment.load.exception.TMSCreateAPIFailureException;
import com.macys.logistics.shipment.load.exception.TMSDeleteAPIFailureException;
import com.macys.logistics.shipment.load.exception.TMSMappingException;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InBoundShipmentServiceImplTest {

  @InjectMocks
  InBoundShipmentServiceImpl inBoundShipmentService;
  @Mock
  TMRestApiClient tmRestApiClient;
  private ShipmentEnterpriseMessage shipmentEnterpriseMessage;

  @Test
  void createShipment_TM() {
    shipmentEnterpriseMessage = DataFactory.getShipmentEnterprisePayload();
    inBoundShipmentService.createShipment(shipmentEnterpriseMessage);
    verify(tmRestApiClient, times(1)).restProcessCreateShipment(any(
        RestProcessShipmentOrderCreate.class));
  }

  @Test
  void deleteShipment_TM() {
    shipmentEnterpriseMessage = DataFactory.getShipmentEnterprisePayload();
    inBoundShipmentService.deleteShipment(shipmentEnterpriseMessage);
    verify(tmRestApiClient, times(1)).restProcessDeleteShipment(any(
        RestProcessShipmentOrderDelete.class));
  }

  @Test
  void createShipment_Exception() {
    shipmentEnterpriseMessage = new ShipmentEnterpriseMessage();
    TMSMappingException thrown = assertThrows(TMSMappingException.class,
        () -> inBoundShipmentService.createShipment(shipmentEnterpriseMessage));
    assertTrue(thrown.getMessage().contains("CreateShipment|Error Occurred while mapping shipment in TM"));
  }

  @Test
  void deleteShipment_Exception() {
    shipmentEnterpriseMessage = new ShipmentEnterpriseMessage();
    shipmentEnterpriseMessage.setShipmentNumber(12345L);
    RestProcessShipmentOrderDelete shipmentOrderDelete = new RestProcessShipmentOrderDelete();
    RestShipmentDelete restShipmentDelete = new RestShipmentDelete();
    restShipmentDelete.setShipmentNumber("12345");
    shipmentOrderDelete.setShipment(Collections.singletonList(restShipmentDelete));

    when(tmRestApiClient.restProcessDeleteShipment(any(RestProcessShipmentOrderDelete.class)))
        .thenThrow(new RuntimeException("API Error"));
    assertThrows(NotFoundException.class, () -> {
      inBoundShipmentService.deleteShipment(shipmentEnterpriseMessage);
    });
  }

  @Test
  void createShipment_Collect_assigned_srr() {
    shipmentEnterpriseMessage = DataFactory.getShipmentEnterprise_carrierSRR();
    inBoundShipmentService.createShipment(shipmentEnterpriseMessage);
    verify(tmRestApiClient, times(1)).restProcessCreateShipment(any(
        RestProcessShipmentOrderCreate.class));
  }

  @Test
  void createShipment_TmsLoadProcessException()  {
    shipmentEnterpriseMessage = DataFactory.getShipmentEnterprisePayload();
    String exceptionMessage = "TMS Load Process Failure";
    TmsLoadProcessException mockException = new TmsLoadProcessException(exceptionMessage);
    doThrow(mockException).when(tmRestApiClient).restProcessCreateShipment(any(RestProcessShipmentOrderCreate.class));
    TMSCreateAPIFailureException thrownException = assertThrows(
        TMSCreateAPIFailureException.class,
        () -> inBoundShipmentService.createShipment(shipmentEnterpriseMessage),
        "Error Occurred while mapping shipment in TM "
    );
    assert (thrownException.getMessage().contains(exceptionMessage));
    verify(tmRestApiClient).restProcessCreateShipment(any(RestProcessShipmentOrderCreate.class));
  }

  @Test
  void deleteShipment_TmsLoadProcessException()  {
    shipmentEnterpriseMessage = new ShipmentEnterpriseMessage();
    shipmentEnterpriseMessage.setShipmentNumber(78899200L);
    String exceptionMessage = "TMS Load Process Failure";
    TmsLoadProcessException mockException = new TmsLoadProcessException(exceptionMessage);
    doThrow(mockException).when(tmRestApiClient).restProcessDeleteShipment(any(RestProcessShipmentOrderDelete.class));
    TMSDeleteAPIFailureException thrownException = assertThrows(
        TMSDeleteAPIFailureException.class,
        () -> inBoundShipmentService.deleteShipment(shipmentEnterpriseMessage),
        "Error Occurred while deleting shipment in TM"
    );
    assert (thrownException.getMessage().contains(exceptionMessage));
    verify(tmRestApiClient).restProcessDeleteShipment(any(RestProcessShipmentOrderDelete.class));
  }
}
