package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessMaintainEntityReferenceNumber;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ReferenceNumberUpdateDataType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.FindEntitiesResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.StopIdTypeEnumValType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessStopAppointmentResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadTenderDataType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ErrorType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadRateDataType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadRetrieve;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadRetrieveResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadRate;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadUpdateDataType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadUpdate;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadCancel;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessTenderCancel;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessTender;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShipmentResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShipmentData;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ReferenceNumberStructure;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessStopAppointment;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShipmentKeyInfoType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessShipmentRemove;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestFindEntity;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadReturnToOpen;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessShipmentOrderCreateResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestProcessLoadUpdate;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestSelectClauseType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestFilterType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestProcessShipmentOrderCreate;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShipmentLegHeaderType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestLoadUpdateDataType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShipmentLegType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestShipmentType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ContainerType;


import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.util.Constants;
import com.macys.logistics.shipment.load.util.DateUtil;
import lombok.extern.log4j.Log4j2;

import javax.xml.datatype.DatatypeConfigurationException;


import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.macys.logistics.shipment.load.util.Constants.ERROR;



@Component
@Log4j2
public class LoadEnrichmentServiceImpl implements LoadEnrichmentService {


    @Autowired
    LoadMapper loadMapper;

    public static final String DEFAULT_REF_NUM_CATEGORY_ENUM_VAL = "RNC_LD_LEG";
    public static final String REF_NUM_ACTION_ENUM_VAL_ADD = "AT_ADD";
    public static final String SEAL_CND = "SEAL_CND";
    public static final String SEAL_TYP = "SEAL_TYP";
    public static final String SYSTEM_STOP_ID = "SystemStopID";
    public static final String SYSTEM_LOAD_ID = "systemLoadID";

    public static final String LOAD_LEVEL_REQUIRED_ELEMENTS_FROM_LOAD_API = "Id,TotalPieces,"
            + "CarrierCode,CurrentLoadOperationalStatusEnumVal,TrailerNumber,systemShipmentID,ReferenceNumber5,ReferenceNumber6";
    public static final String REFERENCE_NUMBER_STRUCTURE = "ReferenceNumberStructure";
    public static final String STOP = "Stop";
    public static final String SHIPMENT_LEG = "ShipmentLeg";
    public static final String REFERENCE_LEVEL_REQUIRED_ELEMENTS_FROM_LOAD_API = "ReferenceNumberTypeCode,ReferenceNumber,SystemReferenceNumberID";
    public static final String SHIPMENT_LEG_LEVEL_REQUIRED_ELEMENTS_FROM_LOAD_API = "systemShipmentLegID,systemShipmentID,systemLoadID,shipmentNumber";

    public static final String DOOR_DOCK_NBR = "DOOR_DOCK_NBR";
    public static final String REF_NUM_ACTION_ENUM_VAL_DELETE = "AT_DELETE";
    public static final String DEFAULT_REF_NUM_SHP_CATEGORY_ENUM_VAL = "RNC_SHPM";


    @Autowired
    TMRestApiClient tmRestApiClient;

    @Autowired
    private Environment env;


