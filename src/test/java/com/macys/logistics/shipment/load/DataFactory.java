package com.macys.logistics.shipment.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.inbound.*;
import com.macys.logistics.shipment.load.dto.request.Appointment;
import com.macys.logistics.shipment.load.dto.request.Load;
import com.macys.logistics.shipment.load.dto.request.Stop;
import com.macys.logistics.shipment.load.dto.response.YardResponse;
import com.macys.logistics.shipment.load.util.Constants;
import com.macys.logistics.shipment.load.util.DateUtil;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DataFactory {

    //"2020-10-11T10:00:00"
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


    public static String getAppointmentPayloadString() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/AppointmentPayload.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static String getOutboundAppointmentPayloadString() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/OutboundAppointmentPayloadWithFreightType.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static String getOBtmAppointmentPayloadString() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/OBTMAppointmentPayload.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static String getAppointmentPayloadWithNullTrailerString() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/AppointmentPayloadWithTrailerNull.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static String getAppointmentType() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/AppointmentType.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static String getAppointmentStatus() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/AppointmentStatus.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static String getAppointmentSealCondition() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/AppointmentSealCondition.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static List<RestAttachmentMaintainDataType> createTestData() {
        String dummyFileData = "base64-encoded-string";
        String entityKey = "LOAD-123";

        // Category 'NOVNDRCHRGE' and Description "Pallet" -> Should be uploaded
        RestAttachmentMaintainDataType palletDesc = new RestAttachmentMaintainDataType();
        palletDesc.setAttachmentEntityKey(entityKey);
        palletDesc.setSystemAttachmentID("1");
        palletDesc.setDescription("Pallet");
        palletDesc.setFileType("png");
        palletDesc.setFileData(dummyFileData);
        palletDesc.setAttachmentCategoryCode("NOVNDRCHRGE");

        // Category 'NOVNDRCHRGE' and Description "PALLETS" -> Should be uploaded
        RestAttachmentMaintainDataType palletsDesc = new RestAttachmentMaintainDataType();
        palletsDesc.setAttachmentEntityKey(entityKey);
        palletsDesc.setSystemAttachmentID("2");
        palletsDesc.setDescription("PALLETS");
        palletsDesc.setFileType("png");
        palletsDesc.setFileData(dummyFileData);
        palletsDesc.setAttachmentCategoryCode("NOVNDRCHRGE");

        // Null Description -> Should NOT be uploaded
        RestAttachmentMaintainDataType nullDesc = new RestAttachmentMaintainDataType();
        nullDesc.setAttachmentEntityKey(entityKey);
        nullDesc.setSystemAttachmentID("3");
        nullDesc.setDescription(null);
        nullDesc.setFileType("png");
        nullDesc.setFileData(dummyFileData);

        // Category "VNDRCHRGE" and Description "Pallets" -> Should be uploaded
        RestAttachmentMaintainDataType chargebackCat = new RestAttachmentMaintainDataType();
        chargebackCat.setAttachmentEntityKey(entityKey);
        chargebackCat.setSystemAttachmentID("4");
        chargebackCat.setAttachmentCategoryCode("VNDRCHRGE");
        chargebackCat.setFileType("png");
        chargebackCat.setFileData(dummyFileData);
        palletsDesc.setDescription("Pallets");

        // Category "NOVNDRCHRGE" and null Description -> Should NOT be uploaded
        RestAttachmentMaintainDataType noChargeCat = new RestAttachmentMaintainDataType();
        noChargeCat.setAttachmentEntityKey(entityKey);
        noChargeCat.setSystemAttachmentID("5");
        noChargeCat.setAttachmentCategoryCode("NOVNDRCHRGE");
        noChargeCat.setFileType("png");
        noChargeCat.setFileData(dummyFileData);

        // Null Category -> Should NOT be uploaded
        RestAttachmentMaintainDataType nullCat = new RestAttachmentMaintainDataType();
        nullCat.setAttachmentEntityKey(entityKey);
        nullCat.setSystemAttachmentID("6");
        nullCat.setAttachmentCategoryCode(null);
        nullCat.setFileType("png");
        nullCat.setFileData(dummyFileData);

        // Category "VNDRCHRGE" and Description "pallets" -> Should be uploaded
        RestAttachmentMaintainDataType chargepalletsDesc = new RestAttachmentMaintainDataType();
        chargepalletsDesc.setAttachmentEntityKey(entityKey);
        chargepalletsDesc.setSystemAttachmentID("7");
        chargepalletsDesc.setAttachmentCategoryCode("VNDRCHRGE");
        chargepalletsDesc.setFileType("png");
        chargepalletsDesc.setFileData(dummyFileData);
        chargepalletsDesc.setDescription("pallets");

        return List.of(palletDesc, palletsDesc, nullDesc, chargebackCat, noChargeCat, nullCat, chargepalletsDesc);
    }

    public static FindEntitiesResponse getEntitiesResponse() {

        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        StopType stopType = new StopType();
        stopType.setSystemLoadID("5432123");
        stopType.setSystemStopID("53882234");
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        loadType.setCarrierCode("SCNN");
        AddressType address = new AddressType();
        address.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopType.setAddress(address);
        loadType.getStop().add(stopType);
        load.add(loadType);

        entity.getLoad().add(loadType);
        entity.getStop().add(stopType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);
        return findEntitiesResponse;
    }

    public static FindEntitiesResponse getTimezoneResponse() {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<StopType> stops = new ArrayList<StopType>();
        StopType stopType = new StopType();
        stopType.setSystemLoadID("5432123");
        stopType.setSystemStopID("53882234");
        stopType.setStopTypeEnumDescr("Drop");
        AddressType address = new AddressType();
        address.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopType.setAddress(address);
        stops.add(stopType);
        entity.getStop().add(stopType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);

        return findEntitiesResponse;
    }

    public static FindEntitiesResponse getStopApptResponseWithStopsType() throws DatatypeConfigurationException {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        List<StopAppointmentType> stops = new ArrayList<StopAppointmentType>();
        StopAppointmentType stopAppointmentType = new StopAppointmentType();
        AppointmentType appointment = new AppointmentType();
        appointment.setAppointmentFromDateTime(DateUtil.convertStringToXMLDate("2024-04-05T14:00:00"));
        appointment.setAppointmentToDateTime(DateUtil.convertStringToXMLDate("2024-04-05T23:59:00"));
        appointment.setAppointmentStatusEnumVal(AppointmentStatusEnumValType.APP_NOTIFIED);
        stopAppointmentType.setAppointment(appointment);
        stops.add(stopAppointmentType);
        entity.getStopAppointment().add(stopAppointmentType);


        List<StopType> stopTypess = new ArrayList<StopType>();
        StopType stopType = new StopType();
        stopType.setSystemLoadID("5432123");
        stopType.setSystemStopID("53882234");
        stopType.setStopTypeEnumDescr("Drop");
        AddressType address = new AddressType();
        address.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopType.setAddress(address);
        stopTypess.add(stopType);
        entity.getStop().add(stopType);
        loadType.getStop().add(stopType);
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);

        return findEntitiesResponse;
    }

    public static FindEntitiesResponse getStopApptResponseWithSystemApptId() throws DatatypeConfigurationException {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        List<StopAppointmentType> stops = new ArrayList<StopAppointmentType>();
        StopAppointmentType stopAppointmentType = new StopAppointmentType();
        AppointmentType appointment = new AppointmentType();
        appointment.setAppointmentFromDateTime(DateUtil.convertStringToXMLDate("2024-04-05T14:00:00"));
        appointment.setAppointmentToDateTime(DateUtil.convertStringToXMLDate("2024-04-05T23:59:00"));
        appointment.setAppointmentStatusEnumVal(AppointmentStatusEnumValType.APP_NOTIFIED);
        stopAppointmentType.setAppointment(appointment);
        stops.add(stopAppointmentType);
        entity.getStopAppointment().add(stopAppointmentType);


        List<StopType> stopTypess = new ArrayList<StopType>();
        StopType stopType = new StopType();
        stopType.setSystemLoadID("5432123");
        stopType.setSystemStopID("53882234");
        stopType.setStopTypeEnumDescr("Drop");
        stopType.setSystemAppointmentID("12345");
        stopType.setLastComputedArrivalDateTime(DateUtil.convertStringToXMLDate("2024-04-05T14:00:00"));
        AddressType address = new AddressType();
        address.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopType.setAddress(address);
        stopTypess.add(stopType);
        entity.getStop().add(stopType);
        loadType.getStop().add(stopType);
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);

        return findEntitiesResponse;
    }

    public static FindEntitiesResponse getLoadDetails() {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        StopType stopType = new StopType();
        stopType.setSystemLoadID("5432123");
        stopType.setStopStatusEnumVal("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setStopTypeEnumDescr("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setSystemStopID("53882234");
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        loadType.setCarrierCode("SCNN");
        loadType.setCurrentLoadOperationalStatusEnumDescr("Tendered");
        loadType.setCurrentLoadOperationalStatusEnumVal("S_TENDERED");
        loadType.getStop().add(stopType);
        load.add(loadType);

        entity.getLoad().add(loadType);
        entity.getStop().add(stopType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);
        return findEntitiesResponse;
    }

    public static FindEntitiesResponse getExistingSeal() {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setReferenceNumber5("Intact");
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);

        return findEntitiesResponse;
    }

    public static AppointmentPayload getAppointmentPayload() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = Files.lines(Paths.get("src/test/resources/AppointmentPayload.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        AppointmentPayload apptPayload = objectMapper.readValue(message, AppointmentPayload.class);
        return apptPayload;
    }

    public static Load getValidInputLoad() throws ParseException {

        Load load = new Load();

        load.setCarrierCode("JBHNT");
        load.setCarrierType("TL");
        load.setCases(100);
        load.setLoadId("1234");
        load.setFreightTerms("FT_COLLECT");
        load.setTrailerClass("fQ");
        load.setTrailerNumber("1234567");
        load.setEquipmentType("56DRY");
        load.setShipConfirmDate("2021-11-17T10:00:00");

        Stop originStop = new Stop();
        originStop.setShippingLocationCode("SW003063");
        originStop.setShippingLocationType("SFT_DC");

        Stop destStop = new Stop();
        destStop.setShippingLocationCode("NJ006095");
        destStop.setShippingLocationType("STT_HUB");

        Appointment destAppointment = new Appointment();
        destAppointment.setAppointmentNumber("123");
        destAppointment.setAppointmentStartTime(sdf.parse("2020-10-11T10:00:00"));
        destAppointment.setAppointmentEndTime(sdf.parse("2020-10-11T10:00:00"));
        destStop.setAppointment(destAppointment);

        load.setStops(new Stop[]{originStop, destStop});

        return load;
    }


    private static ResponseHeaderType getResponseHeader_Success() {
        ResponseHeaderType responseHeaderType = new ResponseHeaderType();
        responseHeaderType.setCompletedSuccessfully(Boolean.TRUE);

        return responseHeaderType;
    }

    public static ProcessLoadRetrieveResponse getProcessLoadRetrieveResponse_Success() throws DatatypeConfigurationException {
        ProcessLoadRetrieveResponse processLoadRetrieveResponse = new ProcessLoadRetrieveResponse();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("1234");
        loadType.setCarrierCode("PPDC");

        ShipmentLegType shipmentLegType = new ShipmentLegType();
        shipmentLegType.setPieces(BigDecimal.ONE);
        shipmentLegType.setSystemDropStopID("5002");
        shipmentLegType.setSystemPickStopID("5001");
        shipmentLegType.setShipmentNumber("15000000000000000001");
        shipmentLegType.setPickupArrivalDateTime(DatatypeFactory.newInstance()
                .newXMLGregorianCalendar("2024-09-24T13:51:51.648+05:30"));

        shipmentLegType.setSystemShipmentID("SH-1234");

        loadType.getShipmentLeg().add(shipmentLegType);
        processLoadRetrieveResponse.setResponseHeader(getResponseHeader_Success());
        processLoadRetrieveResponse.getLoad().add(loadType);


        return processLoadRetrieveResponse;
    }

    public static ProcessTenderAccept getProcessTenderAccept() throws ParseException {
        Load inputLoad = DataFactory.getValidInputLoad();
        String loadStatus = "S_TENDERED";
        ProcessTenderAccept processTenderAccept = new ProcessTenderAccept();
        LoadTenderDataType loadTenderDataType = new LoadTenderDataType();
        loadTenderDataType.setCarrierCode(inputLoad.getCarrierCode());
        loadTenderDataType.setSystemLoadID(inputLoad.getLoadId());
        loadTenderDataType.setCurrentLoadOperationalStatusEnumVal(loadStatus);
        loadTenderDataType.setTenderAcceptedBy("OBTM-tmsload");


        TripTenderDataType tripTenderDataType = new TripTenderDataType();
        tripTenderDataType.setCarrierCode(inputLoad.getCarrierCode());
        tripTenderDataType.setTenderAcceptedBy("OBTM-tmsload");


        processTenderAccept.getLoadTenderData().add(loadTenderDataType);
        return processTenderAccept;
    }


    public static ProcessTrailerActivitiesResponse getProcessTrailerActivitiesResponse_Success() {
        ProcessTrailerActivitiesResponse processTrailerActivitiesResponse = new ProcessTrailerActivitiesResponse();
        ResponseHeaderType responseHeader = new ResponseHeaderType();
        responseHeader.setCompletedSuccessfully(true);
        processTrailerActivitiesResponse.setResponseHeader(responseHeader);
        return processTrailerActivitiesResponse;
    }

    public static ProcessShipmentRemoveResponse processShipmentRemoveResponse(boolean success) {
        ProcessShipmentRemoveResponse processShipmentRemoveResponse = new ProcessShipmentRemoveResponse();
        processShipmentRemoveResponse.setResponseHeader(success ? getResponseHeader_Success() : getResponseHeader_Failure());


        return processShipmentRemoveResponse;
    }

    private static ResponseHeaderType getResponseHeader_Failure() {
        ResponseHeaderType responseHeaderType = new ResponseHeaderType();
        responseHeaderType.setCompletedSuccessfully(Boolean.TRUE);

        ErrorType errorType = new ErrorType();
        errorType.setSystemMessage("error");
        responseHeaderType.getError().add(errorType);

        return responseHeaderType;
    }

    public static ProcessShipmentRemove processShipmentRemove() {
        ProcessShipmentRemove processShipmentRemove = new ProcessShipmentRemove();

        ShipmentKeyInfoType shipmentKeyInfoType = new ShipmentKeyInfoType();
        shipmentKeyInfoType.setSystemShipmentID("SH-123");
        processShipmentRemove.getShipmentKeyInfo().add(shipmentKeyInfoType);

        return processShipmentRemove;
    }


    public static ProcessShipmentOrderCreateResponse processShipmentOrderCreateResponse(boolean success) {
        ProcessShipmentOrderCreateResponse processShipmentOrderCreateResponse = new ProcessShipmentOrderCreateResponse();
        processShipmentOrderCreateResponse.setResponseHeader(success ? getResponseHeader_Success() : getResponseHeader_Failure());

        return processShipmentOrderCreateResponse;
    }


    public static ProcessAssignToLoadResponse processAssignToLoadResponse(boolean success) {
        ProcessAssignToLoadResponse processAssignToLoadResponse = new ProcessAssignToLoadResponse();
        processAssignToLoadResponse.setResponseHeader(success ? getResponseHeader_Success() : getResponseHeader_Failure());

        return processAssignToLoadResponse;
    }

    public static ProcessAssignToLoad processAssignToLoad() {
        ProcessAssignToLoad processAssignToLoad = new ProcessAssignToLoad();

        return processAssignToLoad;
    }


    public static ProcessShipmentOrderCreate getProcessShipmentOrderCreate() {

        ProcessShipmentOrderCreate processShipmentOrderCreate = new ProcessShipmentOrderCreate();
        ShipmentType shipmentType = new ShipmentType();
        shipmentType.setShipmentNumber("12345678");
        processShipmentOrderCreate.getShipment().add(shipmentType);

        return processShipmentOrderCreate;
    }

    public static ProcessShipmentOrderCreateResponse getShipmentCreateResponse(ProcessShipmentOrderCreate processShipmentOrderCreate) {

        ProcessShipmentOrderCreateResponse processShipmentOrderCreateResponse = new ProcessShipmentOrderCreateResponse();
        for (ShipmentType shipmentType : processShipmentOrderCreate.getShipment()) {
            ShipmentLegHeaderType shipmentHeaderType = new ShipmentLegHeaderType();
            shipmentHeaderType.setSystemShipmentLegID("SHL-" + shipmentType.getContainer());

            processShipmentOrderCreateResponse.getShipmentLegHeader().add(shipmentHeaderType);
        }

        return processShipmentOrderCreateResponse;
    }

    public static ProcessAssignToLoadResponse getProcessAssignToLoadResponse() {

        ProcessAssignToLoadResponse processAssignToLoadResponse = new ProcessAssignToLoadResponse();
        ResponseHeaderType responseHeaderType = new ResponseHeaderType();
        responseHeaderType.setCompletedSuccessfully(Boolean.TRUE);
        processAssignToLoadResponse.setResponseHeader(responseHeaderType);

        return processAssignToLoadResponse;
    }

    public static ProcessAssignToLoad getProcessAssignToLoad() {
        ProcessAssignToLoad processAssignToLoad = new ProcessAssignToLoad();
        processAssignToLoad.setSystemLoadID("1234");
        List<String> shipmentIdList = new ArrayList<>();
        shipmentIdList.add("123345678");
        processAssignToLoad.getSystemShipmentLegID().addAll(shipmentIdList);
        return processAssignToLoad;
    }

    public static ProcessStopAppointmentResponse getProcessStopAppointmentResponse() {

        ProcessStopAppointmentResponse processStopAppointmentResponse = new ProcessStopAppointmentResponse();
        ResponseHeaderType responseHeaderType = new ResponseHeaderType();
        responseHeaderType.setCompletedSuccessfully(Boolean.TRUE);
        processStopAppointmentResponse.setResponseHeader(responseHeaderType);

        return processStopAppointmentResponse;
    }

    public static ProcessStopAppointment getProcessStopAppointment() throws DatatypeConfigurationException {
        ProcessStopAppointment processStopAppointment = new ProcessStopAppointment();
        StopAppointmentType stopAppointmentType = new StopAppointmentType();
        stopAppointmentType.setSystemLoadID("53882212");
        stopAppointmentType.setSystemStopID("53882234");

        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setAppointmentStatusEnumVal(AppointmentStatusEnumValType.APP_NULL);
        XMLGregorianCalendar xgc = DateUtil.convertStringToXMLDate("2024-04-05T14:00:00");
        appointmentType.setAppointmentFromDateTime(xgc);
        appointmentType.setAppointmentToDateTime(DateUtil.scheduledToArrivalTime(xgc));

        stopAppointmentType.setAppointment(appointmentType);

        processStopAppointment.getStopAppointment().add(stopAppointmentType);
        return processStopAppointment;
    }

    public static AppointmentPayload getAppointmentYardArea() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = DataFactory.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("AppointmentYardAreaId.json");
        AppointmentPayload apptPayload = objectMapper.readValue(inputStream, AppointmentPayload.class);
        return apptPayload;
    }

    public static String getAppointmentPayloadUnloadComplete() throws IOException {
        ClassLoader classLoader = DataFactory.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("AppointmentYardAreaId.json");
        String message = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
        return message;
    }


    public static FindEntitiesResponse getLoad() {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setReferenceNumber6("1466");
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);

        return findEntitiesResponse;
    }

    public static FindEntitiesResponse getDoocDoorUpdatedLoad() {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setId("5432123");
        loadType.setSystemLoadID("5432123");
        loadType.setReferenceNumber6("Moved out of the door..");
        ReferenceNumberType referenceNumberType = new ReferenceNumberType();
        referenceNumberType.setReferenceNumberTypeCode("DOOR_DOCK_NBR");
        referenceNumberType.setSystemReferenceNumberID("40901089");
        loadType.getReferenceNumberStructure().add(referenceNumberType);
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);

        return findEntitiesResponse;
    }

    public static ProcessLoadRetrieveResponse getProcessLoadRetrieveResponseForDoorDock() {
        ProcessLoadRetrieveResponse processLoadRetrieveResponse = new ProcessLoadRetrieveResponse();
        ReferenceNumberType referenceNumberType = new ReferenceNumberType();
        referenceNumberType.setReferenceNumberTypeCode("DOOR_DOCK_NBR");
        referenceNumberType.setSystemReferenceNumberID("40901089");
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        processLoadRetrieveResponse.getLoad().add(loadType);
        processLoadRetrieveResponse.getLoad().get(0).getReferenceNumberStructure().add(referenceNumberType);

        return processLoadRetrieveResponse;


    }


    public static String getAppointmentPayloadUnloading() throws IOException {
        ClassLoader classLoader = DataFactory.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("AppointmentYardAreaIdUnloading.json");
        String message = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
        return message;
    }

    public static String getAppointmentDestLocn() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/AppointmentDestLocn.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static FindEntitiesResponse getTrueStopResponse() throws DatatypeConfigurationException {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        List<StopAppointmentType> stops = new ArrayList<StopAppointmentType>();
        StopAppointmentType stopAppointmentType = new StopAppointmentType();
        AppointmentType appointment = new AppointmentType();
        appointment.setAppointmentFromDateTime(DateUtil.convertStringToXMLDate("2024-04-05T14:00:00"));
        appointment.setAppointmentToDateTime(DateUtil.convertStringToXMLDate("2024-04-05T23:59:00"));
        appointment.setAppointmentStatusEnumVal(AppointmentStatusEnumValType.APP_NOTIFIED);
        appointment.setAppointmentNumber("53882234");
        appointment.setId("123456");
        stopAppointmentType.setAppointment(appointment);
        StopType stopType = new StopType();
        stopType.setStopStatusEnumVal("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setStopTypeEnumDescr("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setSystemStopID("53882234");
        AddressType address = new AddressType();
        address.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopType.setAddress(address);
        stops.add(stopAppointmentType);
        entity.getStopAppointment().add(stopAppointmentType);
        entity.getStop().add(stopType);
        loadType.getStop().add(stopType);
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);

        return findEntitiesResponse;
    }


    public static FindEntitiesResponse getDateCompareStopResponse() throws DatatypeConfigurationException {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        List<StopAppointmentType> stops = new ArrayList<StopAppointmentType>();
        StopAppointmentType stopAppointmentType = new StopAppointmentType();
        AppointmentType appointment = new AppointmentType();
        appointment.setAppointmentFromDateTime(DateUtil.convertStringToXMLDate("2024-04-05T11:00:00"));
        appointment.setAppointmentToDateTime(DateUtil.convertStringToXMLDate("2024-04-05T23:59:00"));
        appointment.setAppointmentStatusEnumVal(AppointmentStatusEnumValType.APP_NOTIFIED);
        appointment.setAppointmentNumber("53882234");
        appointment.setId("123456");
        stopAppointmentType.setAppointment(appointment);
        StopType stopType = new StopType();
        stopType.setStopStatusEnumVal("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setStopTypeEnumDescr("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setSystemStopID("53882234");
        AddressType address = new AddressType();
        address.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopType.setAddress(address);
        stops.add(stopAppointmentType);
        entity.getStopAppointment().add(stopAppointmentType);
        entity.getStop().add(stopType);
        loadType.getStop().add(stopType);
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);
        return findEntitiesResponse;
    }

    public static FindEntitiesResponse getTimeZoneIsNullResponse() throws DatatypeConfigurationException {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        List<StopAppointmentType> stops = new ArrayList<StopAppointmentType>();
        StopAppointmentType stopAppointmentType = new StopAppointmentType();
        stopAppointmentType.setAppointment(null);
        StopType stopType = new StopType();
        stopType.setStopStatusEnumVal("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setStopTypeEnumDescr("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setSystemStopID("53882234");
        stops.add(stopAppointmentType);
        entity.getStopAppointment().add(stopAppointmentType);
        AddressType address = new AddressType();
        stopType.setAddress(address);
        entity.getStop().add(stopType);
        loadType.getStop().add(stopType);
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);
        return findEntitiesResponse;
    }

    public static FindEntitiesResponse getFetchStopEntitiesResponse() throws DatatypeConfigurationException {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<StopType> stops = new ArrayList<StopType>();
        StopType stopType = new StopType();
        stopType.setSystemLoadID("5432123");
        stopType.setLastComputedArrivalDateTime(DateUtil.convertStringToXMLDate("2024-04-05T14:00:00"));
        stopType.setSystemAppointmentID("12345");
        stops.add(stopType);
        entity.getStop().add(stopType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);

        return findEntitiesResponse;
    }

    public static FindEntitiesResponse getCancelledStopApptResponse() throws DatatypeConfigurationException {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<StopAppointmentType> stops = new ArrayList<StopAppointmentType>();
        StopAppointmentType stopAppointmentType = new StopAppointmentType();
        AppointmentType appointment = new AppointmentType();
        entity.getStopAppointment().add(stopAppointmentType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);
        return findEntitiesResponse;
    }


    public static String getAppointmentPayloadStringCancelledStatus() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/AppointmentPayloadForCancelledStatus.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static AppointmentPayload getAppointmentPayloadStringCancelled() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = DataFactory.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("AppointmentPayloadForCancelledStatus.json");
        AppointmentPayload apptPayload = objectMapper.readValue(inputStream, AppointmentPayload.class);
        return apptPayload;
    }

    public static ProcessLoadRetrieveResponse getCancelledProcessLoadRetrieveResponseForDoorDock() {
        ProcessLoadRetrieveResponse processLoadRetrieveResponse = new ProcessLoadRetrieveResponse();
        ReferenceNumberType referenceNumberType = new ReferenceNumberType();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        processLoadRetrieveResponse.getLoad().add(loadType);
        return processLoadRetrieveResponse;

    }

    public static FindEntitiesResponse getLoadDetailsForCancelledStatus() {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        StopType stopType = new StopType();
        stopType.setSystemLoadID("5432123");
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        loadType.setCarrierCode("SCNN");
        loadType.setCurrentLoadOperationalStatusEnumDescr("Tendered");
        loadType.setCurrentLoadOperationalStatusEnumVal("S_TENDERED");
        loadType.getStop().add(stopType);
        load.add(loadType);

        entity.getLoad().add(loadType);
        entity.getStop().add(stopType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);
        return findEntitiesResponse;
    }


    public static AppointmentPayload getOutboundAppointmentPayload() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = Files.lines(Paths.get("src/test/resources/OutboundAppointmentPayload.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        AppointmentPayload apptPayload = objectMapper.readValue(message, AppointmentPayload.class);
        return apptPayload;
    }

    public static YardResponse getYardResponse() {
        YardResponse expectedResponse = new YardResponse();
        expectedResponse.setYardAreaNbr("2");
        expectedResponse.setLocNbr("3");
        expectedResponse.setYardAreaId("Y2");
        return expectedResponse;
    }

    public static ProcessTender createTestProcessTender() {
        LoadTenderDataType loadTenderDataType = LoadTenderDataType.builder()
                .systemLoadID("123456")
                .build();
        ProcessTender processTender = ProcessTender.builder().autoAcceptFlag(false).autoAcceptControlEnumVal("")
                .loadTenderData(Arrays.asList(loadTenderDataType))
                .build();
        return processTender;
    }


    public static ProcessLoadUpdate createTestProcessLoadUpdate() {
        LoadUpdateDataType loadUpdateData = LoadUpdateDataType.builder()
                .systemLoadID("123456")
                .executionSchedulingOverriddenFlag(true)
                .build();
        ProcessLoadUpdate processLoadUpdate = ProcessLoadUpdate.builder()
                .loadUpdateData(Collections.singletonList(loadUpdateData))
                .build();
        return processLoadUpdate;
    }

    public static ProcessLoadRate createTestProcessLoadRate() {
        LoadRateDataType loadRateDataType = LoadRateDataType.builder().systemLoadID("123456").carrierCode("SCNN").build();
        ProcessLoadRate processLoadRate = ProcessLoadRate.builder().loadRateData(Arrays.asList(loadRateDataType)).build();
        return processLoadRate;
    }

    public static ProcessLoadCancel createTestProcessLoadCancel() {
        ProcessLoadCancel processLoadCancel = ProcessLoadCancel.builder().
                systemLoadID(Arrays.asList("123456")).build();
        return processLoadCancel;
    }

    public static ProcessTenderCancel createTestProcessTenderCancel() {
        LoadTenderDataType loadTenderDataType = LoadTenderDataType.builder()
                .systemLoadID("123456")
                .carrierCode("SCNN")
                .currentLoadOperationalStatusEnumVal("").build();
        ProcessTenderCancel processTenderCancel = ProcessTenderCancel.builder()
                .loadTenderData(Arrays.asList(loadTenderDataType))
                .build();
        return processTenderCancel;
    }


    public static RestFindEntitiesResponse getRestFindEntitiesResponse() throws DatatypeConfigurationException {
        RestFindEntitiesResponse restFindEntitiesResponse = new RestFindEntitiesResponse();
        List<StopType> stopTypes = new ArrayList<StopType>();
        StopType stops = new StopType();
        stops.setSystemLoadID("5432123");
        stops.setSystemStopID("53882234");
        stops.setStopTypeEnumDescr("Drop");
        stops.setPickedFlexibleQuantity1(BigDecimal.valueOf(1.0));
        stopTypes.add(stops);
        restFindEntitiesResponse.setData(stopTypes);
        return restFindEntitiesResponse;
    }

    public static LoadRestFindEntitiesResponse getLoadRestFindEntitiesResponse() throws DatatypeConfigurationException {
        LoadRestFindEntitiesResponse restFindEntitiesResponse = new LoadRestFindEntitiesResponse();
        List<LoadType> loadTypes = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setSealNumber("1234");
        loadType.setServiceCode("SCNN");
        loadTypes.add(loadType);

        restFindEntitiesResponse.setData(loadTypes);
        return restFindEntitiesResponse;
    }

    public static FindEntitiesResponse getRestEntitiesResponse() {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        List<StopType> stops = new ArrayList<StopType>();
        StopType stopType = new StopType();
        stopType.setSystemLoadID("5432123");
        stopType.setSystemStopID("53882234");
        stopType.setStopTypeEnumDescr("Drop");
        AddressType address = new AddressType();
        address.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopType.setAddress(address);
        stops.add(stopType);
        entity.getStop().add(stopType);
        loadType.getStop().add(stopType);
        load.add(loadType);
        entity.getLoad().add(loadType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);

        return findEntitiesResponse;
    }

    public static RestFindEntity getRestFindEntity() {
        RestFindEntity restFindEntity = new RestFindEntity();
        List<RestFilterType> filter = new ArrayList<>();
        RestFilterType restFilterType1 = new RestFilterType();
        restFilterType1.setName("SYSTEM_STOP_ID");
        restFilterType1.setOp(Constants.EQ);
        List<String> apps = Arrays.asList("5432123");
        restFilterType1.setValue(apps);
        filter.add(restFilterType1);
        restFindEntity.setFilter(filter);
        return restFindEntity;
    }

    public static RestProcessShipmentOrderCreate getRestProcessShipmentOrderCreate() {
        RestProcessShipmentOrderCreate processShipmentOrderCreate = new RestProcessShipmentOrderCreate();
        RestShipmentType shipmentType = new RestShipmentType();
        shipmentType.setShipmentNumber("12345678");
        processShipmentOrderCreate.getShipment().add(shipmentType);
        return processShipmentOrderCreate;
    }

    public static RestProcessTrailerActivities getRestProcessTrailerActivities() {
        RestProcessTrailerActivities restProcessTrailerActivities = new RestProcessTrailerActivities();
        restProcessTrailerActivities.getTrailerActivity().add(new RestTrailerActivityType());
        restProcessTrailerActivities.getTrailerActivity().get(0).setSystemLoadID("1234");
        restProcessTrailerActivities.getTrailerActivity().get(0).setStopSequenceNumber(1);
        restProcessTrailerActivities.getTrailerActivity().get(0).setTrailerActivityTypeEnumVal("EC_DRIVER_CHECKOUT");
        restProcessTrailerActivities.getTrailerActivity().get(0).setReasonCode("TRNT");
        restProcessTrailerActivities.getTrailerActivity().get(0).setActivityDateTime("2021-11-17T10:00:00");

        return restProcessTrailerActivities;
    }

    public static LoadType fetchLoad() throws DatatypeConfigurationException {


        StopType stopType = new StopType();
        stopType.setSystemLoadID("342322");
        stopType.setSystemStopID("53882234");
        stopType.setStopTypeEnumDescr("Drop");
        stopType.setLastComputedArrivalDateTime(DateUtil.convertStringToXMLDate("2024-04-05T14:00:00"));
        stopType.setStopSequenceNumber(2);
        stopType.setSystemAppointmentID("539034");
        AddressType address = new AddressType();
        address.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopType.setAddress(address);

        ShipmentLegType shipmentLeg = new ShipmentLegType();
        shipmentLeg.setSystemShipmentLegID("144606103");
        shipmentLeg.setSystemShipmentID("9074984");
        shipmentLeg.setShipmentNumber("SH-009074984");
        shipmentLeg.setSystemLoadID("5432123");

        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("342322");
        loadType.setSealNumber("1234");
        loadType.setServiceCode("SCNN");
        loadType.setId("342322");
        loadType.setTotalPieces(BigDecimal.valueOf(100));
        loadType.setCurrentLoadOperationalStatusEnumVal("S_OPEN");
        loadType.setTrailerNumber("Test123");
        loadType.setCarrierCode("SCNN");
        loadType.setReferenceNumber5("1234");
        loadType.setReferenceNumber6("Test1234");
        loadType.getStop().add(stopType);
        loadType.getShipmentLeg().add(shipmentLeg);

        List<ReferenceNumberType> referenceNumberStructure = new ArrayList<>();
        ReferenceNumberType referenceNumberType = new ReferenceNumberType();
        referenceNumberType.setSystemReferenceNumberID("123");
        referenceNumberType.setReferenceNumberTypeCode("TEST");

        return loadType;
    }


    public static Map<String, String> getSealConditionAllowedStatusCodes() {
        Map<String, String> sealConditionStatusCodes = new HashMap<String, String>();
        sealConditionStatusCodes.put("UNLOADING", "UNLOADING");
        sealConditionStatusCodes.put("READY_FOR_UNLOAD", "READY FOR UNLOAD");
        return sealConditionStatusCodes;
    }


    public static String getOutboundCancelledAppointmentPayloadString() throws IOException {
        String apptPayload = Files.lines(Paths.get("src/test/resources/OutboundCancelledAppointmentPayload.json"), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        return apptPayload;
    }

    public static AppointmentPayload getOutboundAppointmentPayloadStringCancelled() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = DataFactory.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("OutboundCancelledAppointmentPayload.json");
        AppointmentPayload apptPayload = objectMapper.readValue(inputStream, AppointmentPayload.class);
        return apptPayload;
    }

    public static FindEntitiesResponse getLoadforDoublePupDetails() throws DatatypeConfigurationException {
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        List<UIEntityType> entities = new ArrayList<UIEntityType>();
        UIEntityType entity = new UIEntityType();
        List<LoadType> load = new ArrayList<LoadType>();

        StopType stopType = new StopType();
        stopType.setSystemLoadID("5432123");
        stopType.setStopStatusEnumVal("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setStopTypeEnumDescr("Drop");
        stopType.setStopTypeEnumVal("Drop");
        stopType.setSystemStopID("53882234");
        stopType.setLastComputedArrivalDateTime(DateUtil.convertStringToXMLDate("2024-04-05T14:00:00"));
        stopType.setStopSequenceNumber(1);
        stopType.setSystemAppointmentID("539034");
        AddressType address = new AddressType();
        address.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopType.setAddress(address);
        StopType stopTypeTwo = new StopType();
        stopTypeTwo.setSystemLoadID("5432123");
        stopTypeTwo.setStopStatusEnumVal("Drop");
        stopTypeTwo.setStopTypeEnumVal("Drop");
        stopTypeTwo.setStopTypeEnumDescr("Drop");
        stopTypeTwo.setStopTypeEnumVal("Drop");
        stopTypeTwo.setSystemStopID("53882235");
        stopTypeTwo.setLastComputedArrivalDateTime(DateUtil.convertStringToXMLDate("2024-04-05T14:00:00"));
        stopTypeTwo.setStopSequenceNumber(1);
        stopTypeTwo.setSystemAppointmentID("539034");
        AddressType addressType = new AddressType();
        addressType.setTimeZoneOffset(BigDecimal.valueOf(-3));
        stopTypeTwo.setAddress(addressType);


        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("5432123");
        loadType.setId("5432123");
        loadType.setCarrierCode("SCNN");
        loadType.setCurrentLoadOperationalStatusEnumDescr("Tendered");
        loadType.setCurrentLoadOperationalStatusEnumVal("S_TENDERED");
        loadType.getStop().add(stopType);
        loadType.getStop().add(stopTypeTwo);
        load.add(loadType);

        ShipmentLegType shipmentLeg = new ShipmentLegType();
        shipmentLeg.setSystemShipmentLegID("144606103");
        shipmentLeg.setSystemShipmentID("9074984");
        shipmentLeg.setShipmentNumber("SH-009074984");
        shipmentLeg.setSystemLoadID("5432123");
        shipmentLeg.setSystemDropStopID("53882234");

        ShipmentLegType shipmentLegOne = new ShipmentLegType();
        shipmentLegOne.setSystemShipmentLegID("144606104");
        shipmentLegOne.setSystemShipmentID("9074985");
        shipmentLegOne.setShipmentNumber("SH-009074985");
        shipmentLegOne.setSystemLoadID("5432123");
        shipmentLegOne.setSystemDropStopID("53882235");

        loadType.getShipmentLeg().add(shipmentLeg);
        loadType.getShipmentLeg().add(shipmentLegOne);

        entity.getLoad().add(loadType);
        entity.getStop().add(stopType);
        entities.add(entity);
        findEntitiesResponse.setEntity(entities);
        return findEntitiesResponse;
    }

    public static ShipmentResponse getShipmentResponse() {
        ShipmentResponse response = new ShipmentResponse();
        List<ShipmentData> data = new ArrayList<>();
        ShipmentData shipmentData = new ShipmentData();
        shipmentData.setShipmentNumber("SH-009074984");
        List<ReferenceNumberStructure> referenceNumberStructure = new ArrayList<>();
        ReferenceNumberStructure refernce = new ReferenceNumberStructure();
        refernce.setSystemReferenceNumberID("44892791");
        refernce.setReferenceNumberTypeCode("OB_SHIP_APPT");
        refernce.setReferenceNumber("53882234");
        referenceNumberStructure.add(refernce);
        shipmentData.setReferenceNumberStructure(referenceNumberStructure);
        data.add(shipmentData);
        response.setData(data);
        return response;

    }

    public static RestProcessShipmentOrderDelete getRestProcessShipmentOrderDelete() {
        RestProcessShipmentOrderDelete shipmentOrderDelete = new RestProcessShipmentOrderDelete();
        RestShipmentDelete restShipmentDelete = new RestShipmentDelete();
        restShipmentDelete.setShipmentNumber("12345678");
        shipmentOrderDelete.setShipment(Collections.singletonList(restShipmentDelete));
        return shipmentOrderDelete;
    }

    public static ShipmentEnterpriseMessage getShipmentEnterprisePayload() {
        ShipmentEnterpriseMessage shipmentEnterpriseMessage = new ShipmentEnterpriseMessage();
        shipmentEnterpriseMessage.setShipmentNumber(71L);
        shipmentEnterpriseMessage.setEventType("CREATE");
        ShipmentDetails shipmentDetails = new ShipmentDetails();
        shipmentDetails.setVendorNumber(501);
        shipmentDetails.setVendorDunsNumber(28966L);
        shipmentDetails.setVendorName("BuviFOM#5");
        shipmentDetails.setReadyToShipDate("2025-01-17");
        shipmentDetails.setTotalCube(7.0);
        shipmentDetails.setTotalWeight(100.0);
        shipmentDetails.setTotalCarton(20L);
        shipmentDetails.setTotalPallet(12L);
        shipmentDetails.setFreightClassName("92.5");
        shipmentDetails.setFilledQuantity(12L);

        Origin origin = new Origin();
        origin.setLocationId("40.7512-73.9954");
        origin.setLocationName("dunwoody");
        origin.setAddressLine("214 West 34th Street");
        shipmentDetails.setOrigin(origin);

        Destination destination = new Destination();
        destination.setLocationAbbreviation("SC");
        destination.setLocationNbr(7);
        shipmentDetails.setDestination(destination);

        PurchaseOrders purchaseOrders = new PurchaseOrders();
        purchaseOrders.setDivisionNumber(71);
        purchaseOrders.setDepartmentNumber(357);
        purchaseOrders.setInDcDate("11/07/2024");
        shipmentDetails.setPurchaseOrders(Collections.singletonList(purchaseOrders));
        shipmentEnterpriseMessage.setShipmentDetails(shipmentDetails);

        SpecialRoutingRequestDetails specialRoutingRequestDetails = new SpecialRoutingRequestDetails();
        SRRRequestDetails srrRequestDetails = new SRRRequestDetails();
        srrRequestDetails.setLabelName("Broker Name");
        srrRequestDetails.setUserInputValue("John Smith");
        specialRoutingRequestDetails.setSrrRequestDetails(Collections.singletonList(srrRequestDetails));
        shipmentDetails.setSpecialRoutingRequestDetails(specialRoutingRequestDetails);
        return shipmentEnterpriseMessage;
    }

    public static ShipmentEnterpriseMessage getShipmentEnterprise_carrierSRR() {
        ShipmentEnterpriseMessage shipmentMessage = new ShipmentEnterpriseMessage();
        shipmentMessage.setShipmentNumber(71L);
        shipmentMessage.setEventType("CREATE");
        ShipmentDetails shipmentDetails = new ShipmentDetails();
        shipmentDetails.setVendorNumber(501);
        shipmentDetails.setVendorDunsNumber(28966L);
        shipmentDetails.setVendorName("BuviFOM#5");
        shipmentDetails.setReadyToShipDate("2025-01-17");
        shipmentDetails.setTotalCube(7.0);
        shipmentDetails.setTotalWeight(100.0);
        shipmentDetails.setTotalCarton(20L);
        shipmentDetails.setTotalPallet(12L);
        shipmentDetails.setFreightClassName("Don't Know");
        shipmentDetails.setFilledQuantity(12L);

        Origin origin = new Origin();
        origin.setLocationId("40.7512-73.9954");
        origin.setLocationName("dunwoody");
        origin.setAddressLine("214 West 34th Street");
        shipmentDetails.setOrigin(origin);

        Destination destination = new Destination();
        destination.setLocationAbbreviation("SC");
        destination.setLocationNbr(7);
        shipmentDetails.setDestination(destination);

        PurchaseOrders purchaseOrders = new PurchaseOrders();
        purchaseOrders.setDivisionNumber(71);
        purchaseOrders.setDepartmentNumber(357);
        purchaseOrders.setInDcDate("11/07/2024");
        shipmentDetails.setPurchaseOrders(Collections.singletonList(purchaseOrders));
        shipmentMessage.setShipmentDetails(shipmentDetails);

        SpecialRoutingRequestDetails specialRoutingRequestDetails = new SpecialRoutingRequestDetails();
        specialRoutingRequestDetails.setSrrTypeDescription("Collect Pre-Assigned Carrier");
        specialRoutingRequestDetails.setCarrierScac("SCNN");
        SRRRequestDetails srrRequestDetails = new SRRRequestDetails();
        srrRequestDetails.setLabelName("Carrier Name");
        srrRequestDetails.setUserInputValue("SCHNEIDER TRANSPORT");
        SRRRequestDetails srrRequestDetails1 = new SRRRequestDetails();
        srrRequestDetails1.setLabelName("Trailer Number");
        srrRequestDetails1.setUserInputValue("899267748");
        specialRoutingRequestDetails.setSrrRequestDetails(
                List.of(srrRequestDetails, srrRequestDetails1));
        shipmentDetails.setSpecialRoutingRequestDetails(specialRoutingRequestDetails);
        return shipmentMessage;
    }

    public static ProcessLoadRetrieveResponse getProcessLoadRetrieveResponse_Success_CartonCount() throws DatatypeConfigurationException {
        ProcessLoadRetrieveResponse processLoadRetrieveResponse = new ProcessLoadRetrieveResponse();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("1234");
        loadType.setCarrierCode("PPDC");
        loadType.setLoadDescription("TEST");
        loadType.setTotalPieces(BigDecimal.ONE);

        ShipmentLegType shipmentLegType = new ShipmentLegType();
        shipmentLegType.setPieces(BigDecimal.ONE);
        shipmentLegType.setSystemDropStopID("5002");
        shipmentLegType.setSystemPickStopID("5001");
        shipmentLegType.setShipmentNumber("15000000000000000001");
        shipmentLegType.setPickupArrivalDateTime(DatatypeFactory.newInstance()
                .newXMLGregorianCalendar("2024-09-24T13:51:51.648+05:30"));

        shipmentLegType.setSystemShipmentID("SH-1234");

        loadType.getShipmentLeg().add(shipmentLegType);
        processLoadRetrieveResponse.setResponseHeader(getResponseHeader_Success());
        processLoadRetrieveResponse.getLoad().add(loadType);


        return processLoadRetrieveResponse;
    }

    // ==================== EDI 990 TEST DATA FACTORIES ====================

    /**
     * Creates a valid EDI 990 PubSub message JSON payload with Accept reservationActionCode
     */
    public static String createEdi990AcceptJsonPayload() {
        return "{\n" +
                "  \"messageId\": \"test-msg-accept-12345\",\n" +
                "  \"data\": {\n" +
                "    \"transactionSetHeader\": {\n" +
                "      \"transactionSetIdentifierCode\": \"990\",\n" +
                "      \"transactionSetControlNumber\": \"0001\"\n" +
                "    },\n" +
                "    \"beginningSegment\": {\n" +
                "      \"standardCarrierAlphaCode\": \"ABCD\",\n" +
                "      \"shipmentIdentification\": \"12345678901\",\n" +
                "      \"date\": \"20240211\",\n" +
                "      \"reservationActionCode\": \"A\"\n" +
                "    },\n" +
                "    \"businessInstructionsAndReferenceNumber\": {\n" +
                "      \"referenceIdentificationQualifier\": \"BM\",\n" +
                "      \"referenceIdentification\": \"BOL123456\"\n" +
                "    },\n" +
                "    \"transactionSetTrailer\": {\n" +
                "      \"numberOfIncludedSegments\": \"3\",\n" +
                "      \"transactionSetControlNumber\": \"0001\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    /**
     * Creates a valid EDI 990 PubSub message JSON payload with Decline reservationActionCode
     */
    public static String createEdi990DeclineJsonPayload() {
        return "{\n" +
                "  \"messageId\": \"test-msg-decline-67890\",\n" +
                "  \"data\": {\n" +
                "    \"transactionSetHeader\": {\n" +
                "      \"transactionSetIdentifierCode\": \"990\",\n" +
                "      \"transactionSetControlNumber\": \"0002\"\n" +
                "    },\n" +
                "    \"beginningSegment\": {\n" +
                "      \"standardCarrierAlphaCode\": \"EFGH\",\n" +
                "      \"shipmentIdentification\": \"98765432101\",\n" +
                "      \"date\": \"20240211\",\n" +
                "      \"reservationActionCode\": \"D\",\n" +
                "      \"reservationDeclinedReasonCode\": \"CPU\"\n" +
                "    },\n" +
                "    \"businessInstructionsAndReferenceNumber\": {\n" +
                "      \"referenceIdentificationQualifier\": \"CN\",\n" +
                "      \"referenceIdentification\": \"CARRIER123\"\n" +
                "    },\n" +
                "    \"transactionSetTrailer\": {\n" +
                "      \"numberOfIncludedSegments\": \"3\",\n" +
                "      \"transactionSetControlNumber\": \"0002\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    /**
     * Creates an EDI 990 message with invalid reservation action code
     */
    public static String createEdi990InvalidReservationActionCodePayload() {
        return "{\n" +
                "  \"messageId\": \"test-msg-invalid-99999\",\n" +
                "  \"data\": {\n" +
                "    \"transactionSetHeader\": {\n" +
                "      \"transactionSetIdentifierCode\": \"990\",\n" +
                "      \"transactionSetControlNumber\": \"0003\"\n" +
                "    },\n" +
                "    \"beginningSegment\": {\n" +
                "      \"standardCarrierAlphaCode\": \"INVL\",\n" +
                "      \"shipmentIdentification\": \"99999999999\",\n" +
                "      \"date\": \"20240211\",\n" +
                "      \"reservationActionCode\": \"X\"\n" +
                "    },\n" +
                "    \"transactionSetTrailer\": {\n" +
                "      \"numberOfIncludedSegments\": \"2\",\n" +
                "      \"transactionSetControlNumber\": \"0003\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    /**
     * Creates an EDI 990 message with missing reservation action code
     */
    public static String createEdi990MissingReservationActionCodePayload() {
        return "{\n" +
                "  \"messageId\": \"test-msg-missing-22222\",\n" +
                "  \"data\": {\n" +
                "    \"transactionSetHeader\": {\n" +
                "      \"transactionSetIdentifierCode\": \"990\",\n" +
                "      \"transactionSetControlNumber\": \"0004\"\n" +
                "    },\n" +
                "    \"beginningSegment\": {\n" +
                "      \"standardCarrierAlphaCode\": \"MISS\",\n" +
                "      \"shipmentIdentification\": \"22222222222\",\n" +
                "      \"date\": \"20240211\"\n" +
                "    },\n" +
                "    \"transactionSetTrailer\": {\n" +
                "      \"numberOfIncludedSegments\": \"2\",\n" +
                "      \"transactionSetControlNumber\": \"0004\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    /**
     * Creates an EDI 990 message with missing shipment ID
     */
    public static String createEdi990MissingShipmentIdPayload() {
        return "{\n" +
                "  \"messageId\": \"test-msg-no-shipment-33333\",\n" +
                "  \"data\": {\n" +
                "    \"transactionSetHeader\": {\n" +
                "      \"transactionSetIdentifierCode\": \"990\",\n" +
                "      \"transactionSetControlNumber\": \"0005\"\n" +
                "    },\n" +
                "    \"beginningSegment\": {\n" +
                "      \"standardCarrierAlphaCode\": \"NOID\",\n" +
                "      \"date\": \"20240211\",\n" +
                "      \"reservationActionCode\": \"A\"\n" +
                "    },\n" +
                "    \"transactionSetTrailer\": {\n" +
                "      \"numberOfIncludedSegments\": \"2\",\n" +
                "      \"transactionSetControlNumber\": \"0005\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    /**
     * Creates a malformed JSON payload for testing JSON parsing failures
     */
    public static String createEdi990MalformedJsonPayload() {
        return "{\n" +
                "  \"messageId\": \"test-msg-malformed\",\n" +
                "  \"data\": {\n" +
                "    \"beginningSegment\": {\n" +
                "      \"standardCarrierAlphaCode\": \"MALFORM\",\n" +
                "      \"shipmentIdentification\": \"44444444444\",\n" +
                "      \"reservationActionCode\": \"A\",,\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    /**
     * Creates an EDI 990 message with missing messageId
     */
    public static String createEdi990MissingMessageIdPayload() {
        return "{\n" +
                "  \"data\": {\n" +
                "    \"transactionSetHeader\": {\n" +
                "      \"transactionSetIdentifierCode\": \"990\",\n" +
                "      \"transactionSetControlNumber\": \"0006\"\n" +
                "    },\n" +
                "    \"beginningSegment\": {\n" +
                "      \"standardCarrierAlphaCode\": \"NOMSG\",\n" +
                "      \"shipmentIdentification\": \"55555555555\",\n" +
                "      \"date\": \"20240211\",\n" +
                "      \"reservationActionCode\": \"A\"\n" +
                "    },\n" +
                "    \"transactionSetTrailer\": {\n" +
                "      \"numberOfIncludedSegments\": \"2\",\n" +
                "      \"transactionSetControlNumber\": \"0006\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    /**
     * Creates an EDI 990 message with null data section
     */
    public static String createEdi990NullDataPayload() {
        return "{\n" +
                "  \"messageId\": \"test-msg-null-data-66666\",\n" +
                "  \"data\": null\n" +
                "}";
    }

    /**
     * Creates an EDI 990 Accept message with all schema fields populated
     */
    public static String createEdi990AcceptWithFullDetailsPayload() {
        return "{\n" +
                "  \"messageId\": \"test-msg-full-accept-77777\",\n" +
                "  \"data\": {\n" +
                "    \"transactionSetHeader\": {\n" +
                "      \"transactionSetIdentifierCode\": \"990\",\n" +
                "      \"transactionSetControlNumber\": \"0007\"\n" +
                "    },\n" +
                "    \"beginningSegment\": {\n" +
                "      \"standardCarrierAlphaCode\": \"FULL\",\n" +
                "      \"shipmentIdentification\": \"77777777777\",\n" +
                "      \"date\": \"20240211\",\n" +
                "      \"reservationActionCode\": \"A\"\n" +
                "    },\n" +
                "    \"businessInstructionsAndReferenceNumber\": {\n" +
                "      \"referenceIdentificationQualifier\": \"BM\",\n" +
                "      \"referenceIdentification\": \"BOL-FULL-12345\"\n" +
                "    },\n" +
                "    \"transactionSetTrailer\": {\n" +
                "      \"numberOfIncludedSegments\": \"3\",\n" +
                "      \"transactionSetControlNumber\": \"0007\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    /**
     * Creates an EDI 990 Decline message with reservation declined reason code
     */
    public static String createEdi990DeclineWithFullDetailsPayload() {
        return "{\n" +
                "  \"messageId\": \"test-msg-full-decline-88888\",\n" +
                "  \"data\": {\n" +
                "    \"transactionSetHeader\": {\n" +
                "      \"transactionSetIdentifierCode\": \"990\",\n" +
                "      \"transactionSetControlNumber\": \"0008\"\n" +
                "    },\n" +
                "    \"beginningSegment\": {\n" +
                "      \"standardCarrierAlphaCode\": \"DCLN\",\n" +
                "      \"shipmentIdentification\": \"88888888888\",\n" +
                "      \"date\": \"20240211\",\n" +
                "      \"reservationActionCode\": \"D\",\n" +
                "      \"reservationDeclinedReasonCode\": \"CPU\"\n" +
                "    },\n" +
                "    \"businessInstructionsAndReferenceNumber\": {\n" +
                "      \"referenceIdentificationQualifier\": \"CN\",\n" +
                "      \"referenceIdentification\": \"DECLINE-REF-001\"\n" +
                "    },\n" +
                "    \"transactionSetTrailer\": {\n" +
                "      \"numberOfIncludedSegments\": \"3\",\n" +
                "      \"transactionSetControlNumber\": \"0008\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

}
