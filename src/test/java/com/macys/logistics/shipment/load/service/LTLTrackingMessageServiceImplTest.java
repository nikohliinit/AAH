package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.gen.transportationmanager.FindEntitiesResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.LoadType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessMaintainEntityReferenceNumber;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ReferenceNumberType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ReferenceNumberUpdateDataType;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentTrackingMessage;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LTLTrackingMessageServiceImplTest {

    @InjectMocks
    private LTLTrackingMessageServiceImpl service;

    @Mock
    private LoadEnrichmentService loadEnrichmentService;

    @Mock
    private LoadMapper loadMapper;

    @Mock
    private TMRestApiClient tmRestApiClient;

    @Mock
    private FindEntitiesResponse findEntitiesResponse;

    @Mock
    private LoadType load;

    private ShipmentTrackingMessage trackingMessageWithId;
    private ShipmentTrackingMessage trackingMessageWithoutId;

    @BeforeEach
    void init() {
        trackingMessageWithId = new ShipmentTrackingMessage();
        trackingMessageWithId.setLoadId(123456);
        trackingMessageWithId.setProNumber("PRO-789");

        trackingMessageWithoutId = new ShipmentTrackingMessage();
        trackingMessageWithoutId.setLoadId(null);
        trackingMessageWithoutId.setProNumber("PRO-000");
    }

    @Test
    void addProNumberToTM_adds_when_not_present() {
        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse)).thenReturn(load);
        when(load.getReferenceNumberStructure()).thenReturn(Collections.emptyList());

        ArgumentCaptor<ProcessMaintainEntityReferenceNumber> reqCaptor =
                ArgumentCaptor.forClass(ProcessMaintainEntityReferenceNumber.class);

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(reqCaptor.capture());
        ProcessMaintainEntityReferenceNumber sent = reqCaptor.getValue();
        List<ReferenceNumberUpdateDataType> updates = sent.getReferenceNumberUpdateData();
        assertEquals(1, updates.size());

        ReferenceNumberUpdateDataType upd = updates.getFirst();
        assertEquals(Constants.REF_NUM_ACTION_ENUM_VAL_ADD, upd.getReferenceNumberActionEnumVal());
        assertEquals(Constants.DEFAULT_REF_NUM_SHP_CATEGORY_ENUM_VAL, upd.getReferenceNumberCategoryEnumVal());
        assertEquals(String.valueOf(trackingMessageWithId.getLoadId()), upd.getReferenceNumberEntityKey());
        assertEquals(Constants.REF_NUM_CARR_PRO_NUMBER_TYPE_CODE, upd.getReferenceNumberTypeCode());
        assertEquals(trackingMessageWithId.getProNumber(), upd.getReferenceNumber());
    }

    @Test
    void addProNumberToTM_handles_validation_exception_when_load_null() {
        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse)).thenReturn(null);

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, never()).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_handles_TmsLoadProcessException_from_tm_client() {
        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse)).thenReturn(load);
        when(load.getReferenceNumberStructure()).thenReturn(Collections.emptyList());

        doThrow(new TmsLoadProcessException("TM error while adding pro"))
                .when(tmRestApiClient)
                .processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_does_nothing_when_loadId_null() {
        service.addProNumberToTM(trackingMessageWithoutId);
        verify(loadEnrichmentService, never()).fetchLoadDetails(anyString());
        verify(loadMapper, never()).getLoad(any());
        verify(tmRestApiClient, never()).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_handles_generic_exception() {
        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse))
                .thenThrow(new RuntimeException("unexpected"));

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, never()).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_skips_when_pro_number_already_exists() {
        ReferenceNumberType ref = new ReferenceNumberType();
        ref.setReferenceNumberTypeCode(Constants.REF_NUM_CARR_PRO_NUMBER_TYPE_CODE);
        ref.setReferenceNumber("PRO-123");
        List<ReferenceNumberType> refs = new ArrayList<>();
        refs.add(ref);

        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse)).thenReturn(load);
        when(load.getReferenceNumberStructure()).thenReturn(refs);

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, never()).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_adds_when_ref_list_has_non_matching_type_code() {
        ReferenceNumberType ref = new ReferenceNumberType();
        ref.setReferenceNumberTypeCode("OTHER_CODE");
        ref.setReferenceNumber("X");
        List<ReferenceNumberType> refs = new ArrayList<>();
        refs.add(ref);

        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse)).thenReturn(load);
        when(load.getReferenceNumberStructure()).thenReturn(refs);

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_adds_when_ref_has_matching_type_but_blank_number() {
        ReferenceNumberType ref = new ReferenceNumberType();
        ref.setReferenceNumberTypeCode(Constants.REF_NUM_CARR_PRO_NUMBER_TYPE_CODE);
        ref.setReferenceNumber("");
        List<ReferenceNumberType> refs = new ArrayList<>();
        refs.add(ref);

        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse)).thenReturn(load);
        when(load.getReferenceNumberStructure()).thenReturn(refs);

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_adds_when_ref_has_matching_type_but_null_number() {
        ReferenceNumberType ref = new ReferenceNumberType();
        ref.setReferenceNumberTypeCode(Constants.REF_NUM_CARR_PRO_NUMBER_TYPE_CODE);
        ref.setReferenceNumber(null);
        List<ReferenceNumberType> refs = new ArrayList<>();
        refs.add(ref);

        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse)).thenReturn(load);
        when(load.getReferenceNumberStructure()).thenReturn(refs);

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_adds_when_ref_structure_is_null() {
        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse)).thenReturn(load);
        when(load.getReferenceNumberStructure()).thenReturn(null);

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_handles_TmsLoadProcessException_from_fetchLoadDetails() {
        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenThrow(new TmsLoadProcessException("TM unavailable"));

        service.addProNumberToTM(trackingMessageWithId);

        verify(loadMapper, never()).getLoad(any());
        verify(tmRestApiClient, never()).processMaintainEntityReferenceNumber(any());
    }

    @Test
    void addProNumberToTM_handles_generic_exception_from_tm_client() {
        when(loadEnrichmentService.fetchLoadDetails(eq(String.valueOf(trackingMessageWithId.getLoadId()))))
                .thenReturn(findEntitiesResponse);
        when(loadMapper.getLoad(findEntitiesResponse)).thenReturn(load);
        when(load.getReferenceNumberStructure()).thenReturn(Collections.emptyList());

        doThrow(new RuntimeException("connection reset"))
                .when(tmRestApiClient)
                .processMaintainEntityReferenceNumber(any(ProcessMaintainEntityReferenceNumber.class));

        service.addProNumberToTM(trackingMessageWithId);

        verify(tmRestApiClient, times(1)).processMaintainEntityReferenceNumber(any());
    }
}
