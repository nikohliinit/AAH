package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.response.Location;
import com.macys.logistics.shipment.load.dto.response.LocationDTO;
import com.macys.logistics.shipment.load.dto.response.YardResponse;
import com.macys.logistics.shipment.load.logging.CommonMetrics;
import com.macys.logistics.shipment.load.util.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceClientImplTest {

    @InjectMocks
    LocationServiceClientImpl locationServiceClient;

    @Value("${loc.dtl.api.path}")
    private String fileLocation;
    @Mock
    private RestTemplate restTemplate;
    AppointmentPayload payload;

    @InjectMocks
    TestConstants testConstants;

    @BeforeEach
    void init() throws IOException {

        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("application-test.properties"));
        ReflectionTestUtils.setField(locationServiceClient, "appointmentServiceBasePath", properties.getProperty("appointment.service.base.url"));
        ReflectionTestUtils.setField(locationServiceClient, "yardInfoUrl",  properties.getProperty("tms.shipment.rest.api.yard.info.url"));
        ReflectionTestUtils.setField(locationServiceClient, "yardAreaTypNbr", properties.getProperty("tms.shipment.rest.api.yard.area.type.number") );
    }

    @Test
    void update_findLocnEntityByLocnAbbr() {
        Location location = new Location();
        List<LocationDTO> items = new ArrayList<>();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        locn.setId(0);
        locn.setStatus(null);
        items.add(locn);
        location.setItems(items);
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonMetrics.X_CLIENT_ID.value(), CommonMetrics.TMS.value());
        headers.add(CommonMetrics.X_SUB_CLIENT_ID.value(), CommonMetrics.TMS.value());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        lenient().when(restTemplate.exchange(fileLocation + "3", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<Location>() {})).thenReturn(new ResponseEntity<>(location, HttpStatus.OK));
        locationServiceClient.findLocnEntityByLocnAbbr(locn.getLocationNumber());
        verify(restTemplate, times(1)).exchange(fileLocation + "3", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<Location>() {});
    }

    @Test
    void testFindLocEntityByLocAbbr_HttpClientErrorException() {
        Location location = new Location();
        List<LocationDTO> items = new ArrayList<>();
        LocationDTO locn = new LocationDTO();
        locn.setAbbreviation("ST");
        locn.setLocnTypCode("DC");
        locn.setLocationNumber("3");
        locn.setId(0);
        locn.setStatus(null);
        items.add(locn);
        location.setItems(items);
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonMetrics.X_CLIENT_ID.value(), CommonMetrics.TMS.value());
        headers.add(CommonMetrics.X_SUB_CLIENT_ID.value(), CommonMetrics.TMS.value());
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        HttpClientErrorException clientError = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        lenient().when(restTemplate.exchange(fileLocation + "3", HttpMethod.GET, requestEntity, Location.class)).thenThrow(clientError);
        LocationDTO locationDTO = locationServiceClient.findLocnEntityByLocnAbbr(locn.getLocationNumber());
        Assertions.assertNull(locationDTO);
    }
    @Test
    void testGetYardInformation_Success() throws IOException {
        AppointmentPayload apptPayload = DataFactory.getAppointmentPayload();
        YardResponse expectedResponse = DataFactory.getYardResponse();
        when(restTemplate.getForObject(testConstants.YARD_AREA_INFO_URL, YardResponse.class)).thenReturn(expectedResponse);
        Optional<YardResponse> actualResponse = locationServiceClient.getYardInformation(apptPayload);
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedResponse, actualResponse.get());
    }

    @Test
    void testGetYardInformation_HttpClientErrorException() throws IOException {
        AppointmentPayload apptPayload = DataFactory.getAppointmentPayload();
        HttpClientErrorException clientError = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        when(restTemplate.getForObject(testConstants.YARD_AREA_INFO_URL, YardResponse.class))
                .thenThrow(clientError);
        Optional<YardResponse> actualResponse = locationServiceClient.getYardInformation(apptPayload);
        Assertions.assertTrue(actualResponse.isEmpty());
    }

    @Test
    void testGetYardInformation_ResourceAccessException() throws IOException {
        AppointmentPayload apptPayload = DataFactory.getAppointmentPayload();
        ResourceAccessException resourceAccessException = new ResourceAccessException(HttpStatus.BAD_REQUEST.getReasonPhrase());
        when(restTemplate.getForObject(testConstants.YARD_AREA_INFO_URL, YardResponse.class)).thenThrow(resourceAccessException);
        Assertions.assertThrows(ResourceAccessException.class, () -> locationServiceClient.getYardInformation(apptPayload));
    }

    @Test
    void testGetYardInformation_HttpServerErrorException() throws IOException {
        AppointmentPayload apptPayload = DataFactory.getAppointmentPayload();
        HttpServerErrorException serverError = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForObject(testConstants.YARD_AREA_INFO_URL, YardResponse.class)).thenThrow(serverError);
        Assertions.assertThrows(HttpServerErrorException.class, () -> locationServiceClient.getYardInformation(apptPayload));
    }

    @Test
    void testGetYardInformation_UnexpectedException() throws IOException {
        AppointmentPayload apptPayload = DataFactory.getAppointmentPayload();
        RuntimeException runtimeException = new RuntimeException("Unexpected error");
        when(restTemplate.getForObject(testConstants.YARD_AREA_INFO_URL, YardResponse.class))
                .thenThrow(runtimeException);
        Optional<YardResponse> actualResponse = locationServiceClient.getYardInformation(apptPayload);
        Assertions.assertFalse(actualResponse.isPresent());
    }

    @Test
    void testFindLocationByLocNbrResourceAccessErrorRecovery() {
        String result = locationServiceClient.recover(new ResourceAccessException("Bad Request"), "");
        assertNull(result);
    }

    @Test
    void testFindLocationByLocNbrHttpServerErrorRecovery() {
        String result = locationServiceClient.recover(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR), "");
        assertNull(result);
    }

    @Test
    void testGetYardInformationResourceAccessErrorRecovery() {
        String result = locationServiceClient.recover(new ResourceAccessException("Bad Request"), payload);
        assertNull(result);
    }

    @Test
    void testGetYardInformationHttpServerErrorRecovery() {
        String result = locationServiceClient.recover(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR), payload);
        assertNull(result);
    }
}
