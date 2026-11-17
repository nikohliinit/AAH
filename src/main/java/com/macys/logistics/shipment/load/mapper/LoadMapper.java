package com.macys.logistics.shipment.load.mapper;

import static com.macys.logistics.shipment.load.util.DateUtil.getDeliveryFromDateTime;
import static com.macys.logistics.shipment.load.util.DateUtil.getDeliveryToDateTime;
import static com.macys.logistics.shipment.load.util.DateUtil.getPickupFromDateTime;
import static com.macys.logistics.shipment.load.util.DateUtil.getPickupToDateTime;
import static com.macys.logistics.shipment.load.util.DateUtil.getSimpleDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import com.macys.logistics.shipment.load.dto.Shipment;
import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.AddPreBuiltLoad;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ContainerType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.FindEntitiesResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadConfirmType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadCreateDataType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadTenderDataType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessAssignToLoad;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadConfirm;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadRetrieve;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessLoadRetrieveResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessTenderAccept;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessTrailerActivities;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ReferenceNumberType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestFindEntitiesResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShipmentType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.StopType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.TrailerActivityType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.UIEntityType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadRestFindEntitiesResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessStopAppointment;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.AppointmentType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.AppointmentStatusEnumValType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestFindEntity;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestRetrieveChildCollectionType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestShipmentType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.StopAppointmentType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestSelectClauseType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestFilterType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestProcessShipmentOrderCreate;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShipmentLegType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.WeightByFreightClassType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShippingInformationType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.request.Appointment;
import com.macys.logistics.shipment.load.dto.request.Load;
import com.macys.logistics.shipment.load.dto.request.Stop;
import com.macys.logistics.shipment.load.exception.TMSMappingException;
import com.macys.logistics.shipment.load.util.Constants;
import com.macys.logistics.shipment.load.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.CollectionUtils;


@Component
@Log4j2
public class LoadMapper {
    public static final String EC_DRIVER_CHECKOUT = "EC_DRIVER_CHECKOUT";
    
    @Autowired
    private Environment env;

    @Value("${photograbber.api.maxrows:1000}")
    private String photograbberApiMaxRows;
    /**
     * Maps
     *
     * @param inputLoad
     * @return
     */

    public AddPreBuiltLoad mapLoadRequest(Load inputLoad) {
        log.debug("mapLoadRequest| mapping input load");

        AddPreBuiltLoad addPreBuiltLoad = new AddPreBuiltLoad();

        Stop origin = inputLoad.getStops()[0];
        Stop destination = inputLoad.getStops()[1];
        try {
            ShipmentType shipmentType = new ShipmentType();

            shipmentType.setCustomerCode(Constants.DEFAULT_CUSTOMER_CODE);
            shipmentType.setShipmentEntryVersionCode(Constants.DEFAULT_CUSTOMER_CODE);
            shipmentType.setDivisionCode(Constants.DEFAULT_CUSTOMER_CODE);
            shipmentType.setShipmentInputSourceEnumVal(Constants.DEFAULT_SHIPMENT_SOURCE_ENUMVAL);
            shipmentType.setHoldFlag(Boolean.FALSE);
            shipmentType.setFreightTermsEnumVal(Constants.DEFAULT_FREIGHT_TERMS_ENUMVAL);
            shipmentType.setShipmentConsolidationClassCode(Constants.DEFAULT_SHIPMENT_CONSOLIDATION_CODE);

            shipmentType.setIgnoreReferenceNumbersFlag(Boolean.FALSE);
            //mapping the Reference Number value to 0 while creating load with dummy shipment
            List<ReferenceNumberType> shipTypeRefNumList = getReferenceNumberTypeList();
            ReferenceNumberType newShipTypeRefNum = new ReferenceNumberType();
            newShipTypeRefNum.setReferenceNumberTypeCode(Constants.CNTR_NUM_REF_KEY);
            newShipTypeRefNum.setReferenceNumber(Constants.CNTR_NUM_REF_VALUE);

            shipTypeRefNumList.add(newShipTypeRefNum);
            shipmentType.setReferenceNumberStructure(shipTypeRefNumList);

            shipmentType.setShipFromLocationTypeEnumVal(origin.getShippingLocationType());
            shipmentType.setShipFromLocationCode(origin.getShippingLocationCode());

            shipmentType.setShipToLocationTypeEnumVal(destination.getShippingLocationType());
            shipmentType.setShipToLocationCode(destination.getShippingLocationCode());

            XMLGregorianCalendar currDate = DateUtil.getCurrentXMLDate();
            shipmentType.setPickupFromDateTime(getPickupFromDateTime(DateUtil.getSimpleDate(currDate)));
            shipmentType.setPickupToDateTime(getPickupToDateTime(DateUtil.getSimpleDate(currDate)));
            shipmentType.setDeliveryFromDateTime(getDeliveryFromDateTime(DateUtil.getSimpleDate(currDate)));
            shipmentType.setDeliveryToDateTime(getDeliveryToDateTime(DateUtil.getSimpleDate(currDate)));

            shipmentType.setRecomputeDatesFlag(Boolean.FALSE);
            shipmentType.setCommodityCode(inputLoad.getFreightType());

            shipmentType.setIgnoreContainersFlag(Boolean.FALSE);
            shipmentType.setIgnoreThroughPointsFlag(Boolean.TRUE);
            shipmentType.setIgnoreChargeOverridesFlag(Boolean.FALSE);

            // These are needed to not populate Appointment from and to dates at the shipment level per 03/24 findings
            shipmentType.setShipFromAppointmentRequiredFlag(Boolean.FALSE);
            shipmentType.setShipToAppointmentRequiredFlag(Boolean.FALSE);

            //map containers
            shipmentType.getContainer().add(getContainerType(inputLoad.getCases(), null));
            shipmentType.setIgnoreThroughPointsFlag(Boolean.TRUE);
            shipmentType.setIgnoreChargeOverridesFlag(Boolean.FALSE);

            //Load Details
            LoadCreateDataType loadCreateDataType = new LoadCreateDataType();

            loadCreateDataType.setSystemPlanID(Constants.DEFAULT_PLAN_ID);
            loadCreateDataType.setCarrierCode(inputLoad.getCarrierCode());
            loadCreateDataType.setServiceCode(inputLoad.getCarrierType());
            loadCreateDataType.setLoadScheduledDateTime(currDate);
            loadCreateDataType.setLoadCompositionApprovedFlag(Boolean.TRUE);
            loadCreateDataType.setTrailerNumber(inputLoad.getTrailerNumber());
            loadCreateDataType.setLoadScheduleCompletedFlag(Boolean.TRUE);
            loadCreateDataType.setEquipmentTypeCode(inputLoad.getEquipmentType());
            loadCreateDataType.setTractorEquipmentTypeCode(inputLoad.getEquipmentType());

            addPreBuiltLoad.getShipment().add(shipmentType);
            addPreBuiltLoad.setLoadCreateData(loadCreateDataType);

            addPreBuiltLoad.setLoadStatus(Constants.LDS_OPEN_STATUS);
            addPreBuiltLoad.setEnableLoadLevelNotificationFlag(Boolean.TRUE);

        } catch (Exception e) {
            log.error("Error mapping input load {}", e.getMessage());
            throw new TMSMappingException(e);
        }

        return addPreBuiltLoad;
    }
    
