package com.macys.logistics.shipment.load.camel.processors;

import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.FindEntitiesResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadType;
import com.macys.logistics.shipment.load.exception.ShipConfirmProcessingException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.service.LoadEnrichmentService;
import com.macys.logistics.shipment.load.util.ShipConfirmTestUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static com.macys.logistics.shipment.load.util.Constants.EQ;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipConfirmProcessorTest {
    @Mock
    Exchange exchange;

    @Mock
    Message message;
    @InjectMocks
    ShipConfirmProcessor shipConfirmProcessor;
    @Mock
    LoadMapper loadMapper;
    @Mock
    LoadEnrichmentService loadEnrichmentService;
    @BeforeEach
    void init() {
        lenient().when(exchange.getIn()).thenReturn(message);
    }

    @Test
    void appointmentShipConfirmProcessor_throws_exception() {
        when(exchange.getIn().getBody(String.class)).thenReturn(null);
        Throwable thrown =
                assertThrows(ShipConfirmProcessingException.class,
                        () -> shipConfirmProcessor.process(exchange));
        assertEquals("Received empty appointment ship confirm event", thrown.getMessage());
    }

    @Test
    void appointmentShipConfirmProcessor_processed_successfully() {
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getDoocDoorUpdatedLoad();
        when(exchange.getIn().getBody(String.class)).thenReturn(ShipConfirmTestUtil.getShipConfirmPayload());
        when(loadEnrichmentService.fetchLoadId(Arrays.asList("12345"),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        shipConfirmProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).triggerLoadCompleteForObtmAppointments(anyString(),any(LoadType.class),anyString(),anyInt());
    }

    @Test
    void appointmentShipConfirmProcessor_payload_having_number_of_cartons_null_processed_successfully() {
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getDoocDoorUpdatedLoad();
        when(exchange.getIn().getBody(String.class)).thenReturn(ShipConfirmTestUtil.getShipConfirmPayloadExtraFields());
        when(loadEnrichmentService.fetchLoadId(Arrays.asList("12345"),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        shipConfirmProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).triggerLoadCompleteForObtmAppointments(anyString(),any(LoadType.class),anyString(),anyInt());
    }


    @Test
    void appointmentShipConfirmProcessor_payload_having_load_description_processed_successfully() {
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getDoocDoorUpdatedLoad();
        loadDetails.getEntity().get(0).getLoad().getFirst().setLoadDescription("56789");
        when(exchange.getIn().getBody(String.class)).thenReturn(ShipConfirmTestUtil.getShipConfirmPayloadExtraFields());
        when(loadEnrichmentService.fetchLoadId(Arrays.asList("12345"),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        shipConfirmProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).triggerLoadCompleteForObtmAppointments(anyString(),any(LoadType.class),anyString(),anyInt());
    }

    @Test
    void appointmentShipConfirmProcessor_throws_exception_when_payload_is_invalid() {
        when(exchange.getIn().getBody(String.class)).thenReturn("{\n" + "    \"fulfillmentOrder\": {");
        Throwable thrown =
                assertThrows(ShipConfirmProcessingException.class,
                        () -> shipConfirmProcessor.process(exchange));
        assertEquals("Exception occurred while processing Appointment Event Unexpected end-of-input: expected close marker for Object (start marker at [Source: (String)\"{\n" +
                "    \"fulfillmentOrder\": {\"; line: 2, column: 25])\n" +
                " at [Source: (String)\"{\n" +
                "    \"fulfillmentOrder\": {\"; line: 2, column: 26] (through reference chain: com.macys.logistics.shipment.load.dto.shipconfirm.ShipConfirmPayload[\"fulfillmentOrder\"])"  , thrown.getMessage());
    }

}