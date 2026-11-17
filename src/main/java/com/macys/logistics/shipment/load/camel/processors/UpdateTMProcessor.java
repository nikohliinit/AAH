package com.macys.logistics.shipment.load.camel.processors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.AppointmentSealConditionDetail;
import com.macys.logistics.shipment.load.dto.SealCondition;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.exception.TmsLoadValidationException;
import com.macys.logistics.shipment.load.logging.ShipmentLoadIntegrationMetricsHelper;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.service.LoadEnrichmentService;
import com.macys.logistics.shipment.load.service.LocationServiceClient;
import com.macys.logistics.shipment.load.util.Constants;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessTenderCancel;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.Objects;

@Component
@Log4j2
public class UpdateTMProcessor implements Processor {
	@Value("${seal.restriction.enabled}")
	private String sealRestrictionEnabled;
	@Value("${restricted.destinations}")
	private String destinations;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	LoadMapper loadMapper;

	@Autowired
	private LoadEnrichmentService loadEnrichmentService;
	@Autowired
	private LocationServiceClient locationServiceClient;
	@Autowired
	ShipmentLoadIntegrationMetricsHelper metricsHelper;


	@Value("#{${update.sealcondition.status.codes}}")
	private Map<String, String> updateSealConditionStatusCodes;
	@Value("${status.names}")
	private String tenderedListOfNames;
	@Value("${cancel.load.status}")
    private String allowedCancelLoadStatus;

	@Value("${feature.doublepup.enabled}")
    String doublePupEnabled;
	@Autowired
	private  Tracer tracer;
    @Autowired
	TMRestApiClient tmRestApiClient;

	@Value("${sealNumber.restrictionValues}")
	private String restrictionValues;

	/**
	  *
	  * @param exchange - Contains APPOINTMENT PAYLOAD event
	  * @throws Exception
	  */
	  @Override
	  public void process(Exchange exchange) throws Exception {
		  Map<String, Object> headers=exchange.getIn().getHeaders();
		  log.debug("Update TM Processor headers from kafka {} ", headers);
		  String correlationId=exchange.getIn().getHeader("correlationId",String.class);
		  Span camelContextSpan = MicrometerUtility.initializeSpan(exchange,tracer);
		  try(Tracer.SpanInScope ws = tracer.withSpan(camelContextSpan)){
			  MDC.put("traceId", correlationId);

		  String appointmentPayloadObject = exchange.getIn().getBody(String.class);


		  if (Optional.ofNullable(appointmentPayloadObject).isEmpty()) {
	        log.error("Received null in request body");
	        throw new TmsLoadValidationException("Received empty appointmentPayload");
	      }

		  objectMapper =
	              Optional.ofNullable(objectMapper).isPresent() ? objectMapper : new ObjectMapper();
	      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      AppointmentPayload apptPayload = objectMapper.readValue(
	    		  appointmentPayloadObject, AppointmentPayload.class);

		  log.info("Processing appointment update for number: {}", apptPayload.getAppointmentNumber());
	      log.debug("Received appointmentPayload request event: {}", apptPayload);

	      String loadId = null;
	      ShipmentResponse shipmentResponse = new ShipmentResponse();
	      boolean isOldAppointment = false;

	      FindEntitiesResponse findEntitiesResponse = loadEnrichmentService.fetchLoadId(Arrays.asList(apptPayload.getAppointmentNumber()),Constants.EQ);

	      if (hasNoStops(findEntitiesResponse)) {
	          shipmentResponse = loadEnrichmentService.searchShipmentWithOldAppointmentRef(apptPayload.getAppointmentNumber());

	          if (validateShipmentResponseForOldAppointment(shipmentResponse)) {
	              findEntitiesResponse.setEntity(createEntityList(shipmentResponse));
	              isOldAppointment = true;
	          }
	      }
	      if (!validateFindEntitiesResponse(findEntitiesResponse, apptPayload)) {
			  return;
		  }

	      loadId = findEntitiesResponse.getEntity().get(0).getStop().get(0).getSystemLoadID();
		  //Find load details from TM
	      FindEntitiesResponse loadDetails = loadEnrichmentService.fetchLoadDetails(loadId);
		  LoadType load = loadMapper.getLoad(loadDetails);
		  validateLoadNotNull(load,loadId);

		  if (Boolean.TRUE.equals(isOldAppointment) && validateShipmentResponseForOldAppointment(shipmentResponse)) {
		    shipmentResponse.getData().stream()
		        .findFirst()
		        .map(ShipmentData:: getShipmentNumber)
		        .flatMap(shipmentNumber -> load.getShipmentLeg().stream()
		            .filter(shipment -> shipment.getShipmentNumber().equals(shipmentNumber))
		            .map(ShipmentLegType::getSystemDropStopID)
		            .findFirst()
		        )
		        .ifPresent(apptPayload::setAppointmentNumber);
		  }

		  // For OBTM Appointments on LOAD COMPLETE, update carton count and load tracking number
		  updateCartonCountforOBTMAppts(loadId, load, apptPayload);

		  updateFlexibleQ1andFlexibleQ2andReRate(loadId,load,apptPayload);

		  //cancel appointment in flo should update the  load -For Double pup
		  updateLoadWhenAppointmentCancel(apptPayload, load);
		  if (Constants.CANCELLED.equals(apptPayload.getAppointmentStatus()) && !(isDoublePUP(load))) {
			  cancelLoad(load);
		  }

	      if(apptPayload.getAppointmentScheduledArivalTs() != null && !Constants.CANCELLED.equals(apptPayload.getAppointmentStatus())) {
	    	  loadEnrichmentService.updateStopAppointment(apptPayload, load);
	      }
          // Trailer Number updates in TM when FLO is updated
          updateTrailerNumberWhenMismatch(apptPayload, load);
		  checkDisplayFlag(apptPayload,load);
		  processSealChanges(apptPayload, load, loadId);
          } catch (Exception e) {
	      log.error("An exception caught while processing appointmentPayload event: {} ", e.getMessage());
	      throw e;
	    } finally {
			  camelContextSpan.end();
		  }
	  }