    /**
     * Map complete load object to input load
     *
     * @param processLoadRetrieveResponse processLoadRetrieveResponse
     * @return
     */
    public Load mapLoadRetrieveResponse(ProcessLoadRetrieveResponse processLoadRetrieveResponse) {

        final Load load;

        try {
            load = new Load();
            final LoadType loadType = processLoadRetrieveResponse.getLoad().get(0);
            load.setLoadId(loadType.getSystemLoadID());
            load.setLoadStatus(loadType.getCurrentLoadOperationalStatusEnumVal());

            if(loadType.getStop()!=null && !loadType.getStop().isEmpty()) {

                Stop[] stops = new Stop[2];

                stops[0] = mapStopResponse(loadType.getStop().get(0), Boolean.FALSE);
                stops[0].setShippingLocationType(loadType.getShipmentLeg().get(0).getShipFromLocationTypeEnumVal());

                stops[1] = mapStopResponse(loadType.getStop().get(1), Boolean.TRUE);
                stops[1].setShippingLocationType(loadType.getShipmentLeg().get(0).getShipToLocationTypeEnumVal());
                load.setStops(stops);

            }

            load.setCarrierCode(loadType.getCarrierCode());
            load.setCarrierType(loadType.getServiceCode());
            load.setTrailerNumber(loadType.getTrailerNumber());
            load.setTrailerClass("");
            load.setFreightTerms(loadType.getFreightTermsEnumVal());
            load.setLoadStatus(loadType.getCurrentLoadOperationalStatusEnumVal());
            load.setCases(Optional.ofNullable(loadType.getTotalNumberOfShipments()).orElse(1));
            load.setFreightType(Constants.DEFAULT_FREIGHT_CLASS_CODE);


        } catch (Exception e) {
            log.error("Error in mapping load retrieve response {}", e.getMessage());
            throw new TMSMappingException(e);
        }

        return load;
    }

    /**
     * Maps load retrieve request from load id
     *
     * @param loadId loadId
     * @return
     */
    public ProcessLoadRetrieve mapProcessLoadRetrieve(String loadId) {
        ProcessLoadRetrieve processLoadRetrieve = new ProcessLoadRetrieve();
        processLoadRetrieve.getSystemLoadID().add(loadId);

        return processLoadRetrieve;
    }
    
