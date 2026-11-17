package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessTenderAccept;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessTenderAcceptResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessTenderReject;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessTenderRejectResponse;
import com.macys.logistics.shipment.load.dto.inbound.BeginningSegment;
import com.macys.logistics.shipment.load.dto.inbound.BusinessInstructionsAndReferenceNumber;
import com.macys.logistics.shipment.load.dto.inbound.Edi990Data;
import com.macys.logistics.shipment.load.exception.Edi990ProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for Edi990ServiceImpl.
 * Tests both processAcceptance and processDecline methods with various
 * scenarios.
 * Target: ≥98% method, line, and branch coverage.
 */
@ExtendWith(MockitoExtension.class)
class Edi990ServiceImplTest {

    @InjectMocks
    private Edi990ServiceImpl edi990Service;

    @Mock
    private TMRestApiClient tmRestApiClient;

    private Edi990Data acceptData;
    private Edi990Data declineData;
    private BeginningSegment beginningSegment;
    private static final String SHIPMENT_ID = "12345678901234";
    // First 11 chars = "12345678901", number() strips leading zeros → "12345678901"
    private static final String EXPECTED_SYSTEM_LOAD_ID = "12345678901";
    private static final String CARRIER_CODE = "ABCD";

    @BeforeEach
    void setup() {
        beginningSegment = new BeginningSegment();
        beginningSegment.setShipmentIdentification(SHIPMENT_ID);
        beginningSegment.setStandardCarrierAlphaCode(CARRIER_CODE);
        beginningSegment.setReservationActionCode("A");
    }

    // ==================== ACCEPTANCE TESTS ====================

    @Test
    void testProcessAcceptance_Success() {
        acceptData = createAcceptData();
        when(tmRestApiClient.processTenderAccept(any(ProcessTenderAccept.class)))
                .thenReturn(new ProcessTenderAcceptResponse());

        edi990Service.processAcceptance(acceptData, SHIPMENT_ID);

        ArgumentCaptor<ProcessTenderAccept> captor = ArgumentCaptor.forClass(ProcessTenderAccept.class);
        verify(tmRestApiClient, times(1)).processTenderAccept(captor.capture());

        ProcessTenderAccept captured = captor.getValue();
        assertNotNull(captured);
        assertEquals("processTenderAccept", captured.getApiHeader().getOperationName());
        assertEquals(1, captured.getLoadTenderData().size());
        assertEquals(EXPECTED_SYSTEM_LOAD_ID, captured.getLoadTenderData().get(0).getSystemLoadID());
        assertEquals(CARRIER_CODE, captured.getLoadTenderData().get(0).getCarrierCode());
        assertEquals(CARRIER_CODE, captured.getLoadTenderData().get(0).getTenderAcceptedBy());
        assertEquals("S_TENDERED", captured.getLoadTenderData().get(0).getCurrentLoadOperationalStatusEnumVal());
    }

    @Test
    void testProcessAcceptance_Success_WithBusinessInstructionsAndReferenceNumber() {
        acceptData = createAcceptDataWithReferences();
        when(tmRestApiClient.processTenderAccept(any(ProcessTenderAccept.class)))
                .thenReturn(new ProcessTenderAcceptResponse());

        edi990Service.processAcceptance(acceptData, SHIPMENT_ID);

        verify(tmRestApiClient, times(1)).processTenderAccept(any(ProcessTenderAccept.class));
    }

    @Test
    void testProcessAcceptance_Success_WithNullBusinessInstructionsAndReferenceNumber() {
        acceptData = new Edi990Data();
        acceptData.setBeginningSegment(beginningSegment);
        acceptData.setBusinessInstructionsAndReferenceNumber(null);
        when(tmRestApiClient.processTenderAccept(any(ProcessTenderAccept.class)))
                .thenReturn(new ProcessTenderAcceptResponse());

        edi990Service.processAcceptance(acceptData, SHIPMENT_ID);

        verify(tmRestApiClient, times(1)).processTenderAccept(any(ProcessTenderAccept.class));
    }

    @Test
    void testProcessAcceptance_Success_WithNullResponse() {
        acceptData = createAcceptData();
        when(tmRestApiClient.processTenderAccept(any(ProcessTenderAccept.class)))
                .thenReturn(null);

        edi990Service.processAcceptance(acceptData, SHIPMENT_ID);

        verify(tmRestApiClient, times(1)).processTenderAccept(any(ProcessTenderAccept.class));
    }