	private void processSealChanges(AppointmentPayload apptPayload, LoadType load, String loadId) {
		if(isValidStatusSealUpdate(apptPayload.getAppointmentStatus())) {
			String sealNumber = StringUtils.EMPTY;
			String sealType = StringUtils.EMPTY;
			List<String> restrictedValues = Arrays.asList(restrictionValues.split(","));

			// Call to retrieve the seal condition details
			List<AppointmentSealConditionDetail> appointmentSealConditionDetailList = tmRestApiClient.callAppointmentSealCondition(Integer.valueOf(apptPayload.getAppointmentNumber()));

			// Check if the list is empty before attempting to access it
			if (!appointmentSealConditionDetailList.isEmpty()) {
				Optional<AppointmentSealConditionDetail> maxSeqNbr = appointmentSealConditionDetailList.stream()
						.max(Comparator.comparing(AppointmentSealConditionDetail::getSequenceNbr));
				sealType = maxSeqNbr.map(AppointmentSealConditionDetail::getAppointmentSealCnd).orElse(StringUtils.EMPTY);
				sealNumber = maxSeqNbr.map(AppointmentSealConditionDetail::getSealNbr).orElse(StringUtils.EMPTY);
			}
			if (validateSealCondition(apptPayload, load) && sealNumber != null && !restrictedValues.contains(sealNumber.toUpperCase())) {
				loadEnrichmentService.addOrUpdateSealCondition(apptPayload, loadId, load);
			}

			if (validateSealType(apptPayload, sealType) && sealNumber != null && !restrictedValues.contains(sealNumber.toUpperCase())) {
				// Update seal type at load level
				loadEnrichmentService.addOrUpdateSealType(apptPayload, loadId, load, sealType);
			}
		}else{
			log.warn("Appointment status is not Ready for Unload for appointment to update seal condition/type {}",
							apptPayload.getAppointmentNumber());

		}
	}

    private void validateLoadNotNull(LoadType load, String loadId){
		  if(load == null) {
			  log.error("Load details are not available for "+ loadId);
			  throw new TmsLoadValidationException("Load details are not available for "+ loadId);
		  }
	  }

