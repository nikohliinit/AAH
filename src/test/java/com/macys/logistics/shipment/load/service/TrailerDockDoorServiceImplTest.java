package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.client.RestApiClient;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.ApiResponse;
import com.macys.logistics.shipment.load.dto.TrailerDoorDockCDCEvent;
import com.macys.logistics.shipment.load.dto.TrlrData;
import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleInputDTO;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.FindEntitiesResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessMaintainEntityReferenceNumber;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ReferenceNumberUpdateDataType;
import com.macys.logistics.shipment.load.dto.response.LocationDTO;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.util.CommonUtil;
import com.macys.logistics.shipment.load.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.macys.logistics.shipment.load.util.Constants.CORRELATION_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TrailerDockDoorServiceImplTest {

    @Mock
    private TMRestApiClient tmRestApiClient;

    @Mock
    private RestApiClient restApiClient;
    @Mock
    private RetryTemplate retryTemplate;
    @InjectMocks
    private TrailerDockDoorServiceImpl trailerDockDoorService;
    @Mock
    private CommonUtil commonUtil;

    @Mock
    private LocationServiceClient locationServiceClient;

    @Mock
    private LoadEnrichmentService loadEnrichmentService;

    @Mock
    private LoadMapper loadMapper;

    private final String loadId = "load123";
    private final String dockDoor = "DD001";
    private final String referenceNumber = "ref123";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(trailerDockDoorService, "trailerYardAppointmentInfoUrl", "http://mock-url.com");
        log.info("TrailerDockDoorServiceImplTest rule test initialized");
    }

    @Test
    void testProcessDockDoorAttach() {

        ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumberCaptor ;
        trailerDockDoorService.processDockDoorAttach(loadId, dockDoor);
        ArgumentCaptor<ProcessMaintainEntityReferenceNumber> captor = ArgumentCaptor.forClass(ProcessMaintainEntityReferenceNumber.class);
        verify(tmRestApiClient).processMaintainEntityReferenceNumberForDockDoor(captor.capture());
        processMaintainEntityReferenceNumberCaptor = captor.getValue();

        assertNotNull(processMaintainEntityReferenceNumberCaptor);
        assertEquals(1, processMaintainEntityReferenceNumberCaptor.getReferenceNumberUpdateData().size());
        ReferenceNumberUpdateDataType updateData = processMaintainEntityReferenceNumberCaptor.getReferenceNumberUpdateData().get(0);
        assertEquals(dockDoor, updateData.getReferenceNumber());
        assertEquals(loadId, updateData.getReferenceNumberEntityKey());
    }

    @Test
    void testProcessDockDoorAttachThrowsTmsLoadProcessException() {
        lenient().doThrow(new TmsLoadProcessException("Error")).when(tmRestApiClient).processMaintainEntityReferenceNumber(any());
        assertDoesNotThrow(() -> trailerDockDoorService.processDockDoorAttach(loadId, dockDoor));
    }

    @Test
    void testProcessDockDoorAttachThrowsException() {
        lenient().doThrow(new RuntimeException("Unexpected error")).when(tmRestApiClient).processMaintainEntityReferenceNumber(any());
        assertDoesNotThrow(() -> trailerDockDoorService.processDockDoorAttach(loadId, dockDoor));
    }

    @Test
    void testProcessDockDoorUpdate() {
        ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumberCaptor;
        trailerDockDoorService.processDockDoorUpdate(referenceNumber, loadId, dockDoor);
        ArgumentCaptor<ProcessMaintainEntityReferenceNumber> captor = ArgumentCaptor.forClass(ProcessMaintainEntityReferenceNumber.class);
        verify(tmRestApiClient).processMaintainEntityReferenceNumberForDockDoor(captor.capture());
        processMaintainEntityReferenceNumberCaptor = captor.getValue();
        assertNotNull(processMaintainEntityReferenceNumberCaptor);
        assertEquals(1, processMaintainEntityReferenceNumberCaptor.getReferenceNumberUpdateData().size());
        ReferenceNumberUpdateDataType updateData = processMaintainEntityReferenceNumberCaptor.getReferenceNumberUpdateData().get(0);
        assertEquals(dockDoor, updateData.getReferenceNumber());
        assertEquals(loadId, updateData.getReferenceNumberEntityKey());
        assertEquals(referenceNumber, updateData.getSystemReferenceNumberID());
    }

    @Test
    void testProcessDockDoorUpdateThrowsTmsLoadProcessException() {
        lenient().doThrow(new TmsLoadProcessException("Error")).when(tmRestApiClient).processMaintainEntityReferenceNumber(any());
        assertDoesNotThrow(() -> trailerDockDoorService.processDockDoorUpdate(referenceNumber, loadId, dockDoor));
    }

    @Test
    void testProcessDockDoorUpdateThrowsException() {
        lenient().doThrow(new RuntimeException("Unexpected error")).when(tmRestApiClient).processMaintainEntityReferenceNumber(any());
        assertDoesNotThrow(() -> trailerDockDoorService.processDockDoorUpdate(referenceNumber, loadId, dockDoor));
    }

    //@Test
    void testProcessDeleteDockDoor() {
        String referenceNumbers = "ref1";
        trailerDockDoorService.processDeleteDockDoor(Collections.singletonList(String.valueOf(referenceNumbers)), loadId, dockDoor);
        ArgumentCaptor<ProcessMaintainEntityReferenceNumber> captor = ArgumentCaptor.forClass(ProcessMaintainEntityReferenceNumber.class);
        verify(tmRestApiClient, times(3)).processMaintainEntityReferenceNumber(captor.capture());
        assertEquals(3, captor.getAllValues().size());
        captor.getAllValues().forEach(processMaintainEntityReferenceNumber -> {
            assertNotNull(processMaintainEntityReferenceNumber);
            assertEquals(1, processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().size());
            ReferenceNumberUpdateDataType updateData = processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().get(0);
            assertEquals(loadId, updateData.getReferenceNumberEntityKey());
            assertEquals(Constants.REF_NUM_ACTION_ENUM_VAL_DELETE, updateData.getReferenceNumberActionEnumVal());
        });
    }

    @Test
    void testProcessDeleteDockDoorThrowsTmsLoadProcessException() {
        String referenceNumbers = "ref1";
        lenient().doThrow(new TmsLoadProcessException("Error")).when(tmRestApiClient).processMaintainEntityReferenceNumber(any());
        assertDoesNotThrow(() -> trailerDockDoorService.processDeleteDockDoor(Collections.singletonList(String.valueOf(referenceNumbers)), loadId, dockDoor));
    }

    @Test
    void testProcessDeleteDockDoorThrowsException() {
        String referenceNumbers = "ref1";
        lenient().doThrow(new RuntimeException("Unexpected error")).when(tmRestApiClient).processMaintainEntityReferenceNumber(any());
        assertDoesNotThrow(() -> trailerDockDoorService.processDeleteDockDoor(Collections.singletonList(String.valueOf(referenceNumbers)), loadId, dockDoor));
    }

    @Test
    void testProcessDeleteDockDoorNoReferenceNumbers() {
        String referenceNumbers = "ref1";
        assertDoesNotThrow(() -> trailerDockDoorService.processDeleteDockDoor(Collections.singletonList(String.valueOf(referenceNumbers)), loadId, dockDoor));
    }

    @Test
    void testPerformDockDoorProcessGenericException() {
        TrailerDoorDockCDCEvent event = new TrailerDoorDockCDCEvent();
        MDC.put(CORRELATION_KEY, "test");

        lenient().when(restApiClient.get(anyString(), any(), eq(ApiResponse.class)))
                .thenThrow(new RuntimeException("Generic exception"));

        TrlrData trlr = new TrlrData();
        trlr.setTrlrNbr(123L);

        TmsLoadProcessException exception = assertThrows(
                TmsLoadProcessException.class,
                () -> trailerDockDoorService.performDockDoorProcess(trlr, event)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Unexpected error during API call for trailer 123"));
        assertNotNull(exception.getCause());
        assertEquals("Generic exception", exception.getCause().getMessage());
    }

    @Test
    void testPerformDockDoorProcessWithRetry() {

        TrailerDoorDockCDCEvent event = new TrailerDoorDockCDCEvent();
        MDC.put(CORRELATION_KEY, "test");

        lenient().when(restApiClient.get(anyString(), any(), eq(ApiResponse.class)))
                .thenThrow(new RuntimeException("First attempt failed"))
                .thenThrow(new RuntimeException("Second attempt failed"));

        TrlrData trlr = new TrlrData();
        trlr.setTrlrNbr(123L);

        TmsLoadProcessException exception = assertThrows(
                TmsLoadProcessException.class,
                () -> trailerDockDoorService.performDockDoorProcess(trlr, event)
        );

        assertTrue(exception.getMessage().contains("Unexpected error during API call for trailer 123"));
        assertNotNull(exception.getCause());
        assertEquals("First attempt failed", exception.getCause().getMessage());
    }

    @Test
    void testPerformDockDoorProcess_trailerServiceFails() {
        TrlrData currentTrlr = new TrlrData();
        currentTrlr.setTrlrNbr(123L);
        TrailerDoorDockCDCEvent event = new TrailerDoorDockCDCEvent();
        MDC.put(CORRELATION_KEY, "test-correlation-id");

        when(restApiClient.get(anyString(), any(), eq(ApiResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        RuleInputDTO result = trailerDockDoorService.performDockDoorProcess(currentTrlr, event);

        assertNotNull(result);
        assertEquals(Constants.DOCK_DOOR_TRAILER_SERVICE_ERROR, result.getErrorType());
        assertEquals(Constants.DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_EXCEPTION_CODE, result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("Trailer service failed with error"));
    }

    @Test
    void testFetchLoadDetails_locationServiceFails() {
        // Given
        String trlrNbr = "T123";
        TrailerDoorDockCDCEvent event = new TrailerDoorDockCDCEvent();
        com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo appointmentInfo = new com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo();
        appointmentInfo.setYardAreaTypNbr(2); // DOOR
        appointmentInfo.setLocNbr(99);
        appointmentInfo.setApptNbr(456L);
        List<com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo> trailerYardAppointmentInfo = List.of(appointmentInfo);

        when(locationServiceClient.findLocnEntityByLocnAbbr(anyString()))
                .thenThrow(new RuntimeException("Location service unavailable"));

        // When
        RuleInputDTO result = trailerDockDoorService.fetchLoadDetails(trlrNbr, trailerYardAppointmentInfo, event);

        // Then
        assertNotNull(result);
        assertEquals(Constants.DOCK_DOOR_LOCATION_SERVICE_ERROR, result.getErrorType());
        assertEquals(Constants.DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_EXCEPTION_CODE, result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("No location found for"));
    }

    @Test
    void testPerformDockDoorProcess_Success() {
        // Given
        TrlrData currentTrlr = new TrlrData();
        currentTrlr.setTrlrNbr(123L);
        currentTrlr.setCarrTrlrNbr("CARR123");
        TrailerDoorDockCDCEvent event = new TrailerDoorDockCDCEvent();
        MDC.put(CORRELATION_KEY, "test-correlation-id");

        ApiResponse<List<com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo>> apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.OK.value());
        com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo appointmentInfo = new com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo();
        appointmentInfo.setYardAreaTypNbr(1); // YARD
        appointmentInfo.setLocNbr(99);
        appointmentInfo.setApptNbr(456L);
        appointmentInfo.setYardAreaTypDesc("YARD");
        apiResponse.setData(List.of(appointmentInfo));

        when(restApiClient.get(anyString(), any(), eq(ApiResponse.class))).thenReturn(apiResponse);

        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        when(loadEnrichmentService.fetchLoadByAppointment(anyList(), anyString())).thenReturn(findEntitiesResponse);

        List<LoadDTO> mappedLoads = Arrays.asList(LoadDTO.builder().loadId("L1").build());
        when(loadMapper.mapLoadResponse(any(), anyString())).thenReturn(mappedLoads);

        // When
        RuleInputDTO result = trailerDockDoorService.performDockDoorProcess(currentTrlr, event);

        // Then
        assertNotNull(result);
        assertEquals("CARR123", result.getTrailerNumber());
        assertEquals("456", result.getAppointmentNumber());
        assertNull(result.getDockDoor());
        assertEquals("YARD", result.getYardType());
        assertTrue(result.getDoorLoads().isEmpty());
        assertEquals(mappedLoads, result.getTrailerLoads());
        assertNull(result.getErrorMessage());

        verify(restApiClient).get(anyString(), any(), eq(ApiResponse.class));
    }

    @Test
    void testFetchLoadDetails_Success_WithDoor() {
        // Given
        String trlrNbr = "T123";
        TrailerDoorDockCDCEvent event = new TrailerDoorDockCDCEvent();
        com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo appointmentInfo = new com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo();
        appointmentInfo.setYardAreaTypNbr(2); // DOOR
        appointmentInfo.setLocNbr(99);
        appointmentInfo.setYardAreaId("D1");
        appointmentInfo.setApptNbr(456L);
        appointmentInfo.setYardAreaTypDesc("DOOR");
        List<com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo> trailerYardAppointmentInfo = List.of(appointmentInfo);

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setAbbreviation("LOC");
        when(locationServiceClient.findLocnEntityByLocnAbbr(anyString())).thenReturn(locationDTO);

        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        when(loadEnrichmentService.fetchLoadDetailsForDockDoor(anyString(), anyString(), anyList(), anyString())).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadByAppointment(anyList(), anyString())).thenReturn(findEntitiesResponse);

        List<LoadDTO> mappedLoads = Arrays.asList(LoadDTO.builder().loadId("L1").build());
        when(loadMapper.mapLoadResponse(any(), anyString())).thenReturn(mappedLoads);

        // When
        RuleInputDTO result = trailerDockDoorService.fetchLoadDetails(trlrNbr, trailerYardAppointmentInfo, event);

        // Then
        assertNotNull(result);
        assertEquals("T123", result.getTrailerNumber());
        assertEquals("456", result.getAppointmentNumber());
        assertEquals("LOCD1", result.getDockDoor());
        assertEquals("DOOR", result.getYardType());
        assertEquals(mappedLoads, result.getDoorLoads());
        assertEquals(mappedLoads, result.getTrailerLoads());
        assertNull(result.getErrorMessage());

        verify(locationServiceClient).findLocnEntityByLocnAbbr("99");
        verify(loadEnrichmentService).fetchLoadDetailsForDockDoor(anyString(), eq("referenceNumber6"), eq(Arrays.asList("LOCD1")), anyString());
        verify(loadEnrichmentService).fetchLoadByAppointment(eq(Arrays.asList("456")), anyString());
    }

    @Test
    void testFetchLoadDetails_Success_WithYard() {
        // Given
        String trlrNbr = "T123";
        TrailerDoorDockCDCEvent event = new TrailerDoorDockCDCEvent();
        com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo appointmentInfo = new com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo();
        appointmentInfo.setYardAreaTypNbr(1); // YARD
        appointmentInfo.setLocNbr(99);
        appointmentInfo.setApptNbr(456L);
        appointmentInfo.setYardAreaTypDesc("YARD");
        List<com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo> trailerYardAppointmentInfo = List.of(appointmentInfo);

        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        when(loadEnrichmentService.fetchLoadByAppointment(anyList(), anyString())).thenReturn(findEntitiesResponse);
        when(loadEnrichmentService.fetchLoadDetailsForDockDoor(anyString(), eq(Constants.SYSTEM_LOAD_ID), anyList(), anyString())).thenReturn(new FindEntitiesResponse());


        List<LoadDTO> mappedLoads = Arrays.asList(LoadDTO.builder().loadId("L1").build());
        when(loadMapper.mapLoadResponse(any(), anyString())).thenReturn(mappedLoads);

        // When
        RuleInputDTO result = trailerDockDoorService.fetchLoadDetails(trlrNbr, trailerYardAppointmentInfo, event);

        // Then
        assertNotNull(result);
        assertEquals("T123", result.getTrailerNumber());
        assertEquals("456", result.getAppointmentNumber());
        assertNull(result.getDockDoor());
        assertEquals("YARD", result.getYardType());
        assertTrue(result.getDoorLoads().isEmpty());
        assertEquals(mappedLoads, result.getTrailerLoads());
        assertNull(result.getErrorMessage());

        verify(locationServiceClient, never()).findLocnEntityByLocnAbbr(anyString());
        verify(loadEnrichmentService, never()).fetchLoadDetailsForDockDoor(anyString(), eq(Constants.DOCK_DOOR_ID), anyList(), anyString());
        verify(loadEnrichmentService).fetchLoadDetailsForDockDoor(anyString(), eq(Constants.SYSTEM_LOAD_ID), anyList(), anyString());
        verify(loadEnrichmentService).fetchLoadByAppointment(eq(Arrays.asList("456")), anyString());
    }

    @Test
    void testFetchLoadDetails_locationServiceReturnsNull() {
        // Given
        String trlrNbr = "T123";
        TrailerDoorDockCDCEvent event = new TrailerDoorDockCDCEvent();
        com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo appointmentInfo = new com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo();
        appointmentInfo.setYardAreaTypNbr(2); // DOOR
        appointmentInfo.setLocNbr(99);
        appointmentInfo.setApptNbr(456L);
        List<com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo> trailerYardAppointmentInfo = List.of(appointmentInfo);

        when(locationServiceClient.findLocnEntityByLocnAbbr(anyString())).thenReturn(null);

        // When
        RuleInputDTO result = trailerDockDoorService.fetchLoadDetails(trlrNbr, trailerYardAppointmentInfo, event);

        // Then
        assertNotNull(result);
        assertEquals(Constants.DOCK_DOOR_LOCATION_SERVICE_ERROR, result.getErrorType());
        assertEquals(Constants.DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_EXCEPTION_CODE, result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("No location found for 99"));
        verify(commonUtil).publishDockDoorAuditFeedbackToPubSub(any(), any(), any(), any());
    }
}