    @Test
    void testProcessAcceptance_Success_WithExactly11CharShipmentId() {
        BeginningSegment seg = new BeginningSegment();
        seg.setShipmentIdentification("12345678901");
        seg.setStandardCarrierAlphaCode(CARRIER_CODE);

        acceptData = new Edi990Data();
        acceptData.setBeginningSegment(seg);

        when(tmRestApiClient.processTenderAccept(any(ProcessTenderAccept.class)))
                .thenReturn(new ProcessTenderAcceptResponse());

        edi990Service.processAcceptance(acceptData, "12345678901");

        ArgumentCaptor<ProcessTenderAccept> captor = ArgumentCaptor.forClass(ProcessTenderAccept.class);
        verify(tmRestApiClient, times(1)).processTenderAccept(captor.capture());
        assertEquals("12345678901", captor.getValue().getLoadTenderData().get(0).getSystemLoadID());
    }

    @Test
    void testProcessAcceptance_Success_WithShortShipmentId() {
        // Shipment ID shorter than 11 chars — used as-is, then number() applied
        BeginningSegment seg = new BeginningSegment();
        seg.setShipmentIdentification("123456");
        seg.setStandardCarrierAlphaCode(CARRIER_CODE);

        acceptData = new Edi990Data();
        acceptData.setBeginningSegment(seg);

        when(tmRestApiClient.processTenderAccept(any(ProcessTenderAccept.class)))
                .thenReturn(new ProcessTenderAcceptResponse());

        edi990Service.processAcceptance(acceptData, "123456");

        ArgumentCaptor<ProcessTenderAccept> captor = ArgumentCaptor.forClass(ProcessTenderAccept.class);
        verify(tmRestApiClient, times(1)).processTenderAccept(captor.capture());
        assertEquals("123456", captor.getValue().getLoadTenderData().get(0).getSystemLoadID());
    }

    @Test
    void testProcessAcceptance_Success_WithBlankCarrierCode() {
        // TIBCO xsl:if: carrier code not set when blank
        BeginningSegment seg = new BeginningSegment();
        seg.setShipmentIdentification(SHIPMENT_ID);
        seg.setStandardCarrierAlphaCode("   ");

        acceptData = new Edi990Data();
        acceptData.setBeginningSegment(seg);

        when(tmRestApiClient.processTenderAccept(any(ProcessTenderAccept.class)))
                .thenReturn(new ProcessTenderAcceptResponse());

        edi990Service.processAcceptance(acceptData, SHIPMENT_ID);

        ArgumentCaptor<ProcessTenderAccept> captor = ArgumentCaptor.forClass(ProcessTenderAccept.class);
        verify(tmRestApiClient, times(1)).processTenderAccept(captor.capture());
        assertNull(captor.getValue().getLoadTenderData().get(0).getCarrierCode());
        assertNull(captor.getValue().getLoadTenderData().get(0).getTenderAcceptedBy());
    }

    @Test
    void testProcessAcceptance_ThrowsException_WhenApiCallFails() {
        acceptData = createAcceptData();
        RuntimeException apiException = new RuntimeException("TM API error");
        when(tmRestApiClient.processTenderAccept(any(ProcessTenderAccept.class)))
                .thenThrow(apiException);

        Edi990ProcessingException thrown = assertThrows(Edi990ProcessingException.class,
                () -> edi990Service.processAcceptance(acceptData, SHIPMENT_ID));

        assertTrue(thrown.getMessage().contains("Failed to process tender acceptance"));
        assertTrue(thrown.getMessage().contains(SHIPMENT_ID));
        assertEquals(apiException, thrown.getCause());
        verify(tmRestApiClient, times(1)).processTenderAccept(any(ProcessTenderAccept.class));
    }

    @Test
    void testProcessAcceptance_ThrowsException_WhenBeginningSegmentIsNull() {
        acceptData = new Edi990Data();
        acceptData.setBeginningSegment(null);

        Edi990ProcessingException thrown = assertThrows(Edi990ProcessingException.class,
                () -> edi990Service.processAcceptance(acceptData, SHIPMENT_ID));

        assertNotNull(thrown);
        assertTrue(thrown.getMessage().contains(SHIPMENT_ID));
        verify(tmRestApiClient, never()).processTenderAccept(any(ProcessTenderAccept.class));
    }

    // ==================== DECLINE TESTS ====================