	private void updateCartonCountforOBTMAppts(String loadId, LoadType load, AppointmentPayload apptPayload) {
		if(isLoadComplete(apptPayload.getAppointmentStatus(),apptPayload.getAppointmentType(),apptPayload.getAppointmentFreightType())){
			loadEnrichmentService.triggerLoadCompleteForObtmAppointments(loadId, load, apptPayload.getAppointmentNumber(), apptPayload.getAppointmentNumberOfCartons());
		}
	}

  private boolean hasNoStops(FindEntitiesResponse findEntitiesResponse) {
    return Optional.ofNullable(findEntitiesResponse)
        .map(FindEntitiesResponse::getEntity)
        .filter(entities -> !entities.isEmpty())
        .map(entities -> entities.get(0).getStop())
        .filter(stops -> !stops.isEmpty())
        .isEmpty();

  }

	private void updateTrailerNumberWhenMismatch(AppointmentPayload apptPayload, LoadType load) {
		log.debug("UpdateTMProcessor :: process :: Trailer Number received from Appointment Payload: {}", apptPayload.getAppointmentCarrierTrailerNumber());
		if(isDoublePUP(load) && apptPayload.getAppointmentCarrierTrailerNumber() !=null &&
				!(Constants.CANCELLED.equals(apptPayload.getAppointmentStatus()))) {
			handleDoublePUPTrailerNumberMismatch(apptPayload,load);
		}
		else if (validateTrailerNumberMismatch(apptPayload,load) &&
            !(Constants.CANCELLED.equals(apptPayload.getAppointmentStatus()))) {
			loadEnrichmentService.processLoadUpdate(load.getId(), apptPayload.getAppointmentCarrierTrailerNumber());
		}
	}

	private void handleDoublePUPTrailerNumberMismatch(AppointmentPayload apptPayload, LoadType load) {
		try {
			List<StopType> stopTypes = getSortedDropStops(load);
			List<Integer> valueMatchIndex = getValueMatchIndex(stopTypes, apptPayload, false);
			List<Integer> valueMatchIndexForCancel = getValueMatchIndex(stopTypes, apptPayload, true);

			String trailerNumbersData = load.getTrailerNumber();
			if (!StringUtils.isEmpty(trailerNumbersData)) {
				List<String> trailerNumbersInTms = getNonEmptyTrailerNumbers(trailerNumbersData);

				if (!valueMatchIndex.isEmpty()) {
					handleTrailerNumberMismatch(apptPayload, load,  trailerNumbersInTms, valueMatchIndex);
				} else if (!valueMatchIndexForCancel.isEmpty()) {
					handleTrailerNumberCancellation(apptPayload, load, trailerNumbersData, trailerNumbersInTms, valueMatchIndexForCancel);
				}
			}
		} catch (Exception e) {
			log.error("An exception caught while processing trailer update in doublepup scenario for appointment: {}, {}",
					apptPayload.getAppointmentNumber(), e.getMessage());
		}
	}

	private List<StopType> getSortedDropStops(LoadType load) {
		return load.getStop().stream()
				.filter(stop -> Constants.DROP.equals(stop.getStopTypeEnumDescr()))
				.sorted(Comparator.comparingInt(StopType::getStopSequenceNumber))
				.toList();
	}

	private List<Integer> getValueMatchIndex(List<StopType> stopTypes, AppointmentPayload apptPayload, boolean isCancelled) {
		return IntStream.range(0, stopTypes.size())
				.filter(i -> stopTypes.get(i).getSystemStopID().equalsIgnoreCase(apptPayload.getAppointmentNumber()) &&
						(isCancelled == Constants.CANCELLED.equalsIgnoreCase(apptPayload.getAppointmentStatus())))
				.boxed()
				.toList();
	}

	private List<String> getNonEmptyTrailerNumbers(String trailerNumbersData) {
		return Arrays.stream(trailerNumbersData.split(","))
				.filter(s -> s != null && !s.isEmpty())
				.toList();
	}