    public Stop mapStopResponse(StopType stopType, Boolean isDropStop) {
        Stop stop = new Stop();
        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber(stopType.getSystemAppointmentID());

        if (Boolean.TRUE.equals(isDropStop)) {
            appointment.setAppointmentNumber(stopType.getSystemStopID());
            
            Optional<XMLGregorianCalendar> optionalLatestDeliveryDt = Optional.ofNullable(stopType.getLatestFromDeliveryDateTime());
            optionalLatestDeliveryDt.ifPresent(latestDelDt -> appointment.setAppointmentStartTime(DateUtil.getSimpleDate(latestDelDt)));
            
            Optional<XMLGregorianCalendar> optionalEarliestDeliveryDt = Optional.ofNullable(stopType.getEarliestToDeliveryDateTime());
            optionalEarliestDeliveryDt.ifPresent(ealiestDelDt -> appointment.setAppointmentEndTime(DateUtil.getSimpleDate(ealiestDelDt)));
        } else {
        	Optional<XMLGregorianCalendar> optionalLatestPickupDt = Optional.ofNullable(stopType.getLatestFromPickupDateTime());
        	optionalLatestPickupDt.ifPresent(latestPickDt -> appointment.setAppointmentStartTime(DateUtil.getSimpleDate(latestPickDt)));
            
        	Optional<XMLGregorianCalendar> optionalEarliestPickupDt = Optional.ofNullable(stopType.getEarliestToPickupDateTime());
        	optionalEarliestPickupDt.ifPresent(earliestPickDt -> appointment.setAppointmentEndTime(DateUtil.getSimpleDate(earliestPickDt)));
        }

        stop.setAppointment(appointment);
        stop.setStopId(stopType.getSystemStopID());
        stop.setShippingLocationCode(stopType.getShippingLocationCode());

        return stop;
    }
    
    private List<ReferenceNumberType> getReferenceNumberTypeList() {
        ArrayList<ReferenceNumberType> shipTypeRefNumList = new ArrayList<>();
        ReferenceNumberType shipTypeRefNum = new ReferenceNumberType();
        shipTypeRefNum.setReferenceNumberTypeCode(Constants.MCY_SHP_TYP_REF_KEY);
        shipTypeRefNum.setReferenceNumber(Constants.OB_THROUGH_MERGE_REF_VALUE);
        shipTypeRefNumList.add(shipTypeRefNum);
        return shipTypeRefNumList;
    }
    
    private ContainerType getContainerType(Integer cases, String containerId) {
        ContainerType containerType = new ContainerType();

        Optional.ofNullable(containerId).ifPresent(containerType::setId);
        containerType.setContainerTypeCode(Constants.DEFAULT_CONTAINER_TYPE_CODE);
        containerType.setQuantity(BigDecimal.valueOf(cases));
        containerType.setContainerShippingInformation(Constants.DEFAULT_SHIPPING_INFORMATION_TYPE);
        containerType.setIgnoreContainerOrientationsFlag(Boolean.TRUE);
        containerType.getWeightByFreightClass().add(Constants.DEFAULT_WEIGHT_BY_FREIGHT_CLASS_TYPE);

        return containerType;
    }
    
    public ProcessTenderAccept mapProcessTenderAccept(Load inputLoad,String loadStatus) {
        ProcessTenderAccept processTenderAccept = new ProcessTenderAccept();
        try {
        LoadTenderDataType loadTenderDataType =  new LoadTenderDataType();
        loadTenderDataType.setCarrierCode(inputLoad.getCarrierCode());
        loadTenderDataType.setSystemLoadID(inputLoad.getLoadId());
        loadTenderDataType.setCurrentLoadOperationalStatusEnumVal(loadStatus);
        loadTenderDataType.setTenderAcceptedBy("OBTM-tmsload");

        processTenderAccept.getLoadTenderData().add(loadTenderDataType);
        } catch (Exception e) {
            log.error("Error in mapping TenderAccept {}", e.getMessage());
            throw new TMSMappingException(e);
        }

        return processTenderAccept;
    }
    
    /**
    *
    * @param systemLoadId systemLoadId
    * @param shipConfirmDate shipConfirmDate
    * @return
    */
   public ProcessTrailerActivities mapProcessTrailerActivities(String systemLoadId, String shipConfirmDate) {
       ProcessTrailerActivities processTrailerActivities = new ProcessTrailerActivities();

       processTrailerActivities.getTrailerActivity().add(new TrailerActivityType());
       processTrailerActivities.getTrailerActivity().get(0).setSystemLoadID(systemLoadId);
       processTrailerActivities.getTrailerActivity().get(0).setStopSequenceNumber(1);
       processTrailerActivities.getTrailerActivity().get(0).setTrailerActivityTypeEnumVal(EC_DRIVER_CHECKOUT);
       processTrailerActivities.getTrailerActivity().get(0).setReasonCode("TRNT");

       XMLGregorianCalendar convertedShipConfirmDate = DateUtil.stringToXMLDateByFormat(shipConfirmDate);
       processTrailerActivities.getTrailerActivity().get(0).setActivityDateTime(convertedShipConfirmDate);

       return processTrailerActivities;
   }

   
   /**
    * Maps load confirm request from load id
    *
    * @param loadId loadId
    * @param shipConfirmDate shipConfirmDate
    * @return
    */
   public ProcessLoadConfirm mapProcessLoadConfirm(String loadId, String shipConfirmDate) {
       XMLGregorianCalendar xmlGregorianCalendar = DateUtil.stringToXMLDateByFormat(shipConfirmDate);

       ProcessLoadConfirm processLoadConfirm = new ProcessLoadConfirm();

       LoadConfirmType loadConfirmType = new LoadConfirmType();
       loadConfirmType.setSystemLoadID(loadId);
       loadConfirmType.setShipConfirmDateTime(xmlGregorianCalendar);
       loadConfirmType.setPrintMasterBillOfLadingFlag(false);

       processLoadConfirm.getLoadConfirmData().add(loadConfirmType);

       return processLoadConfirm;
   }
   
