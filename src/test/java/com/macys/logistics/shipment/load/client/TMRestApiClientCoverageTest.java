package com.macys.logistics.shipment.load.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.AppointmentSealConditionDetail;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.inbound.RestProcessShipmentOrderDelete;
import com.macys.logistics.shipment.load.dto.inbound.RestShipmentDelete;
import com.macys.logistics.shipment.load.dto.request.RestAddPreBuiltLoad;
import com.macys.logistics.shipment.load.exception.TmRestApiException;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.logging.TmsLoadMetricsHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TMRestApiClientCoverageTest {

    @Mock
    private TmsLoadMetricsHelper metricsHelper;
    @Mock
    private TmRestApiService tmRestApiService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private TMRestApiClient tmRestApiClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tmRestApiClient, "processLoadRetrievePath", "/retrieve");
        ReflectionTestUtils.setField(tmRestApiClient, "processProcessMaintainEntityReferenceNumberPath", "/refNum");
        ReflectionTestUtils.setField(tmRestApiClient, "findEntitieStopDetailsPath", "/stops");
        ReflectionTestUtils.setField(tmRestApiClient, "findEntitieLoadDetailsPath", "/loads");
        ReflectionTestUtils.setField(tmRestApiClient, "processStopAppointmentPath", "/appointment");
        ReflectionTestUtils.setField(tmRestApiClient, "processLoadRatePath", "/rate");
        ReflectionTestUtils.setField(tmRestApiClient, "processLoadUpdatePath", "/update");
        ReflectionTestUtils.setField(tmRestApiClient, "processLoadCancelPath", "/cancel");
        ReflectionTestUtils.setField(tmRestApiClient, "processTenderCancelPath", "/tenderCancel");
        ReflectionTestUtils.setField(tmRestApiClient, "processReturnToOpenPath", "/returnToOpen");
        ReflectionTestUtils.setField(tmRestApiClient, "processTenderPath", "/tender");
        ReflectionTestUtils.setField(tmRestApiClient, "processTrailerActivitiesPath", "/trailerAct");
        ReflectionTestUtils.setField(tmRestApiClient, "processShipmentOrderCreatePath", "/shipCreate");
        ReflectionTestUtils.setField(tmRestApiClient, "processShipmentOrderUpdatePath", "/shipUpdate");
        ReflectionTestUtils.setField(tmRestApiClient, "processShipmentOrderDeletePath", "/shipDelete");
        ReflectionTestUtils.setField(tmRestApiClient, "processShipmentRemovePath", "/shipRemove");
        ReflectionTestUtils.setField(tmRestApiClient, "processAssignToLoadPath", "/assignToLoad");
        ReflectionTestUtils.setField(tmRestApiClient, "sealConditionAppointmentPath", "/sealCond/");
        ReflectionTestUtils.setField(tmRestApiClient, "addPreBuiltLoadPath", "/addPreBuilt");
        ReflectionTestUtils.setField(tmRestApiClient, "processTenderAcceptPath", "/tenderAccept");
        ReflectionTestUtils.setField(tmRestApiClient, "processTenderRejectPath", "/tenderReject");
        ReflectionTestUtils.setField(tmRestApiClient, "fetchAttachementDataPath", "/fetchAttach");
        ReflectionTestUtils.setField(tmRestApiClient, "processShipment", "/searchShipment");
    }

    // ======= processLoadRetrieve =======

    @Test
    void processLoadRetrieve_success() {
        ProcessLoadRetrieve input = new ProcessLoadRetrieve();
        ProcessLoadRetrieveResponse expected = new ProcessLoadRetrieveResponse();
        when(tmRestApiService.sendRequest(anyString(), any(ProcessLoadRetrieve.class),
                eq(ProcessLoadRetrieveResponse.class)))
                .thenReturn(expected);

        ProcessLoadRetrieveResponse result = tmRestApiClient.processLoadRetrieve(input);

        assertSame(expected, result);
        verify(metricsHelper).populateTransactionDomainAndType(anyString(), eq("ProcessLoadRetrieveResponse"));
    }

    // ======= processMaintainEntityReferenceNumber =======

    @Test
    void processMaintainEntityReferenceNumber_success() {
        ProcessMaintainEntityReferenceNumber input = new ProcessMaintainEntityReferenceNumber();
        tmRestApiClient.processMaintainEntityReferenceNumber(input);
        verify(tmRestApiService).sendRequest(anyString(), any(ProcessMaintainEntityReferenceNumber.class),
                eq(ProcessMaintainEntityReferenceNumberResponse.class));
    }

    @Test
    void processMaintainEntityReferenceNumber_exception() {
        ProcessMaintainEntityReferenceNumber input = new ProcessMaintainEntityReferenceNumber();
        when(tmRestApiService.sendRequest(anyString(), any(), eq(ProcessMaintainEntityReferenceNumberResponse.class)))
                .thenThrow(new TmRestApiException("fail"));
        assertDoesNotThrow(() -> tmRestApiClient.processMaintainEntityReferenceNumber(input));
    }

    // ======= findLoadID =======

    @Test
    void findLoadID_success() throws Exception {
        RestFindEntity input = new RestFindEntity();
        FindEntitiesResponse expected = new FindEntitiesResponse();
        when(tmRestApiService.sendRequestToGetLoadID(anyString(), any(RestFindEntity.class)))
                .thenReturn(expected);

        FindEntitiesResponse result = tmRestApiClient.findLoadID(input);

        assertSame(expected, result);
        verify(metricsHelper).populateTransactionDomainAndType(anyString(), eq("FindEntitiesResponse"));
    }

    @Test
    void findLoadID_exception() throws Exception {
        RestFindEntity input = new RestFindEntity();
        when(tmRestApiService.sendRequestToGetLoadID(anyString(), any(RestFindEntity.class)))
                .thenThrow(new TmRestApiException("fail"));

        FindEntitiesResponse result = tmRestApiClient.findLoadID(input);

        assertNotNull(result);
    }

    // ======= restFindEntities =======

    @Test
    void restFindEntities_success() {
        RestFindEntity input = new RestFindEntity();
        FindEntitiesResponse expected = new FindEntitiesResponse();
        when(tmRestApiService.sendRequestToGetLoadDetails(anyString(), any(RestFindEntity.class)))
                .thenReturn(expected);

        FindEntitiesResponse result = tmRestApiClient.restFindEntities(input);

        assertSame(expected, result);
    }

    @Test
    void restFindEntities_exception() {
        RestFindEntity input = new RestFindEntity();
        when(tmRestApiService.sendRequestToGetLoadDetails(anyString(), any(RestFindEntity.class)))
                .thenThrow(new TmRestApiException("fail"));

        FindEntitiesResponse result = tmRestApiClient.restFindEntities(input);

        assertNotNull(result);
    }

    // ======= processTender =======

    @Test
    void processTender_success() {
        ProcessTender input = new ProcessTender();
        tmRestApiClient.processTender(input);
        verify(tmRestApiService).sentRequest(anyString(), any(ProcessTender.class), eq(ProcessTenderResponse.class));
    }

    @Test
    void processTender_exception() {
        ProcessTender input = new ProcessTender();
        when(tmRestApiService.sentRequest(anyString(), any(ProcessTender.class), eq(ProcessTenderResponse.class)))
                .thenThrow(new TmRestApiException("fail"));
        assertDoesNotThrow(() -> tmRestApiClient.processTender(input));
    }

    // ======= processLoadCancel =======

    @Test
    void processLoadCancel_success() {
        ProcessLoadCancel input = new ProcessLoadCancel();
        tmRestApiClient.processLoadCancel(input);
        verify(tmRestApiService).sentRequest(anyString(), any(ProcessLoadCancel.class),
                eq(ProcessLoadCancelResponse.class));
    }

    @Test
    void processLoadCancel_exception() {
        ProcessLoadCancel input = new ProcessLoadCancel();
        when(tmRestApiService.sentRequest(anyString(), any(ProcessLoadCancel.class),
                eq(ProcessLoadCancelResponse.class)))
                .thenThrow(new TmRestApiException("fail"));
        assertDoesNotThrow(() -> tmRestApiClient.processLoadCancel(input));
    }

    // ======= processTenderCancel =======

    @Test
    void processTenderCancel_success() {
        ProcessTenderCancel input = new ProcessTenderCancel();
        tmRestApiClient.processTenderCancel(input);
        verify(tmRestApiService).sentRequest(anyString(), any(ProcessTenderCancel.class),
                eq(ProcessTenderCancelResponse.class));
    }

    @Test
    void processTenderCancel_exception() {
        ProcessTenderCancel input = new ProcessTenderCancel();
        when(tmRestApiService.sentRequest(anyString(), any(ProcessTenderCancel.class),
                eq(ProcessTenderCancelResponse.class)))
                .thenThrow(new TmRestApiException("fail"));
        assertDoesNotThrow(() -> tmRestApiClient.processTenderCancel(input));
    }

    // ======= processTrailerNumberUpdate =======

    @Test
    void processTrailerNumberUpdate_success() {
        ProcessLoadUpdate input = new ProcessLoadUpdate();
        tmRestApiClient.processTrailerNumberUpdate(input);
        verify(tmRestApiService).sentRequest(anyString(), any(ProcessLoadUpdate.class),
                eq(ProcessLoadUpdateResponse.class));
    }

    @Test
    void processTrailerNumberUpdate_exception() {
        ProcessLoadUpdate input = new ProcessLoadUpdate();
        when(tmRestApiService.sentRequest(anyString(), any(ProcessLoadUpdate.class),
                eq(ProcessLoadUpdateResponse.class)))
                .thenThrow(new TmRestApiException("fail"));
        assertDoesNotThrow(() -> tmRestApiClient.processTrailerNumberUpdate(input));
    }

    // ======= processLoadRate =======

    @Test
    void processLoadRate_success() {
        ProcessLoadRate input = new ProcessLoadRate();
        tmRestApiClient.processLoadRate(input);
        verify(tmRestApiService).sentRequest(anyString(), any(ProcessLoadRate.class),
                eq(ProcessLoadRateResponse.class));
    }

    @Test
    void processLoadRate_exception() {
        ProcessLoadRate input = new ProcessLoadRate();
        when(tmRestApiService.sentRequest(anyString(), any(ProcessLoadRate.class), eq(ProcessLoadRateResponse.class)))
                .thenThrow(new TmRestApiException("fail"));
        assertDoesNotThrow(() -> tmRestApiClient.processLoadRate(input));
    }

    // ======= processStopAppointment =======

    @Test
    void processStopAppointment_success() {
        ProcessStopAppointment input = new ProcessStopAppointment();
        ProcessStopAppointmentResponse expected = new ProcessStopAppointmentResponse();
        when(tmRestApiService.sendRequestForProcessStopAppointment(anyString(), any(ProcessStopAppointment.class)))
                .thenReturn(expected);

        ProcessStopAppointmentResponse result = tmRestApiClient.processStopAppointment(input);

        assertSame(expected, result);
    }

    @Test
    void processStopAppointment_exception() {
        ProcessStopAppointment input = new ProcessStopAppointment();
        when(tmRestApiService.sendRequestForProcessStopAppointment(anyString(), any(ProcessStopAppointment.class)))
                .thenThrow(new TmRestApiException("fail"));

        ProcessStopAppointmentResponse result = tmRestApiClient.processStopAppointment(input);

        assertNotNull(result);
    }

    // ======= restProcessTrailerActivities =======

    @Test
    void restProcessTrailerActivities_success() throws Exception {
        RestProcessTrailerActivities input = new RestProcessTrailerActivities();
        ProcessTrailerActivitiesResponse expected = new ProcessTrailerActivitiesResponse();
        when(tmRestApiService.sentRestRequest(anyString(), any(RestProcessTrailerActivities.class)))
                .thenReturn("{json}");
        when(objectMapper.readValue("{json}", ProcessTrailerActivitiesResponse.class)).thenReturn(expected);

        ProcessTrailerActivitiesResponse result = tmRestApiClient.restProcessTrailerActivities(input);

        assertSame(expected, result);
    }

    @Test
    void restProcessTrailerActivities_exception() throws Exception {
        RestProcessTrailerActivities input = new RestProcessTrailerActivities();
        when(tmRestApiService.sentRestRequest(anyString(), any(RestProcessTrailerActivities.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.restProcessTrailerActivities(input));
    }

    // ======= restProcessCreateShipment =======

    @Test
    void restProcessCreateShipment_success() throws Exception {
        RestProcessShipmentOrderCreate input = new RestProcessShipmentOrderCreate();
        RestShipmentType ship = new RestShipmentType();
        ship.setShipmentNumber("SH001");
        input.setShipment(List.of(ship));

        ProcessShipmentOrderCreateResponse expected = new ProcessShipmentOrderCreateResponse();
        when(tmRestApiService.sentRestRequest(anyString(), any(RestProcessShipmentOrderCreate.class)))
                .thenReturn("{json}");
        when(objectMapper.readValue("{json}", ProcessShipmentOrderCreateResponse.class)).thenReturn(expected);

        ProcessShipmentOrderCreateResponse result = tmRestApiClient.restProcessCreateShipment(input);

        assertSame(expected, result);
    }

    @Test
    void restProcessCreateShipment_exception() throws Exception {
        RestProcessShipmentOrderCreate input = new RestProcessShipmentOrderCreate();
        when(tmRestApiService.sentRestRequest(anyString(), any(RestProcessShipmentOrderCreate.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.restProcessCreateShipment(input));
    }

    // ======= restProcessDeleteShipment =======

    @Test
    void restProcessDeleteShipment_success() throws Exception {
        RestProcessShipmentOrderDelete input = new RestProcessShipmentOrderDelete();
        RestShipmentDelete ship = new RestShipmentDelete();
        ship.setShipmentNumber("SH002");
        input.setShipment(List.of(ship));

        ProcessShipmentOrderDeleteResponse expected = new ProcessShipmentOrderDeleteResponse();
        when(tmRestApiService.sentRestRequest(anyString(), any(RestProcessShipmentOrderDelete.class)))
                .thenReturn("{del}");
        when(objectMapper.readValue("{del}", ProcessShipmentOrderDeleteResponse.class)).thenReturn(expected);

        ProcessShipmentOrderDeleteResponse result = tmRestApiClient.restProcessDeleteShipment(input);

        assertSame(expected, result);
    }

    @Test
    void restProcessDeleteShipment_exception() throws Exception {
        RestProcessShipmentOrderDelete input = new RestProcessShipmentOrderDelete();
        when(tmRestApiService.sentRestRequest(anyString(), any(RestProcessShipmentOrderDelete.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.restProcessDeleteShipment(input));
    }

    // ======= processShipmentRemove =======

    @Test
    void processShipmentRemove_success() throws Exception {
        ProcessShipmentRemove input = new ProcessShipmentRemove();
        ProcessShipmentRemoveResponse expected = new ProcessShipmentRemoveResponse();
        when(tmRestApiService.sentRestRequest(anyString(), any(ProcessShipmentRemove.class))).thenReturn("{rem}");
        when(objectMapper.readValue("{rem}", ProcessShipmentRemoveResponse.class)).thenReturn(expected);

        ProcessShipmentRemoveResponse result = tmRestApiClient.processShipmentRemove(input);

        assertSame(expected, result);
    }

    @Test
    void processShipmentRemove_exception() throws Exception {
        ProcessShipmentRemove input = new ProcessShipmentRemove();
        when(tmRestApiService.sentRestRequest(anyString(), any(ProcessShipmentRemove.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.processShipmentRemove(input));
    }

    // ======= processAssignShipmentsToLoad =======

    @Test
    void processAssignShipmentsToLoad_success() throws Exception {
        ProcessAssignToLoad input = new ProcessAssignToLoad();
        ProcessAssignToLoadResponse expected = new ProcessAssignToLoadResponse();
        when(tmRestApiService.sentRestRequest(anyString(), any(ProcessAssignToLoad.class))).thenReturn("{assign}");
        when(objectMapper.readValue("{assign}", ProcessAssignToLoadResponse.class)).thenReturn(expected);

        ProcessAssignToLoadResponse result = tmRestApiClient.processAssignShipmentsToLoad(input);

        assertSame(expected, result);
    }

    @Test
    void processAssignShipmentsToLoad_exception() throws Exception {
        ProcessAssignToLoad input = new ProcessAssignToLoad();
        when(tmRestApiService.sentRestRequest(anyString(), any(ProcessAssignToLoad.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.processAssignShipmentsToLoad(input));
    }

    // ======= addPreBuiltLoad =======

    @Test
    void addPreBuiltLoad_success() {
        RestAddPreBuiltLoad input = new RestAddPreBuiltLoad();
        AddPreBuiltLoadResponse expected = new AddPreBuiltLoadResponse();
        when(tmRestApiService.sendRequest(anyString(), any(RestAddPreBuiltLoad.class),
                eq(AddPreBuiltLoadResponse.class)))
                .thenReturn(expected);

        AddPreBuiltLoadResponse result = tmRestApiClient.addPreBuiltLoad(input);

        assertSame(expected, result);
        verify(metricsHelper).populateTransactionDomainAndType(anyString(), eq("AddPreBuiltLoadResponse"));
    }

    @Test
    void addPreBuiltLoad_exception() {
        RestAddPreBuiltLoad input = new RestAddPreBuiltLoad();
        when(tmRestApiService.sendRequest(anyString(), any(RestAddPreBuiltLoad.class),
                eq(AddPreBuiltLoadResponse.class)))
                .thenThrow(new TmRestApiException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.addPreBuiltLoad(input));
    }

    // ======= processTenderAccept =======

    @Test
    void processTenderAccept_success() {
        ProcessTenderAccept input = new ProcessTenderAccept();
        ProcessTenderAcceptResponse expected = new ProcessTenderAcceptResponse();
        when(tmRestApiService.sendRequest(anyString(), any(ProcessTenderAccept.class),
                eq(ProcessTenderAcceptResponse.class)))
                .thenReturn(expected);

        ProcessTenderAcceptResponse result = tmRestApiClient.processTenderAccept(input);

        assertSame(expected, result);
    }

    @Test
    void processTenderAccept_exception() {
        ProcessTenderAccept input = new ProcessTenderAccept();
        when(tmRestApiService.sendRequest(anyString(), any(ProcessTenderAccept.class),
                eq(ProcessTenderAcceptResponse.class)))
                .thenThrow(new TmRestApiException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.processTenderAccept(input));
    }

    // ======= processTenderReject =======

    @Test
    void processTenderReject_success() {
        ProcessTenderReject input = new ProcessTenderReject();
        ProcessTenderRejectResponse expected = new ProcessTenderRejectResponse();
        when(tmRestApiService.sendRequestUntyped(anyString(), any(ProcessTenderReject.class),
                eq(ProcessTenderRejectResponse.class)))
                .thenReturn(expected);

        ProcessTenderRejectResponse result = tmRestApiClient.processTenderReject(input);

        assertSame(expected, result);
    }

    @Test
    void processTenderReject_exception() {
        ProcessTenderReject input = new ProcessTenderReject();
        when(tmRestApiService.sendRequestUntyped(anyString(), any(ProcessTenderReject.class),
                eq(ProcessTenderRejectResponse.class)))
                .thenThrow(new TmRestApiException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.processTenderReject(input));
    }

    // ======= processTrailerActivities (sendRequest version) =======

    @Test
    void processTrailerActivities_success() {
        RestProcessTrailerActivities input = new RestProcessTrailerActivities();
        ProcessTrailerActivitiesResponse expected = new ProcessTrailerActivitiesResponse();
        when(tmRestApiService.sendRequest(anyString(), any(RestProcessTrailerActivities.class),
                eq(ProcessTrailerActivitiesResponse.class)))
                .thenReturn(expected);

        ProcessTrailerActivitiesResponse result = tmRestApiClient.processTrailerActivities(input);

        assertSame(expected, result);
    }

    @Test
    void processTrailerActivities_exception() {
        RestProcessTrailerActivities input = new RestProcessTrailerActivities();
        when(tmRestApiService.sendRequest(anyString(), any(RestProcessTrailerActivities.class),
                eq(ProcessTrailerActivitiesResponse.class)))
                .thenThrow(new TmRestApiException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.processTrailerActivities(input));
    }

    // ======= getAttachmentsInformationFromRest =======

    @Test
    void getAttachmentsInformationFromRest_success() {
        RestProcessMaintainEntityAttachment input = new RestProcessMaintainEntityAttachment();
        RestProcessMaintainEntityAttachmentResponse expected = new RestProcessMaintainEntityAttachmentResponse();
        when(tmRestApiService.sentRequest(anyString(), any(RestProcessMaintainEntityAttachment.class),
                eq(RestProcessMaintainEntityAttachmentResponse.class))).thenReturn(expected);

        RestProcessMaintainEntityAttachmentResponse result = tmRestApiClient.getAttachmentsInformationFromRest(input);

        assertSame(expected, result);
    }

    @Test
    void getAttachmentsInformationFromRest_genericException_throwsTmsLoadProcessException() {
        RestProcessMaintainEntityAttachment input = new RestProcessMaintainEntityAttachment();
        when(tmRestApiService.sentRequest(anyString(), any(RestProcessMaintainEntityAttachment.class),
                eq(RestProcessMaintainEntityAttachmentResponse.class)))
                .thenThrow(new RuntimeException("generic"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.getAttachmentsInformationFromRest(input));
    }

    @Test
    void getAttachmentsInformationFromRest_httpClientError422_rethrows() {
        RestProcessMaintainEntityAttachment input = new RestProcessMaintainEntityAttachment();
        HttpClientErrorException httpEx = new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY);
        RuntimeException wrapper = new RuntimeException("wrapped", httpEx);
        when(tmRestApiService.sentRequest(anyString(), any(RestProcessMaintainEntityAttachment.class),
                eq(RestProcessMaintainEntityAttachmentResponse.class)))
                .thenThrow(wrapper);

        Exception thrown = assertThrows(RuntimeException.class,
                () -> tmRestApiClient.getAttachmentsInformationFromRest(input));
        assertSame(wrapper, thrown);
    }

    @Test
    void getAttachmentsInformationFromRest_httpClientErrorNon422_throwsTmsLoadProcessException() {
        RestProcessMaintainEntityAttachment input = new RestProcessMaintainEntityAttachment();
        HttpClientErrorException httpEx = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        RuntimeException wrapper = new RuntimeException("wrapped", httpEx);
        when(tmRestApiService.sentRequest(anyString(), any(RestProcessMaintainEntityAttachment.class),
                eq(RestProcessMaintainEntityAttachmentResponse.class)))
                .thenThrow(wrapper);

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.getAttachmentsInformationFromRest(input));
    }

    // ======= searchShipment =======

    @Test
    void searchShipment_success() {
        RestFindEntity input = new RestFindEntity();
        ShipmentResponse expected = new ShipmentResponse();
        when(tmRestApiService.sentRequest(anyString(), any(RestFindEntity.class), eq(ShipmentResponse.class)))
                .thenReturn(expected);

        ShipmentResponse result = tmRestApiClient.searchShipment(input);

        assertSame(expected, result);
    }

    @Test
    void searchShipment_exception() {
        RestFindEntity input = new RestFindEntity();
        when(tmRestApiService.sentRequest(anyString(), any(RestFindEntity.class), eq(ShipmentResponse.class)))
                .thenThrow(new TmRestApiException("fail"));

        ShipmentResponse result = tmRestApiClient.searchShipment(input);

        assertNotNull(result);
    }

    // ======= processLoadReturnToOpen =======

    @Test
    void processLoadReturnToOpen_success() {
        ProcessLoadReturnToOpen input = new ProcessLoadReturnToOpen();
        tmRestApiClient.processLoadReturnToOpen(input);
        verify(tmRestApiService).sentRequest(anyString(), any(ProcessLoadReturnToOpen.class),
                eq(ProcessLoadReturnToOpenResponse.class));
    }

    @Test
    void processLoadReturnToOpen_exception() {
        ProcessLoadReturnToOpen input = new ProcessLoadReturnToOpen();
        when(tmRestApiService.sentRequest(anyString(), any(ProcessLoadReturnToOpen.class),
                eq(ProcessLoadReturnToOpenResponse.class)))
                .thenThrow(new TmRestApiException("fail"));
        assertDoesNotThrow(() -> tmRestApiClient.processLoadReturnToOpen(input));
    }

    // ======= processRestTrailerNumberUpdate =======

    @Test
    void processRestTrailerNumberUpdate_success() throws Exception {
        RestProcessLoadUpdate input = new RestProcessLoadUpdate();
        ProcessLoadUpdateResponse expected = new ProcessLoadUpdateResponse();
        when(tmRestApiService.sentRestRequest(anyString(), any(RestProcessLoadUpdate.class))).thenReturn("{upd}");
        when(objectMapper.readValue("{upd}", ProcessLoadUpdateResponse.class)).thenReturn(expected);

        ProcessLoadUpdateResponse result = tmRestApiClient.processRestTrailerNumberUpdate(input);

        assertSame(expected, result);
    }

    @Test
    void processRestTrailerNumberUpdate_exception() throws Exception {
        RestProcessLoadUpdate input = new RestProcessLoadUpdate();
        when(tmRestApiService.sentRestRequest(anyString(), any(RestProcessLoadUpdate.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.processRestTrailerNumberUpdate(input));
    }

    // ======= processUpdateShipment =======

    @Test
    void processUpdateShipment_success() {
        RestProcessShipmentOrderCreate input = new RestProcessShipmentOrderCreate();
        ProcessShipmentOrderCreateResponse expected = new ProcessShipmentOrderCreateResponse();
        when(tmRestApiService.sendRequest(anyString(), any(RestProcessShipmentOrderCreate.class),
                eq(ProcessShipmentOrderCreateResponse.class))).thenReturn(expected);

        ProcessShipmentOrderCreateResponse result = tmRestApiClient.processUpdateShipment(input);

        assertSame(expected, result);
    }

    @Test
    void processUpdateShipment_exception() {
        RestProcessShipmentOrderCreate input = new RestProcessShipmentOrderCreate();
        when(tmRestApiService.sendRequest(anyString(), any(RestProcessShipmentOrderCreate.class),
                eq(ProcessShipmentOrderCreateResponse.class)))
                .thenThrow(new TmRestApiException("fail"));

        assertThrows(TmsLoadProcessException.class, () -> tmRestApiClient.processUpdateShipment(input));
    }

    // ======= callAppointmentSealCondition =======

    @Test
    void callAppointmentSealCondition_success() {
        Integer appointmentNbr = 123;
        AppointmentSealConditionDetail detail = new AppointmentSealConditionDetail();
        AppointmentSealConditionDetail[] details = { detail };
        when(restTemplate.getForObject(anyString(), eq(AppointmentSealConditionDetail[].class), any(Integer.class)))
                .thenReturn(details);

        List<AppointmentSealConditionDetail> result = tmRestApiClient.callAppointmentSealCondition(appointmentNbr);

        assertEquals(1, result.size());
        assertSame(detail, result.get(0));
    }

    @Test
    void callAppointmentSealCondition_httpServerError() {
        Integer appointmentNbr = 123;
        when(restTemplate.getForObject(anyString(), eq(AppointmentSealConditionDetail[].class), any(Integer.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        List<AppointmentSealConditionDetail> result = tmRestApiClient.callAppointmentSealCondition(appointmentNbr);

        assertTrue(result.isEmpty());
    }

    @Test
    void callAppointmentSealCondition_resourceAccessException() {
        Integer appointmentNbr = 123;
        when(restTemplate.getForObject(anyString(), eq(AppointmentSealConditionDetail[].class), any(Integer.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        List<AppointmentSealConditionDetail> result = tmRestApiClient.callAppointmentSealCondition(appointmentNbr);

        assertTrue(result.isEmpty());
    }

    @Test
    void callAppointmentSealCondition_httpClientError() {
        Integer appointmentNbr = 456;
        when(restTemplate.getForObject(anyString(), eq(AppointmentSealConditionDetail[].class), any(Integer.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        List<AppointmentSealConditionDetail> result = tmRestApiClient.callAppointmentSealCondition(appointmentNbr);

        assertTrue(result.isEmpty());
    }

    @Test
    void callAppointmentSealCondition_genericException() {
        Integer appointmentNbr = 789;
        when(restTemplate.getForObject(anyString(), eq(AppointmentSealConditionDetail[].class), any(Integer.class)))
                .thenThrow(new RuntimeException("unexpected"));

        List<AppointmentSealConditionDetail> result = tmRestApiClient.callAppointmentSealCondition(appointmentNbr);

        assertTrue(result.isEmpty());
    }
}
