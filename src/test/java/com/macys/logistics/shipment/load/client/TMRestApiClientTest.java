package com.macys.logistics.shipment.load.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.inbound.RestProcessShipmentOrderDelete;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.logging.TmsLoadMetricsHelper;
import com.macys.logistics.shipment.load.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TMRestApiClientTest {

    public static final String DEFAULT_REF_NUM_CATEGORY_ENUM_VAL = "RNC_LD_LEG";
    public static final String REF_NUM_ACTION_ENUM_VAL_ADD = "AT_ADD";
    public static final String SEAL_CND = "SEAL_CND";
    public static final String SEAL_TYPE = "SEAL_TYPE";
    public static final String COUNT_SHIPMENT = "CountOfShipmentsDroppedAtStop";
    public static final String SYSTEM_STOP_ID = "SystemStopID";
    public static final String SYSTEM_LOAD_ID = "systemLoadID";
    @Mock
    private TmsLoadMetricsHelper metricsHelper;
    @Mock
    private TmRestApiService tmRestApiService;
    @InjectMocks
    private TMRestApiClient tmRestApiClient;
    @Mock
    ObjectMapper objectMapper;
    @Value("${tm.rest.api.path.process.load.retrieve}")
    private String processLoadRetrievePath;
    @Value("${tm.rest.api.path.process.load.referenceNumber}")
    private String referenceNumberPath;
    @Value("${tm.rest.api.path.process.stops.search}")
    private String findEntitiesPath;

    @Value("${tm.rest.api.path.process.maintainAppointment}")
    private String processStopPath;

    @Value("${tm.rest.api.path.process.trailer.update}")
    private String trailerUpdatePath;

    @Value("${tm.rest.api.path.process.rate}")
    private String processRatePath;

    @Value("${tm.rest.api.path.process.load.cancel}")
    private String processLoadCancelPath;
    @Value("${tm.rest.api.path.process.load.tender.cancel}")
    private String processLoadTenderCancelPath;
    @Value("${tm.rest.api.path.process.load.tender}")
    private String processTenderPath;

    @Value("${tm.rest.api.path.process.search.shipment}")
    private String processShipment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(tmRestApiClient, "processTrailerActivitiesPath",
                "mockProcessTrailerActivitiesPath");
        ReflectionTestUtils.setField(tmRestApiClient, "processShipmentOrderCreatePath",
                "mockProcessShipmentOrderCreatePath");
    }

    @Test
    void test_ProcessLoadRetrieve_success() {
        ProcessLoadRetrieve retrieveInput = new ProcessLoadRetrieve();
        retrieveInput.setSystemLoadID(List.of("12345"));
        ProcessLoadRetrieveResponse expectedResponse = new ProcessLoadRetrieveResponse();
        when(tmRestApiService.sendRequest(eq(processLoadRetrievePath), ArgumentMatchers.any(ProcessLoadRetrieve.class),
                eq(ProcessLoadRetrieveResponse.class)))
                .thenReturn(expectedResponse);
        ProcessLoadRetrieveResponse actualResponse = tmRestApiClient.processLoadRetrieve(retrieveInput);
        assertEquals(expectedResponse, actualResponse);
        verify(tmRestApiService).sendRequest(processLoadRetrievePath, retrieveInput, ProcessLoadRetrieveResponse.class);

    }

    @Test
    void test_ProcessLoadRetrieve_noResponse() {
        ProcessLoadRetrieve retrieveInput = new ProcessLoadRetrieve();
        retrieveInput.setSystemLoadID(List.of("12345"));
        when(tmRestApiService.sendRequest(eq(processLoadRetrievePath), ArgumentMatchers.any(ProcessLoadRetrieve.class),
                eq(ProcessLoadRetrieveResponse.class)))
                .thenReturn(null);
        ProcessLoadRetrieveResponse actualResponse = tmRestApiClient.processLoadRetrieve(retrieveInput);
        assertNull(actualResponse);
        verify(tmRestApiService).sendRequest(processLoadRetrievePath, retrieveInput, ProcessLoadRetrieveResponse.class);

    }

    @Test
    void test_processMaintainEntityReferenceNumber_success() {
        ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
        ReferenceNumberUpdateDataType referenceNumberUpdateDataType = new ReferenceNumberUpdateDataType();
        referenceNumberUpdateDataType.setReferenceNumberActionEnumVal(REF_NUM_ACTION_ENUM_VAL_ADD);
        referenceNumberUpdateDataType.setReferenceNumberCategoryEnumVal(DEFAULT_REF_NUM_CATEGORY_ENUM_VAL);
        referenceNumberUpdateDataType.setReferenceNumber("Intact");
        referenceNumberUpdateDataType.setReferenceNumberEntityKey("12345");
        referenceNumberUpdateDataType.setReferenceNumberTypeCode(SEAL_CND);

        ReferenceNumberUpdateDataType referenceNumberUpdateDataTypeSealType = new ReferenceNumberUpdateDataType();
        referenceNumberUpdateDataTypeSealType.setReferenceNumberActionEnumVal(REF_NUM_ACTION_ENUM_VAL_ADD);
        referenceNumberUpdateDataTypeSealType.setReferenceNumberCategoryEnumVal(DEFAULT_REF_NUM_CATEGORY_ENUM_VAL);
        referenceNumberUpdateDataTypeSealType.setReferenceNumber("Other");
        referenceNumberUpdateDataTypeSealType.setReferenceNumberEntityKey("12345");
        referenceNumberUpdateDataTypeSealType.setReferenceNumberTypeCode(SEAL_TYPE);
        processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(referenceNumberUpdateDataType);
        processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(referenceNumberUpdateDataTypeSealType);
        tmRestApiClient.processMaintainEntityReferenceNumber(processMaintainEntityReferenceNumber);
        verify(tmRestApiService).sendRequest(referenceNumberPath, processMaintainEntityReferenceNumber,
                ProcessMaintainEntityReferenceNumberResponse.class);

    }

    @Test
    void test_processMaintainEntityReferenceNumber_exception() throws IOException {
        ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
        ReferenceNumberUpdateDataType referenceNumberUpdateDataType = new ReferenceNumberUpdateDataType();
        referenceNumberUpdateDataType.setReferenceNumberActionEnumVal(REF_NUM_ACTION_ENUM_VAL_ADD);
        referenceNumberUpdateDataType.setReferenceNumberCategoryEnumVal(DEFAULT_REF_NUM_CATEGORY_ENUM_VAL);
        referenceNumberUpdateDataType.setReferenceNumber("Intact");
        referenceNumberUpdateDataType.setReferenceNumberEntityKey("");
        referenceNumberUpdateDataType.setReferenceNumberTypeCode(SEAL_CND);

        ReferenceNumberUpdateDataType referenceNumberUpdateDataTypeSealType = new ReferenceNumberUpdateDataType();
        referenceNumberUpdateDataTypeSealType.setReferenceNumberActionEnumVal(REF_NUM_ACTION_ENUM_VAL_ADD);
        referenceNumberUpdateDataTypeSealType.setReferenceNumberCategoryEnumVal(DEFAULT_REF_NUM_CATEGORY_ENUM_VAL);
        referenceNumberUpdateDataTypeSealType.setReferenceNumber("Other");
        referenceNumberUpdateDataTypeSealType.setReferenceNumberEntityKey("");
        referenceNumberUpdateDataTypeSealType.setReferenceNumberTypeCode(SEAL_TYPE);

        processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(referenceNumberUpdateDataType);
        processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(referenceNumberUpdateDataTypeSealType);
        tmRestApiClient.processMaintainEntityReferenceNumber(processMaintainEntityReferenceNumber);
        verify(tmRestApiService).sendRequest(referenceNumberPath, processMaintainEntityReferenceNumber,
                ProcessMaintainEntityReferenceNumberResponse.class);
        verify(tmRestApiService).sendRequest(referenceNumberPath, processMaintainEntityReferenceNumber,
                ProcessMaintainEntityReferenceNumberResponse.class);

    }

    @Test
    void test_findEntities_loadId_success() throws IOException {
        RestFindEntity findEntities = new RestFindEntity();
        RestFilterType filterType = new RestFilterType();
        filterType.setName(SYSTEM_STOP_ID);
        filterType.setOp(Constants.EQ);
        filterType.setValue(List.of("12345"));
        findEntities.setFilter(List.of(filterType));
        FindEntitiesResponse expectedResponse = new FindEntitiesResponse();
        when(tmRestApiService.sendRequestToGetLoadID(eq(findEntitiesPath), ArgumentMatchers.any(RestFindEntity.class)))
                .thenReturn(expectedResponse);
        FindEntitiesResponse actualResponse = tmRestApiClient.findLoadID(findEntities);
        assertEquals(expectedResponse, actualResponse);
        verify(tmRestApiService).sendRequestToGetLoadID(findEntitiesPath, findEntities);

    }

    @Test
    void test_findEntities_loadId_exception() throws JsonProcessingException {
        RestFindEntity findEntities = new RestFindEntity();
        RestFilterType filterType = new RestFilterType();
        filterType.setName(SYSTEM_STOP_ID);
        filterType.setOp(Constants.EQ);
        filterType.setValue(List.of("12345"));
        findEntities.setFilter(List.of(filterType));
        when(tmRestApiService.sendRequestToGetLoadID(eq(findEntitiesPath), ArgumentMatchers.any(RestFindEntity.class)))
                .thenReturn(null);
        FindEntitiesResponse actualResponse = tmRestApiClient.findLoadID(findEntities);
        assertNull(actualResponse);
        verify(tmRestApiService).sendRequestToGetLoadID(findEntitiesPath, findEntities);

    }

    @Test
    void test_findEntities_load_details_success() throws IOException {
        RestFindEntity findEntities = new RestFindEntity();
        RestFilterType filterType = new RestFilterType();
        filterType.setName(SYSTEM_LOAD_ID);
        filterType.setOp(Constants.EQ);
        filterType.setValue(List.of("12345"));
        findEntities.setFilter(List.of(filterType));
        FindEntitiesResponse expectedResponse = new FindEntitiesResponse();
        when(tmRestApiService.sendRequestToGetLoadDetails(eq(findEntitiesPath),
                ArgumentMatchers.any(RestFindEntity.class)))
                .thenReturn(expectedResponse);
        FindEntitiesResponse actualResponse = tmRestApiClient.restFindEntities(findEntities);
        assertEquals(expectedResponse, actualResponse);
        verify(tmRestApiService).sendRequestToGetLoadDetails(findEntitiesPath, findEntities);

    }

    @Test
    void test_findEntities_load_details_exception() {
        RestFindEntity findEntities = new RestFindEntity();
        RestFilterType filterType = new RestFilterType();
        filterType.setName(SYSTEM_LOAD_ID);
        filterType.setOp(Constants.EQ);
        filterType.setValue(List.of("12345"));
        findEntities.setFilter(List.of(filterType));
        when(tmRestApiService.sendRequestToGetLoadDetails(eq(findEntitiesPath),
                ArgumentMatchers.any(RestFindEntity.class)))
                .thenReturn(null);
        FindEntitiesResponse actualResponse = tmRestApiClient.restFindEntities(findEntities);
        assertNull(actualResponse);
        verify(tmRestApiService).sendRequestToGetLoadDetails(findEntitiesPath, findEntities);

    }

    @Test
    void testProcessTenderSuccess() {
        ProcessTender processTender = DataFactory.createTestProcessTender();
        tmRestApiClient.processTender(processTender);
        verify(tmRestApiService).sentRequest(eq(processTenderPath), ArgumentMatchers.any(ProcessTender.class),
                eq(ProcessTenderResponse.class));
    }

    @Test
    void testProcessTenderException() {
        ProcessTender processTender = DataFactory.createTestProcessTender();
        doThrow(new RuntimeException("Test exception")).when(tmRestApiService).sentRequest(anyString(),
                eq(processTender), eq(ProcessTenderResponse.class));
        tmRestApiClient.processTender(processTender);
        verify(tmRestApiService).sentRequest(eq(processTenderPath), ArgumentMatchers.any(ProcessTender.class),
                eq(ProcessTenderResponse.class));

    }

    @Test
    void testProcessTrailerNumberUpdateSuccess() {
        ProcessLoadUpdate processLoadUpdate = DataFactory.createTestProcessLoadUpdate();
        tmRestApiClient.processTrailerNumberUpdate(processLoadUpdate);
        verify(metricsHelper).populateTransactionDomainAndType(anyString(), eq("ProcessLoadUpdateResponse"));
        verify(tmRestApiService).sentRequest(eq(trailerUpdatePath), ArgumentMatchers.any(ProcessLoadUpdate.class),
                eq(ProcessLoadUpdateResponse.class));
    }

    @Test
    void testProcessLoadRateSuccess() {
        ProcessLoadRate processLoadRate = DataFactory.createTestProcessLoadRate();
        tmRestApiClient.processLoadRate(processLoadRate);
        verify(metricsHelper).populateTransactionDomainAndType(anyString(), eq("ProcessLoadRateResponse"));
        verify(tmRestApiService).sentRequest(eq(processRatePath), ArgumentMatchers.any(ProcessLoadRate.class),
                eq(ProcessLoadRateResponse.class));
    }

    @Test
    void testProcessLoadRateException() {
        ProcessLoadRate processLoadRate = DataFactory.createTestProcessLoadRate();
        doThrow(new RuntimeException("Test exception")).when(tmRestApiService).sentRequest(anyString(),
                eq(processLoadRate), eq(ProcessLoadRateResponse.class));
        tmRestApiClient.processLoadRate(processLoadRate);
        verify(tmRestApiService).sentRequest(eq(processRatePath), ArgumentMatchers.any(ProcessLoadRate.class),
                eq(ProcessLoadRateResponse.class));
    }

    @Test
    void testProcessLoadCancelSuccess() {
        ProcessLoadCancel processLoadCancel = DataFactory.createTestProcessLoadCancel();
        tmRestApiClient.processLoadCancel(processLoadCancel);
        verify(tmRestApiService).sentRequest(eq(processLoadCancelPath), ArgumentMatchers.any(ProcessLoadCancel.class),
                eq(ProcessLoadCancelResponse.class));
    }

    @Test
    void testProcessLoadCancelException() {
        ProcessLoadCancel processLoadCancel = DataFactory.createTestProcessLoadCancel();
        doThrow(new RuntimeException("Test exception")).when(tmRestApiService).sentRequest(anyString(),
                eq(processLoadCancel), eq(ProcessLoadCancelResponse.class));
        tmRestApiClient.processLoadCancel(processLoadCancel);
        verify(tmRestApiService).sentRequest(eq(processLoadCancelPath), ArgumentMatchers.any(ProcessLoadCancel.class),
                eq(ProcessLoadCancelResponse.class));

    }

    @Test
    void testProcessTenderCancelSuccess() {
        ProcessTenderCancel processTenderCancel = DataFactory.createTestProcessTenderCancel();
        processTenderCancel.getLoadTenderData().get(0).setCurrentLoadOperationalStatusEnumVal(Constants.TENDERED);
        tmRestApiClient.processTenderCancel(processTenderCancel);
        verify(tmRestApiService).sentRequest(eq(processLoadTenderCancelPath),
                ArgumentMatchers.any(ProcessTenderCancel.class), eq(ProcessTenderCancelResponse.class));
    }

    @Test
    void testProcessTenderAcceptedCancelSuccess() {
        ProcessTenderCancel processTenderCancel = DataFactory.createTestProcessTenderCancel();
        processTenderCancel.getLoadTenderData().get(0)
                .setCurrentLoadOperationalStatusEnumVal(Constants.TENDERED_ACCEPTED);
        tmRestApiClient.processTenderCancel(processTenderCancel);
        verify(tmRestApiService).sentRequest(eq(processLoadTenderCancelPath),
                ArgumentMatchers.any(ProcessTenderCancel.class), eq(ProcessTenderCancelResponse.class));
    }

    @Test
    void testProcessTenderCancelException() {
        ProcessTenderCancel processTenderCancel = DataFactory.createTestProcessTenderCancel();
        doThrow(new RuntimeException("Test exception")).when(tmRestApiService).sentRequest(anyString(),
                eq(processTenderCancel), eq(ProcessTenderCancelResponse.class));
        tmRestApiClient.processTenderCancel(processTenderCancel);
        verify(tmRestApiService).sentRequest(eq(processLoadTenderCancelPath),
                ArgumentMatchers.any(ProcessTenderCancel.class), eq(ProcessTenderCancelResponse.class));

    }

    @Test
    void testProcessStopAppointmentSuccess() throws DatatypeConfigurationException {
        ProcessStopAppointment restProcessRequest = DataFactory.getProcessStopAppointment();
        tmRestApiClient.processStopAppointment(restProcessRequest);
        verify(tmRestApiService).sendRequestForProcessStopAppointment(processStopPath, restProcessRequest);
    }

    @Test
    void testProcessStopAppointmentSuccessException() throws DatatypeConfigurationException {
        ProcessStopAppointment restProcessRequest = DataFactory.getProcessStopAppointment();
        doThrow(new RuntimeException("Test exception")).when(tmRestApiService)
                .sendRequestForProcessStopAppointment(anyString(), eq(restProcessRequest));
        tmRestApiClient.processStopAppointment(restProcessRequest);
        verify(tmRestApiService).sendRequestForProcessStopAppointment(eq(processStopPath),
                ArgumentMatchers.any(ProcessStopAppointment.class));

    }

    @Test
    void testProcessTrailerNumberUpdateException() {
        ProcessLoadUpdate processLoadUpdate = DataFactory.createTestProcessLoadUpdate();
        doThrow(new RuntimeException("Test exception")).when(tmRestApiService).sentRequest(anyString(),
                eq(processLoadUpdate), eq(ProcessLoadUpdateResponse.class));
        tmRestApiClient.processTrailerNumberUpdate(processLoadUpdate);
        verify(tmRestApiService).sentRequest(eq(trailerUpdatePath), ArgumentMatchers.any(ProcessLoadUpdate.class),
                eq(ProcessLoadUpdateResponse.class));
    }

    @Test
    void testRestProcessTrailerActivitiesSuccess() throws JsonProcessingException {
        RestProcessTrailerActivities mockTrailerActivities = DataFactory.getRestProcessTrailerActivities();
        String response = DataFactory.getProcessTrailerActivitiesResponse_Success().toString();
        when(tmRestApiService.sentRestRequest(any(), any())).thenReturn(response);
        tmRestApiClient.restProcessTrailerActivities(mockTrailerActivities);
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockTrailerActivities));
    }

    @Test
    void testRestProcessTrailerActivitiesException() throws JsonProcessingException {
        RestProcessTrailerActivities mockTrailerActivities = DataFactory.getRestProcessTrailerActivities();
        when(tmRestApiService.sentRestRequest(any(), any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        TmsLoadProcessException thrown = assertThrows(TmsLoadProcessException.class,
                () -> tmRestApiClient.restProcessTrailerActivities(mockTrailerActivities));
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockTrailerActivities));
    }

    @Test
    void testRestProcessCreateShipmentSuccess() throws JsonProcessingException {
        RestProcessShipmentOrderCreate mockShipmentOrderCreate = DataFactory.getRestProcessShipmentOrderCreate();
        String response = DataFactory.processShipmentOrderCreateResponse(true).toString();
        when(tmRestApiService.sentRestRequest(any(), any())).thenReturn(response);
        tmRestApiClient.restProcessCreateShipment(mockShipmentOrderCreate);
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockShipmentOrderCreate));
    }

    @Test
    void testRestProcessCreateShipmentException() throws JsonProcessingException {
        RestProcessShipmentOrderCreate mockShipmentOrderCreate = DataFactory.getRestProcessShipmentOrderCreate();
        when(tmRestApiService.sentRestRequest(any(), any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        TmsLoadProcessException thrown = assertThrows(TmsLoadProcessException.class,
                () -> tmRestApiClient.restProcessCreateShipment(mockShipmentOrderCreate));
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockShipmentOrderCreate));
    }

    @Test
    void testProcessShipmentRemoveSuccess() throws JsonProcessingException {
        ProcessShipmentRemove mockShipmentRemoveRequest = DataFactory.processShipmentRemove();
        String response = DataFactory.processShipmentRemoveResponse(true).toString();
        when(tmRestApiService.sentRestRequest(any(), any())).thenReturn(response);
        tmRestApiClient.processShipmentRemove(mockShipmentRemoveRequest);
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockShipmentRemoveRequest));
    }

    @Test
    void testProcessShipmentRemoveException() throws JsonProcessingException {
        ProcessShipmentRemove mockShipmentRemoveRequest = DataFactory.processShipmentRemove();
        when(tmRestApiService.sentRestRequest(any(), any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        assertThrows(TmsLoadProcessException.class,
                () -> tmRestApiClient.processShipmentRemove(mockShipmentRemoveRequest));
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockShipmentRemoveRequest));
    }

    @Test
    void testProcessAssignShipmentsToLoadSuccess() throws JsonProcessingException {
        ProcessAssignToLoad mockProcessAssignToLoad = DataFactory.processAssignToLoad();
        String response = DataFactory.processAssignToLoadResponse(true).toString();
        when(tmRestApiService.sentRestRequest(any(), any())).thenReturn(response);
        tmRestApiClient.processAssignShipmentsToLoad(mockProcessAssignToLoad);
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockProcessAssignToLoad));
    }

    @Test
    void testProcessAssignShipmentsToLoadException() throws JsonProcessingException {
        ProcessAssignToLoad mockProcessAssignToLoad = DataFactory.processAssignToLoad();
        when(tmRestApiService.sentRestRequest(any(), any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        assertThrows(TmsLoadProcessException.class,
                () -> tmRestApiClient.processAssignShipmentsToLoad(mockProcessAssignToLoad));
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockProcessAssignToLoad));
    }

    @Test
    void testprocessTenderAcceptSuccess() throws DatatypeConfigurationException, ParseException {
        ProcessTenderAccept restProcessRequest = DataFactory.getProcessTenderAccept();
        tmRestApiClient.processTenderAccept(restProcessRequest);
        verify(tmRestApiService).sendRequest(eq(processStopPath), ArgumentMatchers.any(ProcessTenderAccept.class),
                eq(ProcessTenderAcceptResponse.class));
    }

    @Test
    void testprocessTenderAcceptException() throws DatatypeConfigurationException, ParseException {
        ProcessTenderAccept restProcessRequest = DataFactory.getProcessTenderAccept();
        when(tmRestApiService.sendRequest(processStopPath, restProcessRequest, ProcessTenderAcceptResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        assertThrows(TmsLoadProcessException.class,
                () -> tmRestApiClient.processTenderAccept(restProcessRequest));
        verify(tmRestApiService).sendRequest(eq(processStopPath), ArgumentMatchers.any(ProcessTenderAccept.class),
                eq(ProcessTenderAcceptResponse.class));

    }

    @Test
    void testSearchShipment_Success() throws Exception {
        RestFindEntity findEntities = new RestFindEntity();
        ShipmentResponse mockResponse = new ShipmentResponse();
        when(tmRestApiService.sentRequest(any(), eq(findEntities), eq(ShipmentResponse.class)))
                .thenReturn(mockResponse);
        ShipmentResponse response = tmRestApiClient.searchShipment(findEntities);
        assertNotNull(response);
        assertEquals(mockResponse, response);
        verify(metricsHelper).populateTransactionDomainAndType(
                "TM_REST_API_CALL-TMS_SHIPMENT_SEARCH", "ShipmentResponse");
        verify(tmRestApiService).sentRequest(any(), eq(findEntities), eq(ShipmentResponse.class));
    }

    @Test
    void testSearchShipment_Exception() {
        RestFindEntity findEntities = new RestFindEntity();
        when(tmRestApiService.sentRequest(any(), eq(findEntities), eq(ShipmentResponse.class)))
                .thenThrow(new RuntimeException("Test exception"));
        ShipmentResponse response = tmRestApiClient.searchShipment(findEntities);
        assertNotNull(response);
        verify(metricsHelper).populateTransactionDomainAndType(
                "TM_REST_API_CALL-TMS_SHIPMENT_SEARCH", "ShipmentResponse");
        verify(tmRestApiService).sentRequest(any(), eq(findEntities), eq(ShipmentResponse.class));
    }

    @Test
    void testRestProcessDeleteShipmentSuccess() throws JsonProcessingException {
        RestProcessShipmentOrderDelete mockShipmentOrderDelete = DataFactory.getRestProcessShipmentOrderDelete();
        String response = DataFactory.processShipmentOrderCreateResponse(true).toString();
        when(tmRestApiService.sentRestRequest(any(), any())).thenReturn(response);
        tmRestApiClient.restProcessDeleteShipment(mockShipmentOrderDelete);
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockShipmentOrderDelete));
    }

    @Test
    void testRestProcessDeleteShipmentException() throws JsonProcessingException {
        RestProcessShipmentOrderDelete mockShipmentOrderDelete = DataFactory.getRestProcessShipmentOrderDelete();
        when(tmRestApiService.sentRestRequest(any(), any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        TmsLoadProcessException thrown = assertThrows(TmsLoadProcessException.class,
                () -> tmRestApiClient.restProcessDeleteShipment(mockShipmentOrderDelete));
        verify(tmRestApiService, times(1)).sentRestRequest(any(), eq(mockShipmentOrderDelete));
    }

    @Test
    void testProcessTrailerActivities_Success() {
        RestProcessTrailerActivities trailerActivities = new RestProcessTrailerActivities();
        ProcessTrailerActivitiesResponse expectedResponse = new ProcessTrailerActivitiesResponse();
        when(tmRestApiService.sendRequest(anyString(), any(RestProcessTrailerActivities.class),
                eq(ProcessTrailerActivitiesResponse.class)))
                .thenReturn(expectedResponse);
        ProcessTrailerActivitiesResponse actualResponse = tmRestApiClient.processTrailerActivities(trailerActivities);
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(metricsHelper).populateTransactionDomainAndType(anyString(), eq("ProcessTrailerActivitiesResponse"));
        verify(tmRestApiService).sendRequest(anyString(), eq(trailerActivities),
                eq(ProcessTrailerActivitiesResponse.class));
    }

    @Test
    void testProcessTrailerActivities_Exception() {
        RestProcessTrailerActivities trailerActivities = new RestProcessTrailerActivities();
        when(tmRestApiService.sendRequest(anyString(), any(RestProcessTrailerActivities.class),
                eq(ProcessTrailerActivitiesResponse.class))).thenThrow(new RuntimeException("API call failed"));
        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.processTrailerActivities(trailerActivities));
    }
}