   public ProcessAssignToLoad processAssignToLoadRequest(String loadId, List<String> shipmentIdList) {

       ProcessAssignToLoad processAssignToLoad = new ProcessAssignToLoad();
       processAssignToLoad.setSystemLoadID(loadId);
       processAssignToLoad.getSystemShipmentLegID().addAll(shipmentIdList);
       return processAssignToLoad;
   }

    public FindEntitiesResponse mapFindEntitiesResponse(RestFindEntitiesResponse restresponse) {

        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        findEntitiesResponse.setResponseHeader(restresponse.getResponseHeader());
        findEntitiesResponse.setResponseHeader(restresponse.getResponseHeader());
        findEntitiesResponse.setApiHeader(restresponse.getApiHeader());
        List<StopType> stopTypes = new ArrayList<>();
        List<UIEntityType> uiEntityList = new ArrayList<>();
        UIEntityType uiEntity = new UIEntityType();
        if (!CollectionUtils.isEmpty(restresponse.getData())) {
            stopTypes.add(restresponse.getData().get(0));
        }
        uiEntity.setStop(stopTypes);
        uiEntityList.add(uiEntity);
        findEntitiesResponse.setEntity(uiEntityList);


        return findEntitiesResponse;
    }

    public FindEntitiesResponse mapLoadFindEntitiesResponse(LoadRestFindEntitiesResponse restresponse) {

        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        findEntitiesResponse.setResponseHeader(restresponse.getResponseHeader());
        findEntitiesResponse.setResponseHeader(restresponse.getResponseHeader());
        findEntitiesResponse.setApiHeader(restresponse.getApiHeader());
        List<LoadType> loadTypes = restresponse.getData();
        List<UIEntityType> uiEntityList = new ArrayList<>();
        UIEntityType uiEntity = new UIEntityType();
        uiEntity.setLoad(loadTypes);
        uiEntityList.add(uiEntity);
        findEntitiesResponse.setEntity(uiEntityList);

        return findEntitiesResponse;

        }

    public ProcessStopAppointment mapRestStopAppointmentRequest(AppointmentPayload apptPayload, LoadType load)
            throws DatatypeConfigurationException {

        log.debug("mapRestStopAppointmentRequest for load Id {}", load.getId());
        StopType stop = getStop(apptPayload, load);
        String scheduledArrTs = apptPayload.getAppointmentScheduledArivalTs().
                substring(0, apptPayload.getAppointmentScheduledArivalTs().indexOf('.'));
        BigDecimal timezone = stop.getAddress().getTimeZoneOffset();
        if (timezone != null) {
            log.debug("updateStopAppointment| Timezone offset {}", timezone);
        } else {
            log.warn("updateStopAppointment| No timezone offset found, Cannot update Scheduled arrival Time ");
            return null;
        }




        XMLGregorianCalendar xgc = DateUtil.convertToLocalTimezone(scheduledArrTs, timezone);
        if (stop.getLastComputedArrivalDateTime() != null) {
            XMLGregorianCalendar computedArrivalDt = DateUtil.roundOffMinutes(DateUtil.removeTimezoneXML(stop.getLastComputedArrivalDateTime()));
            boolean isArrivalDtUnchanged = computedArrivalDt != null
                    && computedArrivalDt.compare(DateUtil.removeTimezoneXML(xgc)) == DatatypeConstants.EQUAL;
            boolean isChangeOriginIgnored = apptPayload.getAppointmentChangeOrigin() != null
                    && Constants.IGNORE_CHANGE_ORIGIN.contains(apptPayload.getAppointmentChangeOrigin());
            boolean isLastUpdatedUserIgnored = apptPayload.getAppointmentLastUpdUserIdV() != null
                    && Constants.IGNORE_LAST_UPDATED_USERIDV.contains(apptPayload.getAppointmentLastUpdUserIdV());
            log.debug("updateStopAppointment : isArrivalDtUnchanged {}, isChangeOriginIgnored {}, isLastUpdatedUserIgnored {}", isArrivalDtUnchanged, isChangeOriginIgnored, isLastUpdatedUserIgnored);
            if ((isArrivalDtUnchanged && stop.getSystemAppointmentID() != null) || isChangeOriginIgnored || isLastUpdatedUserIgnored) {
                log.warn("updateStopAppointment| No update to scheduled arrival date and time OR Change origin,last updated user id details {},{}", apptPayload.getAppointmentChangeOrigin(), apptPayload.getAppointmentLastUpdUserIdV());
                return null;
            }
        }

        AppointmentType appointmentType = AppointmentType.builder()
                .appointmentStatusEnumVal(AppointmentStatusEnumValType.APP_NOTIFIED)
                .appointmentFromDateTime(xgc)
                .appointmentToDateTime(DateUtil.scheduledToArrivalTime(xgc))
                .build();
        StopAppointmentType stopAppointmentType = StopAppointmentType.builder()
                .systemLoadID(load.getId())
                .systemStopID(apptPayload.getAppointmentNumber())
                .appointment(appointmentType)
                .build();
        return ProcessStopAppointment.builder()
                .stopAppointment(Arrays.asList(stopAppointmentType)).build();

    }

