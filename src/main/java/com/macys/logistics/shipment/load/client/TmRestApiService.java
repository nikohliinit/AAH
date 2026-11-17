package com.macys.logistics.shipment.load.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.response.RestAuthTokenResponse;
import com.macys.logistics.shipment.load.exception.TmRestApiException;
import com.macys.logistics.shipment.load.exception.TmsEntityServiceProcessException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.macys.logistics.shipment.load.util.Constants.*;

@Service
@Log4j2
public class TmRestApiService {

    @Autowired
    LoadMapper loadMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${tm.cloud.rest.auth.clientid}")
    private String clientId;
    @Value("${tm.cloud.rest.auth.client.secret}")
    private String clientSecret;
    @Value("${tm.cloud.rest.auth.scope}")
    private String scope;
    @Value("${tm.cloud.rest.auth.token-uri}")
    private String tmCloudAuthTokenUri;
    @Value("${tm.cloud.rest.api.base.url}")
    private String tmRestAPIBaseUri;

    private String authToken = null;
    private long tokenExpirationTime = 0;

    protected String getToken() {
        // Check if token exists and is not expired
        if (authToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            return authToken;
        }
        // Prepare the request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // Form data for token request
        String js = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret + "&scope="
                + scope;
        HttpEntity<String> entity = new HttpEntity<>(js, headers);
        // Make the request
        ResponseEntity<RestAuthTokenResponse> response = restTemplate.exchange(tmCloudAuthTokenUri, HttpMethod.POST,
                entity,
                RestAuthTokenResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.debug("Auth Token has generated successfully");
        }
        // Extract token from response
        authToken = extractTokenFromResponse(response.getBody());
        // Set expiration time 59 minutes from now
        tokenExpirationTime = System.currentTimeMillis() + (59 * 60 * 1000);

        return authToken;
    }

    /**
     * Extract Token
     *
     * @param body
     * @return
     */
    private String extractTokenFromResponse(RestAuthTokenResponse body) {
        if (body == null || StringUtils.isAllBlank(body.getAccessToken())) {
            log.error("Auth token generation failed");
            throw new TmRestApiException("TM AUTH token call failed!! Token is not generated");
        }
        return body.getAccessToken();
    }

    @Retryable(retryFor = { HttpServerErrorException.class, ResourceAccessException.class,
            TmsEntityServiceProcessException.class }, maxAttemptsExpression = "#{${api.max.retry.attempts}}", backoff = @Backoff(delayExpression = "${api.retry.backoff.delay}"))
    public <T, R extends HasResponseHeader> R sendRequest(String path, T request, Class<R> responseType) {

        String token = getToken();

        // Prepare the request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("apiSourceEnumVal", API_SOURCE_CLIENT_ID);
        headers.add("clientID", API_SOURCE_CLIENT_ID);
        try {
            ResponseEntity<R> response = restAPICall(request, headers, path, responseType);
            return response.getBody();
        } catch (Exception ex) {
            log.error(TM_REST_API_FAILURE_PREFIX + path + DUE_TO_SEPARATOR + ex.getMessage(), ex);
            throw new TmRestApiException(TM_REST_API_FAILURE_PREFIX + path + DUE_TO_SEPARATOR + ex.getMessage());
        }
    }