    @Test
    void testProcessDecline_Success_WithReservationDeclinedReasonCode() {
        declineData = createDeclineDataWithReason();
        when(tmRestApiClient.processTenderReject(any(ProcessTenderReject.class)))
                .thenReturn(new ProcessTenderRejectResponse());

        edi990Service.processDecline(declineData, SHIPMENT_ID);

        ArgumentCaptor<ProcessTenderReject> captor = ArgumentCaptor.forClass(ProcessTenderReject.class);
        verify(tmRestApiClient, times(1)).processTenderReject(captor.capture());

        ProcessTenderReject captured = captor.getValue();
        assertNotNull(captured);
        assertEquals("processTenderReject", captured.getApiHeader().getOperationName());
        assertEquals(1, captured.getLoadTenderData().size());
        assertEquals(EXPECTED_SYSTEM_LOAD_ID, captured.getLoadTenderData().get(0).getSystemLoadID());
        assertEquals(CARRIER_CODE, captured.getLoadTenderData().get(0).getCarrierCode());
        assertEquals(CARRIER_CODE, captured.getLoadTenderData().get(0).getTenderAcceptedBy());
        assertEquals("S_TENDERED", captured.getLoadTenderData().get(0).getCurrentLoadOperationalStatusEnumVal());
    }

    @Test
    void testProcessDecline_Success_WithoutReservationDeclinedReasonCode() {
        declineData = createDeclineDataWithoutReason();
        when(tmRestApiClient.processTenderReject(any(ProcessTenderReject.class)))
                .thenReturn(new ProcessTenderRejectResponse());

        edi990Service.processDecline(declineData, SHIPMENT_ID);

        verify(tmRestApiClient, times(1)).processTenderReject(any(ProcessTenderReject.class));
    }

    @Test
    void testProcessDecline_Success_WithBusinessInstructionsAndReferenceNumber() {
        declineData = createDeclineDataWithReferences();
        when(tmRestApiClient.processTenderReject(any(ProcessTenderReject.class)))
                .thenReturn(new ProcessTenderRejectResponse());

        edi990Service.processDecline(declineData, SHIPMENT_ID);

        verify(tmRestApiClient, times(1)).processTenderReject(any(ProcessTenderReject.class));
    }

    @Test
    void testProcessDecline_Success_WithNullBusinessInstructionsAndReferenceNumber() {
        declineData = createDeclineDataWithoutReason();
        declineData.setBusinessInstructionsAndReferenceNumber(null);
        when(tmRestApiClient.processTenderReject(any(ProcessTenderReject.class)))
                .thenReturn(new ProcessTenderRejectResponse());

        edi990Service.processDecline(declineData, SHIPMENT_ID);

        verify(tmRestApiClient, times(1)).processTenderReject(any(ProcessTenderReject.class));
    }

    @Test
    void testProcessDecline_Success_WithNullResponse() {
        declineData = createDeclineDataWithoutReason();
        when(tmRestApiClient.processTenderReject(any(ProcessTenderReject.class)))
                .thenReturn(null);

        edi990Service.processDecline(declineData, SHIPMENT_ID);

        verify(tmRestApiClient, times(1)).processTenderReject(any(ProcessTenderReject.class));
    }

    @Test
    void testProcessDecline_Success_WithLeadingZerosInShipmentId() {
        BeginningSegment seg = new BeginningSegment();
        seg.setShipmentIdentification("00098765432M");
        seg.setStandardCarrierAlphaCode(CARRIER_CODE);
        seg.setReservationActionCode("D");

        declineData = new Edi990Data();
        declineData.setBeginningSegment(seg);

        when(tmRestApiClient.processTenderReject(any(ProcessTenderReject.class)))
                .thenReturn(new ProcessTenderRejectResponse());

        edi990Service.processDecline(declineData, "00098765432M");

        ArgumentCaptor<ProcessTenderReject> captor = ArgumentCaptor.forClass(ProcessTenderReject.class);
        verify(tmRestApiClient, times(1)).processTenderReject(captor.capture());
        // First 11 chars = "00098765432", number() = 98765432
        assertEquals("98765432", captor.getValue().getLoadTenderData().get(0).getSystemLoadID());
    }

    @Test
    void testProcessDecline_Success_WithExactly11CharShipmentId() {
        BeginningSegment seg = new BeginningSegment();
        seg.setShipmentIdentification("99999999999");
        seg.setStandardCarrierAlphaCode(CARRIER_CODE);
        seg.setReservationActionCode("D");

        declineData = new Edi990Data();
        declineData.setBeginningSegment(seg);

        when(tmRestApiClient.processTenderReject(any(ProcessTenderReject.class)))
                .thenReturn(new ProcessTenderRejectResponse());

        edi990Service.processDecline(declineData, "99999999999");

        ArgumentCaptor<ProcessTenderReject> captor = ArgumentCaptor.forClass(ProcessTenderReject.class);
        verify(tmRestApiClient, times(1)).processTenderReject(captor.capture());
        assertEquals("99999999999", captor.getValue().getLoadTenderData().get(0).getSystemLoadID());
    }