	private void handleTrailerNumberMismatch(AppointmentPayload apptPayload, LoadType load,
											 List<String> trailerNumbersInTms, List<Integer> valueMatchIndex) {
		String currentTrailerNumber = trailerNumbersInTms.get(valueMatchIndex.get(0));
		if (!currentTrailerNumber.equals(apptPayload.getAppointmentCarrierTrailerNumber())) {
		  List<String> mutableTrailerNumbersInTms = new ArrayList<>(trailerNumbersInTms);
		  mutableTrailerNumbersInTms.set(valueMatchIndex.get(0), apptPayload.getAppointmentCarrierTrailerNumber());
		  String newTrailerNumber = String.join(",", mutableTrailerNumbersInTms);
			if (!StringUtils.isEmpty(newTrailerNumber)) {
				loadEnrichmentService.processLoadUpdate(load.getId(), newTrailerNumber);
			}
		}
	}

	private void handleTrailerNumberCancellation(AppointmentPayload apptPayload, LoadType load, String trailerNumbersData,
												 List<String> trailerNumbersInTms, List<Integer> valueMatchIndexForCancel) {
		String trailerToCancel = trailerNumbersInTms.get(valueMatchIndexForCancel.get(0));
		if (trailerToCancel.equals(apptPayload.getAppointmentCarrierTrailerNumber())) {
			String newTrailerNumber = trailerNumbersData
					.replace(trailerToCancel, "")
					.replaceAll("(^,)|(,$)", "")
					.strip();

			Optional.ofNullable(newTrailerNumber)
					.filter(trailer -> !trailer.isBlank())
					.ifPresent(trailer -> loadEnrichmentService.processLoadUpdate(load.getId(), trailer));
		}
	}


	private boolean validateTrailerNumberMismatch(AppointmentPayload apptPayload, LoadType load) {

		return StringUtils.isNotEmpty(apptPayload.getAppointmentCarrierTrailerNumber()) && Optional.ofNullable(load)
				.map(loadInfo -> (StringUtils.isEmpty(loadInfo.getTrailerNumber()))
						|| !loadInfo.getTrailerNumber().equalsIgnoreCase(apptPayload.getAppointmentCarrierTrailerNumber()))
				.orElse(false);
	}

	private boolean isDoublePUP(LoadType load) {

	  log.debug("Doublepup-Feature status {}", doublePupEnabled);
	  if (doublePupEnabled != null && doublePupEnabled.equalsIgnoreCase("false")) {
        return false;
	  }
		return  Optional.ofNullable(load)
				.map(loadInfo -> !(StringUtils.isEmpty(loadInfo.getTrailerNumber()))
						&& (loadInfo.getTrailerNumber().split(",").length > 1))
				.orElse(false);
	}

	private boolean validateFindEntitiesResponse(FindEntitiesResponse findEntitiesResponse, AppointmentPayload apptPayload) {
	    String appointmentNumber = apptPayload.getAppointmentNumber();

	    boolean isInvalidResponse = Optional.ofNullable(findEntitiesResponse)
	        .map(FindEntitiesResponse::getEntity)
	        .filter(entities -> !entities.isEmpty())
	        .map(entities -> entities.get(0).getStop())
	        .filter(stops -> !stops.isEmpty())
	        .isEmpty();

	    if (isInvalidResponse) {
	        log.warn("No load Id found for appointment {}. Stopping processing for this event.", appointmentNumber);
			return false;
	    }
		return true;
	}

