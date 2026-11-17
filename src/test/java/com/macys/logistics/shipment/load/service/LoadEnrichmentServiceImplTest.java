package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.AppointmentSealConditionDetail;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.macys.logistics.shipment.load.util.Constants.EQ;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class LoadEnrichmentServiceImplTest {

    @InjectMocks
    LoadEnrichmentServiceImpl loadEnrichmentService;

    @Mock
    LoadMapper loadMapper;

    @Mock
    private Logger log;

    @Mock
    TMRestApiClient tmRestApiClient;

    @Mock
    private Environment env;

    @BeforeEach
    void init() {

    }

    @Test
    void update_load_refnumupdate_load_refnum() throws IOException, DatatypeConfigurationException {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        String sealType = "seal-type-123";
        AppointmentSealConditionDetail appointmentSealConditionDetail = new AppointmentSealConditionDetail();
        appointmentSealConditionDetail.setAppointmentSealCnd(sealType);
        appointmentSealConditionDetail.setSequenceNbr(1);

        List<AppointmentSealConditionDetail> sealConditionDetailList = Arrays.asList(appointmentSealConditionDetail);
        when(tmRestApiClient.callAppointmentSealCondition(Integer.valueOf(appointmentPayload.getAppointmentNumber())))
                .thenReturn(sealConditionDetailList);
        LoadType load = DataFactory.fetchLoad();
        loadEnrichmentService.addOrUpdateSealCondition(appointmentPayload, "1234567",load);
        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));
    }


    @Test
    void fetch_LoadRetrieveDetails() {

        loadEnrichmentService.fetchLoadRetrieveDetails(eq("1234567"));
        verify(tmRestApiClient, times(1)).processLoadRetrieve(any(ProcessLoadRetrieve.class));
    }

    @Test
    void fetch_LoadRetrieveDetails_exception(){

        doThrow(new TmsLoadProcessException("fetchLoadRetrieveDetails| exception while fetching ProcessLoadRetrieveResponse Details")).
                when(tmRestApiClient).processLoadRetrieve(any(ProcessLoadRetrieve.class));
        assertThrows(TmsLoadProcessException.class, () -> loadEnrichmentService.fetchLoadRetrieveDetails("1234567"));
    }

    @Test
    void process_cancle_load_test() {

        ProcessLoadCancel processLoadCancel = ProcessLoadCancel.builder().
                systemLoadID(List.of("12334")).build();
        loadEnrichmentService.processCancelLoad(processLoadCancel);
        verify(tmRestApiClient, times(1)).processLoadCancel(any(ProcessLoadCancel.class));
    }

    @Test
    void process_cancle_tenderedload_test() {

        LoadTenderDataType loadTenderDataType = LoadTenderDataType.builder()
                .systemLoadID("1234")
                .carrierCode("SCNN")
                .currentLoadOperationalStatusEnumVal("S_TENDERED").build();
        ProcessTenderCancel processTenderCancel = ProcessTenderCancel.builder()
                .loadTenderData(Arrays.asList(loadTenderDataType))
                .build();
        loadEnrichmentService.processCancleTender(processTenderCancel);
        verify(tmRestApiClient, times(1)).processTenderCancel(any(ProcessTenderCancel.class));
    }

    @Test
    void process_cancle_load_test_exception() {

        ProcessLoadCancel processLoadCancel = ProcessLoadCancel.builder().
                systemLoadID(List.of("12334")).build();
        doThrow(new TmsLoadProcessException("cancelLoad| exception while cancelling load")).
                when(tmRestApiClient).processLoadCancel(any(ProcessLoadCancel.class));
        assertThrows(TmsLoadProcessException.class, () -> loadEnrichmentService.processCancelLoad((processLoadCancel)));
    }

    @Test
    void process_cancle_tenderedload_test_exception() {

        LoadTenderDataType loadTenderDataType = LoadTenderDataType.builder()
                .systemLoadID("1234")
                .carrierCode("SCNN")
                .currentLoadOperationalStatusEnumVal("S_TENDERED").build();
        ProcessTenderCancel processTenderCancel = ProcessTenderCancel.builder()
                .loadTenderData(Arrays.asList(loadTenderDataType))
                .build();
        doThrow(new TmsLoadProcessException("tenderCancle| exception while cancelling load")).
                when(tmRestApiClient).processTenderCancel(any(ProcessTenderCancel.class));
        assertThrows(TmsLoadProcessException.class, () -> loadEnrichmentService.processCancleTender((processTenderCancel)));
    }

    @Test
    void testUpdateStopAppointment() throws Exception {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        when(loadMapper.mapRestStopAppointmentRequest(any(AppointmentPayload.class),any(LoadType.class))).thenReturn(DataFactory.getProcessStopAppointment());
        when(tmRestApiClient.processStopAppointment(any(ProcessStopAppointment.class))).thenReturn(DataFactory.getProcessStopAppointmentResponse());
        loadEnrichmentService.updateStopAppointment(appointmentPayload, DataFactory.fetchLoad());
        verify(tmRestApiClient, times(1)).processStopAppointment(any(ProcessStopAppointment.class));
        verify(tmRestApiClient, times(1)).processLoadRate(any(ProcessLoadRate.class));
    }
    
    @Test
    void testUpdateStopAppointment_exceptionHandling() throws Exception {
       
        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();

        LoadType load = DataFactory.fetchLoad();
        when(loadMapper.mapRestStopAppointmentRequest(any(AppointmentPayload.class), any(LoadType.class)))
            .thenReturn(DataFactory.getProcessStopAppointment());
       
        when(tmRestApiClient.processStopAppointment(any(ProcessStopAppointment.class)))
            .thenThrow(new TmsLoadProcessException("Simulated exception for testing"));

        loadEnrichmentService.updateStopAppointment(appointmentPayload, load);

        verify(tmRestApiClient, times(1)).processStopAppointment(any(ProcessStopAppointment.class));
        verify(tmRestApiClient, times(0)).processLoadRate(any(ProcessLoadRate.class));
        verify(tmRestApiClient, times(0)).processTrailerNumberUpdate(any(ProcessLoadUpdate.class));
    }
    
    
    @Test
    void testUpdateStopAppointmentNull() throws Exception {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getFetchStopEntitiesResponse();
        ProcessStopAppointment processStopAppointment=null;
        when(loadMapper.mapRestStopAppointmentRequest(any(AppointmentPayload.class),any(LoadType.class))).thenReturn(processStopAppointment);
        loadEnrichmentService.updateStopAppointment(appointmentPayload,DataFactory.fetchLoad());
        verify(tmRestApiClient, times(0)).processStopAppointment(any(ProcessStopAppointment.class));
        verify(tmRestApiClient, times(0)).processLoadRate(any(ProcessLoadRate.class));
    }

    @Test
    void processLoadUpdate_Success() throws IOException {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        loadEnrichmentService.processLoadUpdate("1234567", appointmentPayload.getAppointmentCarrierTrailerNumber());
        verify(tmRestApiClient, times(1)).processTrailerNumberUpdate(any(ProcessLoadUpdate.class));
    }
    
    @Test
    void processLoadUpdate_on_exception() throws IOException {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        doThrow(new RuntimeException("LoadEnrichmentServiceImpl :: processLoadUpdate :: Error calling tms to update trailer number for load Id - 1234567"))
        .when(tmRestApiClient).processTrailerNumberUpdate(any(ProcessLoadUpdate.class));
        loadEnrichmentService.processLoadUpdate("1234567", appointmentPayload.getAppointmentCarrierTrailerNumber());
        verify(tmRestApiClient, times(1)).processTrailerNumberUpdate(any(ProcessLoadUpdate.class));
    }
    @Test
    void testUpdateStopAppointment_executionSchedulingOverriddenFlag_Success() throws Exception {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        when(loadMapper.mapRestStopAppointmentRequest(any(AppointmentPayload.class),any(LoadType.class))).thenReturn(DataFactory.getProcessStopAppointment());
        doNothing().when(tmRestApiClient).processTrailerNumberUpdate(any(ProcessLoadUpdate.class));
        when(tmRestApiClient.processStopAppointment(any(ProcessStopAppointment.class))).thenReturn(DataFactory.getProcessStopAppointmentResponse());
        loadEnrichmentService.updateStopAppointment(appointmentPayload, DataFactory.fetchLoad());
        verify(tmRestApiClient, times(1)).processStopAppointment(any(ProcessStopAppointment.class));
        verify(tmRestApiClient,times(1)).processTrailerNumberUpdate(any(ProcessLoadUpdate.class));
        verify(tmRestApiClient, times(1)).processLoadRate(any(ProcessLoadRate.class));
    }

    @Test
    void testUpdateStopAppointment_executionSchedulingOverriddenFlag_Null() throws Exception {
        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getFetchStopEntitiesResponse();
        ProcessStopAppointment processStopAppointment=null;
        lenient().when(loadMapper.mapRestStopAppointmentRequest(any(AppointmentPayload.class),any(LoadType.class))).thenReturn(processStopAppointment);
        loadEnrichmentService.updateStopAppointment(appointmentPayload, DataFactory.fetchLoad());
        verify(tmRestApiClient, times(0)).processStopAppointment(any(ProcessStopAppointment.class));
        verify(tmRestApiClient,times(0)).processTrailerNumberUpdate(any(ProcessLoadUpdate.class));
        verify(tmRestApiClient, times(0)).processLoadRate(any(ProcessLoadRate.class));
    }

    @Test
    void testUpdateStopAppointment_executionSchedulingOverriddenFlag_Failure() throws Exception {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        FindEntitiesResponse findEntitiesResponse = DataFactory.getEntitiesResponse();
        when(loadMapper.mapRestStopAppointmentRequest(any(AppointmentPayload.class),any(LoadType.class))).thenReturn(DataFactory.getProcessStopAppointment());
        when(tmRestApiClient.processStopAppointment(any(ProcessStopAppointment.class))).thenReturn(DataFactory.getProcessStopAppointmentResponse());
        loadEnrichmentService.updateStopAppointment(appointmentPayload, DataFactory.fetchLoad());
        verify(tmRestApiClient, times(1)).processStopAppointment(any(ProcessStopAppointment.class));
        verify(tmRestApiClient,times(1)).processTrailerNumberUpdate(any(ProcessLoadUpdate.class));
        verify(tmRestApiClient, times(1)).processLoadRate(any(ProcessLoadRate.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Open","Planned","Tendered","Tender Accepted","Canceled" })
    void processDisplayFlag_Status_Open(String status) throws IOException, DatatypeConfigurationException {

        AppointmentPayload appointmentPayload = DataFactory.getOutboundAppointmentPayload();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadDetails.setCurrentLoadOperationalStatusEnumDescr(status);
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        if("Planned".equals(status))
            verify(tmRestApiClient, times(1)).processTender(any(ProcessTender.class));
        else
            verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void testDisplayFlag_errorCheck() throws IOException,DatatypeConfigurationException{
        LoadType loadDetails = DataFactory.fetchLoad();
        loadDetails.setCurrentLoadOperationalStatusEnumDescr("Planned");

        doThrow(new RuntimeException("dispatchFlagTrigger| exception while calling process tender")).when(tmRestApiClient).processTender(any(ProcessTender.class));
        try{
            loadEnrichmentService.dispatchFlagTrigger(DataFactory.getOutboundAppointmentPayload(), loadDetails );
        }catch (Exception e) {
            verify(log).error(eq("dispatchFlagTrigger| exception while calling process tender"), e.getMessage());
        }
    }

    @Test
     void processDisplayFlag_Status_No()  throws IOException,DatatypeConfigurationException {
        AppointmentPayload appointmentPayload=AppointmentPayload.builder().appointmentDispatchFlag("N").build();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadDetails.setCurrentLoadOperationalStatusEnumDescr("Planned");
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void processDisplayFlag_Status_Check() throws IOException,DatatypeConfigurationException {
        AppointmentPayload appointmentPayload=AppointmentPayload.builder().appointmentDispatchFlag("Y").build();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadDetails.setCurrentLoadOperationalStatusEnumDescr(null);
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void processDisplayFlag_Status_LoadCheck() throws IOException,DatatypeConfigurationException {
        AppointmentPayload appointmentPayload=AppointmentPayload.builder().appointmentDispatchFlag("Y").build();
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, null);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void processDisplayFlag_Status_LoadDetailsCheck() throws IOException,DatatypeConfigurationException{
        AppointmentPayload appointmentPayload=AppointmentPayload.builder().appointmentDispatchFlag("Y").build();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void processDisplayFlag_Status_cancel() throws IOException,DatatypeConfigurationException {
        AppointmentPayload appointmentPayload = DataFactory.getOutboundAppointmentPayload();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadDetails.setCurrentLoadOperationalStatusEnumDescr("cancel");
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void fetch_loadId_feature_toggle_false() {
        when(tmRestApiClient.findLoadID(any(RestFindEntity.class))).thenReturn(DataFactory.getEntitiesResponse());
        loadEnrichmentService.fetchLoadId(Arrays.asList("1234567"),EQ);
        verify(tmRestApiClient, times(0)).restFindEntities(any(RestFindEntity.class));
        verify(tmRestApiClient, times(1)).findLoadID(any(RestFindEntity.class));
    }


    @Test
    void fetch_loadId_feature_toggle_false_exception() {
        doThrow(new TmsLoadProcessException("fetchLoadId| exception while fetching Load Id"))
                .when(tmRestApiClient)
                .findLoadID(any(RestFindEntity.class));

        List<String> loadIds = List.of("1234567");

        assertThrows(
                TmsLoadProcessException.class,
                () -> loadEnrichmentService.fetchLoadId(loadIds, null)
        );
    }

    @Test
    void fetch_loadId_feature_toggle_false_runtimeexception() {
        doThrow(new RuntimeException("fetchLoadId| exception while fetching Load Id"))
                .when(tmRestApiClient)
                .findLoadID(any(RestFindEntity.class));

        List<String> loadIds = List.of("1234567");

        assertThrows(
                RuntimeException.class,
                () -> loadEnrichmentService.fetchLoadId(loadIds, null)
        );
    }

    @Test
     void update_load_refnum_feature_toggle_false() throws IOException, DatatypeConfigurationException {
        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        LoadType load = DataFactory.fetchLoad();
        loadEnrichmentService.addOrUpdateSealCondition(appointmentPayload, "1234567",load);
        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));
    }

    @Test
     void fetch_loadDetails_feature_toggle_false() {
        when(loadMapper.createRestFindEntity(any(),any(),any(),any())).thenReturn(new RestFindEntity());
        loadEnrichmentService.fetchLoadDetails(eq("1234567"));
        verify(tmRestApiClient, times(1)).restFindEntities(any(RestFindEntity.class));
    }

    @Test
    void fetch_loadDetails_feature_toggle_false_exception(){      
        when(loadMapper.createRestFindEntity(any(),any(),any(),any())).thenReturn(new RestFindEntity());
        doThrow(new TmsLoadProcessException("fetchLoadDetails| exception while fetching Load Details")).
                when(tmRestApiClient).restFindEntities(any(RestFindEntity.class));
        assertThrows(TmsLoadProcessException.class, () -> loadEnrichmentService.fetchLoadDetails("1234567"));
    }

    @Test
     void fetch_LoadRetrieveDetails_feature_toggle_false() {
        loadEnrichmentService.fetchLoadRetrieveDetails(eq("1234567"));
        verify(tmRestApiClient, times(1)).processLoadRetrieve(any(ProcessLoadRetrieve.class));
    }

    @Test
    void fetch_LoadRetrieveDetails_feature_toggle_false_exception(){
        doThrow(new TmsLoadProcessException("fetchLoadRetrieveDetails| exception while fetching ProcessLoadRetrieveResponse Details")).
                when(tmRestApiClient).processLoadRetrieve(any(ProcessLoadRetrieve.class));
        assertThrows(TmsLoadProcessException.class, () -> loadEnrichmentService.fetchLoadRetrieveDetails("1234567"));
    }

    @Test
     void process_cancle_load_feature_toggle_false_test() {
        ProcessLoadCancel processLoadCancel = ProcessLoadCancel.builder().
                systemLoadID(List.of("12334")).build();
        loadEnrichmentService.processCancelLoad(processLoadCancel);
        verify(tmRestApiClient, times(1)).processLoadCancel(any(ProcessLoadCancel.class));
    }

    @Test
     void process_cancle_tenderedload_feature_toggle_false_test() {
        LoadTenderDataType loadTenderDataType = LoadTenderDataType.builder()
                .systemLoadID("1234")
                .carrierCode("SCNN")
                .currentLoadOperationalStatusEnumVal("S_TENDERED").build();
        ProcessTenderCancel processTenderCancel = ProcessTenderCancel.builder()
                .loadTenderData(Arrays.asList(loadTenderDataType))
                .build();
        loadEnrichmentService.processCancleTender(processTenderCancel);
        verify(tmRestApiClient, times(1)).processTenderCancel(any(ProcessTenderCancel.class));
    }

    @Test
    void process_cancle_load_test_feature_toggle_false_exception() {
        ProcessLoadCancel processLoadCancel = ProcessLoadCancel.builder().
                systemLoadID(List.of("12334")).build();
        doThrow(new TmsLoadProcessException("cancelLoad| exception while cancelling load")).
                when(tmRestApiClient).processLoadCancel(any(ProcessLoadCancel.class));
        assertThrows(TmsLoadProcessException.class, () -> loadEnrichmentService.processCancelLoad((processLoadCancel)));
    }

    @Test
    void process_cancle_tenderedload_feature_toggle_false_test_exception() {
        LoadTenderDataType loadTenderDataType = LoadTenderDataType.builder()
                .systemLoadID("1234")
                .carrierCode("SCNN")
                .currentLoadOperationalStatusEnumVal("S_TENDERED").build();
        ProcessTenderCancel processTenderCancel = ProcessTenderCancel.builder()
                .loadTenderData(Arrays.asList(loadTenderDataType))
                .build();
        doThrow(new TmsLoadProcessException("tenderCancle| exception while cancelling load")).
                when(tmRestApiClient).processTenderCancel(any(ProcessTenderCancel.class));
        assertThrows(TmsLoadProcessException.class, () -> loadEnrichmentService.processCancleTender((processTenderCancel)));
    }



    @Test
     void processDisplayFlag_Status_Open_feature_toggle_false() throws IOException,DatatypeConfigurationException {
        AppointmentPayload appointmentPayload = DataFactory.getOutboundAppointmentPayload();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void processDisplayFlag_Status_Planned_feature_toggle_false() throws IOException,DatatypeConfigurationException {

        AppointmentPayload appointmentPayload = DataFactory.getOutboundAppointmentPayload();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadDetails.setCurrentLoadOperationalStatusEnumDescr("Planned");
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        verify(tmRestApiClient, times(1)).processTender(any(ProcessTender.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Canceled","Tendered","Tender Accepted"})
     void processDisplayFlag_Status_Tendered__feature_toggle_false() throws IOException,DatatypeConfigurationException {
        AppointmentPayload appointmentPayload = DataFactory.getOutboundAppointmentPayload();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void testDisplayFlag_errorCheck__feature_toggle_false() throws IOException,DatatypeConfigurationException{
        LoadType loadDetails = DataFactory.fetchLoad();
        loadDetails.setCurrentLoadOperationalStatusEnumDescr("Planned");
        doThrow(new RuntimeException("dispatchFlagTrigger| exception while calling process tender")).when(tmRestApiClient).processTender(any(ProcessTender.class));
        try{
            loadEnrichmentService.dispatchFlagTrigger(DataFactory.getOutboundAppointmentPayload(), loadDetails );
        }catch (Exception e) {
            verify(log).error(eq("dispatchFlagTrigger| exception while calling process tender"), e.getMessage());
        }
    }

    @Test
     void processDisplayFlag_Status_No__feature_toggle_false() throws IOException,DatatypeConfigurationException {
        AppointmentPayload appointmentPayload=AppointmentPayload.builder().appointmentDispatchFlag("N").build();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadDetails.setCurrentLoadOperationalStatusEnumDescr("Planned");
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void processDisplayFlag_Status_Check__feature_toggle_false() throws IOException,DatatypeConfigurationException {
        AppointmentPayload appointmentPayload=AppointmentPayload.builder().appointmentDispatchFlag("Y").build();
        LoadType loadDetails = DataFactory.fetchLoad();
        loadDetails.setCurrentLoadOperationalStatusEnumDescr(null);
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, loadDetails);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }

    @Test
     void processDisplayFlag_Status_LoadCheck_() throws IOException,DatatypeConfigurationException {
        AppointmentPayload appointmentPayload = AppointmentPayload.builder().appointmentDispatchFlag("Y").build();
        loadEnrichmentService.dispatchFlagTrigger(appointmentPayload, null);
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
        verify(tmRestApiClient, times(0)).processTender(any(ProcessTender.class));
    }


    @Test
    void processLoadRate()  {
        LoadRateDataType loadRateDataType = LoadRateDataType.builder().systemLoadID("5432123").carrierCode("SCNN").build();
        ProcessLoadRate processLoadRate = ProcessLoadRate.builder().loadRateData(Arrays.asList(loadRateDataType)).build();
        loadEnrichmentService.processLoadRate(processLoadRate);
        verify(tmRestApiClient, times(1)).processLoadRate(any(ProcessLoadRate.class));
    }

    @Test
    void deleteReferenceNumber()  {
        ShipmentResponse shipment = DataFactory.getShipmentResponse();
        loadEnrichmentService.deleteReferenceNumber(shipment.getData().get(0).getShipmentNumber(),shipment);
        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));
    }

    @Test
    void deleteReferenceNumbercatchCheck() {
        ShipmentResponse shipment = DataFactory.getShipmentResponse();
        doThrow(new RuntimeException("deleteReferenceNumber| exception while delete referenceNumber from the shipment")).when(tmRestApiClient).processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));
        try {
            loadEnrichmentService.deleteReferenceNumber(shipment.getData().get(0).getShipmentNumber(),shipment);

        } catch (Exception e) {
            verify(log).error(eq("deleteReferenceNumber| exception while delete referenceNumber from the shipment"), e.getMessage());
        }
    }

    @Test
    void searchShipment() {
        when(loadMapper.createRestFindEntity(any(),any(),any(),any())).thenReturn(new RestFindEntity());
        loadEnrichmentService.searchShipment(eq("1234567"));
        verify(tmRestApiClient, times(1)).searchShipment(any(RestFindEntity.class));
    }

    @Test
    void dsearchShipmentCatchCheck() {
        when(loadMapper.createRestFindEntity(any(),any(),any(),any())).thenReturn(new RestFindEntity());
        doThrow(new RuntimeException("searchShipment| exception while searching the shipment details")).when(tmRestApiClient).searchShipment(any(RestFindEntity.class));
        try {
            loadEnrichmentService.searchShipment(eq("1234567"));
        } catch (Exception e) {
            verify(log).error(eq("searchShipment| exception while searching the shipment details"), e.getMessage());
        }
    }
    
    @Test
    void testProcessLoadReturnToOpen_Successfull() {
        
        ProcessLoadReturnToOpen processLoadReturnToOpen = mock(ProcessLoadReturnToOpen.class);
        LoadPlanType loadPlan = mock(LoadPlanType.class);
        when(loadPlan.getSystemLoadID()).thenReturn("12345");
        when(processLoadReturnToOpen.getLoadPlan()).thenReturn(Collections.singletonList(loadPlan));
        loadEnrichmentService.processLoadReturnToOpen(processLoadReturnToOpen);
        verify(tmRestApiClient,times(1)).processLoadReturnToOpen(processLoadReturnToOpen);
        
    }
    
    @Test
    void testProcessLoadReturnToOpen_Exception_case() {
        
        ProcessLoadReturnToOpen processLoadReturnToOpen = mock(ProcessLoadReturnToOpen.class);
        LoadPlanType loadPlan = mock(LoadPlanType.class);
        when(loadPlan.getSystemLoadID()).thenReturn("12345");
        when(processLoadReturnToOpen.getLoadPlan()).thenReturn(Collections.singletonList(loadPlan));
       
       
        doThrow(new RuntimeException("processLoadReturnToOpen| exception while return to open")).when(tmRestApiClient).processLoadReturnToOpen(processLoadReturnToOpen); 
        try {
          loadEnrichmentService.processLoadReturnToOpen(processLoadReturnToOpen);

        } catch (Exception e) {
            verify(log).error(eq("processLoadReturnToOpen| exception while return to open"), e.getMessage());
        }
        
    }
    
    @Test
    void testRemoveShipmentFromLoad_Success() {
        String shipmentNumber = "12345";
        String systemLoadID = "98765";
        loadEnrichmentService.removeShipmentFromLoad(shipmentNumber, systemLoadID);
        verify(tmRestApiClient, times(1)).processShipmentRemove(any(ProcessShipmentRemove.class));
    }
    
    
    @Test
    void testRemoveShipmentFromLoad_Exception_Case() {
      String shipmentNumber = "12345";
      String systemLoadID = "98765";
        doThrow(new RuntimeException("An error occurred while removing the shipment")).when(tmRestApiClient).processShipmentRemove(any(ProcessShipmentRemove.class)); 
        try {
          loadEnrichmentService.removeShipmentFromLoad(shipmentNumber, systemLoadID);

        } catch (Exception e) {
            verify(log).error(eq("An error occurred while removing the shipment"), e.getMessage());
        }
    }


    @Test
    void testUpdateStopAppointment_warningCheck() throws Exception {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        ProcessStopAppointmentResponse processStopAppointmentResponse = DataFactory.getProcessStopAppointmentResponse();
        ResponseHeaderType responseHeaderType = new ResponseHeaderType();
        responseHeaderType.setCompletedSuccessfully(Boolean.TRUE);
        List<ErrorType> list = new ArrayList<>();
        ErrorType error = new ErrorType();
        error.setSeverity("warning");
        list.add(error);
        responseHeaderType.setError(list);
        processStopAppointmentResponse.setResponseHeader(responseHeaderType);
        when(loadMapper.mapRestStopAppointmentRequest(any(AppointmentPayload.class),any(LoadType.class))).thenReturn(DataFactory.getProcessStopAppointment());
        when(tmRestApiClient.processStopAppointment(any(ProcessStopAppointment.class))).thenReturn(processStopAppointmentResponse);
        loadEnrichmentService.updateStopAppointment(appointmentPayload, DataFactory.fetchLoad());
        verify(tmRestApiClient, times(1)).processStopAppointment(any(ProcessStopAppointment.class));
        verify(tmRestApiClient, times(1)).processLoadRate(any(ProcessLoadRate.class));
    }

    @Test
    void testUpdateStopAppointment_ErrorCheck() throws Exception {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        ProcessStopAppointmentResponse processStopAppointmentResponse = DataFactory.getProcessStopAppointmentResponse();
        ResponseHeaderType responseHeaderType = new ResponseHeaderType();
        responseHeaderType.setCompletedSuccessfully(Boolean.TRUE);
        List<ErrorType> list = new ArrayList<>();
        ErrorType error = new ErrorType();
        error.setSeverity("Error");
        list.add(error);
        responseHeaderType.setError(list);
        processStopAppointmentResponse.setResponseHeader(responseHeaderType);
        when(loadMapper.mapRestStopAppointmentRequest(any(AppointmentPayload.class),any(LoadType.class))).thenReturn(DataFactory.getProcessStopAppointment());
        when(tmRestApiClient.processStopAppointment(any(ProcessStopAppointment.class))).thenReturn(processStopAppointmentResponse);
        loadEnrichmentService.updateStopAppointment(appointmentPayload, DataFactory.fetchLoad());
        verify(tmRestApiClient, times(0)).processLoadRate(any(ProcessLoadRate.class));
    }

    @Test
    void testUpdateStopAppointment_ErrorCheckWithNull() throws Exception {

        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        ProcessStopAppointmentResponse processStopAppointmentResponse = DataFactory.getProcessStopAppointmentResponse();
        ResponseHeaderType responseHeaderType = new ResponseHeaderType();
        responseHeaderType.setCompletedSuccessfully(Boolean.TRUE);
        responseHeaderType.setError(null);
        processStopAppointmentResponse.setResponseHeader(responseHeaderType);
        when(loadMapper.mapRestStopAppointmentRequest(any(AppointmentPayload.class),any(LoadType.class))).thenReturn(DataFactory.getProcessStopAppointment());
        when(tmRestApiClient.processStopAppointment(any(ProcessStopAppointment.class))).thenReturn(processStopAppointmentResponse);
        loadEnrichmentService.updateStopAppointment(appointmentPayload, DataFactory.fetchLoad());
        verify(tmRestApiClient, times(1)).processStopAppointment(any(ProcessStopAppointment.class));
        verify(tmRestApiClient, times(1)).processLoadRate(any(ProcessLoadRate.class));
    }
    
    @Test
    void searchShipmentWithOldAppointmentRef() {
        when(loadMapper.createRestFindEntity(any(),any(),any(),any())).thenReturn(new RestFindEntity());
        loadEnrichmentService.searchShipmentWithOldAppointmentRef(eq("1234567"));
        verify(tmRestApiClient, times(1)).searchShipment(any(RestFindEntity.class));
    }

    @Test
    void searchShipmentWithOldAppointmentRefCatchCheck() {
        when(loadMapper.createRestFindEntity(any(),any(),any(),any())).thenReturn(new RestFindEntity());
        doThrow(new RuntimeException("searchShipment| exception while searching the shipment details")).when(tmRestApiClient).searchShipment(any(RestFindEntity.class));
        try {
            loadEnrichmentService.searchShipmentWithOldAppointmentRef(eq("1234567"));
        } catch (Exception e) {
            verify(log).error(eq("searchShipment| exception while searching the shipment details"), e.getMessage());
        }
    }

    @Test
    void testLoadCompleteProcessForObtm_Success() throws Exception {

        when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(DataFactory.getShipmentCreateResponse(DataFactory.getProcessShipmentOrderCreate()));
        when(loadMapper.processAssignToLoadRequest(anyString(),anyList())).thenReturn(DataFactory.getProcessAssignToLoad());
        when(tmRestApiClient.processAssignShipmentsToLoad(any(ProcessAssignToLoad.class))).thenReturn(DataFactory.getProcessAssignToLoadResponse());
        when(tmRestApiClient.processShipmentRemove(any(ProcessShipmentRemove.class))).thenReturn(DataFactory.processShipmentRemoveResponse(Boolean.TRUE));
        loadEnrichmentService.triggerLoadCompleteForObtmAppointments("1234", DataFactory.getProcessLoadRetrieveResponse_Success().getLoad().get(0),"54678", 5);
        verify(tmRestApiClient, times(1)).processRestTrailerNumberUpdate(any(RestProcessLoadUpdate.class));
    }

    @Test
    void testLoadCompleteProcessForObtm_WithLoadTrackingNumber_Success() throws Exception {

        loadEnrichmentService.triggerLoadCompleteForObtmAppointments("1234", DataFactory.getProcessLoadRetrieveResponse_Success_CartonCount().getLoad().get(0),"54678", 1);
        verify(tmRestApiClient, times(0)).processUpdateShipment(any());
        verify(tmRestApiClient, times(0)).restProcessCreateShipment(any());
    }

    @Test
    void testLoadCompleteProcessForObtm_WithLoadTrackingNumberAndHighCartonCount_Success() throws Exception {

        when(loadMapper.updateShipmentOrderForRest(any(),any(),any())).thenReturn(new RestProcessShipmentOrderCreate());
        loadEnrichmentService.triggerLoadCompleteForObtmAppointments("1234", DataFactory.getProcessLoadRetrieveResponse_Success_CartonCount().getLoad().get(0),"54678", 5);
        verify(tmRestApiClient, times(1)).processUpdateShipment(any());
        verify(tmRestApiClient, times(0)).restProcessCreateShipment(any());
    }

    @Test
    void testLoadComplete_PiecesCountZero() throws DatatypeConfigurationException {
        LoadType loadType = DataFactory.getProcessLoadRetrieveResponse_Success().getLoad().get(0);

        loadEnrichmentService.triggerLoadCompleteForObtmAppointments("1234",loadType,"54678", 0);
        verify(tmRestApiClient, times(0)).processAssignShipmentsToLoad(any());
        verify(tmRestApiClient, times(0)).processShipmentRemove(any());
        verify(tmRestApiClient, times(0)).processRestTrailerNumberUpdate(any());

    }
    @Test
    void testLoadComplete_CreateShipmentFailure() throws DatatypeConfigurationException {
        LoadType loadType = DataFactory.getProcessLoadRetrieveResponse_Success().getLoad().get(0);

        doThrow(new NullPointerException("exception while fetching ProcessLoadRetrieveResponse Details")).
                when(tmRestApiClient).restProcessCreateShipment(any());

        loadEnrichmentService.triggerLoadCompleteForObtmAppointments("1234", loadType, "54678", 5);
        verify(tmRestApiClient, times(0)).processAssignShipmentsToLoad(any());
        verify(tmRestApiClient, times(0)).processShipmentRemove(any());
        verify(tmRestApiClient, times(0)).processRestTrailerNumberUpdate(any());

    }


    @Test
    void addOrUpdateSealType_success() throws IOException, DatatypeConfigurationException {


        AppointmentPayload appointmentPayload = DataFactory.getAppointmentPayload();
        String sealType = "seal-type-123";

        AppointmentSealConditionDetail appointmentSealConditionDetail =
                new AppointmentSealConditionDetail();
        appointmentSealConditionDetail.setAppointmentSealCnd(sealType);
        appointmentSealConditionDetail.setSequenceNbr(1);

        List<AppointmentSealConditionDetail> sealConditionDetailList =
                Arrays.asList(appointmentSealConditionDetail);

        when(tmRestApiClient.callAppointmentSealCondition(
                Integer.valueOf(appointmentPayload.getAppointmentNumber())))
                .thenReturn(sealConditionDetailList);


        LoadType load = DataFactory.fetchLoad();
        load.setReferenceNumber8("Test");
        load.setReferenceNumber7("Test");


        assertDoesNotThrow(() ->
                loadEnrichmentService.addOrUpdateSealType(
                        appointmentPayload, "1234567", load,"test"));


        verify(tmRestApiClient, times(0))
                .processMaintainEntityReferenceNumber(
                        any(ProcessMaintainEntityReferenceNumber.class));
    }

    @Test
    void deleteSealDetailsInTM_success() {


        String loadId = "1234567";
        String referenceTypeCode = LoadEnrichmentServiceImpl.SEAL_TYP;

        List<String> systemReferenceNbrList =
                Arrays.asList("OTHER", "BOLT");


        assertDoesNotThrow(() ->
                loadEnrichmentService.deleteSealDetailsInTM(
                        systemReferenceNbrList,
                        loadId,
                        referenceTypeCode));


        verify(tmRestApiClient, times(2))
                .processMaintainEntityReferenceNumber(
                        any(ProcessMaintainEntityReferenceNumber.class));
    }

    @Test
    void deleteSealDetailsInTM_on_TmsLoadProcessException() {

        List<String> systemReferenceNbrList = Arrays.asList("SEAL123", "SEAL456");
        String loadId = "1234567";
        String referenceTypeCode = "SEAL_TYP";

        doThrow(new TmsLoadProcessException(
                "deleteSealDetailsInTM| exception while delete seal info for load"))
                .when(tmRestApiClient)
                .processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));

        loadEnrichmentService.deleteSealDetailsInTM(
                systemReferenceNbrList,
                loadId,
                referenceTypeCode
        );

        verify(tmRestApiClient, times(1))
                .processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));
    }

    @Test
    void deleteSealDetailsInTM_on_RuntimeException() {

        List<String> systemReferenceNbrList = Arrays.asList("SEAL789");
        String loadId = "1234567";
        String referenceTypeCode = "SEAL_TYP";

        doThrow(new RuntimeException("Error calling tms to delete seal info for load"))
                .when(tmRestApiClient)
                .processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));

        loadEnrichmentService.deleteSealDetailsInTM(
                systemReferenceNbrList,
                loadId,
                referenceTypeCode
        );

        verify(tmRestApiClient, times(1))
                .processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));
    }






}