    public StopType getStop(AppointmentPayload apptPayload, LoadType load) {
        return load.getStop().stream().filter(stops -> stops.getSystemStopID().equals(apptPayload.getAppointmentNumber()))
                .findFirst()
                .orElse(null);
    }

    public LoadType getLoad(FindEntitiesResponse loadDetails) {
        return Optional.ofNullable(loadDetails)
                .map(FindEntitiesResponse :: getEntity)
                .filter(loads -> !loads.isEmpty())
                .map(loads -> loads.get(0))
                .map(loadData -> loadData.getLoad().get(0))
                .orElse(null);
    }
    
    public RestFindEntity createRestFindEntity(String propertyPath, String key, String operator, List<String> value) {
      // Handle null parameters
      if (propertyPath == null ) {
          throw new IllegalArgumentException("propertyPath must not be null");
      }
      

      RestFindEntity restFindEntity = new RestFindEntity();
      Page page = new Page();
      page.setMaxRows(photograbberApiMaxRows);
      restFindEntity.setPage(page);

      // Set up filter
      if(key == null || operator == null || value == null) {
        restFindEntity.setFilter(createFilterList(propertyPath));
      }else {
        restFindEntity.setFilter(createFilterList(key, operator, value));
      }
      // Set up select clause
      RestSelectClauseType selectClause = new RestSelectClauseType();
      selectClause.setName(parseSelectFields(propertyPath));

      // Set up nested collections dynamically
      List<RestRetrieveChildCollectionType> nestedCollections = createNestedCollections(propertyPath);
      selectClause.setCollection(nestedCollections);

      restFindEntity.setSelect(selectClause);

      return restFindEntity;
  }
    
    private List<RestFilterType> createFilterList(String key, String operator, List<String> value) {
      RestFilterType restFilterType = new RestFilterType();
      restFilterType.setName(key);
      restFilterType.setOp(operator);
      restFilterType.setValue(value != null ? value : Collections.emptyList());

      List<RestFilterType> filterList = new ArrayList<>();
      filterList.add(restFilterType);
      return filterList;
  }  

  private List<RestFilterType> createFilterList(String propertyPath) {
      List<RestFilterType> filterList = new ArrayList<>();
      int index = 0;
      while (Boolean.TRUE.equals(env.containsProperty(propertyPath + Constants.NESTED_FILTER_COLLECTION_PREFIX + index + Constants.NESTED_COLLECTION_NAME_SUFFIX))) {
          String collectionName = env.getProperty(propertyPath + Constants.NESTED_FILTER_COLLECTION_PREFIX + index + Constants.NESTED_COLLECTION_NAME_SUFFIX);
          String operator = env.getProperty(propertyPath + Constants.NESTED_FILTER_COLLECTION_PREFIX + index + Constants.NESTED_COLLECTION_OPERATOR_SUFFIX);

          RestFilterType restFilterType = new RestFilterType();
          restFilterType.setName(collectionName != null ? collectionName : "");
          restFilterType.setOp(operator != null ? operator : "");
          if (Constants.UPDATED_DATE_TIME.equalsIgnoreCase(restFilterType.getName())) {
              restFilterType.setValue(getFilterValueForDateTime(restFilterType.getOp()));
          } else {
              String value = env.getProperty(propertyPath + Constants.NESTED_FILTER_COLLECTION_PREFIX + index + Constants.NESTED_COLLECTION_VALUE_SUFFIX);
              restFilterType.setValue(value != null ? Arrays.asList(value.split(",")) : Collections.emptyList());
          }

          filterList.add(restFilterType);
          index++;
      }
      return filterList;
  }

  private List<String> getFilterValueForDateTime(String operator) {
      if (Constants.OPERATOR_GREATER_THAN_EQUAL.equalsIgnoreCase(operator)) {
          return Collections.singletonList(getLastOneHourDateTime());
      } else if (Constants.OPERATOR_LESS_THAN_EQUAL.equalsIgnoreCase(operator)) {
          return Collections.singletonList(getCurrentDateTime());
      } else {
          return Collections.singletonList(getCurrentDate());
      }
  }