    /**
     * Send request without HasResponseHeader type constraint.
     * Workaround for DTOs that don't implement HasResponseHeader interface.
     */
    @Retryable(retryFor = { HttpServerErrorException.class, ResourceAccessException.class,
            TmsEntityServiceProcessException.class }, maxAttemptsExpression = "#{${api.max.retry.attempts}}", backoff = @Backoff(delayExpression = "${api.retry.backoff.delay}"))
    public <T, R> R sendRequestUntyped(String path, T request, Class<R> responseType) {

        String token = getToken();

        // Prepare the request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("apiSourceEnumVal", API_SOURCE_CLIENT_ID);
        headers.add("clientID", API_SOURCE_CLIENT_ID);

        try {
            HttpEntity<T> entity = new HttpEntity<>(request, headers);
            ResponseEntity<R> response = restTemplate.exchange(path, HttpMethod.POST, entity, responseType);

            if (response.getBody() == null) {
                throw new IllegalArgumentException("TM rest Api response is null here");
            }

            return response.getBody();
        } catch (Exception ex) {
            log.error(TM_REST_API_FAILURE_PREFIX + path + DUE_TO_SEPARATOR + ex.getMessage(), ex);
            throw new TmRestApiException(TM_REST_API_FAILURE_PREFIX + path + DUE_TO_SEPARATOR + ex.getMessage());
        }
    }

    private <R extends HasResponseHeader> boolean validateResponse(R response, List<ErrorType> errorList) {
        return response != null && response.getResponseHeader() != null
                && (response.getResponseHeader().isCompletedSuccessfully() || !CollectionUtils.isEmpty(errorList));
    }

    private <R extends HasResponseHeader> List<ErrorType> extractErrors(R response) {
        if (response == null || response.getResponseHeader() == null) {
            return Collections.emptyList();
        }
        return Optional.ofNullable(response.getResponseHeader()).map(ResponseHeaderType::getError)
                .orElse(Collections.emptyList());
    }

    @Retryable(retryFor = { HttpServerErrorException.class, ResourceAccessException.class,
            TmsEntityServiceProcessException.class }, maxAttemptsExpression = "#{${api.max.retry.attempts}}", backoff = @Backoff(delayExpression = "${api.retry.backoff.delay}"))
    public FindEntitiesResponse sendRequestToGetLoadID(String path, RestFindEntity request)
            throws JsonProcessingException {
        HttpHeaders headers = createHttpHeader();
        FindEntitiesResponse findEntitiesResponse = null;
        try {
            String response = getLoadDetilSRestAPICall(path, request, headers);
            if (Optional.ofNullable(response).isPresent()) {
                findEntitiesResponse = loadMapper
                        .mapFindEntitiesResponse(objectMapper.readValue(response, RestFindEntitiesResponse.class));
            }
            return findEntitiesResponse;
        } catch (Exception ex) {
            log.error("sendRequestToGetLoadID - Unexpected error for path  {}: {}", path, ex.getMessage());
            throw new TmRestApiException("Unexpected error for path get Load ID" + path + ": " + ex.getMessage(), ex);
        }

    }

    @Retryable(retryFor = { HttpServerErrorException.class, ResourceAccessException.class,
            TmsEntityServiceProcessException.class }, maxAttemptsExpression = "#{${api.max.retry.attempts}}", backoff = @Backoff(delayExpression = "${api.retry.backoff.delay}"))
    public FindEntitiesResponse sendRequestToGetLoadDetails(String path, RestFindEntity request) {
        HttpHeaders headers = createHttpHeader();
        FindEntitiesResponse findEntitiesResponse = null;
        try {
            String response = getLoadDetilSRestAPICall(path, request, headers);
            if (Optional.ofNullable(response).isPresent()) {
                findEntitiesResponse = loadMapper.mapLoadFindEntitiesResponse(
                        objectMapper.readValue(response, LoadRestFindEntitiesResponse.class));
            }
            return findEntitiesResponse;
        } catch (Exception ex) {
            log.error("sendRequestToGetLoadDetails - Unexpected error for path get Load Details {}: {}", path,
                    ex.getMessage(), ex);
            throw new TmRestApiException("Unexpected error for path get Load " + path + ": " + ex.getMessage(), ex);
        }
    }