    /**
     * Update ref num on the load
     *
     * @param loadId
     */
    public void addOrUpdateSealCondition(AppointmentPayload apptPayload, String loadId, LoadType load) {
        log.info("Entering addOrUpdateSealCondition | loadId={}", loadId);
        try {
            List<String> existingSealConditionReferenceNumbers = load.getReferenceNumberStructure().stream()
                    .filter(reference -> reference.getReferenceNumberTypeCode().equalsIgnoreCase(SEAL_CND))
                    .map(filteredSealNumber -> filteredSealNumber.getSystemReferenceNumberID()).toList();
            if (ObjectUtils.isNotEmpty(existingSealConditionReferenceNumbers)) {
                deleteSealDetailsInTM(existingSealConditionReferenceNumbers, loadId, SEAL_CND);
                log.info(
                        "Existing seal condition changed, deleting old seal condition | loadId={}, existingcondition={}, newSealCondition={}",
                        loadId, load.getReferenceNumber5(), apptPayload.getAppointmentSealConditionDescription()
                );
            }
            ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
            ReferenceNumberUpdateDataType referenceNumberUpdateDataType = new ReferenceNumberUpdateDataType();
            referenceNumberUpdateDataType.setReferenceNumberActionEnumVal(REF_NUM_ACTION_ENUM_VAL_ADD);
            referenceNumberUpdateDataType.setReferenceNumberCategoryEnumVal(DEFAULT_REF_NUM_CATEGORY_ENUM_VAL);
            referenceNumberUpdateDataType.setReferenceNumber(apptPayload.getAppointmentSealConditionDescription());
            referenceNumberUpdateDataType.setReferenceNumberEntityKey(loadId);
            referenceNumberUpdateDataType.setReferenceNumberTypeCode(SEAL_CND);
            processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(referenceNumberUpdateDataType);

            log.info(
                    "Calling TMS REST API to add/update seal condition | loadId={}, sealCondition={}",
                    loadId,
                    apptPayload.getAppointmentSealConditionDescription()
            );
            tmRestApiClient.processMaintainEntityReferenceNumber(processMaintainEntityReferenceNumber);
            log.info("Successfully added/updated seal condition | loadId={}", loadId);

        } catch (TmsLoadProcessException e) {
            log.error("TMS error while updating seal condition | loadId={}, error={}", loadId, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while updating seal condition | loadId={}", loadId);
        }
    }

    public void addOrUpdateSealType(AppointmentPayload apptPayload, String loadId, LoadType load, String sealType) {
        log.info("Entering addOrUpdateSealType | loadId={}, appointment number {}", loadId, apptPayload.getAppointmentNumber());

        try {

            String projectId = env.getProperty("pubsub.supplychain.tmsintg.project-id");

            boolean matched = Constants.GCP_PROJECT_NAME.equalsIgnoreCase(projectId);

            String existingSealType = Optional.ofNullable(
                    matched ? load.getReferenceNumber7() : load.getReferenceNumber8()
            ).orElse("");

            log.info("GCP projectId='{}' → selected {}",
                    projectId,
                    Constants.GCP_PROJECT_NAME.equalsIgnoreCase(projectId)
                            ? "referenceNumber7"
                            : "referenceNumber8");
            if (StringUtils.isNotEmpty(existingSealType) && !existingSealType.equalsIgnoreCase(sealType)) {
                List<String> existingSealConditionReferenceNumbers = load.getReferenceNumberStructure().stream()
                        .filter(reference -> reference.getReferenceNumberTypeCode().equalsIgnoreCase(SEAL_TYP))
                        .map(filteredSealNumber -> filteredSealNumber.getSystemReferenceNumberID()).toList();
                if (ObjectUtils.isNotEmpty(existingSealConditionReferenceNumbers)) {
                    deleteSealDetailsInTM(existingSealConditionReferenceNumbers, loadId, SEAL_TYP);
                    log.info(
                            "Existing seal type changed, deleting old seal references | loadId={}, existingSealType={}, newSealType={}",
                            loadId, existingSealType, sealType
                    );
                }
            }
            //Create one more Object for SealType
            if (StringUtils.isNotEmpty(sealType) && !existingSealType.equalsIgnoreCase(sealType)) {
                ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
                ReferenceNumberUpdateDataType referenceNumberUpdateDataTypeSealType = new ReferenceNumberUpdateDataType();
                referenceNumberUpdateDataTypeSealType.setReferenceNumberActionEnumVal(REF_NUM_ACTION_ENUM_VAL_ADD);
                referenceNumberUpdateDataTypeSealType.setReferenceNumberCategoryEnumVal(DEFAULT_REF_NUM_CATEGORY_ENUM_VAL);
                referenceNumberUpdateDataTypeSealType.setReferenceNumber(sealType);
                referenceNumberUpdateDataTypeSealType.setReferenceNumberEntityKey(loadId);
                referenceNumberUpdateDataTypeSealType.setReferenceNumberTypeCode(SEAL_TYP);
                processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(referenceNumberUpdateDataTypeSealType);
                log.debug("Calling TMS REST API to add/update seal type | loadId={}, sealType={}", loadId, sealType);
                tmRestApiClient.processMaintainEntityReferenceNumber(processMaintainEntityReferenceNumber);
                log.info("Successfully added/updated seal type | loadId={}", loadId);
            } else {
                log.warn("Seal type not found from appointment details, skipping update | appointmentNumber={}, loadId={}",
                        apptPayload.getAppointmentNumber(),
                        loadId
                );
            }
        } catch (TmsLoadProcessException e) {
            log.error("TMS error while updating seal type | loadId={}, error={}", loadId, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while updating seal type | loadId={}", loadId);
        }
    }

    public void deleteSealDetailsInTM(List<String> systemReferenceNbrList, String loadId, String referenceTypeCode) {
        log.debug("Staretd method deleteSealDetailsInTM for load {}", loadId);
        try {
            log.debug("systemReferenceNbrList Of values {}", systemReferenceNbrList);
            for (String refereceNumber : systemReferenceNbrList) {
                ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
                ReferenceNumberUpdateDataType referenceNumberUpdateDataType = new ReferenceNumberUpdateDataType();
                referenceNumberUpdateDataType.setReferenceNumberActionEnumVal(REF_NUM_ACTION_ENUM_VAL_DELETE);
                referenceNumberUpdateDataType.setReferenceNumberCategoryEnumVal(DEFAULT_REF_NUM_CATEGORY_ENUM_VAL);
                referenceNumberUpdateDataType.setReferenceNumberEntityKey(loadId);
                referenceNumberUpdateDataType.setReferenceNumberTypeCode(referenceTypeCode);
                referenceNumberUpdateDataType.setSystemReferenceNumberID(refereceNumber);
                processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(referenceNumberUpdateDataType);


                log.debug("deleteSealDetailsInTM details using rest api call for loadId {}", loadId);
                tmRestApiClient.processMaintainEntityReferenceNumber(processMaintainEntityReferenceNumber);

                log.debug("Ended method deleteSealDetailsInTM for load {}", loadId);
            }
        } catch (TmsLoadProcessException e) {
            log.error("deleteSealDetailsInTM| exception while delete seal info for load {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error calling tms to delete seal info for load {}", e.getMessage());
        }
    }


    /**
     * Fetch Load Id using Appointment number
     *
     * @param apptNum
     */
    public FindEntitiesResponse fetchLoadId(List<String> apptNum,String filterOpertaion) {
        log.debug("fetchLoadId for appointment {}", apptNum);
        FindEntitiesResponse findEntitiesResponse = null;
        try {

            log.debug("fetchLoadId details using rest api for appointment number {}", apptNum);
            RestFindEntity restFindEntity = getRestFindEntity(apptNum, filterOpertaion);
            findEntitiesResponse = tmRestApiClient.findLoadID(restFindEntity);

            return findEntitiesResponse;

        } catch (TmsLoadProcessException e) {
            log.error("fetchLoadId| exception while fetching Load Id {}", e.getMessage());
            throw new TmsLoadProcessException("Error while while fetching Load Id", e);
        } catch (Exception e) {
            log.error("Error calling tms to fetching Load Id {}", e.getMessage());
            throw new TmsLoadProcessException("Error calling tms to fetching Load Id", e);
        }
    }

    /**
     * Fetch Load details using loadId
     *
     * @param loadId
     */
    public FindEntitiesResponse fetchLoadDetails(String loadId) {
        log.debug("fetchLoaddetails for load Id {}", loadId);

        FindEntitiesResponse findEntitiesResponse;
        try {

            log.debug("fetchLoadDetails details using rest api call for loadId {}", loadId);
            RestFindEntity restFindEntity = loadMapper.createRestFindEntity(env.getProperty("load.search.config"), SYSTEM_LOAD_ID, Constants.EQ, Arrays.asList(loadId));
            findEntitiesResponse = tmRestApiClient.restFindEntities(restFindEntity);

            return findEntitiesResponse;
        } catch (TmsLoadProcessException e) {
            log.error("fetchLoadDetails| exception while fetching Load Details {}", e.getMessage());
            throw e;
        }
    }

    public FindEntitiesResponse fetchLoadDetailsForDockDoor(String config,String filterField,List<String> filterValue,String filterOperation) {
        log.debug("fetchLoaddetails for load Id {}", filterValue);

        FindEntitiesResponse findEntitiesResponse;
        try {

            log.debug("fetchLoadDetails details using rest api call for loadId {}", filterValue);
            RestFindEntity restFindEntity = loadMapper.createRestFindEntity(env.getProperty(config), filterField, filterOperation,filterValue);
            findEntitiesResponse = tmRestApiClient.restFindEntitiesByDockDoor(restFindEntity);

            return findEntitiesResponse;
        } catch (TmsLoadProcessException e) {
            log.error("fetchLoadDetails| exception while fetching Load Details {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Update Stop Appointment
     *
     * @param apptPayload, loadId, timezoneResponse, stopAppointment
     */
    @Override
    public void updateStopAppointment(AppointmentPayload apptPayload, LoadType load) throws TmsLoadProcessException, DatatypeConfigurationException {
        log.debug("Updating stop appointment for loadId {} with appointment number {}", load.getId(), apptPayload.getAppointmentNumber());
        log.debug("Full appointment payload for updateStopAppointment: {}", apptPayload);
        ProcessStopAppointment processStopAppointment = null;
        try {
            processStopAppointment = loadMapper.mapRestStopAppointmentRequest(apptPayload, load);

            if (processStopAppointment != null) {
                handleProcessStopAppointment(processStopAppointment, load);
            } else {
                log.debug("updateStopAppointment There is no update from scheduled arrival date and time");
            }
        } catch (TmsLoadProcessException e) {
            log.error("updateStopAppointment| exception while updating stop Id {}", e.getMessage());
        } catch (Exception e) {
            log.error("updateStopAppointment| update stop failed - {} ", e.getMessage());
        }
    }

    private void handleProcessStopAppointment(ProcessStopAppointment processStopAppointment, LoadType load) {
        log.debug("processStopAppointment rest API call for loadId {}", load.getId());
        processStopAppointment.getStopAppointment().get(0).setStopIDTypeEnumVal(StopIdTypeEnumValType.SID_ID);
        ProcessStopAppointmentResponse processStopAppointmentResponse = tmRestApiClient.processStopAppointment(processStopAppointment);

        if (processStopAppointmentResponse == null || processStopAppointmentResponse.getResponseHeader() == null) {
            log.error("processStopAppointment failed or returned no response header for loadId {}", load.getId());
            return;
        }

        List<ErrorType> errorList = processStopAppointmentResponse.getResponseHeader().getError();

        if (ObjectUtils.isEmpty(errorList) || (!ObjectUtils.isEmpty(errorList) &&
                errorList.stream().noneMatch(errorType -> ERROR.equalsIgnoreCase(errorType.getSeverity())))) {
            processLoadRateAndUpdate(load);
        } else {
            log.error("load rate and execution flag update restricted as scheduled arrival date update failed {}", processStopAppointmentResponse.getResponseHeader());
        }
    }

    private void processLoadRateAndUpdate(LoadType load) {
        LoadRateDataType loadRateDataType = LoadRateDataType.builder()
                .systemLoadID(load.getId())
                .carrierCode(load.getCarrierCode())
                .build();

        ProcessLoadRate processLoadRate = ProcessLoadRate.builder()
                .loadRateData(Arrays.asList(loadRateDataType))
                .build();

        LoadUpdateDataType loadUpdateData = LoadUpdateDataType.builder()
                .systemLoadID(load.getId())
                .executionSchedulingOverriddenFlag(true)
                .build();

        ProcessLoadUpdate processLoadUpdate = ProcessLoadUpdate.builder()
                .loadUpdateData(Collections.singletonList(loadUpdateData))
                .build();

        log.debug("processLoadUpdate and processLoadRate details using rest api call for loadId {}", load.getId());
        tmRestApiClient.processTrailerNumberUpdate(processLoadUpdate);
        tmRestApiClient.processLoadRate(processLoadRate);
    }


    /**
     * Fetch timezone details using loadId
     *
     * @param loadId
     */

    @Override
    public ProcessLoadRetrieveResponse fetchLoadRetrieveDetails(String loadId) {
        log.debug("fetchLoadRetrieveDetails for load Id {}", loadId);
        ProcessLoadRetrieveResponse response = null;
        try {
            ProcessLoadRetrieve processLoadRetrieve = new ProcessLoadRetrieve();
            List<String> systemLoadID = new ArrayList<>();
            systemLoadID.add(loadId);
            processLoadRetrieve.setSystemLoadID(systemLoadID);
            processLoadRetrieve.setReferenceNumberTypeCode(DOOR_DOCK_NBR);


            log.debug("processLoadRetrieve using rest api call for loadId {}", loadId);
            response = tmRestApiClient.processLoadRetrieve(processLoadRetrieve);

            return response;
        } catch (TmsLoadProcessException e) {
            log.error("fetchLoadRetrieveDetails| exception while fetching processLoadRetrieveResponse Details {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void processLoadUpdate(String loadId, String trailerNbr) {
        log.debug("LoadEnrichmentServiceImpl :: processLoadUpdate :: trailer Number to be updated for load Id {}", loadId);
        try {
            LoadUpdateDataType loadUpdateData = LoadUpdateDataType.builder()
                    .systemLoadID(loadId)
                    .trailerNumber(trailerNbr)
                    .build();
            ProcessLoadUpdate processLoadUpdate = ProcessLoadUpdate.builder()
                    .loadUpdateData(Collections.singletonList(loadUpdateData))
                    .build();
            log.debug("processLoadUpdate using rest api call for loadId {} ", processLoadUpdate.getLoadUpdateData().get(0).getSystemLoadID());
            tmRestApiClient.processTrailerNumberUpdate(processLoadUpdate);

            log.debug("LoadEnrichmentServiceImpl :: processLoadUpdate :: trailer number updated for load Id {}", loadId);
        } catch (Exception exception) {
            log.error("LoadEnrichmentServiceImpl :: processLoadUpdate :: Error calling tms to update trailer number for load Id - " + loadId, exception);
        }
    }


    @Override
    public void processCancelLoad(ProcessLoadCancel processLoadCancel) {
        log.debug("cancelLoad for processLoadCancel loadId  {}", processLoadCancel.getSystemLoadID());
        try {

            log.debug("processLoadCancel using rest api call for loadId {}", processLoadCancel.getSystemLoadID());
            tmRestApiClient.processLoadCancel(processLoadCancel);

            log.debug("Ended the ProcessLoadCancel()  {} :: ", processLoadCancel.getSystemLoadID());

        } catch (TmsLoadProcessException e) {
            log.error("cancelLoad| exception while cancelling load {}", e.getMessage());
            throw e;
        }
    }


    @Override
    public void processCancleTender(ProcessTenderCancel processTenderCancel) {
        log.debug("Stared the ProcessTenderCancel()  {} ::: ", processTenderCancel);
        try {

            log.debug("ProcessTenderCancel using rest api call for loadId {} ", processTenderCancel.getLoadTenderData().get(0).getSystemLoadID());
            tmRestApiClient.processTenderCancel(processTenderCancel);

            log.debug("Eneded the ProcessTenderCancel()  {} : ", processTenderCancel);
        } catch (TmsLoadProcessException e) {
            log.error("tenderCancle| exception while cancelling load {}", e.getMessage());
            throw e;
        }

    }

    public void dispatchFlagTrigger(AppointmentPayload apptPayload, LoadType load) {
        if (load != null && apptPayload.getAppointmentDispatchFlag().equalsIgnoreCase(Constants.DISPATCH_FLAG)) {
            String currentLoadOperationalStatusDescr = load.getCurrentLoadOperationalStatusEnumDescr();
            if (currentLoadOperationalStatusDescr == null) {
                log.warn("Current load operational status is null");
                return;
            }
            switch (currentLoadOperationalStatusDescr) {
                case Constants.OPEN:
                    logError(load.getId(), Constants.OPEN);
                    break;
                case Constants.PLANNED:
                    loadPlanned(load.getId());
                    break;
                case Constants.TENDERED:
                    logInfo(load.getId(), Constants.TENDERED);
                    break;
                case Constants.TENDERED_ACCEPTED:
                    logInfo(load.getId(), Constants.TENDERED_ACCEPTED);
                    break;
                case Constants.CANCELLED_LOAD:
                    logError(load.getId(), Constants.CANCELLED_LOAD);
                    break;
                default:
                    logInfo(load.getId(), currentLoadOperationalStatusDescr);
            }
        }
    }


    private void logError(String loadId, String status) {
        log.error(Constants.LOAD_ID + loadId + Constants.LOAD_STATUS + status);
    }

    private void logInfo(String loadId, String status) {
        log.debug(Constants.LOAD_ID + loadId + Constants.LOAD_STATUS + status);
    }

    private void loadPlanned(String loadId) {
        log.debug("Tender the current loadId " + loadId + " because it is in Planned status");
        LoadTenderDataType loadTenderDataType = LoadTenderDataType.builder()
                .systemLoadID(loadId)
                .build();
        try {

            log.debug("loadPlanned using rest api call for loadId {}", loadId);
            ProcessTender processTender = ProcessTender.builder().autoAcceptFlag(false).autoAcceptControlEnumVal("").loadTenderData(Arrays.asList(loadTenderDataType)).build();
            tmRestApiClient.processTender(processTender);

        } catch (Exception e) {
            log.error("dispatchFlagTrigger| exception while calling process tender {}", e.getMessage());
        }
    }

    @Override
    public ShipmentResponse searchShipment(String shipmentNumber) {
        log.debug(Constants.SEARCH_SHIPMENT, shipmentNumber);
        ShipmentResponse shipmentResponse = new ShipmentResponse();
        try {
            log.debug(Constants.SEARCH_SHIPMENT_WITH_SHIPMENT_NUMBER, shipmentNumber);
            RestFindEntity restFindEntity = loadMapper.createRestFindEntity(env.getProperty(Constants.SHIPMENT_CONFIG), Constants.SHIPMENT_KEY, Constants.IN, Arrays.asList(shipmentNumber));
            shipmentResponse = tmRestApiClient.searchShipment(restFindEntity);
            return shipmentResponse;
        } catch (Exception e) {
            log.error(Constants.SEARCH_SHIPMENT_EXCEPTION, e.getMessage());
        }
        return shipmentResponse;
    }

    @Override
    public void deleteReferenceNumber(String shipmentNumber, ShipmentResponse shipmentResponse) {
        log.debug(Constants.DELETE_REFERENCE_METHODS_STARTS, shipmentNumber);
        try {
            for (ShipmentData shipment : shipmentResponse.getData()) {
                ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
                List<ReferenceNumberStructure> referenceNumberList = shipment.getReferenceNumberStructure();
                referenceNumberList.stream().filter(referenceTypeCode -> referenceTypeCode.getReferenceNumberTypeCode().equalsIgnoreCase(Constants.OB_SHIP_APPT))
                        .forEach(reference -> {
                            ReferenceNumberUpdateDataType referenceNumberUpdateDataType = new ReferenceNumberUpdateDataType();
                            referenceNumberUpdateDataType.setReferenceNumberActionEnumVal(REF_NUM_ACTION_ENUM_VAL_DELETE);
                            referenceNumberUpdateDataType.setReferenceNumberCategoryEnumVal(DEFAULT_REF_NUM_SHP_CATEGORY_ENUM_VAL);
                            referenceNumberUpdateDataType.setReferenceNumberEntityKey(shipmentNumber);
                            referenceNumberUpdateDataType.setReferenceNumberTypeCode(Constants.OB_SHIP_APPT);
                            referenceNumberUpdateDataType.setReferenceNumber(reference.getReferenceNumber());
                            referenceNumberUpdateDataType.setSystemReferenceNumberID(reference.getSystemReferenceNumberID());
                            processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(referenceNumberUpdateDataType);
                        });
                log.debug(Constants.DELETE_REFERENCE_API_CALL, shipmentNumber);
                if (!processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().isEmpty()) {
                    tmRestApiClient.processMaintainEntityReferenceNumber(processMaintainEntityReferenceNumber);
                }

                log.debug(Constants.DELETE_REFERENCE_METHODS_ENDS, shipmentNumber);
            }
        } catch (Exception e) {
            log.error(Constants.DELETE_REFERENCE_METHODS_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void processLoadRate(ProcessLoadRate processLoadRate) {
        tmRestApiClient.processLoadRate(processLoadRate);
    }

    @Override
    public void removeShipmentFromLoad(String shipmentNumber, String systemLoadID) {
        log.debug(Constants.REMOVE_SHIPMENT_METHODS_STARTS, shipmentNumber);
        try {

            ShipmentKeyInfoType shipmentKeyInfo = ShipmentKeyInfoType.builder()
                    .systemLoadID(systemLoadID)
                    .systemShipmentID(shipmentNumber)
                    .build();
            ProcessShipmentRemove processShipmentRemove = ProcessShipmentRemove.builder()
                    .shipmentKeyInfo(Arrays.asList(shipmentKeyInfo))
                    .build();
            tmRestApiClient.processShipmentRemove(processShipmentRemove);
            log.debug(Constants.REMOVE_SHIPMENT_METHODS_ENDS, processShipmentRemove);
        } catch (Exception e) {
            log.error(Constants.SHIPMENT_REMOVE_METHODS_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void processLoadReturnToOpen(ProcessLoadReturnToOpen processLoadReturnToOpen) {
        log.debug(Constants.PROCESS_LOAD_RETURN_TO_OPEN_STARTS, processLoadReturnToOpen);
        try {

            log.debug(Constants.PROCESS_LOAD_RETURN_TO_OPEN_LOAD_INFO, processLoadReturnToOpen.getLoadPlan().get(0).getSystemLoadID());
            tmRestApiClient.processLoadReturnToOpen(processLoadReturnToOpen);

            log.debug(Constants.PROCESS_LOAD_RETURN_TO_OPEN_STARTS, processLoadReturnToOpen);
        } catch (Exception e) {
            log.error(Constants.PROCESS_LOAD_RETURN_TO_OPEN_EXCEPTION, e.getMessage());

        }

    }

    @Override
    public ShipmentResponse searchShipmentWithOldAppointmentRef(String oldAppointmentNumber) {
        log.debug(Constants.SEARCH_SHIPMENT, oldAppointmentNumber);
        ShipmentResponse shipmentResponse = new ShipmentResponse();
        try {
            log.debug(Constants.SEARCH_SHIPMENT_WITH_OLD_APPOINTMENT_NUMBER, oldAppointmentNumber);
            RestFindEntity restFindEntity = loadMapper.createRestFindEntity(env.getProperty(Constants.SHIPMENT_APPOINTMENT_REFERENCE_CONFIG), Constants.OLD_APPOINTMENT_REFERENCE_KEY, Constants.EQ, Arrays.asList(oldAppointmentNumber));
            shipmentResponse = tmRestApiClient.searchShipment(restFindEntity);
            return shipmentResponse;
        } catch (Exception e) {
            log.error(Constants.SEARCH_SHIPMENT_EXCEPTION, e.getMessage());
        }
        return shipmentResponse;
    }

    @Override
    public void triggerLoadCompleteForObtmAppointments(String loadId, LoadType load, String apptNbr, int pieces) {

        try {
            if (!StringUtils.isEmpty(load.getLoadDescription())) {
                if (pieces > (load.getTotalPieces() != null ? load.getTotalPieces().intValue() : 0)) {
                    log.debug("Load already tendered, updating carton count in TM for load {}, appt {}", loadId, apptNbr);
                    updateCartonCountForShipment(loadId, load, pieces);
                    return;
                } else {
                    log.debug("Load already tendered, No carton update, carton count {} is high or equal in TM {} for load {}, appt {}", pieces, load.getTotalPieces().intValue(), loadId, apptNbr);
                    return;
                }
            }

            log.debug("Begin Load Complete Process for load id {} and appt number {}", loadId, apptNbr);

            // create Shipments
            List<String> shipments = createShipments(loadId, load, apptNbr, pieces);

            if (shipments.isEmpty()) {
                throw new TmsLoadProcessException("Shipment creation process returned empty list for load id " + loadId + " appt nbr " + apptNbr);
            }

            // assign Shipments to the load
            assignShipmentsToLoad(loadId, shipments);

            // remove Dummy shipment
            processRemoveDummyShipment(loadId, load.getShipmentLeg().get(0).getSystemShipmentID());

            //update Load TrackingNumber
            updateLoadTrackingNumber(loadId, apptNbr);
            log.debug("performLoadComplete| process complete for apptNbr {} loadId {}", apptNbr, loadId);

        } catch (Exception e) {
            log.error("error in load complete process for obtm loadId {} apptNbr {} failure reason {}", loadId, apptNbr, e.getMessage());
        }
    }

    @Override
    public void updateCartonCountForShipment(String loadId, LoadType load, Integer appointmentNumberOfCartons) {
        RestProcessShipmentOrderCreate restProcessShipmentOrderCreate = loadMapper.updateShipmentOrderForRest(appointmentNumberOfCartons, loadId, load);
        if (restProcessShipmentOrderCreate != null) {
            tmRestApiClient.processUpdateShipment(restProcessShipmentOrderCreate);
        }
    }

    @Override
    public void updateFlexibleQ1andFlexibleQ2andReRate(String loadId, LoadType load, AppointmentPayload appointmentPayload, ShipmentLegType shipmentLegType) {
        try {
            log.debug("Drop Flag Update - Updating FQ1 and FQ2 for Shipment  - {}", shipmentLegType.getShipmentNumber());
            RestProcessShipmentOrderCreate restProcessShipmentOrderCreate = loadMapper.updateShipmentOrderForRest(shipmentLegType.getPieces().intValue(), loadId, load);
            if (!restProcessShipmentOrderCreate.getShipment().isEmpty()) {
                RestShipmentType shipmentLegType1 = restProcessShipmentOrderCreate.getShipment().get(0);
                shipmentLegType1.setShipmentNumber(shipmentLegType.getShipmentNumber());
                shipmentLegType1.setReferenceNumber22(appointmentPayload.getAppointmentCarrierTrailerNumber());
                shipmentLegType1.setReferenceNumber23(appointmentPayload.getAppointmentNumber());
                if (!shipmentLegType1.getContainer().isEmpty()) {
                    ContainerType container = shipmentLegType1.getContainer().get(0);
                    container.setContainerTypeCode(Constants.DEFAULT_CONTAINER_TYPE_CODE_OUTBOUND);
                    container.getContainerShippingInformation().setVolume(shipmentLegType.getVolume());
                    container.getContainerShippingInformation().setFlexibleQuantity2(shipmentLegType.getFlexibleQuantity2());
                    container.getContainerShippingInformation().setFlexibleQuantity1(shipmentLegType.getFlexibleQuantity1());
                    container.getWeightByFreightClass().getFirst().setFreightClassNominalWeight(shipmentLegType.getNominalWeight());
                }
            }
            tmRestApiClient.processUpdateShipment(restProcessShipmentOrderCreate);
        } catch (TmsLoadProcessException ex) {
            log.error("Drop Flag API Update Failed for Load Id - {}, failure reason {} ", load.getSystemLoadID(), ex.getMessage());
            throw ex;
        }

    }

    private List<String> createShipments(String loadId, LoadType load, String apptNbr, int pieces) {

        List<String> shipmentIDList = new ArrayList<>();
        try {

            log.debug("createShipments | creating shipments for apptNbr {} loadId {} and pieces {}", apptNbr, loadId, pieces);

            if (pieces == 0) {
                throw new TmsLoadProcessException("Carton count is zero for the apptNbr " + apptNbr);
            }

            shipmentIDList = processCreateShipments(loadId, load, apptNbr, pieces);
            log.debug("processCreateShipments | Shipments are created Successfully for apptNbr {} loadId {} and pieces {}", apptNbr, loadId, pieces);

        } catch (Exception e) {
            log.error("error while creating/updating shipment for loadId {} apptNbr {}. Failure reason {}", loadId, apptNbr, e.getMessage());
            throw new TmsLoadProcessException(e.getMessage());
        }


        return shipmentIDList;

    }

    private List<String> processCreateShipments(String loadId, LoadType load, String apptNbr, int pieces) {
        List<String> shipmentIdList = new ArrayList<>();
        try {
            ProcessShipmentOrderCreateResponse processShipmentOrderCreateResponse;
            RestProcessShipmentOrderCreate restProcessShipmentOrderCreate = loadMapper.createShipmentOrderForRest(load, apptNbr, pieces);
            processShipmentOrderCreateResponse = tmRestApiClient.restProcessCreateShipment(restProcessShipmentOrderCreate);

            shipmentIdList.addAll(processShipmentOrderCreateResponse.getShipmentLegHeader().stream().map(ShipmentLegHeaderType::getSystemShipmentLegID).toList());
            return shipmentIdList;
        } catch (Exception e) {
            log.error("error while creating shipment for  apptNbr {} loadId {} failure reason {}", apptNbr, loadId, e.getMessage());
            throw new TmsLoadProcessException(e.getMessage());
        }
    }

    private void assignShipmentsToLoad(String loadId, List<String> shipments) {
        try {
            log.debug("assignShipmentsToLoad | shipmentIdListSize {} loadId {}", shipments.size(), loadId);
            tmRestApiClient.processAssignShipmentsToLoad(loadMapper.processAssignToLoadRequest(loadId, shipments));

        } catch (Exception e) {
            log.error("error while assigning shipments to Load | loadId {} failure reason {}", loadId, e.getMessage());
            throw new TmsLoadProcessException(e.getMessage());
        }
    }

    private void processRemoveDummyShipment(String loadId, String systemShipmentID) {

        log.debug("processRemoveDummyShipment| removing dummy shipment on load {} shipmentId {}", loadId, systemShipmentID);
        ProcessShipmentRemove processShipmentRemove = new ProcessShipmentRemove();

        ShipmentKeyInfoType shipmentKeyInfoType = new ShipmentKeyInfoType();
        shipmentKeyInfoType.setSystemLoadID(loadId);
        shipmentKeyInfoType.setSystemShipmentID(systemShipmentID);
        processShipmentRemove.getShipmentKeyInfo().add(shipmentKeyInfoType);
        tmRestApiClient.processShipmentRemove(processShipmentRemove);
        log.debug("processRemoveDummyShipment | removed dummyshipment on load {} shipmentId {}", loadId, systemShipmentID);
    }

    private void updateLoadTrackingNumber(String loadId, String appointmentId) {
        log.debug("updateLoadTrackingNumber | updating trackingNum for appointmentId {} loadId {}", appointmentId, loadId);


        RestProcessLoadUpdate processLoadUpdate = new RestProcessLoadUpdate();
        List<RestLoadUpdateDataType> loadUpdateData = new ArrayList<>();
        RestLoadUpdateDataType loadUpdateDataType = new RestLoadUpdateDataType();

        loadUpdateDataType.setSystemLoadID(loadId);
        loadUpdateDataType.setLoadDescription(appointmentId);
        loadUpdateDataType.setLoadScheduledDateTime(DateUtil.xmlCalendertoStringByFormat(DateUtil.getCurrentXMLDate(), Constants.DEFAULT_DATE_TIME_FORMAT));
        loadUpdateData.add(loadUpdateDataType);
        processLoadUpdate.setLoadUpdateData(loadUpdateData);

        tmRestApiClient.processRestTrailerNumberUpdate(processLoadUpdate);

        log.debug("updateLoadTrackingNumber completed successfully for appointmentId {} loadId {}", appointmentId, loadId);
    }

    public FindEntitiesResponse fetchLoadByAppointment(List<String> apptNum,String filterOpertaion) {
        log.debug("fetchLoadId for appointment {}", apptNum);
        FindEntitiesResponse findEntitiesResponse = null;
        try {

            log.debug("fetchLoadId details using rest api for appointment number {}", apptNum);
            RestFindEntity restFindEntity = getRestFindEntity(apptNum, filterOpertaion);
            findEntitiesResponse = tmRestApiClient.findLoadByAppointment(restFindEntity);

            return findEntitiesResponse;

        } catch (TmsLoadProcessException e) {
            log.error("fetchLoadId| exception while fetching Load Id {}", e.getMessage());
            throw new TmsLoadProcessException("Error while while fetching Load Id", e);
        } catch (Exception e) {
            log.error("Error calling tms to fetching Load Id {}", e.getMessage());
            throw new TmsLoadProcessException("Error calling tms to fetching Load Id", e);
        }
    }

    @NonNull
    private static RestFindEntity getRestFindEntity(List<String> apptNum, String filterOperation) {
        RestFindEntity restFindEntity = new RestFindEntity();
        List<RestFilterType> filter = new ArrayList<>();
        RestFilterType restFilterType1 = new RestFilterType();
        restFilterType1.setName(SYSTEM_STOP_ID);
        restFilterType1.setOp(filterOperation);
        restFilterType1.setValue(apptNum);
        filter.add(restFilterType1);
        restFindEntity.setFilter(filter);
        RestSelectClauseType select = new RestSelectClauseType();
        List<String> names = Arrays.asList(SYSTEM_LOAD_ID);
        select.setName(names);
        restFindEntity.setSelect(select);
        return restFindEntity;
    }
}