  private List<String> parseSelectFields(String propertyPath) {
      return Optional.ofNullable(env.getProperty(propertyPath + ".select.fields"))
              .map(fields -> Arrays.asList(fields.split(",")))
              .orElse(Collections.emptyList());
  }

  private List<RestRetrieveChildCollectionType> createNestedCollections(String propertyPath) {
      List<RestRetrieveChildCollectionType> collectionList = new ArrayList<>();
      int index = 0;

      while (Boolean.TRUE.equals(env.containsProperty(propertyPath + Constants.NESTED_COLLECTION_PREFIX + index + Constants.NESTED_COLLECTION_NAME_SUFFIX))) {
          String collectionName = env.getProperty(propertyPath + Constants.NESTED_COLLECTION_PREFIX + index + Constants.NESTED_COLLECTION_NAME_SUFFIX);
          String fields = env.getProperty(propertyPath + Constants.NESTED_COLLECTION_PREFIX + index + Constants.NESTED_COLLECTION_FIELDS_SUFFIX);

          RestRetrieveChildCollectionType collection = new RestRetrieveChildCollectionType();
          collection.setName(collectionName != null ? collectionName : "");

          if (fields != null) {
              RestSelectClauseType nestedSelect = new RestSelectClauseType();
              nestedSelect.setName(Arrays.asList(fields.split(",")));
              collection.setSelect(nestedSelect);
          } else {
              collection.setSelect(new RestSelectClauseType());  // Set empty select clause if no fields are provided
          }

          collectionList.add(collection);
          index++;
      }

      return collectionList;
  }
  
  public String getCurrentDate() {
    LocalDate currentDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    return currentDate.format(formatter);
  }