	private boolean validateSealCondition(AppointmentPayload apptPayload, LoadType load) {

		  if(apptPayload.getAppointmentSealConditionDescription()== null ||
                  (load.getReferenceNumber5() != null && apptPayload.getAppointmentSealConditionDescription().equalsIgnoreCase(load.getReferenceNumber5()) )) {
			log.debug("Not a valid seal or Seal condition is already updated on appointment {}", apptPayload.getAppointmentNumber());
			return false;
		}
		//Validation to check seal condition restriction for locations 7291, 7293
		//restrict seal condition update to TM until Maersk is ready to send Seal condition as part of EDI 214
		if(Boolean.parseBoolean(sealRestrictionEnabled)) {
			List<String> restrictedList = Arrays.asList(destinations.split(","));
			if(restrictedList.contains(apptPayload.getAppointmentDestLocNumber())) {
				log.warn("Seal condition update restricted for destination location {}", apptPayload.getAppointmentDestLocNumber());
				return false;
			}
		}

		if ((apptPayload.getAppointmentSealConditionDescription() == null) ||
				(!EnumUtils.isValidEnum(SealCondition.class, apptPayload.getAppointmentSealConditionDescription().replace(" ","_").toUpperCase()))) {
			log.warn("Valid seal condition not available for appointment {}", apptPayload.getAppointmentNumber());
			return false;
		}

		if(!apptPayload.getAppointmentType().equals(Constants.APPT_TYPE_CARR_TO_DC)) {

			log.warn("Appointment type is not Carrier to DC for Appointment {}",
					apptPayload.getAppointmentNumber());
			return false;
		}


		//If above checks pass, execute seal condition update.
		return true;
	}

    private boolean validateSealType(AppointmentPayload apptPayload, String sealType) {

		if(StringUtils.isEmpty(sealType)) {
			log.debug("Not a valid seal type found in flo {}", sealType);
			return false;
		}

        if(!apptPayload.getAppointmentType().equals(Constants.APPT_TYPE_CARR_TO_DC)) {

            log.warn("Appointment type is not Carrier to DC for Appointment {}",
                    apptPayload.getAppointmentNumber());
            return false;
        }

        if(!(isValidStatusSealUpdate(apptPayload.getAppointmentStatus()))){

            log.warn("Appointment status is not Ready for Unload for appointment {}",
                    apptPayload.getAppointmentNumber());
            return false;
        }

        return true;
    }


	public void cancelLoad(LoadType load) {
		log.info("cancelLoad | cancelling load loadID {}", load.getId());
		List<String> tenderedList = Arrays.asList(tenderedListOfNames.split(","));
		ProcessLoadCancel processLoadCancel = ProcessLoadCancel.builder().
				systemLoadID(Arrays.asList(load.getId())).build();

		String currentLoadOperationalStatusDescr = load.getCurrentLoadOperationalStatusEnumDescr();

		BigDecimal numberofCuttons = load.getTotalPieces();

		//  This code will do the cancel operation when the load status is Tendered or Tender Accepeted
		if (tenderedList.contains(currentLoadOperationalStatusDescr)) {
			ProcessTenderCancel processTenderCancel = prepareCancelLoadRequest(load);
			log.info("The status of load is  Tendered Or Tendered Accepect {} ", load.getId());
			loadEnrichmentService.processCancleTender(processTenderCancel);
			log.debug(" Number of Cottons  {}",numberofCuttons);
		}
		//  This code will do the cancel operation when the load status is other than Tendered or Tender Accepeted
		log.info("The status of load is  Planned , In Trasit or Completed {} ", load.getId());
		log.debug(" Before Number of Cottons  {}", numberofCuttons);
		loadEnrichmentService.processCancelLoad(processLoadCancel);
		log.debug(" After Number of Cottons   {}", numberofCuttons);
		removeShipmentReference(load);
		log.debug(" After removal of appointment numbers from shipments for the load   {}", load);
	}

	private void removeShipmentReference(LoadType load) {
	  load.getShipmentLeg().stream().forEach(shipmentLeg -> {

	    ShipmentResponse shipmentResponse = loadEnrichmentService.searchShipment(shipmentLeg.getShipmentNumber());
	      if (validateShipmentResponse(shipmentResponse)) {
	          loadEnrichmentService.deleteReferenceNumber(shipmentLeg.getSystemShipmentID(), shipmentResponse);
	      }

	  });

	}

	private ProcessTenderCancel prepareCancelLoadRequest(LoadType load) {

		LoadTenderDataType loadTenderDataType = LoadTenderDataType.builder()
				.systemLoadID(load.getId())
				.carrierCode(load.getCarrierCode())
				.currentLoadOperationalStatusEnumVal(load.getCurrentLoadOperationalStatusEnumVal()).build();
		return  ProcessTenderCancel.builder()
				.loadTenderData(Arrays.asList(loadTenderDataType))
				.build();

	}