    @Retryable(retryFor = { HttpServerErrorException.class, ResourceAccessException.class,
            TmsEntityServiceProcessException.class }, maxAttemptsExpression = "#{${api.max.retry.attempts}}", backoff = @Backoff(delayExpression = "${api.retry.backoff.delay}"))
    public <T, R extends HasResponseHeader> R sentRequest(String path, T request, Class<R> responseType) {
        String token = getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        try {
            ResponseEntity<R> response = restAPICall(request, headers, path, responseType);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("HTTP error for path {}: {} - {}", path, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new TmRestApiException("HTTP error for path " + path + ": " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error for path {}: {}", path, ex.getMessage(), ex);
            throw new TmRestApiException("Unexpected error for path " + path + ": " + ex.getMessage(), ex);
        }
    }

    @Retryable(retryFor = { HttpServerErrorException.class, ResourceAccessException.class,
            TmsEntityServiceProcessException.class }, maxAttemptsExpression = "#{${api.max.retry.attempts}}", backoff = @Backoff(delayExpression = "${api.retry.backoff.delay}"))
    public ProcessStopAppointmentResponse sendRequestForProcessStopAppointment(String path,
                                                                               ProcessStopAppointment request) {

        HttpHeaders headers = createHttpHeader();
        ProcessStopAppointmentResponse processStopAppointmentResponse = null;

        try {
            RestAppointment appointment = new RestAppointment();
            appointment.setAppointmentFromDateTime(
                    request.getStopAppointment().get(0).getAppointment().getAppointmentFromDateTime().toString());
            appointment.setAppointmentStatusEnumVal(
                    request.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumVal().toString());
            appointment.setAppointmentToDateTime(
                    request.getStopAppointment().get(0).getAppointment().getAppointmentFromDateTime().toString());

            // Create the StopAppointment
            RestStopAppointment stopAppointment = new RestStopAppointment();
            stopAppointment.setAppointment(appointment);
            stopAppointment.setStopIDTypeEnumVal(request.getStopAppointment().get(0).getStopIDTypeEnumVal().toString());
            stopAppointment.setSystemLoadID(request.getStopAppointment().get(0).getSystemLoadID());
            stopAppointment.setSystemStopID(request.getStopAppointment().get(0).getSystemStopID());
            // Create the request
            RestStopAppointmentRequest restStopAppointmentRequest = new RestStopAppointmentRequest();
            restStopAppointmentRequest.setStopAppointment(Collections.singletonList(stopAppointment));
            HttpEntity<RestStopAppointmentRequest> entity = new HttpEntity<>(restStopAppointmentRequest, headers);
            // Make the request
            ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.POST, entity, String.class);
            String responseBody = handleResponse(response);
            if (Optional.ofNullable(responseBody).isPresent()) {
                processStopAppointmentResponse = (objectMapper.readValue(responseBody,
                        ProcessStopAppointmentResponse.class));
            }
            return processStopAppointmentResponse;
        } catch (HttpServerErrorException hse) {
            log.error("Http server error occurred while calling rest API  ", hse);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceAccessException rae) {
            log.error("Resource access error occurred while calling rest API ", rae);
            throw new ResourceAccessException("An error occurred while calling rest API ");
        } catch (Exception ex) {
            log.error("Unexpected error for path {}: {}", path, ex.getMessage(), ex);
            throw new TmRestApiException("Unexpected error for path " + path + ": " + ex.getMessage(), ex);
        }
    }

    private HttpHeaders createHttpHeader() {
        String token = getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private String handleResponse(ResponseEntity<String> response) throws JsonProcessingException {
        if (Optional.ofNullable(response).isEmpty()) {
            log.error("error!!Rest API response is null");
            return null;
        }
        switch (response.getStatusCode().value()) {
            case 200:
                return response.getBody();
            case 422:
                ResponseHeaderType responseHeaderType = objectMapper.readValue(response.getBody(),
                        ResponseHeaderType.class);
                log.error("Request failed with the Response code: {} and response body:{}",
                        response.getStatusCode().value(), responseHeaderType);
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
            case 500:
                log.error("Server Error: Please try again later.");
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
            default:
                log.error("Unexpected response code: " + response.getStatusCode().value());
                throw new TmRestApiException("Service request failed: Please try again later.");
        }
    }

    @Retryable(retryFor = {
            HttpServerErrorException.class, ResourceAccessException.class,
            TmsEntityServiceProcessException.class }, maxAttemptsExpression = "#{${api.max.retry.attempts}}", backoff = @Backoff(delayExpression = "${api.retry.backoff.delay}"))
    public <T> String sentRestRequest(String path, T request) throws JsonProcessingException {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = createHttpHeader();
            HttpEntity<T> entity = new HttpEntity<>(request, headers);
            response = restTemplate.exchange(path, HttpMethod.POST, entity, String.class);
        } catch (HttpServerErrorException hse) {
            log.error("Http server error occurred while calling rest API : ", hse);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, hse.getMessage());
        } catch (ResourceAccessException rae) {
            log.error("Resource access error occurred while calling rest API  ", rae);
            throw new ResourceAccessException("An error occurred while calling rest API due to " + rae.getMessage());
        } catch (Exception ex) {
            log.error("Exception while calling rest API. error message: {} .exception: {}", ex.getMessage(), ex);
            throw new TmsEntityServiceProcessException("Exception while calling rest API.Due to " + ex.getMessage());
        }
        if (response.getBody() == null) {
            throw new IllegalArgumentException("TM rest Api response is null");
        } else if (response.getStatusCode().value() == 200) {
            return response.getBody();
        } else {
            String errorMessage = extractRestErrors(response);
            log.error("error|TM rest api|response code: {} and error:{}", response.getStatusCode().value(),
                    errorMessage);
            throw new TmRestApiException("Error in TM rest api response: " + errorMessage);
        }

    }

