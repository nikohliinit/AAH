package com.macys.logistics.shipment.load.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TmRestApiServiceCoverageTest {

        @Mock
        private LoadMapper loadMapper;
        @Mock
        private ObjectMapper objectMapper;
        @Mock
        private RestTemplate restTemplate;
        @InjectMocks
        private TmRestApiService tmRestApiService;

        @BeforeEach
        void setUp() {
                ReflectionTestUtils.setField(tmRestApiService, "clientId", "testClientId");
                ReflectionTestUtils.setField(tmRestApiService, "clientSecret", "testSecret");
                ReflectionTestUtils.setField(tmRestApiService, "scope", "testScope");
                ReflectionTestUtils.setField(tmRestApiService, "tmCloudAuthTokenUri", "http://auth/token");
                ReflectionTestUtils.setField(tmRestApiService, "tmRestAPIBaseUri", "http://api/base");
                ReflectionTestUtils.setField(tmRestApiService, "authToken", null);
                ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime", 0L);
        }

        // ======= helper: mock a valid auth token =======

        @SuppressWarnings("unchecked")
        private void mockAuthToken() {
                RestAuthTokenResponse tokenResponse = new RestAuthTokenResponse();
                tokenResponse.setAccessToken("test-token");
                ResponseEntity<RestAuthTokenResponse> tokenEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
                lenient().when(restTemplate.exchange(eq("http://auth/token"), eq(HttpMethod.POST),
                        any(HttpEntity.class), eq(RestAuthTokenResponse.class))).thenReturn(tokenEntity);
        }

        // ======= getToken =======

        @Test
        @SuppressWarnings("unchecked")
        void getToken_freshToken() {
                mockAuthToken();
                String token = tmRestApiService.getToken();
                assertEquals("test-token", token);
        }

        @Test
        void getToken_cachedToken() {
                ReflectionTestUtils.setField(tmRestApiService, "authToken", "cached-token");
                ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
                        System.currentTimeMillis() + 3600_000L);

                String token = tmRestApiService.getToken();

                assertEquals("cached-token", token);
                verifyNoInteractions(restTemplate);
        }

        @Test
        @SuppressWarnings("unchecked")
        void getToken_expiredToken_refetches() {
                ReflectionTestUtils.setField(tmRestApiService, "authToken", "old-token");
                ReflectionTestUtils.setField(tmRestApiService, "tokenExpirationTime",
                        System.currentTimeMillis() - 1000L);
                mockAuthToken();

                String token = tmRestApiService.getToken();

                assertEquals("test-token", token);
        }

        @Test
        @SuppressWarnings("unchecked")
        void getToken_nullBody_throws() {
                RestAuthTokenResponse tokenResponse = new RestAuthTokenResponse();
                tokenResponse.setAccessToken(null);
                ResponseEntity<RestAuthTokenResponse> tokenEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
                when(restTemplate.exchange(eq("http://auth/token"), eq(HttpMethod.POST),
                        any(HttpEntity.class), eq(RestAuthTokenResponse.class))).thenReturn(tokenEntity);

                assertThrows(TmRestApiException.class, () -> tmRestApiService.getToken());
        }

        @Test
        @SuppressWarnings("unchecked")
        void getToken_blankAccessToken_throws() {
                RestAuthTokenResponse tokenResponse = new RestAuthTokenResponse();
                tokenResponse.setAccessToken("   ");
                ResponseEntity<RestAuthTokenResponse> tokenEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
                when(restTemplate.exchange(eq("http://auth/token"), eq(HttpMethod.POST),
                        any(HttpEntity.class), eq(RestAuthTokenResponse.class))).thenReturn(tokenEntity);

                assertThrows(TmRestApiException.class, () -> tmRestApiService.getToken());
        }

        @Test
        @SuppressWarnings("unchecked")
        void getToken_nullResponseBody_throws() {
                ResponseEntity<RestAuthTokenResponse> tokenEntity = new ResponseEntity<>(null, HttpStatus.OK);
                when(restTemplate.exchange(eq("http://auth/token"), eq(HttpMethod.POST),
                        any(HttpEntity.class), eq(RestAuthTokenResponse.class))).thenReturn(tokenEntity);

                assertThrows(TmRestApiException.class, () -> tmRestApiService.getToken());
        }

        // ======= sendRequest =======

        @Test
        @SuppressWarnings("unchecked")
        void sendRequest_success() {
                mockAuthToken();
                RestProcessMaintainEntityAttachmentResponse response = new RestProcessMaintainEntityAttachmentResponse();
                ResponseHeaderType header = new ResponseHeaderType();
                header.setCompletedSuccessfully(true);
                response.setResponseHeader(header);
                ResponseEntity<RestProcessMaintainEntityAttachmentResponse> responseEntity = new ResponseEntity<>(
                        response, HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestProcessMaintainEntityAttachmentResponse.class))).thenReturn(responseEntity);

                RestProcessMaintainEntityAttachmentResponse result = tmRestApiService.sendRequest(
                        "/path", new RestProcessMaintainEntityAttachment(),
                        RestProcessMaintainEntityAttachmentResponse.class);

                assertSame(response, result);
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequest_nullBody_throws() {
                mockAuthToken();
                ResponseEntity<RestProcessMaintainEntityAttachmentResponse> responseEntity = new ResponseEntity<>(null,
                        HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestProcessMaintainEntityAttachmentResponse.class))).thenReturn(responseEntity);

                RestProcessMaintainEntityAttachment attachment = new RestProcessMaintainEntityAttachment();
                assertThrows(TmRestApiException.class, () -> tmRestApiService.sendRequest(
                        "/path", attachment,
                        RestProcessMaintainEntityAttachmentResponse.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequest_invalidResponse_throws() {
                mockAuthToken();
                RestProcessMaintainEntityAttachmentResponse response = new RestProcessMaintainEntityAttachmentResponse();
                ResponseHeaderType header = new ResponseHeaderType();
                header.setCompletedSuccessfully(false);
                response.setResponseHeader(header);
                ResponseEntity<RestProcessMaintainEntityAttachmentResponse> responseEntity = new ResponseEntity<>(
                        response, HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestProcessMaintainEntityAttachmentResponse.class))).thenReturn(responseEntity);

                RestProcessMaintainEntityAttachment attachment = new RestProcessMaintainEntityAttachment();
                assertThrows(TmRestApiException.class, () -> tmRestApiService.sendRequest(
                        "/path", attachment,
                        RestProcessMaintainEntityAttachmentResponse.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequest_validResponseWithErrors() {
                mockAuthToken();
                RestProcessMaintainEntityAttachmentResponse response = new RestProcessMaintainEntityAttachmentResponse();
                ResponseHeaderType header = new ResponseHeaderType();
                header.setCompletedSuccessfully(false);
                ErrorType error = new ErrorType();
                error.setSystemMessage("error msg");
                header.getError().add(error);
                response.setResponseHeader(header);
                ResponseEntity<RestProcessMaintainEntityAttachmentResponse> responseEntity = new ResponseEntity<>(
                        response, HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(RestProcessMaintainEntityAttachmentResponse.class))).thenReturn(responseEntity);

                RestProcessMaintainEntityAttachmentResponse result = tmRestApiService.sendRequest(
                        "/path", new RestProcessMaintainEntityAttachment(),
                        RestProcessMaintainEntityAttachmentResponse.class);

                assertSame(response, result);
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequest_nullResponseHeader_throws() {
                mockAuthToken();
                ProcessLoadRetrieveResponse response = new ProcessLoadRetrieveResponse();
                // responseHeader is null by default — exercises extractErrors/validateResponse
                // null-header branches
                ResponseEntity<ProcessLoadRetrieveResponse> responseEntity = new ResponseEntity<>(response,
                        HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(ProcessLoadRetrieveResponse.class))).thenReturn(responseEntity);

                ProcessLoadRetrieve request = new ProcessLoadRetrieve();
                TmRestApiException thrown = assertThrows(TmRestApiException.class, () -> tmRestApiService.sendRequest(
                        "/path", request, ProcessLoadRetrieveResponse.class));
                assertTrue(thrown.getMessage().contains("Error in service response"));
        }

        // ======= sendRequestUntyped =======

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestUntyped_success() {
                mockAuthToken();
                ProcessTenderRejectResponse expected = new ProcessTenderRejectResponse();
                ResponseEntity<ProcessTenderRejectResponse> responseEntity = new ResponseEntity<>(expected,
                        HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(ProcessTenderRejectResponse.class))).thenReturn(responseEntity);

                ProcessTenderRejectResponse result = tmRestApiService.sendRequestUntyped(
                        "/path", new ProcessTenderReject(), ProcessTenderRejectResponse.class);

                assertSame(expected, result);
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestUntyped_nullBody_throws() {
                mockAuthToken();
                ResponseEntity<ProcessTenderRejectResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(ProcessTenderRejectResponse.class))).thenReturn(responseEntity);

                ProcessTenderReject reject = new ProcessTenderReject();
                assertThrows(TmRestApiException.class, () -> tmRestApiService.sendRequestUntyped(
                        "/path", reject, ProcessTenderRejectResponse.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestUntyped_exchangeThrows() {
                mockAuthToken();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(ProcessTenderRejectResponse.class)))
                        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

                ProcessTenderReject reject = new ProcessTenderReject();
                assertThrows(TmRestApiException.class, () -> tmRestApiService.sendRequestUntyped(
                        "/path", reject, ProcessTenderRejectResponse.class));
        }

        // ======= sentRequest =======

        @Test
        @SuppressWarnings("unchecked")
        void sentRequest_success() {
                mockAuthToken();
                ShipmentResponse response = new ShipmentResponse();
                ResponseHeaderType header = new ResponseHeaderType();
                header.setCompletedSuccessfully(true);
                response.setResponseHeader(header);
                ResponseEntity<ShipmentResponse> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(ShipmentResponse.class))).thenReturn(responseEntity);

                ShipmentResponse result = tmRestApiService.sentRequest("/path", new RestFindEntity(),
                        ShipmentResponse.class);

                assertSame(response, result);
        }

        @Test
        @SuppressWarnings("unchecked")
        void sentRequest_httpClientError() {
                mockAuthToken();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(ShipmentResponse.class)))
                        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

                RestFindEntity findEntity = new RestFindEntity();
                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sentRequest("/path", findEntity,
                                ShipmentResponse.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sentRequest_httpServerError() {
                mockAuthToken();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(ShipmentResponse.class)))
                        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

                RestFindEntity findEntity = new RestFindEntity();
                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sentRequest("/path", findEntity,
                                ShipmentResponse.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sentRequest_unexpectedException() {
                mockAuthToken();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(ShipmentResponse.class)))
                        .thenThrow(new RuntimeException("unexpected"));

                RestFindEntity findEntity = new RestFindEntity();
                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sentRequest("/path", findEntity,
                                ShipmentResponse.class));
        }

        // ======= sendRequestForProcessStopAppointment =======

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestForProcessStopAppointment_success() throws Exception {
                mockAuthToken();

                ProcessStopAppointment input = buildProcessStopAppointment();

                ProcessStopAppointmentResponse expected = new ProcessStopAppointmentResponse();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"body\":true}", HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);
                when(objectMapper.readValue("{\"body\":true}", ProcessStopAppointmentResponse.class))
                        .thenReturn(expected);

                ProcessStopAppointmentResponse result = tmRestApiService.sendRequestForProcessStopAppointment("/path",
                        input);

                assertSame(expected, result);
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestForProcessStopAppointment_httpServerError() {
                mockAuthToken();
                ProcessStopAppointment input = buildProcessStopAppointment();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

                assertThrows(HttpServerErrorException.class,
                        () -> tmRestApiService.sendRequestForProcessStopAppointment("/path", input));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestForProcessStopAppointment_resourceAccessException() {
                mockAuthToken();
                ProcessStopAppointment input = buildProcessStopAppointment();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenThrow(new ResourceAccessException("timeout"));

                assertThrows(ResourceAccessException.class,
                        () -> tmRestApiService.sendRequestForProcessStopAppointment("/path", input));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestForProcessStopAppointment_genericException() {
                mockAuthToken();
                ProcessStopAppointment input = buildProcessStopAppointment();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenThrow(new RuntimeException("generic"));

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sendRequestForProcessStopAppointment("/path", input));
        }

        // ======= sendRequestToGetLoadID =======

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestToGetLoadID_success() throws Exception {
                mockAuthToken();
                RestFindEntity input = new RestFindEntity();
                FindEntitiesResponse expected = new FindEntitiesResponse();
                RestFindEntitiesResponse restResp = new RestFindEntitiesResponse();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"data\":1}", HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);
                when(objectMapper.readValue("{\"data\":1}", RestFindEntitiesResponse.class))
                        .thenReturn(restResp);
                when(loadMapper.mapFindEntitiesResponse(restResp)).thenReturn(expected);

                FindEntitiesResponse result = tmRestApiService.sendRequestToGetLoadID("/path", input);

                assertSame(expected, result);
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestToGetLoadID_nullResponse() throws Exception {
                mockAuthToken();
                RestFindEntity input = new RestFindEntity();
                ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);

                FindEntitiesResponse result = tmRestApiService.sendRequestToGetLoadID("/path", input);

                assertNull(result);
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestToGetLoadID_httpServerError() {
                mockAuthToken();
                RestFindEntity input = new RestFindEntity();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sendRequestToGetLoadID("/path", input));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestToGetLoadID_resourceAccessException() {
                mockAuthToken();
                RestFindEntity input = new RestFindEntity();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenThrow(new ResourceAccessException("timeout"));

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sendRequestToGetLoadID("/path", input));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestToGetLoadID_jsonProcessingException_from_handleResponse() throws Exception {
                mockAuthToken();
                RestFindEntity input = new RestFindEntity();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"bad\":1}",
                        HttpStatus.UNPROCESSABLE_ENTITY);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);
                when(objectMapper.readValue("{\"bad\":1}", ResponseHeaderType.class))
                        .thenThrow(mock(JsonProcessingException.class));

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sendRequestToGetLoadID("/path", input));
        }

        // ======= sendRequestToGetLoadDetails =======

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestToGetLoadDetails_success() throws Exception {
                mockAuthToken();
                RestFindEntity input = new RestFindEntity();
                FindEntitiesResponse expected = new FindEntitiesResponse();
                LoadRestFindEntitiesResponse loadResp = new LoadRestFindEntitiesResponse();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"load\":1}", HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);
                when(objectMapper.readValue("{\"load\":1}", LoadRestFindEntitiesResponse.class))
                        .thenReturn(loadResp);
                when(loadMapper.mapLoadFindEntitiesResponse(loadResp)).thenReturn(expected);

                FindEntitiesResponse result = tmRestApiService.sendRequestToGetLoadDetails("/path", input);

                assertSame(expected, result);
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestToGetLoadDetails_exception() {
                mockAuthToken();
                RestFindEntity input = new RestFindEntity();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sendRequestToGetLoadDetails("/path", input));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sendRequestToGetLoadDetails_nullResponseBody() throws Exception {
                mockAuthToken();
                RestFindEntity input = new RestFindEntity();
                ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);

                FindEntitiesResponse result = tmRestApiService.sendRequestToGetLoadDetails("/path", input);

                assertNull(result);
        }

        // ======= sentRestRequest =======

        @Test
        @SuppressWarnings("unchecked")
        void sentRestRequest_success200() throws Exception {
                mockAuthToken();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"ok\":true}", HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);

                String result = tmRestApiService.sentRestRequest("/path", "request-body");

                assertEquals("{\"ok\":true}", result);
        }

        @Test
        @SuppressWarnings("unchecked")
        void sentRestRequest_nullBody_throws() {
                mockAuthToken();
                ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);

                assertThrows(IllegalArgumentException.class,
                        () -> tmRestApiService.sentRestRequest("/path", "request-body"));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sentRestRequest_non200_throwsTmRestApiException() throws Exception {
                mockAuthToken();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"err\":true}", HttpStatus.BAD_REQUEST);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);
                ResponseHeaderType headerType = new ResponseHeaderType();
                ErrorType error = new ErrorType();
                error.setSystemMessage("bad request");
                headerType.getError().add(error);
                when(objectMapper.readValue("{\"err\":true}", ResponseHeaderType.class))
                        .thenReturn(headerType);

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sentRestRequest("/path", "request-body"));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sentRestRequest_httpServerError() {
                mockAuthToken();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

                assertThrows(HttpServerErrorException.class,
                        () -> tmRestApiService.sentRestRequest("/path", "request-body"));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sentRestRequest_resourceAccessException() {
                mockAuthToken();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenThrow(new ResourceAccessException("timeout"));

                assertThrows(ResourceAccessException.class,
                        () -> tmRestApiService.sentRestRequest("/path", "request-body"));
        }

        @Test
        @SuppressWarnings("unchecked")
        void sentRestRequest_genericException() {
                mockAuthToken();
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenThrow(new RuntimeException("unexpected"));

                assertThrows(TmsEntityServiceProcessException.class,
                        () -> tmRestApiService.sentRestRequest("/path", "request-body"));
        }

        // ======= handleResponse via sendRequestForProcessStopAppointment =======

        @Test
        @SuppressWarnings("unchecked")
        void handleResponse_422_throwsHttpClientError() throws Exception {
                mockAuthToken();
                ProcessStopAppointment input = buildProcessStopAppointment();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"e\":1}",
                        HttpStatus.UNPROCESSABLE_ENTITY);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);
                ResponseHeaderType headerType = new ResponseHeaderType();
                when(objectMapper.readValue("{\"e\":1}", ResponseHeaderType.class)).thenReturn(headerType);

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sendRequestForProcessStopAppointment("/path", input));
        }

        @Test
        @SuppressWarnings("unchecked")
        void handleResponse_500_throwsHttpServerError() throws Exception {
                mockAuthToken();
                ProcessStopAppointment input = buildProcessStopAppointment();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);

                assertThrows(HttpServerErrorException.class,
                        () -> tmRestApiService.sendRequestForProcessStopAppointment("/path", input));
        }

        @Test
        @SuppressWarnings("unchecked")
        void handleResponse_unexpectedCode() throws Exception {
                mockAuthToken();
                ProcessStopAppointment input = buildProcessStopAppointment();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", HttpStatus.CREATED);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sendRequestForProcessStopAppointment("/path", input));
        }

        @Test
        @SuppressWarnings("unchecked")
        void handleResponse_nullResponse_returnsNull() throws Exception {
                mockAuthToken();
                RestFindEntity input = new RestFindEntity();
                // simulate restTemplate returning null via the handleResponse null path
                // handleResponse is invoked through getLoadDetilSRestAPICall ->
                // sendRequestToGetLoadID
                // but handleResponse checks Optional.ofNullable(response).isEmpty() for the
                // ResponseEntity
                // We can't easily get a null ResponseEntity from restTemplate, so skip this
                // edge case
                // Instead test sendRequestToGetLoadID with a valid response whose body is null
                ResponseEntity<String> responseEntity = ResponseEntity.ok().body(null);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);

                // handleResponse treats response with null body at status 200 as returning the
                // body (null)
                FindEntitiesResponse result = tmRestApiService.sendRequestToGetLoadID("/path", input);

                assertNull(result);
        }

        // ======= extractRestErrors =======

        @Test
        @SuppressWarnings("unchecked")
        void extractRestErrors_nullHeaderType() throws Exception {
                mockAuthToken();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"data\":1}", HttpStatus.BAD_REQUEST);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);
                when(objectMapper.readValue("{\"data\":1}", ResponseHeaderType.class))
                        .thenReturn(null);

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sentRestRequest("/path", "request-body"));
        }

        @Test
        @SuppressWarnings("unchecked")
        void extractRestErrors_emptyErrorList() throws Exception {
                mockAuthToken();
                ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"d\":1}", HttpStatus.BAD_REQUEST);
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                        .thenReturn(responseEntity);
                ResponseHeaderType headerType = new ResponseHeaderType();
                when(objectMapper.readValue("{\"d\":1}", ResponseHeaderType.class))
                        .thenReturn(headerType);

                assertThrows(TmRestApiException.class,
                        () -> tmRestApiService.sentRestRequest("/path", "request-body"));
        }

        // ======= helper method =======

        private ProcessStopAppointment buildProcessStopAppointment() {
                try {
                        ProcessStopAppointment input = new ProcessStopAppointment();
                        StopAppointmentType stopAppointment = new StopAppointmentType();
                        AppointmentType appointment = new AppointmentType();
                        javax.xml.datatype.XMLGregorianCalendar dateTime = javax.xml.datatype.DatatypeFactory
                                .newInstance()
                                .newXMLGregorianCalendar("2024-01-01T00:00:00");
                        appointment.setAppointmentFromDateTime(dateTime);
                        appointment.setAppointmentStatusEnumVal(AppointmentStatusEnumValType.APP_NOTIFIED);
                        stopAppointment.setAppointment(appointment);
                        stopAppointment.setStopIDTypeEnumVal(StopIdTypeEnumValType.SID_ID);
                        stopAppointment.setSystemLoadID("LD001");
                        stopAppointment.setSystemStopID("ST001");
                        input.getStopAppointment().add(stopAppointment);
                        return input;
                } catch (javax.xml.datatype.DatatypeConfigurationException e) {
                        throw new RuntimeException(e);
                }
        }
}