	private void checkDisplayFlag(AppointmentPayload apptPayload,LoadType load) {
		if(apptPayload.getAppointmentType().startsWith(Constants.OUBOUND_APPOINTMENT) && StringUtils.isNotEmpty(apptPayload.getAppointmentDispatchFlag())) {
			loadEnrichmentService.dispatchFlagTrigger(apptPayload, load);
		}
	}

	private boolean isValidStatusSealUpdate(String appointmentStatus) {
		return updateSealConditionStatusCodes.containsValue(appointmentStatus);
	}

	private void updateLoadWhenAppointmentCancel(AppointmentPayload apptPayload, LoadType load) {
		if ((isDoublePUP(load) && apptPayload.getAppointmentCarrierTrailerNumber() != null) &&
				(!Constants.IN_TRANSIT.equals(load.getCurrentLoadOperationalStatusEnumDescr())) &&
				(!Constants.COMPLETED.equals(load.getCurrentLoadOperationalStatusEnumDescr()))) {
           if(apptPayload.getAppointmentStatus().equalsIgnoreCase(Constants.CANCELLED) &&
				   !load.getShipmentLeg().isEmpty() && load.getShipmentLeg().size() > 1){
			       load.getShipmentLeg().stream()
					   .filter(shipmentDrop -> Objects.equals(shipmentDrop.getSystemDropStopID(), apptPayload.getAppointmentNumber()))
					   .findFirst()
						   .ifPresent(shipmentLeg -> processShipment(shipmentLeg, apptPayload, load));
		   }
		}else if((isDoublePUP(load) && apptPayload.getAppointmentCarrierTrailerNumber() !=null) &&
				(load.getCurrentLoadOperationalStatusEnumDescr().equalsIgnoreCase(Constants.IN_TRANSIT) ||
		       (load.getCurrentLoadOperationalStatusEnumDescr().equalsIgnoreCase(Constants.COMPLETED)))){
			log.error(Constants.LOAD+load.getSystemLoadID()+Constants.LOAD_STATUS_TM+load.getCurrentLoadOperationalStatusEnumDescr());
		}
	}

	private void processShipment(ShipmentLegType shipmentLeg, AppointmentPayload apptPayload, LoadType load) {
		ShipmentResponse shipmentResponse = loadEnrichmentService.searchShipment(shipmentLeg.getShipmentNumber());
		if (validateShipmentResponse(shipmentResponse)) {
		    handleCancelAppointment(shipmentLeg.getSystemShipmentID(), apptPayload,load,shipmentResponse);

		}


	  }

	private boolean validateShipmentResponse(ShipmentResponse shipmentResponse) {
      return shipmentResponse != null && !shipmentResponse.getData().isEmpty() && shipmentResponse.getData().size() > 0;
}

	private boolean validateShipmentResponseForOldAppointment(ShipmentResponse shipmentResponse) {
	    if (shipmentResponse == null || ObjectUtils.isEmpty(shipmentResponse.getData())) {
	        return false;
	    }

	    String shipmentNumber = shipmentResponse.getData().get(0).getShipmentNumber();
	    return shipmentNumber != null && shipmentNumber.contains(Constants.OUTBOUND_LOAD_PREFIX);
	}

	private void deleteReferenceNumberFromShipment(String shipmentNumber, ShipmentResponse shipmentResponse) {
	    loadEnrichmentService.deleteReferenceNumber(shipmentNumber,shipmentResponse);
	}

	private void rerateLoad(LoadType load) {
		LoadRateDataType loadRateDataType = LoadRateDataType.builder().systemLoadID(load.getId()).carrierCode(load.getCarrierCode()).build();
		ProcessLoadRate processLoadRate = ProcessLoadRate.builder().loadRateData(Arrays.asList(loadRateDataType)).build();
		log.debug(Constants.PROCESS_LOAD, load.getId());
		loadEnrichmentService.processLoadRate(processLoadRate);

	}

