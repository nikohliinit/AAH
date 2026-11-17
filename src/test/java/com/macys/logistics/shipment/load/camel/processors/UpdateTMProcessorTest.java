package com.macys.logistics.shipment.load.camel.processors;

import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.AppointmentSealConditionDetail;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.response.LocationDTO;
import com.macys.logistics.shipment.load.exception.TmsLoadValidationException;
import com.macys.logistics.shipment.load.logging.ShipmentLoadIntegrationMetricsHelper;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.service.LoadEnrichmentService;
import com.macys.logistics.shipment.load.service.LocationServiceClient;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static com.macys.logistics.shipment.load.util.Constants.EQ;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UpdateTMProcessorTest {

    @InjectMocks
    UpdateTMProcessor updateTMProcessor;
    @Mock
    Exchange exchange;
    @Mock
    Message message;
    @Mock
    LoadEnrichmentService loadEnrichmentService;
    @Mock
    LocationServiceClient locationServiceClient;
    @Mock
    TMRestApiClient tmRestApiClient;

    @Mock
    ShipmentLoadIntegrationMetricsHelper metricsHelper;
    @Mock
    LoadMapper loadMapper;

    @Mock
    Tracer tracer;

    @Mock
    private Span span;
    @Mock
    private TraceContext traceContext;
    @Mock
    private Tracer.SpanInScope spanInScope;

    @BeforeEach
    void init() {
        final Map<String, String> updateSealConditionStatusCodes=DataFactory.getSealConditionAllowedStatusCodes();
        ReflectionTestUtils.setField(updateTMProcessor, "metricsHelper", metricsHelper);
        ReflectionTestUtils.setField(updateTMProcessor, "sealRestrictionEnabled", "true");
        ReflectionTestUtils.setField(updateTMProcessor, "destinations",  "7291,7293");
        ReflectionTestUtils.setField(updateTMProcessor, "tenderedListOfNames",  "Tendered,Tender Accepted");
        ReflectionTestUtils.setField(updateTMProcessor, "allowedCancelLoadStatus", "Open,Planned,Tendered,Tender Accepted");
        ReflectionTestUtils.setField(updateTMProcessor, "loadMapper",  loadMapper);
        ReflectionTestUtils.setField(updateTMProcessor, "updateSealConditionStatusCodes",  updateSealConditionStatusCodes);
        ReflectionTestUtils.setField(updateTMProcessor, "doublePupEnabled",  "false");
        ReflectionTestUtils.setField(updateTMProcessor, "restrictionValues",  "NA");

        MockitoAnnotations.openMocks(this);
        when(tracer.nextSpan()).thenReturn(span);
        when(span.name(anyString())).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.spanId()).thenReturn("mockSpanId");
        when(tracer.withSpan(any(Span.class))).thenReturn(spanInScope);
        when(span.start()).thenReturn(span);
        when(exchange.getIn()).thenReturn(message);
        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getName()).thenReturn("mockCamelContext");
        when(exchange.getContext()).thenReturn(camelContext);
    }

    @Test
    void updateTMProcessor_throws_exception() throws Exception {
        when(exchange.getIn().getBody(String.class)).thenReturn(null);
        Throwable thrown =
                assertThrows(TmsLoadValidationException.class,
                        () -> updateTMProcessor.process(exchange));
        assertEquals("Received empty appointmentPayload", thrown.getMessage());
    }

    @Test
    void updateTMProcessor_apptType_throws_exception() throws Exception {
        String requestMessage = DataFactory.getAppointmentType();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());
    }

    @Test
    void updateTMProcessor_apptStatus_throws_exception() throws Exception {
        String requestMessage = DataFactory.getAppointmentStatus();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());
    }

    @Test
    void updateTMProcessor_apptSealCondition_throws_exception() throws Exception {
        String requestMessage = DataFactory.getAppointmentSealCondition();
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());
    }

    @Test
    void updateTMProcessor_invokes_one_time() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());
    }

    @Test
    void updateTMProcessor_noSealConditionExist() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setReferenceNumber5(null);
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        loadDetails.setEntity(entities);
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());
    }

    @Test
    void updateTMProcessor_noLoadId() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        findEntitiesResponse.setEntity(null);

        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).fetchLoadDetails(anyString());
    }

    @Test
    void updateTMProcessor_sealConditionAlreadyExist() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getExistingSeal();

        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
    }

    @Test
    void updateTMProcessor_noLoadDetailsExist() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = new FindEntitiesResponse();
        loadDetails.setEntity(null);
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        Throwable thrown = assertThrows(TmsLoadValidationException.class, () -> updateTMProcessor.process(exchange));
        assertEquals("Load details are not available for "+ "5432123", thrown.getMessage());
    }



    @Test
    void updateTMProcessor_destLocn_throws_exception() throws Exception {
        String requestMessage = DataFactory.getAppointmentDestLocn();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        appt.setAppointmentDestLocNumber("7291");
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        
        lenient().when(locationServiceClient.findLocnEntityByLocnAbbr(any())).thenReturn(locn);
        
        when(loadMapper.getLoad(any())).thenReturn(findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
    }

    @Test
    void updateTMProcessor_destLocn_success() throws Exception {
        ReflectionTestUtils.setField(updateTMProcessor, "sealRestrictionEnabled", "false");
        String requestMessage = DataFactory.getAppointmentPayloadUnloading();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        appt.setAppointmentDestLocNumber("3");
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());

    }

    @Test
    void cancelLoadwhenLoadStatusIsTenderedTest() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadStringCancelledStatus();
        AppointmentPayload appt = DataFactory.getAppointmentPayloadStringCancelled();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetailsForCancelledStatus();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getLoadDetailsForCancelledStatus();
        FindEntitiesResponse stopApptResponse = DataFactory.getCancelledStopApptResponse();
        ProcessLoadRetrieveResponse response = DataFactory.getCancelledProcessLoadRetrieveResponseForDoorDock();
        stopApptResponse.setEntity(null);
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList("5432123"),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());
    }

    @Test
    void cancelLoadwhenLoadStatusIsTenderedAcceptedTest() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadStringCancelledStatus();
        AppointmentPayload appt = DataFactory.getAppointmentPayloadStringCancelled();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetailsForCancelledStatus();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getLoadDetailsForCancelledStatus();
        loadDetails.getEntity().get(0).getLoad().get(0).setCurrentLoadOperationalStatusEnumDescr("Tender Accepted");
        FindEntitiesResponse stopApptResponse = DataFactory.getCancelledStopApptResponse();
        ProcessLoadRetrieveResponse response = DataFactory.getCancelledProcessLoadRetrieveResponseForDoorDock();
        stopApptResponse.setEntity(null);
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList("5432123"),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);

        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processCancleTender(any());
        verify(loadEnrichmentService, times(1)).processCancelLoad(any());
    }

    @Test
    void cancelLoadwhenLoadStatusIsPlanned() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadStringCancelledStatus();
        AppointmentPayload appt = DataFactory.getAppointmentPayloadStringCancelled();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetailsForCancelledStatus();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getLoadDetailsForCancelledStatus();
        loadDetails.getEntity().get(0).getLoad().get(0).setCurrentLoadOperationalStatusEnumDescr("Planned");
        FindEntitiesResponse stopApptResponse = DataFactory.getCancelledStopApptResponse();
        ProcessLoadRetrieveResponse response = DataFactory.getCancelledProcessLoadRetrieveResponseForDoorDock();
        stopApptResponse.setEntity(null);
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList("5432123"),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);

        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processCancelLoad(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Open","In Transit","Completed"})
    void cancelLoadwhenLoadStatusIsOpen(String status) throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadStringCancelledStatus();
        AppointmentPayload appt = DataFactory.getAppointmentPayloadStringCancelled();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetailsForCancelledStatus();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getLoadDetailsForCancelledStatus();
        loadDetails.getEntity().get(0).getLoad().get(0).setCurrentLoadOperationalStatusEnumDescr(status);
        FindEntitiesResponse stopApptResponse = DataFactory.getCancelledStopApptResponse();
        ProcessLoadRetrieveResponse response = DataFactory.getCancelledProcessLoadRetrieveResponseForDoorDock();
        stopApptResponse.setEntity(null);
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList("5432123"),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processCancelLoad(any());
    }

    @Test
    void cancelLoadwhenLoadStatusIsTendered_Test() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadStringCancelledStatus();
        AppointmentPayload appt = DataFactory.getAppointmentPayloadStringCancelled();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetailsForCancelledStatus();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getLoadDetailsForCancelledStatus();
        loadDetails.getEntity().get(0).getLoad().get(0).setCurrentLoadOperationalStatusEnumDescr("Tendered");
        FindEntitiesResponse stopApptResponse = DataFactory.getCancelledStopApptResponse();
        ProcessLoadRetrieveResponse response = DataFactory.getCancelledProcessLoadRetrieveResponseForDoorDock();
        stopApptResponse.setEntity(null);
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList("5432123"),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);

        
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processCancleTender(any());
        verify(loadEnrichmentService, times(1)).processCancelLoad(any());
    }

    @Test
    void cancelLoadwhenLoadStatusIsTenderedApptTypeIsDCtoDC_Test() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadStringCancelledStatus();
        AppointmentPayload appt = DataFactory.getAppointmentPayloadStringCancelled();
        appt.setAppointmentType("DC to DC");
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetailsForCancelledStatus();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getLoadDetailsForCancelledStatus();
        loadDetails.getEntity().get(0).getLoad().get(0).setCurrentLoadOperationalStatusEnumDescr("Tendered");
        FindEntitiesResponse stopApptResponse = DataFactory.getCancelledStopApptResponse();
        ProcessLoadRetrieveResponse response = DataFactory.getCancelledProcessLoadRetrieveResponseForDoorDock();
        stopApptResponse.setEntity(null);
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList("5432123"),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processCancleTender(any());
        verify(loadEnrichmentService, times(1)).processCancelLoad(any());
    }

    @Test
    void updateTMProcessor_invokes_one_time_trailerNumber() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        ProcessLoadRetrieveResponse response=DataFactory.getProcessLoadRetrieveResponseForDoorDock();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        doNothing().when(loadEnrichmentService).processLoadUpdate("5432123", appt.getAppointmentCarrierTrailerNumber());
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processLoadUpdate(any(),anyString());
    }

    @Test
    void updateTMProcessor_invokes_one_time_trailerNumber_Null() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber(null);
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        ProcessLoadRetrieveResponse response=DataFactory.getProcessLoadRetrieveResponseForDoorDock();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        
        doNothing().when(loadEnrichmentService).processLoadUpdate("5432123", appt.getAppointmentCarrierTrailerNumber());
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processLoadUpdate(any(),anyString());
    }



    @Test
    void updateTMProcessor_DisplayFlagCheck() throws Exception {
        String requestMessage = DataFactory.getOutboundAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        AppointmentPayload appt = DataFactory.getOutboundAppointmentPayload();
        ProcessLoadRetrieveResponse response=DataFactory.getProcessLoadRetrieveResponseForDoorDock();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());

    }

    @Test
    void updateTMProcessor_DisplayFlagCheckNullCheck() throws Exception {
        String requestMessage = DataFactory.getOutboundAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        AppointmentPayload appt = DataFactory.getOutboundAppointmentPayload();
        appt.setAppointmentFreightType("Palletized");
        appt.setAppointmentDispatchFlag(null);
        ProcessLoadRetrieveResponse response=DataFactory.getProcessLoadRetrieveResponseForDoorDock();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());
    }

    @Test
    void updateTMProcessor_invokes_trailerNumber_double_pups_success() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("CLTLAT51,CLTLAT53");
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        ReflectionTestUtils.setField(updateTMProcessor, "doublePupEnabled",  "true");
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        doNothing().when(loadEnrichmentService).processLoadUpdate("5432123", "TestTrailer,CLTLAT53");
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processLoadUpdate("5432123","TestTrailer,CLTLAT53");
    }
    
    @Test
    void updateTMProcessor_invokes_trailerNumber_double_pups_with_invlid_trailer_number_from_tm() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber(",,");
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        doNothing().when(loadEnrichmentService).processLoadUpdate("5432123", appt.getAppointmentCarrierTrailerNumber());
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processLoadUpdate(any(),anyString());
    }


    @Test
    void updateTMProcessor_invokes_trailerNumber_double_pups_with_invalid_appointment() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("CLTLAT51,CLTLAT52");
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        ProcessLoadRetrieveResponse response=DataFactory.getProcessLoadRetrieveResponseForDoorDock();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        
        loadDetails.getEntity().get(0).getLoad().get(0).getStop().get(0).setSystemStopID("1234");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        ReflectionTestUtils.setField(updateTMProcessor, "doublePupEnabled",  "true");
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).processLoadUpdate(any(),anyString());
    }

    @Test
    void updateTMProcessor_invokes_trailerNumber_double_pups_with_both_trailers_are_same() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("TestTrailer,CLTLAT52");
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        ProcessLoadRetrieveResponse response=DataFactory.getProcessLoadRetrieveResponseForDoorDock();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        FindEntitiesResponse timezoneResponse =DataFactory.getTimezoneResponse();
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        ReflectionTestUtils.setField(updateTMProcessor, "doublePupEnabled",  "true");
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).processLoadUpdate(any(),anyString());
    }


    @Test
    void updateTMProcessor_invokes_trailerNumber_double_pups_with_payload_trailer_number_null() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadWithNullTrailerString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("TestTrailer,CLTLAT52");
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        ProcessLoadRetrieveResponse response=DataFactory.getProcessLoadRetrieveResponseForDoorDock();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        FindEntitiesResponse timezoneResponse =DataFactory.getTimezoneResponse();
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).processLoadUpdate(any(),anyString());

    }

    @Test
    void updateTMProcessor_invokes_trailerNumber_double_pups_with_invalid_trailer_numbers_from_tm() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber(",TestTrailer,CLTLAT52,");
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        ProcessLoadRetrieveResponse response=DataFactory.getProcessLoadRetrieveResponseForDoorDock();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        FindEntitiesResponse timezoneResponse =DataFactory.getTimezoneResponse();
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        ReflectionTestUtils.setField(updateTMProcessor, "doublePupEnabled",  "true");
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).processLoadUpdate(any(),anyString());

    }
    
    @Test
    void updateTMProcessor_invokes_trailerNumber_double_pups_exception_when_processload() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("CLTLAT51,CLTLAT53");
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        ReflectionTestUtils.setField(updateTMProcessor, "doublePupEnabled",  "true");
        doThrow(new RuntimeException("Error calling tms to update trailer number for load Id 5432123"))
        .when(loadEnrichmentService).processLoadUpdate("5432123", "TestTrailer,CLTLAT53");
    
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processLoadUpdate("5432123","TestTrailer,CLTLAT53");
    }

    @Test
    void updateTMProcessor_invokes_double_pups_cancel_appointment() throws Exception {
        String requestMessage = DataFactory.getOutboundCancelledAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadforDoublePupDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("CLTLAT51,CLTLAT53");
        AppointmentPayload appt = DataFactory.getOutboundAppointmentPayloadStringCancelled();
        ShipmentResponse shipment = DataFactory.getShipmentResponse();
        appt.setAppointmentCarrierTrailerNumber("CLTLAT51");
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        doNothing().when(loadEnrichmentService).processLoadUpdate("5432123", "CLTLAT53");
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        when(loadEnrichmentService.searchShipment("SH-009074984")).thenReturn(shipment);
        doNothing().when(loadEnrichmentService).deleteReferenceNumber("9074984",shipment);
        ReflectionTestUtils.setField(updateTMProcessor, "doublePupEnabled",  "true");
        updateTMProcessor.process(exchange);

        ArgumentCaptor<ShipmentResponse> shipmentPayLoadCaptor = ArgumentCaptor.forClass(ShipmentResponse.class);
        ArgumentCaptor<String> loadIdCaptorOfDelete = ArgumentCaptor.forClass(String.class);

        verify(loadEnrichmentService).deleteReferenceNumber(loadIdCaptorOfDelete.capture(),shipmentPayLoadCaptor.capture());

        ShipmentResponse capturedApptPayload = shipmentPayLoadCaptor.getValue();

        assertEquals(shipment.getData().get(0).getShipmentNumber(),
                capturedApptPayload.getData().get(0).getShipmentNumber());
          }

    @Test
    void updateTMProcessor_invokes_double_pups_cancel_appointment_elseCheck() throws Exception {
        String requestMessage = DataFactory.getOutboundCancelledAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadforDoublePupDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setCurrentLoadOperationalStatusEnumDescr("In Transit");
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("CLTLAT51,CLTLAT53");
        AppointmentPayload appt = DataFactory.getOutboundAppointmentPayloadStringCancelled();
        ShipmentResponse shipment = DataFactory.getShipmentResponse();
        appt.setAppointmentCarrierTrailerNumber("CLTLAT51");
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());
    }

    @Test
    void updateTMProcessor_invokes_double_pups_cancel_appointment_elseCheck_completeStatus() throws Exception {
        String requestMessage = DataFactory.getOutboundCancelledAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadforDoublePupDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setCurrentLoadOperationalStatusEnumDescr("Completed");
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("CLTLAT51,CLTLAT53");
        AppointmentPayload appt = DataFactory.getOutboundAppointmentPayloadStringCancelled();
        ShipmentResponse shipment = DataFactory.getShipmentResponse();
        appt.setAppointmentCarrierTrailerNumber("CLTLAT51");
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(0)).addOrUpdateSealType(any(),anyString(),any(),anyString());
    }
    
    
    @Test
    void updateTMProcessor_invokes_double_pups_cancel_appointment_if_load_in_transit() throws Exception {
        String requestMessage = DataFactory.getOutboundCancelledAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadforDoublePupDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("CLTLAT51,CLTLAT53");
        loadDetails.getEntity().get(0).getLoad().get(0).setCurrentLoadOperationalStatusEnumDescr("In Transit");
        AppointmentPayload appt = DataFactory.getOutboundAppointmentPayloadStringCancelled();
        ShipmentResponse shipment = DataFactory.getShipmentResponse();
        appt.setAppointmentCarrierTrailerNumber("CLTLAT51");
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).processCancleTender(any());
        verify(loadEnrichmentService, times(0)).processLoadReturnToOpen(any());
        verify(loadEnrichmentService, times(0)).processLoadUpdate(any(),any());
    }
    
    @ParameterizedTest
    @CsvSource({
        "Tender Accepted, 1, 1, 1, 1, 1",
        "Tendered, 1, 1, 1, 1, 1",
        "Planned, 0, 1, 1, 1, 1",
        "Open, 0, 0, 1, 1, 1",
        "Test, 0, 0, 0, 0, 0"
    })
    void updateTMProcessor_invokes_double_pups_cancel_appointment(String status, int processCancelTenderTimes,
        int processLoadReturnToOpenTimes, int processLoadUpdateTimes, int removeShipmentTimes,int processLoadRateTimes ) throws Exception {
        String requestMessage = DataFactory.getOutboundCancelledAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadforDoublePupDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("CLTLAT51,CLTLAT53");
        loadDetails.getEntity().get(0).getLoad().get(0).setCurrentLoadOperationalStatusEnumDescr(status);
        AppointmentPayload appt = DataFactory.getOutboundAppointmentPayloadStringCancelled();
        ShipmentResponse shipment = DataFactory.getShipmentResponse();
        appt.setAppointmentCarrierTrailerNumber("CLTLAT51");
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");

        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        lenient().doNothing().when(loadEnrichmentService).processLoadUpdate("5432123", "CLTLAT53");
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        ReflectionTestUtils.setField(updateTMProcessor, "doublePupEnabled",  "true");
        when(loadEnrichmentService.searchShipment("SH-009074984")).thenReturn(shipment);
        lenient().doNothing().when(loadEnrichmentService).deleteReferenceNumber("9074984", shipment);

         updateTMProcessor.process(exchange);

        verify(loadEnrichmentService, times(processCancelTenderTimes)).processCancleTender(any());
        verify(loadEnrichmentService, times(processLoadReturnToOpenTimes)).processLoadReturnToOpen(any());
        verify(loadEnrichmentService, times(processLoadUpdateTimes)).processLoadUpdate(any(), any());
        verify(loadEnrichmentService, times(removeShipmentTimes)).removeShipmentFromLoad(any(), any());
        verify(loadEnrichmentService, times(processLoadRateTimes)).processLoadRate(any());
    }
    
    @Test
    void updateTMProcessor_invokes_trailerNumber_double_pups_with_same_trailer_numbers_exists_in_tm() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadString();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        loadDetails.getEntity().get(0).getLoad().get(0).setTrailerNumber("CLTLAT51,CLTLAT51");
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        ReflectionTestUtils.setField(updateTMProcessor, "doublePupEnabled",  "true");
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        doNothing().when(loadEnrichmentService).processLoadUpdate("5432123", "TestTrailer,CLTLAT51");
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).processLoadUpdate("5432123","TestTrailer,CLTLAT51");
    }
    
    @Test
    void updateTMProcessor_verify_fetch_load_return_with_null_data_and_search_shipment_return_data_with_different_shipment() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadUnloadComplete();
        AppointmentPayload appt = DataFactory.getAppointmentYardArea();
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getExistingSeal();
        ShipmentResponse shipmentResponse = new ShipmentResponse();
        List<ShipmentData> shipmentData= new ArrayList<>();
        ShipmentData shipment= new ShipmentData();
        shipment.setShipmentNumber("OB-1234");
        shipment.setSystemLoadID("5432123");
        shipmentData.add(shipment);
        shipmentResponse.setData(shipmentData);  
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.searchShipmentWithOldAppointmentRef(appt.getAppointmentNumber())).thenReturn(shipmentResponse);
        
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService).fetchLoadId(any(),anyString());
        verify(loadEnrichmentService).searchShipmentWithOldAppointmentRef(appt.getAppointmentNumber());
        verify(loadEnrichmentService).fetchLoadDetails("5432123");
    }
    
    @Test
    void updateTMProcessor_verify_fetch_load_return_with_null_data_and_search_shipment_return_data_with_same_shipment() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadUnloadComplete();
        AppointmentPayload appt = DataFactory.getAppointmentYardArea();
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getExistingSeal();
        ShipmentLegType shipmentLegType = new ShipmentLegType();
        shipmentLegType.setShipmentNumber("OB-1234");
        shipmentLegType.setSystemDropStopID("1234");
        loadDetails.getEntity().get(0).getLoad().get(0).getShipmentLeg().add(shipmentLegType);
        ShipmentResponse shipmentResponse = new ShipmentResponse();
        List<ShipmentData> shipmentData= new ArrayList<>();
        ShipmentData shipment= new ShipmentData();
        shipment.setShipmentNumber("OB-1234");
        shipment.setSystemLoadID("5432123");
        shipmentData.add(shipment);
        shipmentResponse.setData(shipmentData);
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.searchShipmentWithOldAppointmentRef(appt.getAppointmentNumber())).thenReturn(shipmentResponse);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService).fetchLoadId(any(),anyString());
        verify(loadEnrichmentService).searchShipmentWithOldAppointmentRef(appt.getAppointmentNumber());
        verify(loadEnrichmentService).fetchLoadDetails("5432123");
    }
    
    @Test
    void updateTMProcessor_verifyFetchLoadReturnWithNullDataAndSearchShipmentReturnNull() throws Exception {
        String requestMessage = DataFactory.getAppointmentPayloadUnloadComplete();
        AppointmentPayload appt = DataFactory.getAppointmentYardArea();
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(0)).fetchLoadDetails(anyString());
    }

    @Test
    void testChangedToN_ShouldUpdateQuantitiesAndCallRateAndRerate() throws Exception {
        // Arrange
        String requestMessage = DataFactory.getOutboundAppointmentPayloadString();
        AppointmentPayload appt = DataFactory.getAppointmentYardArea();
        appt.setAppointmentNumber("53882234");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getExistingSeal();
        findEntitiesResponse.getEntity().get(0).getStop().get(0).setSystemLoadID("53882234");
        LoadType load = new LoadType();
        ShipmentLegType shipmentLeg = new ShipmentLegType();
        shipmentLeg.setFlexibleQuantity1(BigDecimal.ONE);  // wasY = true
        shipmentLeg.setFlexibleQuantity2(BigDecimal.ZERO);
        shipmentLeg.setSystemDropStopID("53882234");
        load.getShipmentLeg().add(shipmentLeg);
        loadDetails.getEntity().get(0).getLoad().get(0).getShipmentLeg().add(shipmentLeg);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("53882234")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);
        Assertions.assertEquals(BigDecimal.ZERO, shipmentLeg.getFlexibleQuantity1());
        Assertions.assertEquals(BigDecimal.ONE, shipmentLeg.getFlexibleQuantity2());
    }

    @Test
    void testChangedToY_ShouldUpdateQuantitiesAndCallRateAndRerate() throws Exception {

        String requestMessage = DataFactory.getOBtmAppointmentPayloadString();
        AppointmentPayload appt = DataFactory.getAppointmentYardArea();
        appt.setAppointmentNumber("54643660");
        appt.setAppointmentDropFlag("Y");
        appt.setAppointmentSealConditionDescription("test");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getExistingSeal();
        findEntitiesResponse.getEntity().get(0).getStop().get(0).setSystemLoadID("54643660");
        LoadType load = new LoadType();
        ShipmentLegType shipmentLeg = new ShipmentLegType();
        shipmentLeg.setFlexibleQuantity1(BigDecimal.ZERO);  // wasY = true
        shipmentLeg.setFlexibleQuantity2(BigDecimal.ONE);
        shipmentLeg.setSystemDropStopID("54643660");
        load.getShipmentLeg().add(shipmentLeg);
        loadDetails.getEntity().get(0).getLoad().get(0).getShipmentLeg().add(shipmentLeg);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("54643660")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));
        updateTMProcessor.process(exchange);

        // Assert
        Assertions.assertEquals(BigDecimal.ZERO, shipmentLeg.getFlexibleQuantity1());
        Assertions.assertEquals(BigDecimal.ONE, shipmentLeg.getFlexibleQuantity2());

    }

    @Test
    void testNoChange_ShouldNotCallRateOrRerate() throws Exception {

        String requestMessage = DataFactory.getOBtmAppointmentPayloadString();
        AppointmentPayload appt = DataFactory.getAppointmentYardArea();
        appt.setAppointmentNumber("54643660");
        appt.setAppointmentSealConditionDescription("test");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getExistingSeal();
        findEntitiesResponse.getEntity().get(0).getStop().get(0).setSystemLoadID("54643660");
        LoadType load = new LoadType();
        ShipmentLegType shipmentLeg = new ShipmentLegType();
        shipmentLeg.setFlexibleQuantity1(BigDecimal.ZERO);  // wasY = true
        shipmentLeg.setFlexibleQuantity2(BigDecimal.ONE);
        shipmentLeg.setSystemDropStopID("54643660");
        load.getShipmentLeg().add(shipmentLeg);
        loadDetails.getEntity().get(0).getLoad().get(0).getShipmentLeg().add(shipmentLeg);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("54643660")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));

        updateTMProcessor.process(exchange);
        Mockito.verify(loadEnrichmentService, times(0)).updateFlexibleQ1andFlexibleQ2andReRate(any(), any(), any(), any());
    }

    @Test
    void testNoChange_ShouldNotCallRateOrRerateSuccess() throws Exception {

        String requestMessage = DataFactory.getOutboundAppointmentPayloadString();
        AppointmentPayload appt = DataFactory.getAppointmentYardArea();
        appt.setAppointmentNumber("53882234");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        FindEntitiesResponse loadDetails = DataFactory.getExistingSeal();
        findEntitiesResponse.getEntity().get(0).getStop().get(0).setSystemLoadID("53882234");
        LoadType load = new LoadType();
        ShipmentLegType shipmentLeg = new ShipmentLegType();
        shipmentLeg.setFlexibleQuantity1(BigDecimal.ONE);
        shipmentLeg.setFlexibleQuantity2(BigDecimal.ZERO);
        shipmentLeg.setSystemDropStopID("53882234");
        load.getShipmentLeg().add(shipmentLeg);
        loadDetails.getEntity().get(0).getLoad().get(0).getShipmentLeg().add(shipmentLeg);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetails("53882234")).thenReturn(loadDetails);
        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));

        updateTMProcessor.process(exchange);
        Mockito.verify(loadEnrichmentService, times(1)).updateFlexibleQ1andFlexibleQ2andReRate(any(), any(), any(), any());
        Mockito.verify(loadEnrichmentService, times(1)).processLoadRate(any());

    }

    @Test
    void updateTMProcessor_appointmentSealConditionDetailList() throws Exception {
        ReflectionTestUtils.setField(updateTMProcessor, "sealRestrictionEnabled", "false");
        String requestMessage = DataFactory.getAppointmentPayloadUnloading();
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        appt.setAppointmentDestLocNumber("3");
        FindEntitiesResponse loadDetails = DataFactory.getLoadDetails();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        ProcessLoadRetrieveResponse response=DataFactory.getProcessLoadRetrieveResponseForDoorDock();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        when(exchange.getIn().getBody(String.class)).thenReturn(requestMessage);
        when(loadEnrichmentService.fetchLoadDetails("5432123")).thenReturn(loadDetails);
        when(loadEnrichmentService.fetchLoadId(Arrays.asList(appt.getAppointmentNumber()),EQ)).thenReturn(findEntitiesResponse);

        when(loadMapper.getLoad(any())).thenReturn(loadDetails.getEntity().get(0).getLoad().get(0));  AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        String sealType = "seal-type-123";
        String sealNumber = "123789";
        AppointmentSealConditionDetail appointmentSealConditionDetail = new AppointmentSealConditionDetail();
        appointmentSealConditionDetail.setAppointmentSealCnd(sealType);
        appointmentSealConditionDetail.setSealNbr(sealNumber);
        appointmentSealConditionDetail.setSequenceNbr(1);

        List<AppointmentSealConditionDetail> sealConditionDetailList = Arrays.asList(appointmentSealConditionDetail);
        when(tmRestApiClient.callAppointmentSealCondition(Integer.valueOf(appointmentPayload.getAppointmentNumber())))
                .thenReturn(sealConditionDetailList);

        updateTMProcessor.process(exchange);
        verify(loadEnrichmentService, times(1)).addOrUpdateSealCondition(any(),anyString(),any());
        verify(loadEnrichmentService, times(1)).addOrUpdateSealType(any(),anyString(),any(),anyString());

    }

}