    private String extractRestErrors(ResponseEntity<String> response) throws JsonProcessingException {
        ResponseHeaderType responseHeaderType = objectMapper.readValue(response.getBody(), ResponseHeaderType.class);
        List<ErrorType> errorTypeList;
        if (responseHeaderType == null) {
            errorTypeList = Collections.emptyList();
        } else {
            errorTypeList = Optional.ofNullable(responseHeaderType).map(ResponseHeaderType::getError)
                    .orElse(Collections.emptyList());
        }
        return errorTypeList.stream().map(ErrorType::getSystemMessage).collect(Collectors.joining("\n"));
    }

    private <R extends HasResponseHeader, T> ResponseEntity<R> restAPICall(T request, HttpHeaders headers, String path,
                                                                           Class<R> responseType) {
        HttpEntity<T> entity = new HttpEntity<>(request, headers);
        // Make the request
        ResponseEntity<R> response = restTemplate.exchange(path, HttpMethod.POST, entity, responseType);
        if (response.getBody() == null) {
            throw new IllegalArgumentException("TM rest Api response is null here");
        }
        R responseBody = response.getBody();
        List<ErrorType> errorList = extractErrors(responseBody);
        if (!validateResponse(responseBody, errorList)) {
            String errorMessage = errorList
                    .stream()
                    .map(ErrorType::getSystemMessage)
                    .collect(Collectors.joining("\n"));
            throw new TmRestApiException("Error in service response: " + errorMessage);
        }
        // Process the response as needed
        return response;

    }

    private String getLoadDetilSRestAPICall(String path, RestFindEntity request, HttpHeaders headers) {
        HttpEntity<RestFindEntity> entity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.POST, entity, String.class);
            return handleResponse(response);
        } catch (HttpServerErrorException hse) {
            log.error("getLoadDetilSRestAPICall - Http server error occurred while calling rest API : ", hse);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceAccessException rae) {
            log.error("getLoadDetilSRestAPICall - Resource access error occurred while calling rest API : ", rae);
            throw new ResourceAccessException("An error occurred while calling rest API");
        } catch (JsonProcessingException e) {
            throw new TmRestApiException("Error in service response: " + e);
        }
    }
}