	private void removeShipmentFromLoad(String shipmentNumber, String systemLoadID) {
		loadEnrichmentService.removeShipmentFromLoad(shipmentNumber,systemLoadID);
	}

	private void handleCancelAppointment(String shipmentNumber, AppointmentPayload apptPayload,LoadType load,ShipmentResponse shipmentResponse) {

      String currentLoadOperationalStatusDescr = load.getCurrentLoadOperationalStatusEnumDescr();
      List<String> allowedCancelLoadStatusList = Arrays.asList(allowedCancelLoadStatus.split(","));
      if(allowedCancelLoadStatusList.contains(currentLoadOperationalStatusDescr))
      {
          List<String> tenderedList = Arrays.asList(tenderedListOfNames.split(","));

          if (tenderedList.contains(currentLoadOperationalStatusDescr)) {
              ProcessTenderCancel processTenderCancel = prepareCancelLoadRequest(load);
              log.debug(Constants.LOAD_TENDERED_STATUS_LOG, load.getId());
              loadEnrichmentService.processCancleTender(processTenderCancel);
              ProcessLoadReturnToOpen processLoadReturnToOpen = prepareLoadReturnToOpen(load);
              loadEnrichmentService.processLoadReturnToOpen(processLoadReturnToOpen);
          }
          if(Constants.PLANNED.equalsIgnoreCase(currentLoadOperationalStatusDescr)) {
              ProcessLoadReturnToOpen processLoadReturnToOpen = prepareLoadReturnToOpen(load);
              loadEnrichmentService.processLoadReturnToOpen(processLoadReturnToOpen);
          }
          handleDoublePUPTrailerNumberMismatch(apptPayload, load);
          removeShipmentFromLoad(shipmentNumber,load.getId());
          deleteReferenceNumberFromShipment(shipmentNumber, shipmentResponse);

          log.debug(Constants.DELETE_REFERENCE);
          rerateLoad(load);

        }
      else {
        log.warn("Current status of load {} is not allowed to cancel appointment" , currentLoadOperationalStatusDescr);
      }

	}

  private ProcessLoadReturnToOpen prepareLoadReturnToOpen(LoadType load) {

    LoadPlanType loadPlanType = LoadPlanType.builder()
        .systemLoadID(load.getId())
        .build();
    return  ProcessLoadReturnToOpen.builder()
        .loadPlan(Arrays.asList(loadPlanType))
        .build();

  }

//Utility method to create entity list
  private List<UIEntityType> createEntityList(ShipmentResponse shipmentResponse) {
      StopType stop = new StopType();
      stop.setSystemLoadID(shipmentResponse.getData().get(0).getSystemLoadID());

      UIEntityType entity = new UIEntityType();
      entity.setStop(Collections.singletonList(stop));

      return Collections.singletonList(entity);
  }

	public boolean isLoadComplete(String status, String appType,String freightType){
		return Constants.LOAD_COMPLETE.equals(status.trim()) && isObtmAppointment(appType,freightType);
	}

	private boolean isObtmAppointment(String appType, String freightType) {
		return Constants.APPT_TYPE_DC_TO_DC.equals(appType.trim()) && (Constants.FRIEGHTTYPE_BACKSTAGE_TO_SDC.equals(freightType.trim()) || Constants.FRIEGHTTYPE_CF_TO_SDC.equals(freightType.trim()));
	}

	public boolean isLoadStatusInTransitOrCompleted(LoadType load){
		return Constants.IN_TRANSIT.equalsIgnoreCase(load.getCurrentLoadOperationalStatusEnumDescr()) ||
				Constants.COMPLETED.equalsIgnoreCase(load.getCurrentLoadOperationalStatusEnumDescr());
	}

