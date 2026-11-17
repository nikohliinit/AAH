package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.client.RestApiClient;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.ApiResponse;
import com.macys.logistics.shipment.load.dto.AuditTrailerFeedback;
import com.macys.logistics.shipment.load.dto.TrailerDoorDockCDCEvent;
import com.macys.logistics.shipment.load.dto.TrlrData;
import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleInputDTO;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo;
import com.macys.logistics.shipment.load.dto.response.LocationDTO;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static com.macys.logistics.shipment.load.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrailerDockDoorServiceImpl implements TrailerDockDoorService {


    private final TMRestApiClient tmRestAPIClient;

    private final  LocationServiceClient locationServiceClient;

    private final LoadEnrichmentService loadEnrichmentService;

    private final LoadMapper loadMapper;
    private final CommonUtil commonUtil;
    private final RestApiClient restApiClient;

    @Value("${trailer.yard.appointment.info.url}")
    private String trailerYardAppointmentInfoUrl;

    @Override
    public String processDockDoorAttach(String loadId, String dockDoor) {
        log.info("Started method processDockDoorAttach for loadId {},dockDoor{}", loadId,dockDoor);

        try {
            ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
            processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(
                    createReferenceNumberUpdateDataType(loadId, dockDoor, REF_NUM_ACTION_ENUM_VAL_ADD, null));
            log.debug("Calling REST API to attach dock door for loadId {},dockDoor{}", loadId,dockDoor);
            tmRestAPIClient.processMaintainEntityReferenceNumberForDockDoor(processMaintainEntityReferenceNumber);
            log.debug("Successfully attached dock door for loadId {},dockDoor{}", loadId,dockDoor);
        } catch (TmsLoadProcessException e) {
            log.error("Error while attaching dock door for load {}: {}", loadId, e.getMessage());
            return FAILURE_PROCESS_STATUS + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error while attaching dock door for load {}: {}", loadId, e.getMessage());
            return FAILURE_PROCESS_STATUS + e.getMessage();
        }
        return SUCCESS_PROCESS_STATUS;
    }

    @Override
    public String processDockDoorUpdate(String referenceNumber, String loadId, String dockDoor) {
        log.info("Started method processDockDoorUpdate for loadId {},referenceNumber {},dockDoor {}", loadId,referenceNumber,dockDoor);

        try {
            ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
            processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(
                    createReferenceNumberUpdateDataType(loadId, dockDoor, REF_NUM_ACTION_ENUM_VAL_UPDATE, referenceNumber)
            );
            log.debug("Calling REST API to update dock door for loadId {},referenceNumber {},dockDoor {}", loadId,referenceNumber,dockDoor);
            tmRestAPIClient.processMaintainEntityReferenceNumberForDockDoor(processMaintainEntityReferenceNumber);
            log.debug("Successfully updated dock door for loadId {},referenceNumber {},dockDoor {}", loadId,referenceNumber,dockDoor);
        } catch (TmsLoadProcessException e) {
            log.error("Error while updating dock door for load {}: {}", loadId, e.getMessage());
            return FAILURE_PROCESS_STATUS + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error while updating dock door for load {}: {}", loadId, e.getMessage());
            return FAILURE_PROCESS_STATUS + e.getMessage();
        }
        return SUCCESS_PROCESS_STATUS;
    }

    @Override
    public String processDeleteDockDoor(List<String> systemReferenceNbrList, String loadId, String dockDoor) {
        log.info("Started method processDeleteDockDoor for loadId {},dockDoor {}", loadId,dockDoor);


        try {
            for (String oldSystemReferenceNbr : systemReferenceNbrList) {
                ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
                processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(
                        createReferenceNumberUpdateDataType(loadId, dockDoor, REF_NUM_ACTION_ENUM_VAL_DELETE, oldSystemReferenceNbr)
                );

                log.debug("Calling REST API to delete dock door for loadId {} and referenceNumber {}", loadId, oldSystemReferenceNbr);
                tmRestAPIClient.processMaintainEntityReferenceNumberForDockDoor(processMaintainEntityReferenceNumber);
            }

            log.debug("Successfully deleted dock doors for loadId {},dockDoor {}", loadId,dockDoor);

        } catch (TmsLoadProcessException e) {
            log.error("Error while deleting dock doors for load {}: {}", loadId, e.getMessage());
            return FAILURE_PROCESS_STATUS + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error while deleting dock doors for load {}: {}", loadId, e.getMessage());
            return FAILURE_PROCESS_STATUS + e.getMessage();
        }
        return SUCCESS_PROCESS_STATUS;
    }

    public RuleInputDTO fetchLoadDetails(String trlrNbr, List<TrailerYardAppointmentInfo> trailerYardAppointmentInfo, TrailerDoorDockCDCEvent event) {
        FindEntitiesResponse loadDetailsByDockDoor = null;
        String dockDoorNumber = null;
        List<LoadDTO> loadDTOByDockDoor = new ArrayList<>();
        String apptNumber=String.valueOf(trailerYardAppointmentInfo.getFirst().getApptNbr());
        LocationDTO locationDTO;
        if (trailerYardAppointmentInfo.getFirst().getYardAreaTypNbr() == 2) {
            try {
                locationDTO = locationServiceClient.findLocnEntityByLocnAbbr(String.valueOf(trailerYardAppointmentInfo.getFirst().getLocNbr()));

            } catch (Exception ex) {
                log.info("Exception occured while calling to location service {}",ex.getMessage());
                publishAuditAndBuildErrorResponse(DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_ERROR_MESSAGE, DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_EXCEPTION_CODE, DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_ACTION, event, trailerYardAppointmentInfo, ex.getMessage());
                return RuleInputDTO.builder()
                        .errorType(DOCK_DOOR_LOCATION_SERVICE_ERROR)
                        .errorCode(DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_EXCEPTION_CODE)
                        .errorMessage(("No location found for ").concat(ex.getMessage()))
                        .build();

            }
            log.info("TrlrNbr [{}] ,Location DTO [{}]", locationDTO);
            if (locationDTO != null) {
                dockDoorNumber = locationDTO.getAbbreviation().concat(trailerYardAppointmentInfo.getFirst().getYardAreaId());
                loadDetailsByDockDoor = getLoadsByDockDoor(DOCK_DOOR_SEARCH_CONFIG, DOCK_DOOR_ID, Arrays.asList(dockDoorNumber),event);
                loadDTOByDockDoor = loadMapper.mapLoadResponse(loadDetailsByDockDoor, apptNumber);
            }else{
                publishAuditAndBuildErrorResponse(DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_ERROR_MESSAGE, DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_EXCEPTION_CODE, DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_ACTION, event, trailerYardAppointmentInfo, DOCK_DOOR_LOCATION_SERVICE_NO_LOCATION_FOUND.concat(String.valueOf(trailerYardAppointmentInfo.getFirst().getLocNbr())));
                return RuleInputDTO.builder()
                        .errorType(DOCK_DOOR_LOCATION_SERVICE_ERROR)
                        .errorCode(DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_EXCEPTION_CODE)
                        .errorMessage(("No location found for ").concat(String.valueOf(trailerYardAppointmentInfo.getFirst().getLocNbr())))
                        .build();
            }
        }

        List<String> apptNumberList = trailerYardAppointmentInfo.stream()
                .map(TrailerYardAppointmentInfo::getApptNbr)
                .map(String::valueOf)
                .toList();
        FindEntitiesResponse findEntitiesResponse = getLoadDetailsByAppointmentNumber(apptNumberList,event);
        List<String> loadIds = findEntitiesResponse.getEntity().stream().map(UIEntityType::getStop).flatMap(stopTypes -> stopTypes.stream()).map(StopType::getSystemLoadID).toList();
        FindEntitiesResponse loadDetailsByLoadIds = getLoadsByDockDoor(DOCK_DOOR_SEARCH_CONFIG, SYSTEM_LOAD_ID, loadIds, event);
        List<LoadDTO> loadDTOByLoadIds = loadMapper.mapLoadResponse(loadDetailsByLoadIds,apptNumber);
        String dockDoor = dockDoorNumber == null ? null : dockDoorNumber.strip();
        return RuleInputDTO.builder()
                .appointmentNumber(apptNumber)
                .trailerNumber(String.valueOf(trlrNbr))
                .dockDoor(dockDoor)
                .yardType(trailerYardAppointmentInfo.getFirst().getYardAreaTypDesc().equalsIgnoreCase(DOOR_REFRENCE) ? DOOR_REFRENCE : YARD_REFRENCE)
                .trailerLoads(loadDTOByLoadIds)
                .doorLoads(loadDTOByDockDoor)
                .build();
    }

    private FindEntitiesResponse getLoadsByDockDoor(String dockDoorConfig,String key,List<String> dockDoorNumbers, TrailerDoorDockCDCEvent event) {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        try{
            findEntitiesResponse = loadEnrichmentService.fetchLoadDetailsForDockDoor(dockDoorConfig, key, dockDoorNumbers, IN);

        } catch (Exception ex) {
            publishAuditAndBuildErrorResponse(DOCK_DOOR_TMS_SERVICE_BY_DOCK_DOOR_ERROR_MESSAGE, DOCK_DOOR_TMS_SERVICE_BY_DOCK_DOOR_EXCEPTION_CODE, DOCK_DOOR_TMS_SERVICE_BY_DOCK_DOOR_ACTION_BY_DOCK_DOOR, event, dockDoorNumbers, ex.getMessage());
        }
            return findEntitiesResponse;
        }


    private FindEntitiesResponse getLoadDetailsByAppointmentNumber(List<String> apptNumberList, TrailerDoorDockCDCEvent event) {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        try{
            findEntitiesResponse= loadEnrichmentService.fetchLoadByAppointment(apptNumberList, IN);
        } catch (Exception ex) {
            publishAuditAndBuildErrorResponse(DOCK_DOOR_TMS_SERVICE_BY_APPOINTMENT_ERROR_MESSAGE, DOCK_DOOR_TMS_SERVICE_BY_APPOINTMENT_EXCEPTION_CODE, DOCK_DOOR_TMS_SERVICE_BY_DOCK_DOOR_ACTION_BY_APPOINTMENT, event, apptNumberList, ex.getMessage());
        }
        return findEntitiesResponse;
    }


    private ReferenceNumberUpdateDataType createReferenceNumberUpdateDataType(String loadId, String dockDoor,
                                                                              String action, String referenceNumber) {
        ReferenceNumberUpdateDataType referenceNumberUpdateDataType = new ReferenceNumberUpdateDataType();
        referenceNumberUpdateDataType.setReferenceNumberActionEnumVal(action);
        referenceNumberUpdateDataType.setReferenceNumberCategoryEnumVal(DEFAULT_REF_NUM_CATEGORY_ENUM_VAL);
        referenceNumberUpdateDataType.setReferenceNumber(dockDoor);
        referenceNumberUpdateDataType.setReferenceNumberEntityKey(loadId);
        referenceNumberUpdateDataType.setReferenceNumberTypeCode(DOOR_DOCK_NBR);

        if (referenceNumber != null) {
            referenceNumberUpdateDataType.setSystemReferenceNumberID(referenceNumber);
        }

        return referenceNumberUpdateDataType;
    }

    @Override
    public RuleInputDTO performDockDoorProcess(TrlrData currentTrlr, TrailerDoorDockCDCEvent event) {
        String trlrDetailsUrl = UriComponentsBuilder.fromHttpUrl(trailerYardAppointmentInfoUrl)
                .queryParam("trlrNbr", currentTrlr.getTrlrNbr())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(CORRELATION_KEY, MDC.get(CORRELATION_KEY));
        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        headers.add(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
        headers.add("Accept-Language", "en-US,en;q=0.9");
        headers.add("Connection", "keep-alive");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ApiResponse<List<TrailerYardAppointmentInfo>> trailerYardInfoApiResponse =
                    restApiClient.get(trlrDetailsUrl, entity, ApiResponse.class);
            log.info("Trlr Number [{}] Trailer Yard Info Response [{}]", currentTrlr, trailerYardInfoApiResponse);

            if (trailerYardInfoApiResponse.getCode() == HttpStatus.OK.value()) {
                return fetchLoadDetails(currentTrlr.getCarrTrlrNbr(), trailerYardInfoApiResponse.getData(), event);
            } else {
                publishAuditAndBuildErrorResponse(DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_ERROR_MESSAGE, DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_EXCEPTION_CODE, DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_ACTION, event, trlrDetailsUrl, trailerYardInfoApiResponse.getData().toString());
                return RuleInputDTO.builder()
                        .errorType(DOCK_DOOR_TRAILER_SERVICE_ERROR)
                        .errorCode(DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_EXCEPTION_CODE)
                        .errorMessage("Trailer service failed with error: " + trailerYardInfoApiResponse.getData() + " for the url " + trlrDetailsUrl)
                        .build();
            }
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException ex) {
            log.error("Error during retryable API call for trailer [{}]: {}", currentTrlr.getTrlrNbr(), ex.getMessage());
            publishAuditAndBuildErrorResponse(DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_ERROR_MESSAGE, DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_EXCEPTION_CODE, DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_ACTION, event, trlrDetailsUrl, ex.getMessage());
            return RuleInputDTO.builder()
                    .errorType(DOCK_DOOR_TRAILER_SERVICE_ERROR)
                    .errorCode(DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_EXCEPTION_CODE)
                    .errorMessage("Trailer service failed with error: " + ex.getMessage())
                    .build();
        } catch (Exception ex) {
            log.error("Unexpected error during API call for trailer [{}]: {}", currentTrlr.getTrlrNbr(), ex.getMessage());
            throw new TmsLoadProcessException("Unexpected error during API call for trailer " + currentTrlr.getTrlrNbr(), ex);
        }
    }

    private void publishAuditAndBuildErrorResponse(String errorMessage, String errorCode, String action, TrailerDoorDockCDCEvent event, Object payload, String exceptionMessage) {
        AuditTrailerFeedback auditFeedback = new AuditTrailerFeedback();
        List<Object> auditPayload = new ArrayList<>();
        auditFeedback.setErrorMessage(errorMessage.concat(exceptionMessage));
        auditFeedback.setErrorCode(errorCode);
        auditPayload.add(Map.of(CORRELATION_KEY, MDC.get(CORRELATION_KEY)));
        auditPayload.add(Map.of(DOCK_DOOR_REQUEST_PAYLOAD, event));
        auditPayload.add(Map.of("payload", payload));
        auditFeedback.setInputArguments(new Object[]{auditPayload});
        commonUtil.publishDockDoorAuditFeedbackToPubSub(auditFeedback, MDC.get(CORRELATION_KEY), action, SHIPMENT_LOAD_FAILURE_STATUS);
    }
}