    public String getLastOneHourDateTime() {
        String timeFrameStr = env.getProperty("photograbber.pool.time.frame.in.hours");
        Long timeFrame = timeFrameStr != null ? Long.valueOf(timeFrameStr) : Constants.DEFAULT_PHOTOGRABBER_POOL_TIME_FRAME;
        String photograbberTimeZone=getConfiguredTimeZone();
        ZonedDateTime easternDateTime = ZonedDateTime.now(ZoneId.of(photograbberTimeZone)).minusHours(timeFrame);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.PHOTOGRABBER_TIMEZONE_TIMEZONE_PATTERN);
        return easternDateTime.format(formatter);
    }

    public String getCurrentDateTime() {
        String photograbberTimeZone=getConfiguredTimeZone();
        ZonedDateTime easternDateTime = ZonedDateTime.now(ZoneId.of(photograbberTimeZone));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.PHOTOGRABBER_TIMEZONE_TIMEZONE_PATTERN);
        return easternDateTime.format(formatter);
    }


    public String getConfiguredTimeZone(){
        String photograbberTimeZone=env.getProperty("photograbber.pool.timezone");
        return photograbberTimeZone !=null ? photograbberTimeZone : Constants.TIMEZONE_UTC;
    }



    public RestProcessShipmentOrderCreate createShipmentOrderForRest( LoadType load, String apptNbr, int pieces) {
        RestProcessShipmentOrderCreate restProcessShipmentOrderCreate = new RestProcessShipmentOrderCreate();
        restProcessShipmentOrderCreate.setResultContents(Constants.SHIPMENT_LEG_RESULT_TYPE_ENUM_VAL);
        restProcessShipmentOrderCreate.setIgnoreAllShipmentReferenceNumbersFlag(Boolean.FALSE);
        restProcessShipmentOrderCreate.setExecuteAPRatingFlag(Boolean.FALSE);
        restProcessShipmentOrderCreate.setExecuteARRatingFlag(Boolean.FALSE);

        log.debug("restProcessShipmentOrderCreate| creating shipments for appt {} ", apptNbr);

        Shipment shipment = new Shipment();
        ShipmentLegType firstLeg = (load != null && load.getShipmentLeg() != null && !load.getShipmentLeg().isEmpty())
                ? load.getShipmentLeg().get(0)
                : null;

        shipment.setShipFromLocationTypeEnumVal(firstLeg != null ? firstLeg.getShipFromLocationTypeEnumVal() : null);
        shipment.setShipFromLocationCode(firstLeg != null ? firstLeg.getShipFromLocationCode() : null);
        shipment.setShipToLocationTypeEnumVal(firstLeg != null ? firstLeg.getShipToLocationTypeEnumVal() : null);
        shipment.setShipToLocationCode(firstLeg != null ? firstLeg.getShipToLocationCode() : null);

        Date pickupArrivalDate = getSimpleDate(firstLeg != null ? firstLeg.getPickupArrivalDateTime() : null);
        final XMLGregorianCalendar pickupToDateTime = getPickupToDateTime(pickupArrivalDate);
        final XMLGregorianCalendar deliveryFromDateTime = getDeliveryFromDateTime(pickupArrivalDate);
        final XMLGregorianCalendar apptStartTime = getDeliveryToDateTime(pickupArrivalDate);
        final XMLGregorianCalendar pickupFromDateTime = getPickupFromDateTime(pickupArrivalDate);

        //map appointment times
        shipment.setPickupFromDateTime(pickupFromDateTime);
        shipment.setPickupToDateTime(pickupToDateTime);
        shipment.setDeliveryFromDateTime(deliveryFromDateTime);
        shipment.setDeliveryToDateTime(apptStartTime);
        shipment.setCommodityCode(Constants.DEFAULT_FREIGHT_CLASS_CODE);
        shipment.setLength(0.0);
        shipment.setWidth(0.0);
        shipment.setHeight(0.0);
        shipment.setWeight(1.0);
        if (pieces != 0) {
            shipment.setQty(pieces);
        } else {
            shipment.setQty(1);
        }

        restProcessShipmentOrderCreate.getShipment().add(mapRestShipmentTypeRequest(shipment));

        log.debug("restProcessShipmentOrderCreate| created shipment request for appt {} ", apptNbr);

        return restProcessShipmentOrderCreate;
    }

    public RestShipmentType mapRestShipmentTypeRequest(Shipment shipment) {
        RestShipmentType shipmentType = new RestShipmentType();
        try {
            shipmentType = buildRestShipmentType(shipment);
            shipmentType.setDivisionCode(Constants.DEFAULT_CUSTOMER_CODE);
            shipmentType.setCommodityCode(shipment.getCommodityCode());
            //mapping the Reference Number value to 20DigitCntrNum while creating load with actual shipment
            List<ReferenceNumberType> shipTypeRefNumList = getReferenceNumberTypeList();
            shipmentType.setReferenceNumberStructure(shipTypeRefNumList);
        } catch (Exception e) {
            log.error("Error while mapping rest shipment: {}", e.getMessage());
            throw new TMSMappingException(e.getMessage());
        }
        return shipmentType;
    }

    public RestShipmentType buildRestShipmentType(Shipment shipment){
        RestShipmentType restShipmentType = new RestShipmentType();
        restShipmentType.setCustomerCode(Constants.DEFAULT_CUSTOMER_CODE);
        restShipmentType.setLogisticsGroupCode(Constants.DEFAULT_CUSTOMER_CODE);
        restShipmentType.setFreightTermsEnumVal(Constants.DEFAULT_FREIGHT_TERMS_ENUMVAL);
        restShipmentType.setSystemPlanID(Constants.DEFAULT_PLAN_ID);
        restShipmentType.setDeferAPRatingFlag(Boolean.TRUE);
        restShipmentType.setDeferARRatingFlag(Boolean.TRUE);
        restShipmentType.setUseOriginDefaultsFlag(Boolean.TRUE);
        restShipmentType.setUseDestinationDefaultsFlag(Boolean.TRUE);
        restShipmentType.setIgnoreReferenceNumbersFlag(Boolean.FALSE);
        restShipmentType.setIgnoreContainersFlag(Boolean.FALSE);
        restShipmentType.setIgnoreChargeOverridesFlag(Boolean.TRUE);
        restShipmentType.setShipFromLocationTypeEnumVal(shipment.getShipFromLocationTypeEnumVal());
        restShipmentType.setShipToLocationTypeEnumVal(shipment.getShipToLocationTypeEnumVal());
        restShipmentType.setShipFromLocationCode(shipment.getShipFromLocationCode());
        restShipmentType.setShipToLocationCode(shipment.getShipToLocationCode());
        restShipmentType.setPickupFromDateTime(DateUtil.xmlCalendertoStringByFormat(shipment.getPickupFromDateTime(), Constants.PICKUP_AND_DELIVERY_DATE_TIME_FORMAT));
        restShipmentType.setPickupToDateTime(DateUtil.xmlCalendertoStringByFormat(shipment.getPickupToDateTime(), Constants.PICKUP_AND_DELIVERY_DATE_TIME_FORMAT));
        restShipmentType.setDeliveryFromDateTime(DateUtil.xmlCalendertoStringByFormat(shipment.getDeliveryFromDateTime(), Constants.PICKUP_AND_DELIVERY_DATE_TIME_FORMAT));
        restShipmentType.setDeliveryToDateTime(DateUtil.xmlCalendertoStringByFormat(shipment.getDeliveryToDateTime(), Constants.PICKUP_AND_DELIVERY_DATE_TIME_FORMAT));

        restShipmentType.setShipmentConsolidationClassCode(Constants.DEFAULT_SHIPMENT_CONSOLIDATION_CODE);
        List<ContainerType> containers = new ArrayList<>();
        containers.add(getContainerTypeDetails(shipment));
        restShipmentType.setContainer(containers);
        return restShipmentType;
    }

    private ContainerType getContainerTypeDetails(Shipment shipment) {
        ContainerType containerType = new ContainerType();

        containerType.setContainerTypeCode(Constants.DEFAULT_CONTAINER_TYPE_CODE);
        containerType.setQuantity(BigDecimal.valueOf(shipment.getQty()));
        containerType.setHeight(BigDecimal.valueOf(shipment.getHeight()));
        containerType.setWidth(BigDecimal.valueOf(shipment.getWidth()));
        containerType.setLength(BigDecimal.valueOf(shipment.getLength()));
        containerType.setContainerShippingInformation(getShipmentVolume());
        containerType.setIgnoreContainerOrientationsFlag(Boolean.TRUE);
        WeightByFreightClassType weightByFreightClassType = new WeightByFreightClassType();
        weightByFreightClassType.setFreightClassCode(Constants.CONTAINER_FREIGHT_CLASS_CODE);
        weightByFreightClassType.setFreightClassNominalWeight(BigDecimal.valueOf(shipment.getWeight()));
        containerType.getWeightByFreightClass().add(weightByFreightClassType);

        return containerType;
    }

    private ShippingInformationType getShipmentVolume() {
        ShippingInformationType shippingInformationType = new ShippingInformationType();
        shippingInformationType.setVolume(BigDecimal.valueOf(1.0));
        return shippingInformationType;
    }

    public RestProcessShipmentOrderCreate updateShipmentOrderForRest(Integer appointmentNumberOfCartons, String loadId, LoadType load) {
        RestProcessShipmentOrderCreate restProcessShipmentOrderCreate = new RestProcessShipmentOrderCreate();
        RestShipmentType shipmentType = new RestShipmentType();
        if (load.getShipmentLeg() != null && !load.getShipmentLeg().isEmpty() && load.getShipmentLeg().get(0) != null) {
            shipmentType.setShipmentNumber(load.getShipmentLeg().get(0).getShipmentNumber());
        }else{
            return null;
        }
        log.debug("Updating carton count {} in TM for load Id {} and shipment nbr {}", appointmentNumberOfCartons, loadId, shipmentType.getShipmentNumber());
        shipmentType.setIgnoreContainersFlag(Boolean.FALSE);
        List<ContainerType> containers = new ArrayList<>();
        ContainerType containerType = new ContainerType();
        containerType.setContainerTypeCode(Constants.DEFAULT_CONTAINER_TYPE_CODE);
        containerType.setQuantity(BigDecimal.valueOf(appointmentNumberOfCartons));
        containerType.setContainerShippingInformation(getShipmentVolume());
        WeightByFreightClassType weightByFreightClassType = new WeightByFreightClassType();
        weightByFreightClassType.setFreightClassCode(Constants.CONTAINER_FREIGHT_CLASS_CODE);
        weightByFreightClassType.setFreightClassNominalWeight(BigDecimal.valueOf(1.0));
        containerType.getWeightByFreightClass().add(weightByFreightClassType);
        containers.add(containerType);
        shipmentType.setContainer(containers);
        restProcessShipmentOrderCreate.getShipment().add(shipmentType);
        restProcessShipmentOrderCreate.setIgnoreAllShipmentReferenceNumbersFlag(Boolean.TRUE);

        return restProcessShipmentOrderCreate;
    }

    public List<LoadDTO> mapLoadResponse(FindEntitiesResponse loadDetailsByLoadIds,String apptNumber) {
        List<LoadType> loadTypes = loadDetailsByLoadIds.getEntity().stream().map(UIEntityType::getLoad).flatMap(List::stream).toList();
        return loadTypes.stream().map(loadType -> LoadDTO.builder()
                .trailerNumber(String.valueOf(loadType.getTrailerNumber()))
                .dockDoor(loadType.getReferenceNumberStructure().stream()
                        .filter(referenceNumberType -> referenceNumberType.getReferenceNumberTypeCode().equals(Constants.DOOR_DOCK_NBR))
                        .findFirst()
                        .map(ReferenceNumberType::getReferenceNumber)
                        .orElse(null))
                .appointmentNumber(loadType.getStop().stream()
                        .filter(stopType -> stopType.getSystemStopID().equalsIgnoreCase(apptNumber))
                        .findFirst()
                        .map(StopType::getSystemStopID)
                        .orElse(null))
                .loadId(loadType.getSystemLoadID())
                .status(loadType.getCurrentLoadOperationalStatusEnumVal())
                .systemReferenceNumberIdOfExistingDockDoor(getValidatedDockDoorNumberList(loadType))
                .build()).toList();

    }

    private List<String> getValidatedDockDoorNumberList(LoadType load) {
        List<ReferenceNumberType> dockDoorNumberList = Optional.ofNullable(load)
                .map(LoadType::getReferenceNumberStructure)
                .orElse(new ArrayList<>());
        //getting door dock numbers list from response
        return dockDoorNumberList.stream()
                .filter(reference -> reference != null &&
                        reference.getReferenceNumberTypeCode() != null &&
                        reference.getReferenceNumberTypeCode().equals("DOOR_DOCK_NBR"))
                .map(obj -> obj != null ? obj.getSystemReferenceNumberID() : null)
                .filter(Objects::nonNull)
                .toList();

    }
}