	public void updateFlexibleQ1andFlexibleQ2andReRate(String loadId, LoadType load, AppointmentPayload apptPayload) {
		List<ShipmentLegType> shipmentLegs = load.getShipmentLeg();

		// Update Drop Flag from FLO to TM before TM In Transit Status i.e., only for Open, Load Tendered and Tender Accepted status
		if (isLoadStatusInTransitOrCompleted(load) || !apptPayload.getAppointmentType().startsWith(Constants.OUBOUND_APPOINTMENT) ||
				isObtmAppointment(apptPayload.getAppointmentType(),apptPayload.getAppointmentFreightType()) || CollectionUtils.isEmpty(shipmentLegs)) {
			log.debug("Drop Flag Update Restricted, either appointment type is not Outbound {} or load status {} cannot be processed", apptPayload.getAppointmentType(), load.getCurrentLoadOperationalStatusEnumDescr());
			return;
		}
		// Find matching shipment legs for the appointment number
		List<ShipmentLegType> matchingLegs = shipmentLegs.stream()
				.filter(leg -> apptPayload.getAppointmentNumber().equals(leg.getSystemDropStopID().trim()))
				.toList();

		if (CollectionUtils.isEmpty(matchingLegs)) {
			log.warn("Drop Flag Update - Matching Shipment Leg not found for Appointment Number - {} in Load Id {} ", apptPayload.getAppointmentNumber(), load.getSystemLoadID());
			return;
		}

		try {
			// For each matching shipment leg for the appointment number update Drop Flag value in TM by defining FQ1 and FQ2 fields
			for (ShipmentLegType shipmentLegType : matchingLegs) {
				BigDecimal fq1 = Optional.ofNullable(shipmentLegType.getFlexibleQuantity1()).orElse(BigDecimal.valueOf(-1));
				BigDecimal fq2 = Optional.ofNullable(shipmentLegType.getFlexibleQuantity2()).orElse(BigDecimal.valueOf(-1));
				String currentDropFlagInTM = currentDropFlagInTM(fq1, fq2);
				String currentDropFlagInFlo = apptPayload.getAppointmentDropFlag();
				if (!currentDropFlagInFlo.equalsIgnoreCase(currentDropFlagInTM)) {
					log.info("Drop Flag Update - Current Drop Flag value in TM and FLO are different for Shipment {}. Drop Flag in FLO {} and TM FQ1 {}, FQ2 {}",
							shipmentLegType.getShipmentNumber(), currentDropFlagInFlo, fq1, fq2);
					// Current Drop Flag in TM and FLO are different for this Shipment Leg
					if (currentDropFlagInFlo.equalsIgnoreCase(Constants.DROP_FLAG_N)) {
						shipmentLegType.setFlexibleQuantity1(BigDecimal.ZERO);
						shipmentLegType.setFlexibleQuantity2(BigDecimal.ONE);
					} else {
						shipmentLegType.setFlexibleQuantity1(BigDecimal.ONE);
						shipmentLegType.setFlexibleQuantity2(BigDecimal.ZERO);
					}
					// Shipment Update call to set FQ1 and FQ2 fields in TM
					loadEnrichmentService.updateFlexibleQ1andFlexibleQ2andReRate(loadId, load, apptPayload, shipmentLegType);
				} else {
					log.debug("Drop Flag Update - Current DropFlag in FLO and TM are same. Drop Flag in FLO {} and in TM {}", currentDropFlagInFlo, currentDropFlagInTM);
					return;
				}
			}
			//Rerate the load once the FQ1 and FQ2 fields are updated
			rerateLoad(load);
		}catch (Exception e){
			log.error("Drop Flag Update Failed for Load iD - {}, failure reason {} ", load.getSystemLoadID(), e.getMessage());
		}
	}

	private String currentDropFlagInTM(BigDecimal fq1, BigDecimal fq2) {
		  //If FQ1 == 0 and FQ2 == 1, drop Flag in TM is N.
		  if(fq1.compareTo(BigDecimal.ZERO) == 0 && fq2.compareTo(BigDecimal.ONE) == 0){
			  return Constants.DROP_FLAG_N;
		  //If FQ1 == 1 and FQ2 == 0, drop Flag in TM is Y.
		  }else if(fq1.compareTo(BigDecimal.ONE) == 0 && fq2.compareTo(BigDecimal.ZERO) == 0){
			  return Constants.DROP_FLAG_Y;
		  }else{
			  return null;
		  }
	}

}