    @Test
    void testProcessDecline_Success_WithBlankCarrierCode() {
        BeginningSegment seg = new BeginningSegment();
        seg.setShipmentIdentification(SHIPMENT_ID);
        seg.setStandardCarrierAlphaCode("");
        seg.setReservationActionCode("D");

        declineData = new Edi990Data();
        declineData.setBeginningSegment(seg);

        when(tmRestApiClient.processTenderReject(any(ProcessTenderReject.class)))
                .thenReturn(new ProcessTenderRejectResponse());

        edi990Service.processDecline(declineData, SHIPMENT_ID);

        ArgumentCaptor<ProcessTenderReject> captor = ArgumentCaptor.forClass(ProcessTenderReject.class);
        verify(tmRestApiClient, times(1)).processTenderReject(captor.capture());
        assertNull(captor.getValue().getLoadTenderData().get(0).getCarrierCode());
        assertNull(captor.getValue().getLoadTenderData().get(0).getTenderAcceptedBy());
    }

    @Test
    void testProcessDecline_ThrowsException_WhenApiCallFails() {
        declineData = createDeclineDataWithoutReason();
        RuntimeException apiException = new RuntimeException("TM API error");
        when(tmRestApiClient.processTenderReject(any(ProcessTenderReject.class)))
                .thenThrow(apiException);

        Edi990ProcessingException thrown = assertThrows(Edi990ProcessingException.class,
                () -> edi990Service.processDecline(declineData, SHIPMENT_ID));

        assertTrue(thrown.getMessage().contains("Failed to process tender decline"));
        assertTrue(thrown.getMessage().contains(SHIPMENT_ID));
        assertEquals(apiException, thrown.getCause());
        verify(tmRestApiClient, times(1)).processTenderReject(any(ProcessTenderReject.class));
    }

    @Test
    void testProcessDecline_ThrowsException_WhenBeginningSegmentIsNull() {
        declineData = new Edi990Data();
        declineData.setBeginningSegment(null);

        Edi990ProcessingException thrown = assertThrows(Edi990ProcessingException.class,
                () -> edi990Service.processDecline(declineData, SHIPMENT_ID));

        assertNotNull(thrown);
        assertTrue(thrown.getMessage().contains(SHIPMENT_ID));
        verify(tmRestApiClient, never()).processTenderReject(any(ProcessTenderReject.class));
    }

    // ==================== HELPER METHODS ====================

    private Edi990Data createAcceptData() {
        Edi990Data data = new Edi990Data();
        data.setBeginningSegment(beginningSegment);
        return data;
    }

    private Edi990Data createAcceptDataWithReferences() {
        Edi990Data data = new Edi990Data();
        data.setBeginningSegment(beginningSegment);

        BusinessInstructionsAndReferenceNumber ref = new BusinessInstructionsAndReferenceNumber();
        ref.setReferenceIdentificationQualifier("BM");
        ref.setReferenceIdentification("BOL123456");
        data.setBusinessInstructionsAndReferenceNumber(ref);

        return data;
    }

    private Edi990Data createDeclineDataWithReason() {
        Edi990Data data = new Edi990Data();
        BeginningSegment seg = new BeginningSegment();
        seg.setShipmentIdentification(SHIPMENT_ID);
        seg.setStandardCarrierAlphaCode(CARRIER_CODE);
        seg.setReservationActionCode("D");
        seg.setReservationDeclinedReasonCode("CPU");
        data.setBeginningSegment(seg);
        return data;
    }

    private Edi990Data createDeclineDataWithoutReason() {
        Edi990Data data = new Edi990Data();
        BeginningSegment seg = new BeginningSegment();
        seg.setShipmentIdentification(SHIPMENT_ID);
        seg.setStandardCarrierAlphaCode(CARRIER_CODE);
        seg.setReservationActionCode("D");
        data.setBeginningSegment(seg);
        return data;
    }

    private Edi990Data createDeclineDataWithReferences() {
        Edi990Data data = createDeclineDataWithReason();

        BusinessInstructionsAndReferenceNumber ref = new BusinessInstructionsAndReferenceNumber();
        ref.setReferenceIdentificationQualifier("CN");
        ref.setReferenceIdentification("CARRIER123");
        data.setBusinessInstructionsAndReferenceNumber(ref);

        return data;
    }
}
