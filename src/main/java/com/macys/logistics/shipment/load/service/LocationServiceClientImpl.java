package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.response.Location;
import com.macys.logistics.shipment.load.dto.response.LocationDTO;
import com.macys.logistics.shipment.load.dto.response.YardResponse;
import com.macys.logistics.shipment.load.logging.CommonMetrics;
import com.macys.logistics.shipment.load.util.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@Log4j2
public class LocationServiceClientImpl implements LocationServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${loc.dtl.api.path}")
    private String locationUrl;

    @Value("${appointment.service.base.url}")
    private String appointmentServiceBasePath;
    
    @Value("${tms.shipment.rest.api.yard.info.url}")
    private String yardInfoUrl;
    
    @Value("${tms.shipment.rest.api.yard.area.type.number}")
    private String yardAreaTypNbr;

    @Override
    @Retryable(retryFor = {HttpServerErrorException.class, ResourceAccessException.class}, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public LocationDTO findLocnEntityByLocnAbbr(String locnNbr) {
        log.info(Constants.LOCN_ENTITY_METHODS_STARTS, locnNbr);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(CommonMetrics.X_CLIENT_ID.value(), CommonMetrics.TMS.value());
            headers.add(CommonMetrics.X_SUB_CLIENT_ID.value(), CommonMetrics.TMS.value());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<Location> responseEntity = restTemplate.exchange(locationUrl + locnNbr, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});
            log.debug(Constants.RESPONSE_ENTITY + responseEntity);
            Location location = responseEntity.getBody();
            log.debug(Constants.LOCATION + location);
            LocationDTO locationInfo= Optional.ofNullable(location)
                    .map(Location::getItems)
                    .filter(items -> !items.isEmpty())
                    .map(items -> items.get(0))
                    .orElse(null);
            log.debug(Constants.LOCATION_INFO+ locationInfo);
            log.info(Constants.LOCN_ENTITY_METHODS_ENDS, locnNbr);
            return locationInfo;
        } catch (HttpServerErrorException hse) {
            log.error(Constants.SERVER_EXCEPTION, locnNbr, hse);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceAccessException rae) {
            log.error(Constants.RESOURCE_EXCEPTION, locnNbr, rae);
            throw new ResourceAccessException(Constants.RESOURCE_ACCESS_EXCEPTION);
        } catch (HttpClientErrorException hce) {
            log.error(Constants.CLIENT_EXCEPTION,
                    locnNbr, hce.getStatusCode(), hce.getResponseBodyAsString(), hce);
            return null;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, locnNbr, e.getMessage());
            return null;
        }
    }


    @Override
    @Retryable(retryFor = {HttpServerErrorException.class, ResourceAccessException.class}, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public Optional<YardResponse> getYardInformation(AppointmentPayload apptPayload) {
        log.info("Started method getYardInformation to get  Yard Area Information apmtNbr: {} {}",apptPayload.getAppointmentNumber(), apptPayload.getAppointmentDestLocNumber());
        try {
            String yardAreaURL = appointmentServiceBasePath.concat(yardInfoUrl);
            String yardAreaId = URLEncoder.encode(apptPayload.getAppointmentCurrTrailerYardAreaID().strip(), StandardCharsets.UTF_8.toString());
            String url = String.format("%s?yardareaid=%s&locnbr=%s&yardareatypnbr=%s", yardAreaURL, yardAreaId, apptPayload.getAppointmentDestLocNumber(), yardAreaTypNbr);
            log.debug("getYardInformation|Door or Yard area info url {}",url);
            YardResponse yardResponse = restTemplate.getForObject(url, YardResponse.class);
            log.info("Ended method getYardInformation to get  Yard Area Information {}", apptPayload.getAppointmentDestLocNumber());
            return Optional.ofNullable(yardResponse);
        } catch (HttpServerErrorException hse) {
            log.error("Http server error occurred while calling appointment service api to get yard info: ", hse);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceAccessException rae) {
            log.error("Resource access error occurred while calling appointment service api to get yard info: ", rae);
            throw new ResourceAccessException("An error occurred while calling appointment service to get yard info");
        } catch (HttpClientErrorException hce) {
            log.error("HTTP client error while calling appointment service to get yard info with apmtNbr {}: status code {} and response body {}",
                    apptPayload.getAppointmentDestLocNumber(), hce.getStatusCode(), hce.getResponseBodyAsString(), hce);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Exception occurred while calling appointment service to get yard info with apmtNbr: {} Error: {}", apptPayload.getAppointmentDestLocNumber(), e.getMessage());
            return Optional.empty();
        }
    }

    @Recover
    public String recover(HttpServerErrorException hse, AppointmentPayload apptPayload) {
        log.warn("Http server error and maximum retries exceeded for get yard information");
        return null;
    }

    @Recover
    public String recover(ResourceAccessException rae, AppointmentPayload apptPayload) {
        log.warn("Resource access error and maximum retries exceeded to get yard information");
        return null;
    }

    @Recover
    public String recover(HttpServerErrorException hse, String locnNbr) {
        log.warn("Http server error and maximum retries exceeded for find location by locNbr");
        return null;
    }

    @Recover
    public String recover(ResourceAccessException rae, String locnNbr) {
        log.warn("Resource access error and maximum retries exceeded find location by locNbr");
        return null;
    }
}
