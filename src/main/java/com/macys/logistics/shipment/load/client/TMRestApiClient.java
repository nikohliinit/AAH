package com.macys.logistics.shipment.load.client;

import static com.macys.logistics.shipment.load.util.Constants.LOAD_FIND_ENTITY_RESPONSE;
import static com.macys.logistics.shipment.load.util.Constants.TM_API_CALL;

import com.macys.logistics.shipment.load.dto.AppointmentSealConditionDetail;

import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.inbound.RestProcessShipmentOrderDelete;
import com.macys.logistics.shipment.load.exception.TmRestApiException;
import com.macys.logistics.shipment.load.exception.TrailerDoorDockException;
import com.macys.logistics.shipment.load.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.request.RestAddPreBuiltLoad;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.logging.TmsLoadMetricsHelper;
import com.macys.logistics.shipment.load.util.Domain;
import lombok.extern.log4j.Log4j2;
import java.util.Arrays;
import java.util.List;



@Component
@Log4j2
public class TMRestApiClient {

    @Autowired
    TmsLoadMetricsHelper metricsHelper;

    @Autowired
    private TmRestApiService tmRestApiService;

    @Autowired
    ObjectMapper objectMapper;


    @Value("${tm.rest.api.path.process.load.retrieve}")
    private String processLoadRetrievePath;

    @Value("${tm.rest.api.path.process.load.referenceNumber}")
    private String processProcessMaintainEntityReferenceNumberPath;


    @Value("${tm.rest.api.path.process.stops.search}")
    private String findEntitieStopDetailsPath;
    @Value("${tm.rest.api.path.process.load.search}")
    private String findEntitieLoadDetailsPath;

    @Value("${tm.rest.api.path.process.maintainAppointment}")
    private String processStopAppointmentPath;

    @Value("${tm.rest.api.path.process.rate}")
    private String processLoadRatePath;

    @Value("${tm.rest.api.path.process.trailer.update}")
    private String processLoadUpdatePath;


    @Value("${tm.rest.api.path.process.load.cancel}")
    private String processLoadCancelPath;

    @Value("${tm.rest.api.path.process.load.tender.cancel}")
    private String processTenderCancelPath;
    
    @Value("${tm.rest.api.path.process.return.to.open}")
    private String processReturnToOpenPath;

    @Value("${tm.rest.api.path.process.load.tender}")
    private String processTenderPath;

    @Value("${tm.rest.api.path.process.stops.trailer.activities}")
    private String processTrailerActivitiesPath;

    @Value("${tm.rest.api.path.process.shipment.order.create}")
    private String processShipmentOrderCreatePath;

    @Value("${tm.rest.api.path.process.shipment.order.update}")
    private String processShipmentOrderUpdatePath;

    @Value("${tm.rest.api.path.process.shipment.order.delete}")
    private String processShipmentOrderDeletePath;

    @Value("${tm.rest.api.path.process.shipment.remove.from.load}")
    private String processShipmentRemovePath;
    @Value("${tm.rest.api.path.process.shipment.assign.to.load}")
    private String processAssignToLoadPath;

    @Value("${tms.shipment.rest.api.sealcondition.info.url}")
    private String sealConditionAppointmentPath;


    @Autowired
    private RestTemplate restTemplate;


    @Value("${tm.rest.api.path.create.pre.built.load}")
    private String addPreBuiltLoadPath;

    @Value("${tm.rest.api.path.process.tender.accept}")
    private String processTenderAcceptPath;

    @Value("${tm.rest.api.path.process.tender.reject}")
    private String processTenderRejectPath;

    @Value("${tm.rest.api.path.fetch.attachment.data}")
    private String fetchAttachementDataPath;

    @Value("${tm.rest.api.path.process.search.shipment}")
    private String processShipment;


