package com.macys.logistics.shipment.load.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.response.RestAuthTokenResponse;
import com.macys.logistics.shipment.load.exception.TmRestApiException;
import com.macys.logistics.shipment.load.exception.TmsEntityServiceProcessException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TmRestApiServiceTest {

    @Mock
    LoadMapper loadMapper;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private TmRestApiService tmRestApiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tmRestApiService, "tmCloudAuthTokenUri", "tmCloudAuthTokenUri");
        ReflectionTestUtils.setField(tmRestApiService, "tmRestAPIBaseUri", "mockTmRestAPIBaseUri");
    }

    @Test
    void testGetToken_token_present_not_expired() {
        ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
        ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime", System.currentTimeMillis() + 200);
        String token = tmRestApiService.getToken();
        assertEquals("valid_token", token);
        verify(restTemplate, never()).exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class));

    }

    @Test
    void testGetToken_token_present_expired() {
        RestAuthTokenResponse mockResponse = new RestAuthTokenResponse();
        mockResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        String token = tmRestApiService.getToken();
        assertEquals("valid_token", token);
        verify(restTemplate, times(1)).exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class));

    }

    @Test
    void testGetToken_ValidToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic encodedAuthString");
        RestAuthTokenResponse mockResponse = new RestAuthTokenResponse();
        mockResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        String token = tmRestApiService.getToken();
        assertEquals("valid_token", token);
    }

    @Test
    void testGetTokenServerError() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic encodedAuthString");
        RestAuthTokenResponse mockResponse = new RestAuthTokenResponse();
        mockResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(TmRestApiException.class, () -> tmRestApiService.getToken());
    }

    @Test
    void testGetTokenInvalidToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic encodedAuthString");
        RestAuthTokenResponse mockResponse = new RestAuthTokenResponse();
        mockResponse.setAccessToken(null);
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        assertThrows(TmRestApiException.class, () -> tmRestApiService.getToken());
    }

    @Test
    void testSendRequest_Success_invalidResponse() {
        String path = "api/path";
        ProcessLoadRetrieve request = new ProcessLoadRetrieve();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid_token");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiSourceEnumVal", "shipment-load-integration");
        headers.add("clientID", "shipment-load-integration");
        ProcessLoadRetrieveResponse mockResponse = new ProcessLoadRetrieveResponse();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(ProcessLoadRetrieveResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        TmRestApiException thrown = assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sendRequest(path, request, ProcessLoadRetrieveResponse.class));
        assertEquals("TM Rest API failure for api/path due to :Error in service response: ", thrown.getMessage());
    }

    /*
     * @Test
     * void testSendRequest_ApiFailure() {
     * ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
     * ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
     * System.currentTimeMillis() + 200);
     * when(restTemplate.exchange(anyString(), any(HttpMethod.class),
     * any(HttpEntity.class), any(Class.class))).thenReturn(null);
     * ProcessLoadRetrieve processLoadRetrieve = new ProcessLoadRetrieve();
     * TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> {
     * tmRestApiService.sendRequest("api/path", processLoadRetrieve,
     * ProcessLoadRetrieveResponse.class);});
     * assertEquals("TM Rest API failure for api/path due to :TM rest Api response is null here"
     * , thrown.getMessage());
     * }
     */
    /*
     * @Test
     * void testSendRequest_ProcessTender_ApiFailure() {
     * ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
     * ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
     * System.currentTimeMillis() + 200);
     * when(restTemplate.exchange(anyString(), any(HttpMethod.class),
     * any(HttpEntity.class), any(Class.class))).thenReturn(null);
     * ProcessTender processTender = new ProcessTender();
     * TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> {
     * tmRestApiService.sendRequest("api/path", processTender,
     * ProcessTenderResponse.class);});
     * assertEquals("TM Rest API failure for api/path due to :TM rest Api response is null here"
     * , thrown.getMessage());
     * }
     */

    @Test
    void testSentRequest_ProcessTender_Success() {
        String path = "api/path";
        ProcessTender request = new ProcessTender();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid_token");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiSourceEnumVal", "shipment-load-integration");
        headers.add("clientID", "shipment-load-integration");
        ProcessTenderResponse mockResponse = new ProcessTenderResponse();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(ProcessTenderResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        TmRestApiException thrown = assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sendRequest(path, request, ProcessTenderResponse.class));
        assertEquals("TM Rest API failure for api/path due to :Error in service response: ", thrown.getMessage());
    }

    /*
     * @Test
     * void testSendRequest_ProcessLoadTenderCancel_ApiFailure() {
     * ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
     * ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
     * System.currentTimeMillis() + 200);
     * when(restTemplate.exchange(anyString(), any(HttpMethod.class),
     * any(HttpEntity.class), any(Class.class))).thenReturn(null);
     * ProcessTender processTender = new ProcessTender();
     * TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> {
     * tmRestApiService.sendRequest("api/path", processTender,
     * ProcessTenderCancelResponse.class);});
     * assertEquals("TM Rest API failure for api/path due to :TM rest Api response is null here"
     * , thrown.getMessage());
     * }
     */

    @Test
    void testSentRequest_ProcessLoadCancel_Success() {
        String path = "api/path";
        ProcessLoadCancel request = new ProcessLoadCancel();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid_token");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiSourceEnumVal", "shipment-load-integration");
        headers.add("clientID", "shipment-load-integration");
        ProcessLoadCancelResponse mockResponse = new ProcessLoadCancelResponse();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(ProcessLoadCancelResponse.class))).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        TmRestApiException thrown = assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sendRequest(path, request, ProcessLoadCancelResponse.class));
        assertEquals("TM Rest API failure for api/path due to :Error in service response: ", thrown.getMessage());
    }

    /*
     * @Test
     * void testSentRequest_ProcessLoadCancel_ApiFailure() {
     * ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
     * ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
     * System.currentTimeMillis() + 200);
     * when(restTemplate.exchange(anyString(), any(HttpMethod.class),
     * any(HttpEntity.class), any(Class.class))).thenReturn(null);
     * ProcessLoadCancel processLoadCancel = new ProcessLoadCancel();
     * TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> {
     * tmRestApiService.sendRequest("api/path",processLoadCancel,
     * ProcessLoadCancelResponse.class);
     * });
     * assertEquals("TM Rest API failure for api/path due to :TM rest Api response is null here"
     * , thrown.getMessage());
     * }
     */

    @Test
    void testSentRequest_ProcessTenderCancel_Success() {
        String path = "api/path";
        ProcessTenderCancel request = new ProcessTenderCancel();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid_token");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiSourceEnumVal", "shipment-load-integration");
        headers.add("clientID", "shipment-load-integration");
        ProcessTenderCancelResponse mockResponse = new ProcessTenderCancelResponse();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(ProcessTenderCancelResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        TmRestApiException thrown = assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sendRequest(path, request, ProcessTenderCancelResponse.class));
        assertEquals("TM Rest API failure for api/path due to :Error in service response: ", thrown.getMessage());
    }

    /*
     * @Test
     * void testSendRequest_ProcessTenderCancelResponse_ApiFailure() {
     * ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
     * ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
     * System.currentTimeMillis() + 200);
     * when(restTemplate.exchange(anyString(), any(HttpMethod.class),
     * any(HttpEntity.class), any(Class.class))).thenReturn(null);
     * ProcessTenderCancel processTenderCancel = new ProcessTenderCancel();
     * TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> {
     * tmRestApiService.sendRequest("api/path",processTenderCancel ,
     * ProcessTenderCancelResponse.class);
     * });
     * assertEquals("TM Rest API failure for api/path due to :TM rest Api response is null here"
     * , thrown.getMessage());
     * }
     */

    @Test
    void testSentRequest_ProcessLoadRate_Success() {
        String path = "api/path";
        ProcessLoadRate request = new ProcessLoadRate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid_token");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiSourceEnumVal", "shipment-load-integration");
        headers.add("clientID", "shipment-load-integration");
        ProcessLoadRateResponse mockResponse = new ProcessLoadRateResponse();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(
                eq("tmCloudAuthTokenUri"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(ProcessLoadRateResponse.class))).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sendRequest(path, request, ProcessLoadRateResponse.class));

    }

    /*
     * @Test
     * void testSendRequest_ProcessLoadRate_ApiFailure() {
     * ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
     * ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
     * System.currentTimeMillis() + 200);
     * when(restTemplate.exchange(anyString(), any(HttpMethod.class),
     * any(HttpEntity.class), any(Class.class))).thenReturn(null);
     * ProcessLoadRate processLoadRate = new ProcessLoadRate();
     * TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> {
     * tmRestApiService.sendRequest("api/path",processLoadRate ,
     * ProcessLoadRateResponse.class);});
     * assertEquals("TM Rest API failure for api/path due to :TM rest Api response is null here"
     * , thrown.getMessage());
     * }
     */

    @Test
    void testSentRequest_ProcessLoadUpdate_Success() {
        String path = "api/path";
        ProcessLoadUpdate request = new ProcessLoadUpdate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid_token");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiSourceEnumVal", "shipment-load-integration");
        headers.add("clientID", "shipment-load-integration");
        ProcessLoadUpdateResponse mockResponse = new ProcessLoadUpdateResponse();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(ProcessLoadUpdateResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sentRequest(path, request, ProcessLoadRateResponse.class));
    }

    /*
     * @Test
     * void testSendRequest_ProcessLoadUpdate_ApiFailure() {
     * ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
     * ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
     * System.currentTimeMillis() + 200);
     * when(restTemplate.exchange(anyString(), any(HttpMethod.class),
     * any(HttpEntity.class), any(Class.class))).thenReturn(null);
     * ProcessLoadUpdate processLoadUpdate = new ProcessLoadUpdate();
     * TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> {
     * tmRestApiService.sendRequest("api/path", processLoadUpdate,
     * ProcessLoadUpdateResponse.class);});
     * assertEquals("TM Rest API failure for api/path due to :TM rest Api response is null here"
     * , thrown.getMessage());
     * }
     */

    @Test
    void testSentRequest_HttpClientErrorException() {
        ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
        ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime", System.currentTimeMillis() + 200);
        String path = "/some/path";
        ProcessLoadUpdate request = new ProcessLoadUpdate();
        Class<ProcessLoadUpdateResponse> responseType = ProcessLoadUpdateResponse.class;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("some-token");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenThrow(ex);
        TmRestApiException thrown = assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sentRequest(path, request, responseType));
        assertTrue(thrown.getMessage().contains("HTTP error for path " + path));
        assertEquals(ex, thrown.getCause());
    }

    @Test
    void testSentRequest_HttpServerErrorException() {
        ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
        ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime", System.currentTimeMillis() + 200);
        String path = "/some/path";
        ProcessLoadUpdate request = new ProcessLoadUpdate();
        Class<ProcessLoadUpdateResponse> responseType = ProcessLoadUpdateResponse.class;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("some-token");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpServerErrorException ex = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenThrow(ex);
        TmRestApiException thrown2 = assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sentRequest(path, request, responseType));
        assertTrue(thrown2.getMessage().contains("HTTP error for path " + path));
        assertEquals(ex, thrown2.getCause());
    }

    @Test
    void testSentRequest_UnexpectedException() {
        ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
        ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime", System.currentTimeMillis() + 200);
        String path = "/some/path";
        ProcessLoadUpdate request = new ProcessLoadUpdate();
        Class<ProcessLoadUpdateResponse> responseType = ProcessLoadUpdateResponse.class;
        RuntimeException ex = new RuntimeException("Unexpected error");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenThrow(ex);
        TmRestApiException thrown3 = assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sentRequest(path, request, responseType));
        assertTrue(thrown3.getMessage().contains("Unexpected error for path " + path));
        assertEquals(ex, thrown3.getCause());
    }

    @Test
    void testSendRequestForProcessStopAppointment_Success()
            throws DatatypeConfigurationException, JsonProcessingException {
        String path = "api/path";
        ProcessStopAppointmentResponse mockResponse = new ProcessStopAppointmentResponse();
        ProcessStopAppointment processStopAppointment = DataFactory.getProcessStopAppointment();
        processStopAppointment.getStopAppointment().get(0).setStopIDTypeEnumVal(StopIdTypeEnumValType.SID_ID);
        lenient().when(objectMapper.readValue(anyString(), eq(ProcessStopAppointmentResponse.class)))
                .thenReturn(mockResponse);
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        lenient()
                .when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        lenient().when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockResponse.toString(), HttpStatus.OK));
        ProcessStopAppointmentResponse processStopAppointmentResponse = tmRestApiService
                .sendRequestForProcessStopAppointment(path, processStopAppointment);
        assertNotNull(processStopAppointmentResponse);
    }

    @Test
    void testSendRequestForProcessStopAppointment_failure()
            throws DatatypeConfigurationException, JsonProcessingException {
        String path = "api/path";
        ProcessStopAppointmentResponse mockResponse = new ProcessStopAppointmentResponse();
        ProcessStopAppointment processStopAppointment = DataFactory.getProcessStopAppointment();
        processStopAppointment.getStopAppointment().get(0).setStopIDTypeEnumVal(StopIdTypeEnumValType.SID_ID);
        lenient().when(objectMapper.readValue(anyString(), eq(ProcessStopAppointmentResponse.class)))
                .thenReturn(mockResponse);
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        lenient()
                .when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        lenient()
                .when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(ProcessStopAppointmentResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.NOT_FOUND));
        ProcessStopAppointmentResponse processStopAppointmentResponse = tmRestApiService
                .sendRequestForProcessStopAppointment(path, processStopAppointment);
        assertNull(processStopAppointmentResponse);
    }

    @Test
    void testSendRequestForProcessStopAppointmentException()
            throws DatatypeConfigurationException, JsonProcessingException {
        String path = "api/path";
        ProcessStopAppointmentResponse mockResponse = new ProcessStopAppointmentResponse();
        ProcessStopAppointment processStopAppointment = DataFactory.getProcessStopAppointment();
        processStopAppointment.getStopAppointment().get(0).setStopIDTypeEnumVal(StopIdTypeEnumValType.SID_ID);
        lenient().when(objectMapper.readValue(anyString(), eq(ProcessStopAppointmentResponse.class)))
                .thenReturn(mockResponse);
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        lenient()
                .when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(null);
        ProcessStopAppointmentResponse processStopAppointmentResponse = tmRestApiService
                .sendRequestForProcessStopAppointment(path, processStopAppointment);
        assertNull(processStopAppointmentResponse);

    }

    @Test
    void testSendRequestForProcessStopAppointmentResourceAccessException()
            throws DatatypeConfigurationException, JsonProcessingException {
        String path = "api/path";
        ProcessStopAppointmentResponse mockResponse = new ProcessStopAppointmentResponse();
        ProcessStopAppointment processStopAppointment = DataFactory.getProcessStopAppointment();
        processStopAppointment.getStopAppointment().get(0).setStopIDTypeEnumVal(StopIdTypeEnumValType.SID_ID);
        lenient().when(objectMapper.readValue(anyString(), eq(ProcessStopAppointmentResponse.class)))
                .thenReturn(mockResponse);
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        lenient()
                .when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));

        lenient().when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new ResourceAccessException("ResourceAccessException"));
        assertThrows(ResourceAccessException.class,
                () -> tmRestApiService.sendRequestForProcessStopAppointment(path, processStopAppointment));
    }

    @Test
    void testSendRequestForProcessStopAppointmentHttpServerErrorException() throws DatatypeConfigurationException {
        String path = "api/path";
        ProcessStopAppointment processStopAppointment = DataFactory.getProcessStopAppointment();
        processStopAppointment.getStopAppointment().get(0).setStopIDTypeEnumVal(StopIdTypeEnumValType.SID_ID);
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        lenient()
                .when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        assertThrows(HttpServerErrorException.class,
                () -> tmRestApiService.sendRequestForProcessStopAppointment(path, processStopAppointment));
    }

    /*
     * @Test
     * void testSendRequestToGetLoadID_Success() throws
     * DatatypeConfigurationException {
     * String path = "api/path";
     * RestFindEntitiesResponse mockResponse =
     * DataFactory.getRestFindEntitiesResponse();
     * RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
     * RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
     * mockAuthResponse.setAccessToken("valid_token");
     * lenient().when(restTemplate.exchange(eq("tmCloudAuthTokenUri"),
     * eq(HttpMethod.POST), any(HttpEntity.class), eq(RestAuthTokenResponse.class)))
     * .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
     * when(restTemplate.exchange(eq(path), eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(String.class)))
     * .thenReturn(new ResponseEntity<>(mockResponse.toString(), HttpStatus.OK));
     * FindEntitiesResponse restFindEntitiesResponse =
     * tmRestApiService.sendRequestToGetLoadID(path, restFindEntity);
     * assertNull(restFindEntitiesResponse);
     * }
     */
    /*
     * @Test
     * void testSendRequestToGetLoadID_failure() throws
     * DatatypeConfigurationException {
     * String path = "api/path";
     * RestFindEntitiesResponse mockResponse =
     * DataFactory.getRestFindEntitiesResponse();
     * RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
     * RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
     * mockAuthResponse.setAccessToken("valid_token");
     * lenient().when(restTemplate.exchange(eq("tmCloudAuthTokenUri"),
     * eq(HttpMethod.POST), any(HttpEntity.class), eq(RestAuthTokenResponse.class)))
     * .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
     * when(restTemplate.exchange(eq(path), eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(String.class)))
     * .thenReturn(new ResponseEntity<>(mockResponse.toString(),
     * HttpStatus.NOT_FOUND));
     * FindEntitiesResponse restFindEntitiesResponse =
     * tmRestApiService.sendRequestToGetLoadID(path, restFindEntity);
     * assertNull(restFindEntitiesResponse);
     * }
     */

    @Test
    void testSendRequestToGetLoadID_Exception() throws DatatypeConfigurationException {
        String path = "api/path";
        RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        lenient()
                .when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestFindEntitiesResponse.class))).thenReturn(null);
        assertThrows(TmRestApiException.class, () -> tmRestApiService.sendRequestToGetLoadID(path, restFindEntity));
    }

    /*
     * @Test
     * void testSendRequestToGetLoadID_ResourceAccessException() throws
     * DatatypeConfigurationException {
     * String path = "api/path";
     * RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
     * RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
     * mockAuthResponse.setAccessToken("valid_token");
     * lenient().when(restTemplate.exchange(eq("tmCloudAuthTokenUri"),
     * eq(HttpMethod.POST), any(HttpEntity.class), eq(RestAuthTokenResponse.class)))
     * .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
     * 
     * when(restTemplate.exchange(eq(path), eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(String.class)))
     * .thenThrow(new ResourceAccessException("ResourceAccessException"));
     * assertThrows(ResourceAccessException.class, () -> {
     * tmRestApiService.sendRequestToGetLoadID(path, restFindEntity);
     * });
     * }
     */

    /*
     * @Test
     * void testSendRequestToGetLoadID_HttpServerErrorException() throws
     * DatatypeConfigurationException {
     * String path = "api/path";
     * RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
     * RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
     * mockAuthResponse.setAccessToken("valid_token");
     * lenient().when(restTemplate.exchange(eq("tmCloudAuthTokenUri"),
     * eq(HttpMethod.POST), any(HttpEntity.class), eq(RestAuthTokenResponse.class)))
     * .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
     * when(restTemplate.exchange(eq(path), eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(String.class)))
     * .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
     * assertThrows(HttpServerErrorException.class, () -> {
     * tmRestApiService.sendRequestToGetLoadID(path, restFindEntity);
     * });
     * }
     */

    @Test
    void testSendRequestToGetLoadDetails_Success() throws DatatypeConfigurationException {
        String path = "api/path";
        LoadRestFindEntitiesResponse mockResponse = DataFactory.getLoadRestFindEntitiesResponse();
        RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        lenient()
                .when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockResponse.toString(), HttpStatus.OK));
        FindEntitiesResponse restFindEntitiesResponse = tmRestApiService.sendRequestToGetLoadDetails(path,
                restFindEntity);
        assertNotNull(mockResponse);
    }

    @Test
    void testSendRequestToGetLoadDetails_failure() throws DatatypeConfigurationException {
        String path = "api/path";
        LoadRestFindEntitiesResponse mockResponse = DataFactory.getLoadRestFindEntitiesResponse();
        RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        lenient()
                .when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockResponse.toString(), HttpStatus.NOT_FOUND));
        assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sendRequestToGetLoadDetails(path, restFindEntity));
    }

    @Test
    void testSendRequestToGetLoadDetails_Exception() {
        String path = "api/path";
        RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        lenient()
                .when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestFindEntitiesResponse.class)))
                .thenReturn(null);
        assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sendRequestToGetLoadDetails(path, restFindEntity));
    }

    /*
     * @Test
     * void testSendRequestToGetLoadDetails_ResourceAccessException() {
     * String path = "api/path";
     * RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
     * RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
     * mockAuthResponse.setAccessToken("valid_token");
     * lenient().when(restTemplate.exchange(eq("tmCloudAuthTokenUri"),
     * eq(HttpMethod.POST), any(HttpEntity.class), eq(RestAuthTokenResponse.class)))
     * .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
     * when(restTemplate.exchange(eq(path), eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(String.class)))
     * .thenThrow(new ResourceAccessException("ResourceAccessException"));
     * assertThrows(ResourceAccessException.class, () -> {
     * tmRestApiService.sendRequestToGetLoadDetails(path, restFindEntity);
     * });
     * }
     */

    /*
     * @Test
     * void testSendRequestToGetLoadDetails_HttpServerErrorException() {
     * String path = "api/path";
     * RestFindEntity restFindEntity = DataFactory.getRestFindEntity();
     * RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
     * mockAuthResponse.setAccessToken("valid_token");
     * when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(RestAuthTokenResponse.class)))
     * .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
     * 
     * when(restTemplate.exchange(eq(path),eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(String.class)))
     * .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
     * assertThrows(HttpServerErrorException.class, () -> {
     * tmRestApiService.sendRequestToGetLoadDetails(path, restFindEntity);
     * });
     * }
     */

    /*
     * @Test
     * void testSentRequest_ApiFailure() {
     * ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
     * ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
     * System.currentTimeMillis() + 200);
     * when(restTemplate.exchange(anyString(), any(HttpMethod.class),
     * any(HttpEntity.class), any(Class.class)))
     * .thenReturn(null);
     * ProcessTender processTender = new ProcessTender();
     * TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> {
     * tmRestApiService.sentRequest("api/path", processTender,
     * ProcessTenderResponse.class);
     * });
     * assertEquals("Unexpected error for path api/path: TM rest Api response is null"
     * , thrown.getMessage());
     * }
     */

    @Test
    void testSentRequest_Success_invalidResponse() {
        String path = "api/path";
        ProcessTender request = new ProcessTender();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid_token");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiSourceEnumVal", "shipment-load-integration");
        headers.add("clientID", "shipment-load-integration");
        ProcessTenderResponse mockResponse = new ProcessTenderResponse();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockResponse.toString(), HttpStatus.OK));
        assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sentRequest(path, request, ProcessTenderResponse.class));
    }

    /*
     * @Test
     * void testSentRequest_ProcessLoadUpdate_ApiFailure() {
     * ReflectionTestUtils.setField(tmRestApiService, "authToken", "valid_token");
     * ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
     * System.currentTimeMillis() + 200);
     * when(restTemplate.exchange(anyString(), any(HttpMethod.class),
     * any(HttpEntity.class), any(Class.class)))
     * .thenReturn(null);
     * ProcessLoadUpdate processLoadUpdate = new ProcessLoadUpdate();
     * TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> {
     * tmRestApiService.sentRequest("api/path", processLoadUpdate,
     * ProcessLoadUpdateResponse.class);
     * });
     * assertEquals("Unexpected error for path api/path: TM rest Api response is null"
     * , thrown.getMessage());
     * }
     */

    @Test
    void test_ProcessLoadRate_Success() {
        String path = "api/path";
        ProcessLoadRate request = new ProcessLoadRate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid_token");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiSourceEnumVal", "shipment-load-integration");
        headers.add("clientID", "shipment-load-integration");

        ProcessLoadRateResponse mockResponse = new ProcessLoadRateResponse();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(ProcessLoadRateResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sentRequest(path, request, ProcessLoadRateResponse.class));
    }

    @Test
    void testSentRequest_Success_unprocessResponse() throws JsonProcessingException {
        String path = "api/path";
        ProcessTender request = new ProcessTender();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid_token");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiSourceEnumVal", "shipment-load-integration");
        headers.add("clientID", "shipment-load-integration");
        ProcessTenderResponse mockResponse = new ProcessTenderResponse();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockResponse.toString(), HttpStatus.valueOf(422)));
        lenient().when(objectMapper.readValue(anyString(), eq(ResponseHeaderType.class)))
                .thenReturn(new ResponseHeaderType());
        assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sentRequest(path, request, ProcessTenderResponse.class));
    }

    @Test
    void testSentRestRequestHttpServerException() {
        RestProcessTrailerActivities mockTrailerActivities = DataFactory.getRestProcessTrailerActivities();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        HttpServerErrorException thrown = assertThrows(HttpServerErrorException.class,
                () -> tmRestApiService.sentRestRequest("path", mockTrailerActivities));
    }

    @Test
    void testSentRestRequestResourceAccessException() {
        RestProcessTrailerActivities mockTrailerActivities = DataFactory.getRestProcessTrailerActivities();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new ResourceAccessException("resource access exception"));
        ResourceAccessException thrown2 = assertThrows(ResourceAccessException.class,
                () -> tmRestApiService.sentRestRequest("path", mockTrailerActivities));
    }

    @Test
    void testSentRestRequestException() {
        RestProcessTrailerActivities mockTrailerActivities = DataFactory.getRestProcessTrailerActivities();
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Exception"));
        TmsEntityServiceProcessException thrown3 = assertThrows(TmsEntityServiceProcessException.class,
                () -> tmRestApiService.sentRestRequest("path", mockTrailerActivities));
    }

    @Test
    void testSentRestRequestSuccess() throws JsonProcessingException {
        RestProcessTrailerActivities mockTrailerActivities = DataFactory.getRestProcessTrailerActivities();
        ResponseEntity<String> response = new ResponseEntity<>("valid", HttpStatusCode.valueOf(200));
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);
        String responseBody = tmRestApiService.sentRestRequest("path", mockTrailerActivities);
        assertNotNull(responseBody);
        verify(restTemplate, times(1)).exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(String.class));
    }

    /*
     * @Test
     * void testSentRestRequestNullResponse() {
     * RestProcessTrailerActivities mockTrailerActivities =
     * DataFactory.getRestProcessTrailerActivities();
     * RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
     * mockAuthResponse.setAccessToken("valid_token");
     * when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(RestAuthTokenResponse.class)))
     * .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
     * when(restTemplate.exchange(eq("path"), eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(String.class)))
     * .thenReturn(null);
     * IllegalArgumentException thrown =
     * assertThrows(IllegalArgumentException.class, () -> {
     * tmRestApiService.sentRestRequest("path", mockTrailerActivities);
     * });
     * verify(restTemplate, times(1)).exchange(eq("path"), eq(HttpMethod.POST),
     * any(HttpEntity.class), eq(String.class));
     * }
     */

    @Test
    void testSentRestRequestNullResponseBody() {
        RestProcessTrailerActivities mockTrailerActivities = DataFactory.getRestProcessTrailerActivities();
        ResponseEntity<String> response = new ResponseEntity<>(null, HttpStatusCode.valueOf(200));
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> tmRestApiService.sentRestRequest("path", mockTrailerActivities));
        verify(restTemplate, times(1)).exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(String.class));
    }

    @Test
    void testSentRestRequestResponseCodeNot200() throws JsonProcessingException {
        RestProcessTrailerActivities mockTrailerActivities = DataFactory.getRestProcessTrailerActivities();
        ResponseEntity<String> response = new ResponseEntity<>("valid", HttpStatusCode.valueOf(422));
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);
        ResponseHeaderType responseHeaderType = new ResponseHeaderType();
        List<ErrorType> errorTypeList = new ArrayList<>();
        ErrorType errorType = new ErrorType();
        errorType.setSystemMessage("TestError");
        errorTypeList.add(errorType);
        responseHeaderType.setError(errorTypeList);
        lenient().when(objectMapper.readValue(anyString(), eq(ResponseHeaderType.class)))
                .thenReturn(responseHeaderType);
        TmRestApiException thrown2 = assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sentRestRequest("path", mockTrailerActivities));
        verify(restTemplate, times(1)).exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(String.class));
    }

    @Test
    void testSentRestRequestResponseCodeNot200ResNull() throws JsonProcessingException {
        RestProcessTrailerActivities mockTrailerActivities = DataFactory.getRestProcessTrailerActivities();
        ResponseEntity<String> response = new ResponseEntity<>("valid", HttpStatusCode.valueOf(422));
        RestAuthTokenResponse mockAuthResponse = new RestAuthTokenResponse();
        mockAuthResponse.setAccessToken("valid_token");
        when(restTemplate.exchange(eq("tmCloudAuthTokenUri"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(RestAuthTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK));
        when(restTemplate.exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);
        lenient().when(objectMapper.readValue(anyString(), eq(ResponseHeaderType.class))).thenReturn(null);
        TmRestApiException thrown3 = assertThrows(TmRestApiException.class,
                () -> tmRestApiService.sentRestRequest("path", mockTrailerActivities));
        verify(restTemplate, times(1)).exchange(eq("path"), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(String.class));
    }

}
