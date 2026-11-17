package com.macys.logistics.shipment.load.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.AppointmentSealConditionDetail;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.AddPreBuiltLoadResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadReturnToOpen;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadReturnToOpenResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadUpdateResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestProcessLoadUpdate;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestProcessShipmentOrderCreate;
import com.macys.logistics.shipment.load.dto.request.RestAddPreBuiltLoad;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TMRestApiClientAdditionalTest {

    @Mock
    private TmsLoadMetricsHelper metricsHelper;

    @Mock
    private TmRestApiService tmRestApiService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TMRestApiClient client;

    @BeforeEach
    void init() {
        // Inject restTemplate mock and configure paths used by these tests
        ReflectionTestUtils.setField(client, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(client, "addPreBuiltLoadPath", "path/addPreBuilt");
        ReflectionTestUtils.setField(client, "processReturnToOpenPath", "path/returnToOpen");
        ReflectionTestUtils.setField(client, "processLoadUpdatePath", "path/loadUpdate");
        ReflectionTestUtils.setField(client, "processShipmentOrderUpdatePath", "path/shipmentUpdate");
        ReflectionTestUtils.setField(client, "processTenderAcceptPath", "path/tenderAccept");
        ReflectionTestUtils.setField(client, "sealConditionAppointmentPath", "http://seal/api/");
    }

    @Test
    void testAddPreBuiltLoad_success() {
        RestAddPreBuiltLoad req = new RestAddPreBuiltLoad();
        AddPreBuiltLoadResponse resp = new AddPreBuiltLoadResponse();
        when(tmRestApiService.sendRequest(eq("path/addPreBuilt"), eq(req), eq(AddPreBuiltLoadResponse.class))).thenReturn(resp);

        AddPreBuiltLoadResponse actual = client.addPreBuiltLoad(req);
        assertEquals(resp, actual);
        verify(tmRestApiService).sendRequest("path/addPreBuilt", req, AddPreBuiltLoadResponse.class);
    }

    @Test
    void testAddPreBuiltLoad_exceptionWrapped() {
        RestAddPreBuiltLoad req = new RestAddPreBuiltLoad();
        when(tmRestApiService.sendRequest(eq("path/addPreBuilt"), eq(req), eq(AddPreBuiltLoadResponse.class)))
                .thenThrow(new RuntimeException("boom"));

        assertThrows(TmsLoadProcessException.class, () -> client.addPreBuiltLoad(req));
        verify(tmRestApiService).sendRequest("path/addPreBuilt", req, AddPreBuiltLoadResponse.class);
    }

    @Test
    void testProcessLoadReturnToOpen_successAndNoThrowOnException() {
        ProcessLoadReturnToOpen req = new ProcessLoadReturnToOpen();
        // success path
        client.processLoadReturnToOpen(req);
        verify(tmRestApiService).sentRequest(eq("path/returnToOpen"), eq(req), eq(ProcessLoadReturnToOpenResponse.class));

        // exception is swallowed
        doThrow(new RuntimeException("fail")).when(tmRestApiService).sentRequest(eq("path/returnToOpen"), eq(req), eq(ProcessLoadReturnToOpenResponse.class));
        client.processLoadReturnToOpen(req);
        verify(tmRestApiService, times(2)).sentRequest(eq("path/returnToOpen"), eq(req), eq(ProcessLoadReturnToOpenResponse.class));
    }

    @Test
    void testProcessRestTrailerNumberUpdate_success() throws Exception {
        RestProcessLoadUpdate req = new RestProcessLoadUpdate();
        String responseJson = "{\"ok\":true}";
        ProcessLoadUpdateResponse mapped = new ProcessLoadUpdateResponse();

        when(tmRestApiService.sentRestRequest(eq("path/loadUpdate"), eq(req))).thenReturn(responseJson);
        when(objectMapper.readValue(eq(responseJson), eq(ProcessLoadUpdateResponse.class))).thenReturn(mapped);

        ProcessLoadUpdateResponse actual = client.processRestTrailerNumberUpdate(req);
        assertNotNull(actual);
        assertEquals(mapped, actual);
        verify(metricsHelper).populateTransactionDomainAndType(anyString(), eq("ProcessLoadUpdateResponse"));
    }

    @Test
    void testProcessRestTrailerNumberUpdate_exceptionFromService() throws JsonProcessingException {
        RestProcessLoadUpdate req = new RestProcessLoadUpdate();
        when(tmRestApiService.sentRestRequest(eq("path/loadUpdate"), eq(req)))
                .thenThrow(new RuntimeException("svc-fail"));

        assertThrows(TmsLoadProcessException.class, () -> client.processRestTrailerNumberUpdate(req));
    }

    @Test
    void testProcessRestTrailerNumberUpdate_exceptionFromMapper() throws Exception {
        RestProcessLoadUpdate req = new RestProcessLoadUpdate();
        String responseJson = "{}";
        when(tmRestApiService.sentRestRequest(eq("path/loadUpdate"), eq(req))).thenReturn(responseJson);
        when(objectMapper.readValue(eq(responseJson), eq(ProcessLoadUpdateResponse.class)))
                .thenThrow(new JsonProcessingException("bad-json") {});

        assertThrows(TmsLoadProcessException.class, () -> client.processRestTrailerNumberUpdate(req));
    }

    @Test
    void testProcessUpdateShipment_success() {
        RestProcessShipmentOrderCreate req = new RestProcessShipmentOrderCreate();
        // The method just delegates to service and returns; validating no exception occurs
        client.processUpdateShipment(req);
        verify(tmRestApiService).sendRequest(eq("path/shipmentUpdate"), eq(req), eq(com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessShipmentOrderCreateResponse.class));
    }

    @Test
    void testProcessUpdateShipment_exceptionWrapped() {
        RestProcessShipmentOrderCreate req = new RestProcessShipmentOrderCreate();
        when(tmRestApiService.sendRequest(eq("path/shipmentUpdate"), eq(req), eq(com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessShipmentOrderCreateResponse.class)))
                .thenThrow(new RuntimeException("update-fail"));

        assertThrows(TmsLoadProcessException.class, () -> client.processUpdateShipment(req));
    }

    @Test
    void testCallAppointmentSealCondition_success() {
        Integer apptNbr = 123;
        AppointmentSealConditionDetail d1 = new AppointmentSealConditionDetail();
        AppointmentSealConditionDetail d2 = new AppointmentSealConditionDetail();
        AppointmentSealConditionDetail[] arr = new AppointmentSealConditionDetail[]{d1, d2};

        when(restTemplate.getForObject(eq("http://seal/api/123"), eq(AppointmentSealConditionDetail[].class), any(Object[].class)))
                .thenReturn(arr);

        var list = client.callAppointmentSealCondition(apptNbr);
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    void testCallAppointmentSealCondition_httpServerError_loggedAndEmpty() {
        Integer apptNbr = 999;
        when(restTemplate.getForObject(anyString(), eq(AppointmentSealConditionDetail[].class), any(Object[].class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));

        var list = client.callAppointmentSealCondition(apptNbr);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void testCallAppointmentSealCondition_resourceAccess_loggedAndEmpty() {
        Integer apptNbr = 999;
        when(restTemplate.getForObject(anyString(), eq(AppointmentSealConditionDetail[].class), any(Object[].class)))
                .thenThrow(new ResourceAccessException("na"));

        var list = client.callAppointmentSealCondition(apptNbr);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void testCallAppointmentSealCondition_httpClientError_loggedAndEmpty() {
        Integer apptNbr = 999;
        when(restTemplate.getForObject(anyString(), eq(AppointmentSealConditionDetail[].class), any(Object[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        var list = client.callAppointmentSealCondition(apptNbr);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void testCallAppointmentSealCondition_genericException_loggedAndEmpty() {
        Integer apptNbr = 999;
        when(restTemplate.getForObject(anyString(), eq(AppointmentSealConditionDetail[].class), any(Object[].class)))
                .thenThrow(new RuntimeException("x"));

        var list = client.callAppointmentSealCondition(apptNbr);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}