    public ProcessLoadRetrieveResponse processLoadRetrieve(ProcessLoadRetrieve retrieveInput) {

        metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PROCESS_LOAD_RETRIEVE.name(),
                "ProcessLoadRetrieveResponse");
        log.info("processLoadRetrieve| calling tms with loadId {}", retrieveInput.getSystemLoadID());
        return tmRestApiService.sendRequest(processLoadRetrievePath, retrieveInput, ProcessLoadRetrieveResponse.class);
    }


    public void processMaintainEntityReferenceNumber(ProcessMaintainEntityReferenceNumber retrieveInput) {
        try {
            log.debug("processMaintainEntityReferenceNumber|  calling rest api to update the reference number to load {}", retrieveInput.getReferenceNumberUpdateData().size());
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_REFERENCE_NUMBER_UPDATE.name(),
                    "ProcessMaintainEntityReferenceNumberResponse");
            tmRestApiService.sendRequest(processProcessMaintainEntityReferenceNumberPath, retrieveInput, ProcessMaintainEntityReferenceNumberResponse.class);
        } catch (Exception e) {
            log.error("ProcessMaintainEntityReferenceNumber error calling rest api to update the reference number to load {}", e.getMessage());
        }
    }

    public FindEntitiesResponse findLoadID(RestFindEntity restFindEntity) {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        try {
            log.debug("findEntities| calling rest api to retrieve the loadID {}", restFindEntity);
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_FIND_ENTITIES_REST_API.name(), LOAD_FIND_ENTITY_RESPONSE);
            findEntitiesResponse = tmRestApiService.sendRequestToGetLoadID(findEntitieStopDetailsPath, restFindEntity);
            return findEntitiesResponse;
        } catch (Exception e) {
            log.error("error calling rest api to retrieve the loadID {}", e.getMessage());
        }
        return findEntitiesResponse;
    }


    public FindEntitiesResponse restFindEntities(RestFindEntity findEntities) {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        try {
            log.debug("findEntities| calling rest api to retrieve the load details {}", findEntities);
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_FIND_ENTITIES_REST_API.name(),
                    LOAD_FIND_ENTITY_RESPONSE);
            findEntitiesResponse = tmRestApiService.sendRequestToGetLoadDetails(findEntitieLoadDetailsPath, findEntities);
            return findEntitiesResponse;
        } catch (Exception e) {
            log.error("error calling rest api to retrieve the load details {}", e.getMessage());
        }
        return findEntitiesResponse;
    }

    public void processTender(ProcessTender processTender) {
        try {
            log.debug("processTender| calling rest api to processTender the load");
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PROCESS_TENDER_LOAD.name(), "processTender");
            tmRestApiService.sentRequest(processTenderPath, processTender, ProcessTenderResponse.class);
        } catch (Exception e) {
            log.error("processTender error calling rest api to processTender the load {}", e.getMessage());
        }
    }

    public void processLoadCancel(ProcessLoadCancel processLoadCancel) {
        try {
            log.debug("processLoadCancel| calling rest api  to cancel the load");
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_CANCELLED_LOAD.name(),
                    "processLoadCancel");
            tmRestApiService.sentRequest(processLoadCancelPath, processLoadCancel, ProcessLoadCancelResponse.class);
        } catch (Exception e) {
            log.error("processLoadCancel error calling rest api  to cancel the load {}", e.getMessage());
        }
    }


    public void processTenderCancel(ProcessTenderCancel processTenderCancel) {
        try {
            log.debug("processTenderCancel| calling rest api to cancel the tenderload");
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_TENDER_CANCELLED_LOAD.name(),
                    "processTenderCancel");
            tmRestApiService.sentRequest(processTenderCancelPath, processTenderCancel, ProcessTenderCancelResponse.class);
        } catch (Exception e) {
            log.error("processTenderCancel error calling rest api to cancel the tenderload {}", e.getMessage());
        }
    }


    public void processTrailerNumberUpdate(ProcessLoadUpdate processLoadUpdate) {
        try {
            log.debug("processLoadUpdate| calling rest api to update the trailer number");
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PERFORM_LOAD_COMPLETE.name(),
                    "ProcessLoadUpdateResponse");
            tmRestApiService.sentRequest(processLoadUpdatePath, processLoadUpdate, ProcessLoadUpdateResponse.class);
        } catch (Exception e) {
            log.error("ProcessLoadUpdate error calling rest api to update the trailer number {}", e.getMessage());
        }
    }


    public void processLoadRate(ProcessLoadRate processLoadRate) {
        try {
            log.debug("processLoadRate| calling rest api  to rate the load");
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PROCESS_LOAD_RATE.name(),
                    "ProcessLoadRateResponse");
            tmRestApiService.sentRequest(processLoadRatePath, processLoadRate, ProcessLoadRateResponse.class);
        } catch (Exception e) {
            log.error("ProcessLoadRate error calling rest api  to rate the load {}", e.getMessage());
        }
    }


    public ProcessStopAppointmentResponse processStopAppointment(ProcessStopAppointment processStopAppointment) {
        ProcessStopAppointmentResponse processStopAppointmentResponse = new ProcessStopAppointmentResponse();
        try {
            log.debug("ProcessStopAppointment| calling rest api  to updating the scheduled arravial date on the load");
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_APPOINTMENT_CREATION.name(), "ProcessStopAppointmentResponse");
            processStopAppointmentResponse = tmRestApiService.sendRequestForProcessStopAppointment(processStopAppointmentPath, processStopAppointment);
            return processStopAppointmentResponse;
        } catch (Exception e) {
            log.error("ProcessStopAppointment error calling rest api to updating the scheduled arravial date on the load {}", e.getMessage());
        }
        return processStopAppointmentResponse;
    }

    public ProcessTrailerActivitiesResponse restProcessTrailerActivities(RestProcessTrailerActivities trailerActivities) {
        try {
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PROCESS_TRAILER_ACTIVITIES.name(), "ProcessTrailerActivitiesResponse");
            log.debug("processTrailerActivities | calling TMS REST API");
            String responseBody = tmRestApiService.sentRestRequest(processTrailerActivitiesPath, trailerActivities);
            return objectMapper.readValue(responseBody, ProcessTrailerActivitiesResponse.class);
        } catch (Exception e) {
            log.error("exception calling rest tms processTrailerActivities api {}", e.getMessage());
            throw new TmsLoadProcessException(e);
        }
    }

    public ProcessShipmentOrderCreateResponse restProcessCreateShipment(RestProcessShipmentOrderCreate shipmentOrderCreate) {
        try {
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_SHIPMENT_CREATE.name(),
                    "ProcessShipmentOrderCreateResponse");
            log.debug("processCreateShipment| calling TMS REST API to create shipment");
            String responseBody = tmRestApiService.sentRestRequest(processShipmentOrderCreatePath, shipmentOrderCreate);
            log.debug("processCreateShipment| Response from TMS REST API for shipment number: {} {}",shipmentOrderCreate.getShipment().getFirst().getShipmentNumber(),responseBody);
            return objectMapper.readValue(responseBody, ProcessShipmentOrderCreateResponse.class);
        } catch (Exception e) {
            log.error("exception occurred while calling TMS REST API to create shipment {}", e.getMessage());
            throw new TmsLoadProcessException(e.getMessage(),e);
        }
    }

    public ProcessShipmentOrderDeleteResponse restProcessDeleteShipment(
        RestProcessShipmentOrderDelete shipmentOrderDelete) {
        try {
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_SHIPMENT_DELETE.name(),
                "ProcessShipmentOrderDeleteResponse");
            log.debug("processDeleteShipment| calling TMS REST API to delete shipment");
            String responseBody = tmRestApiService.sentRestRequest(processShipmentOrderDeletePath, shipmentOrderDelete);
            log.debug("processDeleteShipment| Response from TMS REST API  for shipment number: {} {}",shipmentOrderDelete.getShipment().getFirst().getShipmentNumber(),responseBody);
            return objectMapper.readValue(responseBody, ProcessShipmentOrderDeleteResponse.class);
        } catch (Exception e) {
            log.error("exception occurred while calling TMS REST API to delete shipment {}", e.getMessage());
            throw new TmsLoadProcessException(e.getMessage(),e);
        }
    }

    public ProcessShipmentRemoveResponse processShipmentRemove(ProcessShipmentRemove processShipmentRemove) {
        try {
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_SHIPMENT_REMOVE.name(), "ProcessShipmentRemoveResponse");
            log.debug("processShipmentRemove| calling rest api tms to remove shipment");
            String responseBody = tmRestApiService.sentRestRequest(processShipmentRemovePath, processShipmentRemove);
            return objectMapper.readValue(responseBody, ProcessShipmentRemoveResponse.class);
        } catch (Exception e) {
            log.error("Error while calling rest api tms to remove shipment {}", e.getMessage());
            throw new TmsLoadProcessException(e.getMessage());
        }
    }

    public ProcessAssignToLoadResponse processAssignShipmentsToLoad(ProcessAssignToLoad processAssignToLoad) {
        try {
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PERFORM_LOAD_COMPLETE.name(), "ProcessAssignToLoadResponse");
            log.debug("processAssignToLoadResponse| calling rest api tms to assign shipment to load");
            String responseBody = tmRestApiService.sentRestRequest(processAssignToLoadPath, processAssignToLoad);
            return objectMapper.readValue(responseBody, ProcessAssignToLoadResponse.class);
        } catch (Exception e) {
            log.error("Error occurred while calling rest api tms service to assign shipment to load {}", e.getMessage());
            throw new TmsLoadProcessException(e);
        }
    }


	@Retryable(retryFor = TmsLoadProcessException.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
	public AddPreBuiltLoadResponse addPreBuiltLoad(RestAddPreBuiltLoad addPreBuiltLoad) {
		try {
			metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_APPOINTMENT_CREATION.name(), "AddPreBuiltLoadResponse");
			log.debug("addPreBuiltLoad| calling through rest api tms to add load");
			return tmRestApiService.sendRequest(addPreBuiltLoadPath, addPreBuiltLoad, AddPreBuiltLoadResponse.class);
		} catch (Exception e) {
			log.error("addPreBuiltLoad| Error occurred while calling rest api with to add load:  {}", e.getMessage());
			throw new TmsLoadProcessException(e);
		}
	}

	@Retryable(retryFor = TmsLoadProcessException.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
	public ProcessTenderAcceptResponse processTenderAccept(ProcessTenderAccept processTenderAccept) {
		try {
			metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PROCESS_TENDER_ACCEPT.name(), "ProcessTenderAcceptResponse");
			log.debug("restProcessTenderAccept| calling rest api tms to processTenderAccept {}", processTenderAccept.getLoadTenderData());
			return tmRestApiService.sendRequest(processTenderAcceptPath, processTenderAccept, ProcessTenderAcceptResponse.class);
		} catch (Exception e) {
			log.error("Error occurred while rest api call  processTenderAccept service {}", e.getMessage());
			throw new TmsLoadProcessException(e);
		}
	}

    @Retryable(retryFor = TmsLoadProcessException.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public ProcessTenderRejectResponse processTenderReject(ProcessTenderReject processTenderReject) {
        try {
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PROCESS_TENDER_REJECT.name(), "ProcessTenderRejectResponse");
            log.debug("restProcessTenderReject| calling rest api tms to processTenderReject {}", processTenderReject.getLoadTenderData());
            return tmRestApiService.sendRequestUntyped(processTenderRejectPath, processTenderReject, ProcessTenderRejectResponse.class);
        } catch (Exception e) {
            log.error("Error occurred while rest api call processTenderReject service {}", e.getMessage());
            throw new TmsLoadProcessException(e);
        }
    }

	@Retryable(retryFor = TmsLoadProcessException.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
	public ProcessTrailerActivitiesResponse processTrailerActivities(RestProcessTrailerActivities trailerActivities) {
		try {
			metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PROCESS_TRAILER_ACTIVITIES.name(), "ProcessTrailerActivitiesResponse");
			log.debug("processTrailerActivities | calling TMS REST API");
			return tmRestApiService.sendRequest(processTrailerActivitiesPath, trailerActivities, ProcessTrailerActivitiesResponse.class);
		} catch (Exception e) {
			log.error("processTrailerActivities| failed to update loadId and shipConfirmDate - on load - {}", e.getMessage());
			throw new TmsLoadProcessException(e);
		}
	}
	
	@Retryable(retryFor = TmsLoadProcessException.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public RestProcessMaintainEntityAttachmentResponse getAttachmentsInformationFromRest(RestProcessMaintainEntityAttachment attachmentRequest) {
        try {
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PROCESS_LOAD_RETRIEVE.name(), "RestProcessMaintainEntityAttachment");
            log.debug("fetchAttachementDataPath | calling TMS REST API");
            return tmRestApiService.sentRequest(fetchAttachementDataPath, attachmentRequest, RestProcessMaintainEntityAttachmentResponse.class);
        } catch (Exception e) {
            log.warn("fetchAttachementDataPath| failed to fetch images data - on load -  {}", e.getMessage());
            if (e.getCause() instanceof HttpClientErrorException && ((HttpClientErrorException) e.getCause()).getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
               throw e;
            }
            throw new TmsLoadProcessException(e);
        }
    }
    @Retryable(retryFor = TmsLoadProcessException.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public ShipmentResponse searchShipment(RestFindEntity findEntities) {
        ShipmentResponse shipmentResponse = new ShipmentResponse();
        try {
            log.info("Calling searchShipment.");
            log.debug("searchShipment payload: {}", findEntities);
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_SHIPMENT_SEARCH.name(),
                    "ShipmentResponse");
            shipmentResponse = tmRestApiService.sentRequest(processShipment, findEntities,ShipmentResponse.class);
            return shipmentResponse;
        } catch (Exception e) {
            log.error(Constants.SEARCH_SHIPMENT_API_ERROR +"{}", e.getMessage());
        }
        return shipmentResponse;
    }
    
    public void processLoadReturnToOpen(ProcessLoadReturnToOpen processLoadReturnToOpen) {
      try {
          log.info("ProcessLoadReturnToOpen| calling rest api to return to open load");
          log.debug("processLoadReturnToOpen payload: {}", processLoadReturnToOpen);
          metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_RETURN_TO_OPEN.name(),
                  "processLoadReturnToOpen");
          tmRestApiService.sentRequest(processReturnToOpenPath, processLoadReturnToOpen, ProcessLoadReturnToOpenResponse.class);
      } catch (Exception e) {
          log.error("processLoadReturnToOpen error calling rest api to return to open load {}", e.getMessage());
      }
  }

    public ProcessLoadUpdateResponse processRestTrailerNumberUpdate(RestProcessLoadUpdate processLoadUpdate) {
        try {
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_PERFORM_LOAD_COMPLETE.name(), "ProcessLoadUpdateResponse");
            log.debug("processLoadUpdate| calling rest api to update load tracking number");
            String responseBody = tmRestApiService.sentRestRequest(processLoadUpdatePath, processLoadUpdate);
            return objectMapper.readValue(responseBody, ProcessLoadUpdateResponse.class);
        } catch (Exception e) {
            log.error("Error occurred while calling rest api to update load tracking number {}", e.getMessage());
            throw new TmsLoadProcessException(e.getMessage());
        }
    }

    @Retryable(retryFor = TmsLoadProcessException.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public ProcessShipmentOrderCreateResponse processUpdateShipment(RestProcessShipmentOrderCreate shipmentOrderCreate) {
        try {
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_SHIPMENT_UPDATE.name(),
                    "ProcessShipmentOrderUpdateResponse");
            log.debug("processUpdateShipment| calling rest api of tms to update shipment");
            return tmRestApiService.sendRequest(processShipmentOrderUpdatePath, shipmentOrderCreate, ProcessShipmentOrderCreateResponse.class);
        } catch (Exception e) {
            log.error("Error occurred while rest api call tms service to update shipment {}", e.getMessage(),e);
            throw new TmsLoadProcessException(e.getMessage());
        }
    }

    @Retryable(retryFor = {HttpServerErrorException.class, ResourceAccessException.class}, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public List<AppointmentSealConditionDetail> callAppointmentSealCondition(Integer appointmentNbr )
    {
        log.debug("callAppointmentSealCondition| calling rest api  to fetch seal condition");
        String url =sealConditionAppointmentPath.concat(String.valueOf(appointmentNbr));
        List<AppointmentSealConditionDetail> sealConditionResponse = List.of();
        try
        {
            sealConditionResponse = Arrays.stream(restTemplate.getForObject(url, AppointmentSealConditionDetail[].class,appointmentNbr)).toList();
        }
        catch (HttpServerErrorException hse) {
            log.error("Http server error occurred while calling appointment service api to get seal condition info: {}", hse.getMessage());

        }
        catch (ResourceAccessException rae) {
            log.error("Resource access error occurred while calling appointment service api to get seal condition info: {}", rae.getMessage());

        }
        catch (HttpClientErrorException hce) {
            log.error("HTTP client error while calling appointment service to get seal condition info with appointmentNbr {}: {}",
                    appointmentNbr, hce.getMessage());

        }
        catch (Exception e) {
            log.error("error calling rest api to retrieve the seal condition {}", e.getMessage());

        }
        return sealConditionResponse;
    }

    public void processMaintainEntityReferenceNumberForDockDoor(ProcessMaintainEntityReferenceNumber retrieveInput) {

            log.debug("processMaintainEntityReferenceNumber|  calling rest api to update the reference number to load {}", retrieveInput.getReferenceNumberUpdateData().size());
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_REFERENCE_NUMBER_UPDATE.name(),
                    "ProcessMaintainEntityReferenceNumberResponse");
            tmRestApiService.sendRequest(processProcessMaintainEntityReferenceNumberPath, retrieveInput, ProcessMaintainEntityReferenceNumberResponse.class);

    }

    public FindEntitiesResponse findLoadByAppointment(RestFindEntity restFindEntity) throws TrailerDoorDockException {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        try {
            log.debug("findEntities| calling rest api to retrieve the loadID {}", restFindEntity);
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_FIND_ENTITIES_REST_API.name(), LOAD_FIND_ENTITY_RESPONSE);
            findEntitiesResponse = tmRestApiService.sendRequestToGetLoadID(findEntitieStopDetailsPath, restFindEntity);
            return findEntitiesResponse;
        } catch (Exception e) {
            log.error("error calling rest api to retrieve the loadID {}", e.getMessage());
            throw new TmsLoadProcessException(e.getMessage());
        }

    }

    public FindEntitiesResponse restFindEntitiesByDockDoor(RestFindEntity findEntities) {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        try {
            log.debug("findEntities| calling rest api to retrieve the load details {}", findEntities);
            metricsHelper.populateTransactionDomainAndType(TM_API_CALL + Domain.TMS_FIND_ENTITIES_REST_API.name(),
                    LOAD_FIND_ENTITY_RESPONSE);
            findEntitiesResponse = tmRestApiService.sendRequestToGetLoadDetails(findEntitieLoadDetailsPath, findEntities);
            return findEntitiesResponse;
        } catch (Exception e) {
            log.error("error calling rest api to retrieve the load details {}", e.getMessage());
            throw new TmsLoadProcessException(e.getMessage());
        }

    }
}
