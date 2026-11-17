package com.macys.logistics.shipment.load.mapper;

import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.util.Constants;
import com.macys.logistics.shipment.load.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.request.Appointment;
import com.macys.logistics.shipment.load.dto.request.Load;
import com.macys.logistics.shipment.load.dto.request.Stop;
import com.macys.logistics.shipment.load.exception.TMSMappingException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;
import javax.xml.datatype.DatatypeConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LoadMapperTest {

    @InjectMocks
    TestConstants testConstants;
    
    @Mock
    private Environment env;

    LoadMapper loadMapper = new LoadMapper();
    
    @BeforeEach
    void init() throws IOException {
      ReflectionTestUtils.setField(loadMapper, "env", env );
    }

    @Test
    void loadRequest_mapper() throws Exception {

        Load inputLoad = DataFactory.getValidInputLoad();
        AddPreBuiltLoad addPreBuiltLoad = loadMapper.mapLoadRequest(inputLoad);

        assertEquals(1, addPreBuiltLoad.getShipment().size());

        LoadCreateDataType loadCreateDataType = addPreBuiltLoad.getLoadCreateData();
        assertNotNull(loadCreateDataType);
        assertEquals(testConstants.DEFAULT_PLAN_ID, loadCreateDataType.getSystemPlanID());
        assertTrue(loadCreateDataType.isLoadCompositionApprovedFlag());
        assertTrue(loadCreateDataType.isLoadScheduleCompletedFlag());

        assertEquals(testConstants.LDS_OPEN_STATUS, addPreBuiltLoad.getLoadStatus());
        assertTrue(addPreBuiltLoad.isEnableLoadLevelNotificationFlag());
    }

    @Test
    void loadRequest_shipment_mapper() throws Exception {

        Load inputLoad = DataFactory.getValidInputLoad();
        AddPreBuiltLoad addPreBuiltLoad = loadMapper.mapLoadRequest(inputLoad);

        assertEquals(1, addPreBuiltLoad.getShipment().size());

        //check hardcoded attributes
        ShipmentType shipmentType = addPreBuiltLoad.getShipment().iterator().next();
        assertEquals(1, shipmentType.getContainer().size());
        assertEquals(testConstants.DEFAULT_CUSTOMER_CODE, shipmentType.getCustomerCode());
        assertEquals(testConstants.DEFAULT_CUSTOMER_CODE, shipmentType.getShipmentEntryVersionCode());
        assertEquals(testConstants.DEFAULT_CUSTOMER_CODE, shipmentType.getDivisionCode());
        assertEquals(testConstants.DEFAULT_SHIPMENT_SOURCE_ENUMVAL, shipmentType.getShipmentInputSourceEnumVal());
        assertFalse(shipmentType.isHoldFlag());
        assertEquals(testConstants.DEFAULT_FREIGHT_TERMS_ENUMVAL, shipmentType.getFreightTermsEnumVal());
        assertEquals(testConstants.DEFAULT_SHIPMENT_CONSOLIDATION_CODE, shipmentType.getShipmentConsolidationClassCode());
        assertFalse(shipmentType.isRecomputeDatesFlag());
        assertFalse(shipmentType.isIgnoreContainersFlag());
        assertTrue(shipmentType.isIgnoreThroughPointsFlag());
        assertFalse(shipmentType.isIgnoreChargeOverridesFlag());

        LoadCreateDataType loadCreateDataType = addPreBuiltLoad.getLoadCreateData();
        assertNotNull(loadCreateDataType);
        assertEquals(testConstants.DEFAULT_PLAN_ID, loadCreateDataType.getSystemPlanID());
        assertTrue(loadCreateDataType.isLoadCompositionApprovedFlag());
        assertTrue(loadCreateDataType.isLoadScheduleCompletedFlag());

        assertEquals(testConstants.LDS_OPEN_STATUS, addPreBuiltLoad.getLoadStatus());
        assertTrue(addPreBuiltLoad.isEnableLoadLevelNotificationFlag());
    }

    @Test
    void loadRequest_container_mapper() throws Exception {

        Load inputLoad = DataFactory.getValidInputLoad();
        AddPreBuiltLoad addPreBuiltLoad = loadMapper.mapLoadRequest(inputLoad);

        assertEquals(1, addPreBuiltLoad.getShipment().size());

        //check hardcoded attributes
        ShipmentType shipmentType = addPreBuiltLoad.getShipment().iterator().next();
        assertEquals(1, shipmentType.getContainer().size());
        ContainerType containerType = shipmentType.getContainer().iterator().next();
        assertEquals(testConstants.DEFAULT_CONTAINER_TYPE_CODE, containerType.getContainerTypeCode());
        assertEquals(containerType.getQuantity(), BigDecimal.valueOf(inputLoad.getCases()));
        assertNotNull(containerType.getContainerShippingInformation());
        assertEquals(BigDecimal.valueOf(1000),containerType.getContainerShippingInformation().getVolume());
        //assertEquals(testConstants.DEFAULT_VOLUME, containerType.getContainerShippingInformation().getVolume());
        assertTrue(containerType.isIgnoreContainerOrientationsFlag());

        assertEquals(1, containerType.getWeightByFreightClass().size());
        WeightByFreightClassType weightByFreightClassType = containerType.getWeightByFreightClass().get(0);
        assertEquals(20000, weightByFreightClassType.getFreightClassNominalWeight().intValue());
        // assertEquals(testConstants.DEFAULT_FREIGHT_CLASS_WEIGHT, weightByFreightClassType.getFreightClassNominalWeight());
        assertEquals(testConstants.CONTAINER_FREIGHT_CLASS_CODE, weightByFreightClassType.getFreightClassCode());
    }

    @Test
    void processAssignToLoadRequest_mapper()  {
        List<String> shipmentIdList = new ArrayList<>();
        ProcessAssignToLoad processAssignToLoad = loadMapper.processAssignToLoadRequest("12345",shipmentIdList);

        assertEquals("12345",processAssignToLoad.getSystemLoadID());
    }

    @Test
    void processLoadRetrieve_mapper()  {
        ProcessLoadRetrieve processLoadRetrieve = loadMapper.mapProcessLoadRetrieve("12345");

        assertEquals("12345",processLoadRetrieve.getSystemLoadID().get(0));
    }

    @Test
    void processTenderAccept_mapper() throws Exception {
        Load load = DataFactory.getValidInputLoad();
        ProcessTenderAccept processTenderAccept = loadMapper.mapProcessTenderAccept(load, "S_OPEN");

        assertEquals("OBTM-tmsload",processTenderAccept.getLoadTenderData().get(0).getTenderAcceptedBy());
    }

    @Test
    void processTrailerActivities_mapper() throws Exception {
        ProcessTrailerActivities processTrailerActivities = loadMapper.mapProcessTrailerActivities("12345", "2024-04-20");

        assertEquals("12345", processTrailerActivities.getTrailerActivity().get(0).getSystemLoadID());
    }

    @Test
    void processLoadConfirm_mapper() {
        ProcessLoadConfirm processLoadConfirm = loadMapper.mapProcessLoadConfirm("12345", "2024-04-20");

        assertEquals("12345", processLoadConfirm.getLoadConfirmData().get(0).getSystemLoadID());
    }

    @Test
    void mapStopAppointmentRequest_mapper() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));

        assertEquals("53882234", processStopAppointment.getStopAppointment().get(0).getSystemStopID());
    }

    @Test
    void mapStopAppointmentRequest_appointmentStatus_is_null() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getStopApptResponseWithStopsType();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr(null);
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt, findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals("APP_NOTIFIED", processStopAppointment.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumVal().value());
    }
    @ParameterizedTest
    @ValueSource(strings = {"","PLANNED","SUGGESTED","CONFIRMED","other","Notified"})
    void mapStopAppointmentRequest_appointmentStatus_is_empty(String enumDesc) throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getStopApptResponseWithStopsType();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr("");
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals("APP_NOTIFIED", processStopAppointment.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumVal().value());
    }
    @Test
    void stopResponse_mapper() throws Exception {
        Stop stop = new Stop();
        StopType stopType=new StopType();
        stopType.setSystemStopID("12345");
        stopType.setShippingLocationCode("DC");
        Boolean isDropStop=true;
        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber("12345");
        stop.setAppointment(appointment);
        stop.setStopId(stopType.getSystemLoadID());
        stop.setShippingLocationCode(stopType.getShippingLocationCode());
        stop = loadMapper.mapStopResponse(stopType,isDropStop);
        assertEquals("12345",stop.getStopId());
    }
    @Test
    void stopResponseElse_mapper() throws Exception {
        Stop stop = new Stop();
        StopType stopType=new StopType();
        stopType.setSystemStopID("12345");
        stopType.setShippingLocationCode("DC");
        Boolean isDropStop=false;
        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber("12345");
        stop.setAppointment(appointment);
        stop.setStopId(stopType.getSystemLoadID());
        stop.setShippingLocationCode(stopType.getShippingLocationCode());
        stop = loadMapper.mapStopResponse(stopType,isDropStop);
        assertEquals("12345",stop.getStopId());
    }

    @Test
    void loadRetrieveResponse_mapper() throws Exception {
        ProcessLoadRetrieveResponse processLoadRetrieveResponse = new ProcessLoadRetrieveResponse();
        LoadType loadType = new LoadType();
        loadType.setSystemLoadID("12345");
        loadType.setCurrentLoadOperationalStatusEnumVal("ACTIVE");
        loadType.setCarrierCode("HJNS");
        loadType.setServiceCode("TL");
        loadType.setTrailerNumber("Test");
        loadType.setFreightTermsEnumVal("PREPAID");
        loadType.setTotalNumberOfShipments(5);
        StopType stopType1 = new StopType();
        StopType stopType2 = new StopType();
        loadType.getStop().add(stopType1);
        loadType.getStop().add(stopType2);
        ShipmentLegType shipmentLegType = new ShipmentLegType();
        shipmentLegType.setShipFromLocationTypeEnumVal("FROM");
        shipmentLegType.setShipToLocationTypeEnumVal("TO");
        loadType.getShipmentLeg().add(shipmentLegType);
        processLoadRetrieveResponse.getLoad().add(loadType);

        Load load = loadMapper.mapLoadRetrieveResponse(processLoadRetrieveResponse);

        assertNotNull(load);
        assertEquals("12345", load.getLoadId());
        assertEquals("ACTIVE", load.getLoadStatus());
        assertEquals("HJNS", load.getCarrierCode());
        assertEquals("ACTIVE", load.getLoadStatus());
        assertEquals("TL", load.getCarrierType());
        assertEquals("PREPAID", load.getFreightTerms());
        assertEquals(testConstants.DEFAULT_FREIGHT_CLASS_CODE, load.getFreightType());
        assertNotNull(load.getStops());
        assertEquals(2, load.getStops().length);
    }

    @Test
    void loadRetrieveResponse_Exception() throws Exception {
        ProcessLoadRetrieveResponse processLoadRetrieveResponse = new ProcessLoadRetrieveResponse();
        processLoadRetrieveResponse.getLoad().add(null);

        TMSMappingException thrown = assertThrows(TMSMappingException.class, () -> {
            loadMapper.mapLoadRetrieveResponse(processLoadRetrieveResponse);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void processTenderAccept_Exception() throws Exception {
        ProcessTenderAccept  processTenderAccept = new ProcessTenderAccept();
        processTenderAccept.getLoadTenderData().add(null);
        Load load = null;

        TMSMappingException thrown = assertThrows(TMSMappingException.class, () -> {
            loadMapper.mapProcessTenderAccept(load,"ACTIVE");
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void stopAppointmentRequest_true() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getTrueStopResponse();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr("NOTIFIED");
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals("APP_NOTIFIED", processStopAppointment.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumVal().value());
    }

    @Test
    void stopAppointmentRequest_scheduleArrivalDateEquals() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getDateCompareStopResponse();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr("NOTIFIED");
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals("APP_NOTIFIED", processStopAppointment.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumVal().value());
    }
    @Test
    void stopAppointmentRequest_timeZoneNull() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getTimeZoneIsNullResponse();
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals(null,processStopAppointment);
    }
    @Test
    void stopAppointmentRequest_computedArrivalNull() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getTrueStopResponse();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr("NOTIFIED");
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals("APP_NOTIFIED", processStopAppointment.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumVal().value());
    }
    @Test
    void stopAppointmentRequest_computedArrivalEqual() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        appt.setAppointmentChangeOrigin("XFLO323");
        appt.setAppointmentLastUpdUserIdV("XFLOB151-A");
        FindEntitiesResponse findEntitiesResponse = DataFactory.getStopApptResponseWithSystemApptId();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr("NOTIFIED");
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals(null,processStopAppointment);
    }

    @Test
    void stopAppointmentRequest_change_origin_is_null() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        appt.setAppointmentLastUpdUserIdV("XFLOB151-A");
        FindEntitiesResponse findEntitiesResponse = DataFactory.getStopApptResponseWithSystemApptId();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr("NOTIFIED");
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals(null,processStopAppointment);
    }

    @Test
    void stopAppointmentRequest_last_updated_user_is_null() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        appt.setAppointmentChangeOrigin("XFLO323");
        FindEntitiesResponse findEntitiesResponse = DataFactory.getStopApptResponseWithSystemApptId();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr("NOTIFIED");
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals(null,processStopAppointment);
    }

    @Test
    void mapStopAppointmentRequest_systemAppointmentId_is_not_null() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getStopApptResponseWithSystemApptId();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr(null);
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals("APP_NOTIFIED", processStopAppointment.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumVal().value());
    }

    @Test
    void mapStopAppointmentRequest_notified_apptidNotNull() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getStopApptResponseWithStopsType();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr("Notified");
        findEntitiesResponse.getEntity().get(0).getStop().get(0).setSystemAppointmentID(null);
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertNotEquals("APP_NOTIFIED", processStopAppointment.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumDescr());
    }

    @Test
    void testMapFindEntitiesResponse_ValidResponse() throws DatatypeConfigurationException {
        RestFindEntitiesResponse restFindEntitiesResponse = DataFactory.getRestFindEntitiesResponse();
        FindEntitiesResponse load = loadMapper.mapFindEntitiesResponse(restFindEntitiesResponse);
        assertNotNull(load);
        assertEquals("5432123", restFindEntitiesResponse.getData().get(0).getSystemLoadID());
    }

    @Test
    void testMapLoadFindEntitiesResponse_ValidResponse() throws DatatypeConfigurationException {
        LoadRestFindEntitiesResponse restFindEntitiesResponse = DataFactory.getLoadRestFindEntitiesResponse();
        FindEntitiesResponse load = loadMapper.mapLoadFindEntitiesResponse(restFindEntitiesResponse);
        assertNotNull(load);
        assertEquals("5432123", restFindEntitiesResponse.getData().get(0).getSystemLoadID());
    }
    @Test
    void mapRestStopAppointmentRequest_mapper() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getRestEntitiesResponse();
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));

        assertEquals("53882234", processStopAppointment.getStopAppointment().get(0).getSystemStopID());
    }

    @Test
    void mapRestStopAppointmentRequest_appointmentStatus_is_null() throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getStopApptResponseWithStopsType();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr(null);
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals("APP_NOTIFIED", processStopAppointment.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumVal().value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"","PLANNED","SUGGESTED","CONFIRMED","other","Notified"})
    void mapRestStopAppointmentRequest_appointmentStatus_is_empty(String enumDesc) throws Exception {
        AppointmentPayload appt = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getStopApptResponseWithStopsType();
        findEntitiesResponse.getEntity().get(0).getStopAppointment().get(0).getAppointment().setAppointmentStatusEnumDescr("");
        ProcessStopAppointment processStopAppointment = loadMapper.mapRestStopAppointmentRequest(appt,findEntitiesResponse.getEntity().get(0).getLoad().get(0));
        assertEquals("APP_NOTIFIED", processStopAppointment.getStopAppointment().get(0).getAppointment().getAppointmentStatusEnumVal().value());
    }
    
    @Test
    void testCreateRestFindEntityWithValidProperties() {
       String propertyPath = "test.path";
       String key = "testKey";
       String operator = "eq";
       List<String> value = Collections.singletonList("testValue");

       when(env.getProperty("test.path.select.fields")).thenReturn("field1,field2");
       when(env.containsProperty("test.path.nested.collection[0].name")).thenReturn(true);
       when(env.getProperty("test.path.nested.collection[0].name")).thenReturn("childCollection1");
       when(env.getProperty("test.path.nested.collection[0].fields")).thenReturn("childField1,childField2");
       when(env.containsProperty("test.path.nested.collection[1].name")).thenReturn(false);

       RestFindEntity result = loadMapper.createRestFindEntity(propertyPath, key, operator, value);

       assertNotNull(result);
       assertNotNull(result.getFilter());
       assertEquals(1, result.getFilter().size());
       assertEquals(key, result.getFilter().get(0).getName());
       assertEquals(operator, result.getFilter().get(0).getOp());
       assertEquals(value, result.getFilter().get(0).getValue());

       assertNotNull(result.getSelect());
       assertEquals(Arrays.asList("field1", "field2"), result.getSelect().getName());

       assertNotNull(result.getSelect().getCollection());
       assertEquals(1, result.getSelect().getCollection().size());
       assertEquals("childCollection1", result.getSelect().getCollection().get(0).getName());
       assertEquals(Arrays.asList("childField1", "childField2"), result.getSelect().getCollection().get(0).getSelect().getName());
   }
    
    @Test
    void testCreateRestFindEntityWithFilterProperties() {
       String propertyPath = "test.path";
       String key = null;
       String operator = null;
       List<String> value = Collections.singletonList("testValue");
       
       
       when(env.getProperty("test.path.filter[0].operator")).thenReturn("In");
       when(env.getProperty("test.path.filter[0].name")).thenReturn("childField1,childField2");
       when(env.getProperty("test.path.filter[0].value")).thenReturn("IM,TL");
       when(env.containsProperty("test.path.filter[0].name")).thenReturn(true);
       when(env.containsProperty("test.path.filter[1].name")).thenReturn(false);

       when(env.getProperty("test.path.select.fields")).thenReturn("field1,field2");
       when(env.containsProperty("test.path.nested.collection[0].name")).thenReturn(true);
       when(env.getProperty("test.path.nested.collection[0].name")).thenReturn("childCollection1");
       when(env.getProperty("test.path.nested.collection[0].fields")).thenReturn("childField1,childField2");
       when(env.containsProperty("test.path.nested.collection[1].name")).thenReturn(false);

       RestFindEntity result = loadMapper.createRestFindEntity(propertyPath, key, operator, value);

       assertNotNull(result);
       assertNotNull(result.getFilter());
       assertEquals(1, result.getFilter().size());
       assertEquals("childField1,childField2", result.getFilter().get(0).getName());
       assertEquals("In", result.getFilter().get(0).getOp());
       assertEquals(Arrays.asList("IM","TL"), result.getFilter().get(0).getValue());

       assertNotNull(result.getSelect());
       assertEquals(Arrays.asList("field1", "field2"), result.getSelect().getName());

       assertNotNull(result.getSelect().getCollection());
       assertEquals(1, result.getSelect().getCollection().size());
       assertEquals("childCollection1", result.getSelect().getCollection().get(0).getName());
       assertEquals(Arrays.asList("childField1", "childField2"), result.getSelect().getCollection().get(0).getSelect().getName());
   }
    
    @Test
    void testCreateRestFindEntityWithFilterPropertiesForPhotoGrabberWithCurrentDate() {
       String propertyPath = "test.path";
       String key = null;
       String operator = null;
       String currentDate=getLastOneHourDateTime();
       List<String> value = Collections.singletonList("testValue");
       
       when(env.getProperty("test.path.filter[0].operator")).thenReturn("Ge");
       when(env.getProperty("test.path.filter[0].name")).thenReturn("UpdatedDateTime");
       when(env.containsProperty("test.path.filter[0].name")).thenReturn(true);
       when(env.containsProperty("test.path.filter[1].name")).thenReturn(false);

       when(env.getProperty("test.path.select.fields")).thenReturn("field1,field2");
       when(env.containsProperty("test.path.nested.collection[0].name")).thenReturn(true);
       when(env.getProperty("test.path.nested.collection[0].name")).thenReturn("childCollection1");
       when(env.getProperty("test.path.nested.collection[0].fields")).thenReturn("childField1,childField2");
       when(env.containsProperty("test.path.nested.collection[1].name")).thenReturn(false);
       when(env.getProperty("photograbber.pool.time.frame.in.hours")).thenReturn("1");
       when(env.getProperty("photograbber.pool.timezone")).thenReturn("UTC");

       RestFindEntity result = loadMapper.createRestFindEntity(propertyPath, key, operator, value);

       assertNotNull(result);
       assertNotNull(result.getFilter());
       assertEquals(1, result.getFilter().size());
       assertEquals("UpdatedDateTime", result.getFilter().getFirst().getName());
       assertEquals("Ge", result.getFilter().getFirst().getOp());
       assertEquals(Arrays.asList(currentDate), result.getFilter().getFirst().getValue());

       assertNotNull(result.getSelect());
       assertEquals(Arrays.asList("field1", "field2"), result.getSelect().getName());

       assertNotNull(result.getSelect().getCollection());
       assertEquals(1, result.getSelect().getCollection().size());
       assertEquals("childCollection1", result.getSelect().getCollection().get(0).getName());
       assertEquals(Arrays.asList("childField1", "childField2"), result.getSelect().getCollection().get(0).getSelect().getName());
   }

   @Test
    void testCreateRestFindEntityWithoutNestedCollection() {
       String propertyPath = "test.path";
       String key = "testKey";
       String operator = "ne";
       List<String> value = Arrays.asList("val1", "val2");

       when(env.getProperty("test.path.select.fields")).thenReturn("field1,field2");
       when(env.containsProperty("test.path.nested.collection[0].name")).thenReturn(false);

       RestFindEntity result = loadMapper.createRestFindEntity(propertyPath, key, operator, value);

       assertNotNull(result);
       assertNotNull(result.getFilter());
       assertEquals(1, result.getFilter().size());
       assertEquals(key, result.getFilter().get(0).getName());
       assertEquals(operator, result.getFilter().get(0).getOp());
       assertEquals(value, result.getFilter().get(0).getValue());

       assertNotNull(result.getSelect());
       assertEquals(Arrays.asList("field1", "field2"), result.getSelect().getName());
       assertTrue(result.getSelect().getCollection().isEmpty());
   }

   @Test
    void testCreateRestFindEntityWithNullSelectFields() {
       String propertyPath = "test.path";
       String key = "anotherKey";
       String operator = "lt";
       List<String> value = Collections.singletonList("singleValue");

       when(env.getProperty("test.path.select.fields")).thenReturn(null);
       when(env.containsProperty("test.path.nested.collection[0].name")).thenReturn(false);

       RestFindEntity result = loadMapper.createRestFindEntity(propertyPath, key, operator, value);

       assertNotNull(result);
       assertNotNull(result.getFilter());
       assertEquals(1, result.getFilter().size());
       assertEquals(key, result.getFilter().get(0).getName());
       assertEquals(operator, result.getFilter().get(0).getOp());
       assertEquals(value, result.getFilter().get(0).getValue());

       assertNotNull(result.getSelect());
       assertTrue(result.getSelect().getName().isEmpty());
       assertTrue(result.getSelect().getCollection().isEmpty());
   }
   
    String getCurrentDate() {
        ZonedDateTime easternDateTime = ZonedDateTime.now(ZoneId.of(Constants.TIMEZONE_UTC));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.PHOTOGRABBER_TIMEZONE_TIMEZONE_PATTERN);
        return easternDateTime.format(formatter);
   }

    public String getLastOneHourDateTime() {
        ZonedDateTime easternDateTime = ZonedDateTime.now(ZoneId.of(Constants.TIMEZONE_UTC)).minusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.PHOTOGRABBER_TIMEZONE_TIMEZONE_PATTERN);
        return easternDateTime.format(formatter);
    }

    @Test
    void testCreateShipmentOrderForRest() throws Exception {
        LoadType inputLoad = DataFactory.getProcessLoadRetrieveResponse_Success().getLoad().get(0);
        RestProcessShipmentOrderCreate shipmentOrderCreate = loadMapper.createShipmentOrderForRest(inputLoad, "2.0", 2);
        assertNotNull(shipmentOrderCreate);
        assertEquals(1, shipmentOrderCreate.getShipment().size());
        RestShipmentType restShipmentType = shipmentOrderCreate.getShipment().iterator().next();
        assertEquals(Constants.DEFAULT_CUSTOMER_CODE, restShipmentType.getCustomerCode());
        assertEquals(Constants.SHIPMENT_LEG_RESULT_TYPE_ENUM_VAL, shipmentOrderCreate.getResultContents());
    }

    @Test
    void testUpdateShipmentOrderForRest() throws DatatypeConfigurationException {
        LoadType inputLoad = DataFactory.getProcessLoadRetrieveResponse_Success().getLoad().get(0);
        RestProcessShipmentOrderCreate shipmentOrderCreate = loadMapper.updateShipmentOrderForRest(2, "2.0", inputLoad);
        assertNotNull(shipmentOrderCreate);
        assertEquals(1, shipmentOrderCreate.getShipment().size());
        RestShipmentType restShipmentType = shipmentOrderCreate.getShipment().iterator().next();
        assertEquals("15000000000000000001", restShipmentType.getShipmentNumber());
        assertEquals(BigDecimal.TWO, restShipmentType.getContainer().get(0).getQuantity());
    }
}
