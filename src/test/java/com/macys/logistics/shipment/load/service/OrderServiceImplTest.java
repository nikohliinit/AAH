package com.macys.logistics.shipment.load.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ContainerType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ErrorType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessShipmentOrderCreateResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessShipmentOrderDeleteResponse;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ReferenceNumberType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ResponseHeaderType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestProcessShipmentOrderCreate;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestShipmentType;
import com.macys.logistics.shipment.load.dto.inbound.RestProcessShipmentOrderDelete;
import com.macys.logistics.shipment.load.dto.inbound.orders.Commodity;
import com.macys.logistics.shipment.load.dto.inbound.orders.LineItem;
import com.macys.logistics.shipment.load.dto.inbound.orders.LocationInfo;
import com.macys.logistics.shipment.load.dto.inbound.orders.Order;
import com.macys.logistics.shipment.load.dto.inbound.orders.OrderShipmentMessage;
import com.macys.logistics.shipment.load.dto.inbound.orders.Reference;
import com.macys.logistics.shipment.load.dto.inbound.orders.TimeWindow;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    TMRestApiClient tmRestApiClient;

    @Captor
    ArgumentCaptor<RestProcessShipmentOrderCreate> createCaptor;

    @Captor
    ArgumentCaptor<RestProcessShipmentOrderDelete> deleteCaptor;

    // ========================================================================================
    // createOrder tests
    // ========================================================================================

    @Nested
    class CreateOrderTests {

        @Test
        void createOrder_happyPath_success() {
            OrderShipmentMessage msg = buildFullMessage();
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestProcessShipmentOrderCreate captured = createCaptor.getValue();
            assertNotNull(captured.getShipment());
            assertEquals(1, captured.getShipment().size());
            assertFalse(captured.getIgnoreAllShipmentReferenceNumbersFlag());
            assertFalse(captured.getExecuteAPRatingFlag());
            assertFalse(captured.getExecuteARRatingFlag());
        }

        @Test
        void createOrder_apiFailure_throwsTmsLoadProcessException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ProcessShipmentOrderCreateResponse response = failedCreateResponse("100");
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            TmsLoadProcessException ex = assertThrows(TmsLoadProcessException.class,
                    () -> orderService.createOrder(msg));
            assertTrue(ex.getMessage().contains("ORD-001"));
            assertTrue(ex.getMessage().contains("100"));
        }

        @Test
        void createOrder_nullResponseHeader_noException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
            response.setResponseHeader(null);
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            assertDoesNotThrow(() -> orderService.createOrder(msg));
        }

        @Test
        void createOrder_responseHeaderSuccessTrue_noException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            assertDoesNotThrow(() -> orderService.createOrder(msg));
        }

        @Test
        void createOrder_mapsShipmentFieldsCorrectly() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPreferredAPCarrierCode("CARR01");
            msg.getOrder().setCustomerCode("CUST1");
            msg.getOrder().setFreightTerms("COL");
            msg.getOrder().setShipProject("PROJ123");

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();

            assertEquals("ORD-001", shipment.getShipmentNumber());
            assertEquals("CUST1", shipment.getCustomerCode());
            assertEquals("MCY", shipment.getLogisticsGroupCode());
            assertEquals("MCY", shipment.getDivisionCode());
            assertEquals("CARR01", shipment.getPreferredAPCarrierCode());
            assertEquals("1011", shipment.getSystemPlanID());
            assertEquals("FT_COLLECT", shipment.getFreightTermsEnumVal());
            assertEquals("PROJ123", shipment.getMergeInTransitConsolidationCode());
            assertEquals("SFT_DC", shipment.getShipFromLocationTypeEnumVal());
            assertEquals("STT_DC", shipment.getShipToLocationTypeEnumVal());
            assertEquals("DEST1", shipment.getShipToLocationCode());
            assertEquals("MITCC_DIFF_DESTLOC", shipment.getMitccStrategyEnumVal());

            // Verify flags
            assertTrue(shipment.getDeferAPRatingFlag());
            assertTrue(shipment.getDeferARRatingFlag());
            assertTrue(shipment.getUseOriginDefaultsFlag());
            assertTrue(shipment.getUseDestinationDefaultsFlag());
            assertFalse(shipment.getIgnoreReferenceNumbersFlag());
            assertFalse(shipment.getIgnoreContainersFlag());
            assertTrue(shipment.getIgnoreChargeOverridesFlag());
        }

        @Test
        void createOrder_withPrimaryLoc_overridesShipFrom() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPrimaryLoc("PRIMARY-LOC");
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertEquals("PRIMARY-LOC", shipment.getShipFromLocationCode());
        }

        @Test
        void createOrder_withoutPrimaryLoc_usesOriginLocationId() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPrimaryLoc(null);
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertEquals("ORIGIN1", shipment.getShipFromLocationCode());
        }

        @Test
        void createOrder_withNullOrigin_shipFromIsNull() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPrimaryLoc(null);
            msg.getOrder().setOrigin(null);
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertNull(shipment.getShipFromLocationCode());
        }

        @Test
        void createOrder_withNullDestination_shipToIsNull() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setDestination(null);
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertNull(shipment.getShipToLocationCode());
        }

        @Test
        void createOrder_withNullPreferredCarrier_notSet() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPreferredAPCarrierCode(null);
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertNull(shipment.getPreferredAPCarrierCode());
        }

        @Test
        void createOrder_withEmptyPreferredCarrier_notSet() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPreferredAPCarrierCode("  ");
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertNull(shipment.getPreferredAPCarrierCode());
        }

        @Test
        void createOrder_failedResponseWithNullErrors_extractsEmptyString()  {
            OrderShipmentMessage msg = buildFullMessage();
            ResponseHeaderType mockHeader = mock(ResponseHeaderType.class);
            when(mockHeader.isCompletedSuccessfully()).thenReturn(false);
            when(mockHeader.getError()).thenReturn(null);
            ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
            response.setResponseHeader(mockHeader);
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            TmsLoadProcessException ex = assertThrows(TmsLoadProcessException.class,
                    () -> orderService.createOrder(msg));
            assertTrue(ex.getMessage().contains("ORD-001"));
        }
    }

    // ========================================================================================
    // Freight terms mapping tests
    // ========================================================================================

    @Nested
    class FreightTermsTests {

        static Stream<Arguments> freightTermsProvider() {
            return Stream.of(
                Arguments.of("COL", "FT_COLLECT"),
                Arguments.of("PPD", "FT_PRE_PAID"),
                Arguments.of("XYZ", "FT_NULL"),
                Arguments.of("col", "FT_COLLECT"),
                Arguments.of(null,  "FT_NULL")
            );
        }

        @ParameterizedTest(name = "freightTerms={0} -> {1}")
        @MethodSource("freightTermsProvider")
        void freightTerms_mapsCorrectly(String input, String expected) {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setFreightTerms(input);
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertEquals(expected, createCaptor.getValue().getShipment().getFirst().getFreightTermsEnumVal());
        }
    }

    // ========================================================================================
    // Consolidation class code tests
    // ========================================================================================

    @Nested
    class ConsolidationClassTests {

        @Test
        void consolidation_srr02Route180_usesSrr02Value() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("180");
            msg.getOrder().setReferences(List.of(ref("SRR02", "CONSOL-CLASS-A")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertEquals("CONSOL-CLASS-A",
                    createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_srr02Route190_usesSrr02Value() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("190");
            msg.getOrder().setReferences(List.of(ref("SRR02", "CONSOL-B")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertEquals("CONSOL-B",
                    createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_srr02Route111_usesSrr02Value() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("111");
            msg.getOrder().setReferences(List.of(ref("SRR02", "CONSOL-C")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertEquals("CONSOL-C",
                    createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_srr02Route134_usesSrr02Value() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("134");
            msg.getOrder().setReferences(List.of(ref("SRR02", "CONSOL-D")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertEquals("CONSOL-D",
                    createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_srr03Route122_usesSrr03Value() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("122");
            msg.getOrder().setReferences(List.of(ref("SRR03", "CONSOL-E")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertEquals("CONSOL-E",
                    createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_nullSpclRteNbr_noConsolidation() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr(null);
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertNull(createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_emptySpclRteNbr_noConsolidation() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("  ");
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertNull(createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_excludedSourceId_noConsolidation() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("180");
            LocationInfo origin = new LocationInfo();
            origin.setLocationId("50957364 295");
            msg.getOrder().setOrigin(origin);
            msg.getOrder().setReferences(List.of(ref("SRR02", "CONSOL-SHOULD-NOT-APPEAR")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertNull(createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_unmatchedRoute_noConsolidation() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("999");
            msg.getOrder().setReferences(List.of(ref("SRR02", "CONSOL-F")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertNull(createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_nullOrigin_noNpe() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("180");
            msg.setPrimaryLoc("PL1");
            msg.getOrder().setOrigin(null);
            msg.getOrder().setReferences(List.of(ref("SRR02", "CONSOL-G")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertEquals("CONSOL-G",
                    createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_srr02Reference_notFound_returnsNull() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("180");
            msg.getOrder().setReferences(List.of(ref("OTHER", "VALUE")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertNull(createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }

        @Test
        void consolidation_nullReferences_returnsNull() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setSpclRteNbr("180");
            msg.getOrder().setReferences(null);
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertNull(createCaptor.getValue().getShipment().getFirst().getShipmentConsolidationClassCode());
        }
    }

    // ========================================================================================
    // Date/time field tests
    // ========================================================================================

    @Nested
    class DateTimeTests {

        @Test
        void dateTime_pickupAndDeliveryWindows_mapped() {
            OrderShipmentMessage msg = buildFullMessage();
            TimeWindow pickup = new TimeWindow();
            pickup.setEarliest("2026-01-20T08:00:00Z");
            msg.getOrder().setPickupWindow(pickup);
            TimeWindow delivery = new TimeWindow();
            delivery.setLatest("2026-01-25T17:00:00Z");
            msg.getOrder().setDeliveryWindow(delivery);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();

            assertEquals("2026-01-20T08:00:00Z", shipment.getPickupFromDateTime());
            assertEquals("2026-01-20T08:01:00Z", shipment.getDeliveryFromDateTime());
            assertEquals("2026-01-25T16:59:00Z", shipment.getPickupToDateTime());
            assertEquals("2026-01-25T17:00:00Z", shipment.getDeliveryToDateTime());
        }

        @Test
        void dateTime_nullPickupWindow_noFieldsSet() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setPickupWindow(null);
            msg.getOrder().setDeliveryWindow(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertNull(shipment.getPickupFromDateTime());
            assertNull(shipment.getDeliveryFromDateTime());
            assertNull(shipment.getPickupToDateTime());
            assertNull(shipment.getDeliveryToDateTime());
        }

        @Test
        void dateTime_emptyEarliestAndLatest_noFieldsSet() {
            OrderShipmentMessage msg = buildFullMessage();
            TimeWindow pickup = new TimeWindow();
            pickup.setEarliest("  ");
            msg.getOrder().setPickupWindow(pickup);
            TimeWindow delivery = new TimeWindow();
            delivery.setLatest("");
            msg.getOrder().setDeliveryWindow(delivery);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertNull(shipment.getPickupFromDateTime());
            assertNull(shipment.getPickupToDateTime());
        }

        @Test
        void dateTime_unparseable_returnsAsIs() {
            OrderShipmentMessage msg = buildFullMessage();
            TimeWindow pickup = new TimeWindow();
            pickup.setEarliest("INVALID-DATE");
            msg.getOrder().setPickupWindow(pickup);
            TimeWindow delivery = new TimeWindow();
            delivery.setLatest("ALSO-INVALID");
            msg.getOrder().setDeliveryWindow(delivery);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertEquals("INVALID-DATE", shipment.getPickupFromDateTime());
            assertEquals("INVALID-DATE", shipment.getDeliveryFromDateTime());
            assertEquals("ALSO-INVALID", shipment.getPickupToDateTime());
            assertEquals("ALSO-INVALID", shipment.getDeliveryToDateTime());
        }
    }

    // ========================================================================================
    // Container building tests
    // ========================================================================================

    @Nested
    class ContainerTests {

        @Test
        void container_withLineItems_mapsPiecesVolumeWeight() {
            OrderShipmentMessage msg = buildFullMessage();
            LineItem li = new LineItem();
            li.setPieces(10);
            li.setVolumeCf(new BigDecimal("25.5"));
            li.setWeightLb(new BigDecimal("100.0"));
            msg.getOrder().setLineItems(List.of(li));

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            ContainerType container = createCaptor.getValue().getShipment().getFirst().getContainer().getFirst();
            assertEquals("PIECE", container.getContainerTypeCode());
            assertEquals(new BigDecimal("10"), container.getQuantity());
            assertEquals(new BigDecimal("25.5"), container.getContainerShippingInformation().getVolume());
            assertEquals(new BigDecimal("100.0"), container.getWeightByFreightClass().getFirst().getFreightClassNominalWeight());
            assertEquals("92.5", container.getWeightByFreightClass().getFirst().getFreightClassCode());

            assertTrue(container.isIgnoreContainerOrientationsFlag());
            assertFalse(container.isIgnoreWeightByFreightClassFlag());
            assertTrue(container.isIgnoreShipmentItemsFlag());
            assertTrue(container.isIgnoreReferenceNumbersFlag());
        }

        @Test
        void container_withNullLineItems_noQuantityOrVolume() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setLineItems(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            ContainerType container = createCaptor.getValue().getShipment().getFirst().getContainer().getFirst();
            assertEquals("PIECE", container.getContainerTypeCode());
            assertNull(container.getQuantity());
            assertNull(container.getContainerShippingInformation());
        }

        @Test
        void container_withEmptyLineItems_noQuantityOrVolume() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setLineItems(new ArrayList<>());

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            ContainerType container = createCaptor.getValue().getShipment().getFirst().getContainer().getFirst();
            assertNull(container.getQuantity());
        }

        @Test
        void container_withNullPiecesAndVolume_noFieldsSet() {
            OrderShipmentMessage msg = buildFullMessage();
            LineItem li = new LineItem();
            li.setPieces(null);
            li.setVolumeCf(null);
            li.setWeightLb(null);
            msg.getOrder().setLineItems(List.of(li));

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            ContainerType container = createCaptor.getValue().getShipment().getFirst().getContainer().getFirst();
            assertNull(container.getQuantity());
            assertNull(container.getContainerShippingInformation());
            assertNotNull(container.getWeightByFreightClass());
            assertEquals("92.5", container.getWeightByFreightClass().getFirst().getFreightClassCode());
        }
    }

    // ========================================================================================
    // Reference number building tests
    // ========================================================================================

    @Nested
    class ReferenceNumberTests {

        @Test
        void references_orderRefsPassedThrough() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setReferences(List.of(
                    ref("DEPT_NBR", "100"),
                    ref("VENDOR_BOL", "BOL123")
            ));
            msg.getOrder().setCommodity(null);
            msg.setPrimaryLoc(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().anyMatch(r ->
                    "DEPT_NBR".equals(r.getReferenceNumberTypeCode()) && "100".equals(r.getReferenceNumber())));
            assertTrue(refs.stream().anyMatch(r ->
                    "VENDOR_BOL".equals(r.getReferenceNumberTypeCode()) && "BOL123".equals(r.getReferenceNumber())));
        }

        @Test
        void references_commodityCodeAdded() {
            OrderShipmentMessage msg = buildFullMessage();
            Commodity commodity = new Commodity();
            commodity.setCode("COMM01");
            msg.getOrder().setCommodity(commodity);
            msg.getOrder().setReferences(null);
            msg.setPrimaryLoc(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().anyMatch(r ->
                    "COMMODITY".equals(r.getReferenceNumberTypeCode()) && "COMM01".equals(r.getReferenceNumber())));
        }

        @Test
        void references_vndrAcctAddedWhenPrimaryLocPresent() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPrimaryLoc("PL1");
            LocationInfo origin = new LocationInfo();
            origin.setLocationId("VNDR-LOC");
            msg.getOrder().setOrigin(origin);
            msg.getOrder().setReferences(null);
            msg.getOrder().setCommodity(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().anyMatch(r ->
                    "VNDR_ACCT".equals(r.getReferenceNumberTypeCode()) && "VNDR-LOC".equals(r.getReferenceNumber())));
        }

        @Test
        void references_vndrAcctNotAddedWithoutPrimaryLoc() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPrimaryLoc(null);
            msg.getOrder().setReferences(null);
            msg.getOrder().setCommodity(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().noneMatch(r -> "VNDR_ACCT".equals(r.getReferenceNumberTypeCode())));
        }

        @Test
        void references_vndrAcctNotAddedWhenOriginNull() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPrimaryLoc("PL1");
            msg.getOrder().setOrigin(null);
            msg.getOrder().setReferences(null);
            msg.getOrder().setCommodity(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().noneMatch(r -> "VNDR_ACCT".equals(r.getReferenceNumberTypeCode())));
        }

        @Test
        void references_vndrAcctNotAddedWhenOriginLocationIdEmpty() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setPrimaryLoc("PL1");
            LocationInfo origin = new LocationInfo();
            origin.setLocationId("  ");
            msg.getOrder().setOrigin(origin);
            msg.getOrder().setReferences(null);
            msg.getOrder().setCommodity(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().noneMatch(r -> "VNDR_ACCT".equals(r.getReferenceNumberTypeCode())));
        }

        @Test
        void references_vendorNameAdded() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setVendorName("ACME Corp");
            msg.getOrder().setReferences(null);
            msg.getOrder().setCommodity(null);
            msg.setPrimaryLoc(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().anyMatch(r ->
                    "VendorName".equals(r.getReferenceNumberTypeCode()) && "ACME Corp".equals(r.getReferenceNumber())));
        }

        @Test
        void references_nullVendorName_notAdded() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.setVendorName(null);
            msg.getOrder().setReferences(null);
            msg.getOrder().setCommodity(null);
            msg.setPrimaryLoc(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().noneMatch(r -> "VendorName".equals(r.getReferenceNumberTypeCode())));
        }

        @Test
        void references_refWithEmptyType_skipped() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setReferences(List.of(
                    ref("", "VALUE"),
                    ref("DEPT_NBR", "100")
            ));
            msg.getOrder().setCommodity(null);
            msg.setPrimaryLoc(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertEquals(1, refs.size());
            assertEquals("DEPT_NBR", refs.getFirst().getReferenceNumberTypeCode());
        }

        @Test
        void references_refWithEmptyValue_skipped() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setReferences(List.of(
                    ref("TYPE1", "  "),
                    ref("DEPT_NBR", "200")
            ));
            msg.getOrder().setCommodity(null);
            msg.setPrimaryLoc(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertEquals(1, refs.size());
        }

        @Test
        void references_nullCommodity_notAdded() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setCommodity(null);
            msg.getOrder().setReferences(null);
            msg.setPrimaryLoc(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().noneMatch(r -> "COMMODITY".equals(r.getReferenceNumberTypeCode())));
        }

        @Test
        void references_commodityWithEmptyCode_notAdded() {
            OrderShipmentMessage msg = buildFullMessage();
            Commodity commodity = new Commodity();
            commodity.setCode("  ");
            msg.getOrder().setCommodity(commodity);
            msg.getOrder().setReferences(null);
            msg.setPrimaryLoc(null);
            msg.setVendorName(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.stream().noneMatch(r -> "COMMODITY".equals(r.getReferenceNumberTypeCode())));
        }
    }

    // ========================================================================================
    // Commodity code on shipment tests
    // ========================================================================================

    @Nested
    class CommodityCodeTests {

        @Test
        void commodityCode_setWhenPresent() {
            OrderShipmentMessage msg = buildFullMessage();
            Commodity commodity = new Commodity();
            commodity.setCode("CLOTH");
            msg.getOrder().setCommodity(commodity);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertEquals("CLOTH", createCaptor.getValue().getShipment().getFirst().getCommodityCode());
        }

        @Test
        void commodityCode_nullCommodity_notSet() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setCommodity(null);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertNull(createCaptor.getValue().getShipment().getFirst().getCommodityCode());
        }

        @Test
        void commodityCode_emptyCommodityCode_notSet() {
            OrderShipmentMessage msg = buildFullMessage();
            Commodity commodity = new Commodity();
            commodity.setCode("");
            msg.getOrder().setCommodity(commodity);

            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.restProcessCreateShipment(any())).thenReturn(response);

            orderService.createOrder(msg);

            verify(tmRestApiClient).restProcessCreateShipment(createCaptor.capture());
            assertNull(createCaptor.getValue().getShipment().getFirst().getCommodityCode());
        }
    }

    // ========================================================================================
    // updateOrder tests
    // ========================================================================================

    @Nested
    class UpdateOrderTests {

        @Test
        void updateOrder_happyPath_success() {
            OrderShipmentMessage msg = buildFullMessage();
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            orderService.updateOrder(msg);

            verify(tmRestApiClient).processUpdateShipment(createCaptor.capture());
            RestProcessShipmentOrderCreate captured = createCaptor.getValue();
            assertNotNull(captured.getShipment());
            assertEquals(1, captured.getShipment().size());
        }

        @Test
        void updateOrder_apiFailure_throwsException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ProcessShipmentOrderCreateResponse response = failedCreateResponse("500");
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            TmsLoadProcessException ex = assertThrows(TmsLoadProcessException.class,
                    () -> orderService.updateOrder(msg));
            assertTrue(ex.getMessage().contains("ORD-001"));
            assertTrue(ex.getMessage().contains("500"));
        }

        @Test
        void updateOrder_toleratedErrorCode3_noException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ResponseHeaderType header = new ResponseHeaderType();
            header.setCompletedSuccessfully(false);
            ErrorType error = new ErrorType();
            error.setCode(BigInteger.valueOf(3));
            header.setError(List.of(error));
            ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
            response.setResponseHeader(header);
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            assertDoesNotThrow(() -> orderService.updateOrder(msg));
        }

        @Test
        void updateOrder_mixedErrorCodes3And5_throwsException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ResponseHeaderType header = new ResponseHeaderType();
            header.setCompletedSuccessfully(false);
            ErrorType error3 = new ErrorType();
            error3.setCode(BigInteger.valueOf(3));
            ErrorType error5 = new ErrorType();
            error5.setCode(BigInteger.valueOf(5));
            header.setError(List.of(error3, error5));
            ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
            response.setResponseHeader(header);
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            assertThrows(TmsLoadProcessException.class, () -> orderService.updateOrder(msg));
        }

        @Test
        void updateOrder_emptyErrors_throwsException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ResponseHeaderType header = new ResponseHeaderType();
            header.setCompletedSuccessfully(false);
            header.setError(new ArrayList<>());
            ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
            response.setResponseHeader(header);
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            assertThrows(TmsLoadProcessException.class, () -> orderService.updateOrder(msg));
        }

        @Test
        void updateOrder_nullErrors_throwsException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ResponseHeaderType header = new ResponseHeaderType();
            header.setCompletedSuccessfully(false);
            header.setError(null);
            ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
            response.setResponseHeader(header);
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            assertThrows(TmsLoadProcessException.class, () -> orderService.updateOrder(msg));
        }

        @Test
        void updateOrder_nullResponseHeader_noException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
            response.setResponseHeader(null);
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            assertDoesNotThrow(() -> orderService.updateOrder(msg));
        }

        @Test
        void updateOrder_srr01DcByPassCollect_setsMitccStrategy() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setReferences(List.of(ref("SRR01", "DC By-Pass Collect Container")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            orderService.updateOrder(msg);

            verify(tmRestApiClient).processUpdateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertEquals("MITCC_DIFF_DESTLOC", shipment.getMitccStrategyEnumVal());
        }

        @Test
        void updateOrder_srr01DcByPassPrePaid_setsMitccStrategy() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setReferences(List.of(ref("SRR01", "DC By-Pass Pre-Paid Container")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            orderService.updateOrder(msg);

            verify(tmRestApiClient).processUpdateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertEquals("MITCC_DIFF_DESTLOC", shipment.getMitccStrategyEnumVal());
        }

        @Test
        void updateOrder_srr01OtherValue_clearsMitccStrategy() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setReferences(List.of(ref("SRR01", "Something Else")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            orderService.updateOrder(msg);

            verify(tmRestApiClient).processUpdateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertNull(shipment.getMitccStrategyEnumVal());
        }

        @Test
        void updateOrder_noSrr01Reference_clearsMitccStrategy() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setReferences(List.of(ref("OTHER", "Val")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            orderService.updateOrder(msg);

            verify(tmRestApiClient).processUpdateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertNull(shipment.getMitccStrategyEnumVal());
        }

        @Test
        void updateOrder_nullReferences_clearsMitccStrategy() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setReferences(null);
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            orderService.updateOrder(msg);

            verify(tmRestApiClient).processUpdateShipment(createCaptor.capture());
            RestShipmentType shipment = createCaptor.getValue().getShipment().getFirst();
            assertNull(shipment.getMitccStrategyEnumVal());
        }

        @Test
        void updateOrder_referenceNumbers_noCommodityVndrAcctVendorName() {
            OrderShipmentMessage msg = buildFullMessage();
            Commodity commodity = new Commodity();
            commodity.setCode("COMM01");
            msg.getOrder().setCommodity(commodity);
            msg.setPrimaryLoc("PL1");
            msg.setVendorName("Vendor");
            msg.getOrder().setReferences(List.of(ref("DEPT_NBR", "100")));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            orderService.updateOrder(msg);

            verify(tmRestApiClient).processUpdateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertEquals(1, refs.size());
            assertEquals("DEPT_NBR", refs.getFirst().getReferenceNumberTypeCode());
        }

        @Test
        void updateOrder_refWithNullType_skipped() {
            OrderShipmentMessage msg = buildFullMessage();
            Reference refNull = new Reference();
            refNull.setType(null);
            refNull.setValue("VAL");
            Reference refGood = ref("DEPT_NBR", "100");
            msg.getOrder().setReferences(List.of(refNull, refGood));
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            orderService.updateOrder(msg);

            verify(tmRestApiClient).processUpdateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertEquals(1, refs.size());
        }

        @Test
        void updateOrder_nullRefList_emptyRefs() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setReferences(null);
            ProcessShipmentOrderCreateResponse response = successCreateResponse();
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            orderService.updateOrder(msg);

            verify(tmRestApiClient).processUpdateShipment(createCaptor.capture());
            List<ReferenceNumberType> refs = createCaptor.getValue().getShipment().getFirst().getReferenceNumberStructure();
            assertTrue(refs.isEmpty());
        }

        @Test
        void updateOrder_errorWithNullCode_filtered()  {
            OrderShipmentMessage msg = buildFullMessage();
            ResponseHeaderType header = new ResponseHeaderType();
            header.setCompletedSuccessfully(false);
            ErrorType errorNoCode = new ErrorType();
            errorNoCode.setCode(null);
            ErrorType error3 = new ErrorType();
            error3.setCode(BigInteger.valueOf(3));
            header.setError(List.of(errorNoCode, error3));
            ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
            response.setResponseHeader(header);
            when(tmRestApiClient.processUpdateShipment(any())).thenReturn(response);

            assertDoesNotThrow(() -> orderService.updateOrder(msg));
        }
    }

    // ========================================================================================
    // deleteOrder tests
    // ========================================================================================

    @Nested
    class DeleteOrderTests {

        @Test
        void deleteOrder_happyPath_success() {
            OrderShipmentMessage msg = buildFullMessage();
            ProcessShipmentOrderDeleteResponse response = successDeleteResponse();
            when(tmRestApiClient.restProcessDeleteShipment(any())).thenReturn(response);

            orderService.deleteOrder(msg);

            verify(tmRestApiClient).restProcessDeleteShipment(deleteCaptor.capture());
            RestProcessShipmentOrderDelete captured = deleteCaptor.getValue();
            assertEquals("ORD-001", captured.getShipment().getFirst().getShipmentNumber());
        }

        @Test
        void deleteOrder_apiFailure_throwsException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ResponseHeaderType header = new ResponseHeaderType();
            header.setCompletedSuccessfully(false);
            ErrorType error = new ErrorType();
            error.setCode(BigInteger.valueOf(404));
            header.setError(List.of(error));
            ProcessShipmentOrderDeleteResponse response = new ProcessShipmentOrderDeleteResponse();
            response.setResponseHeader(header);
            when(tmRestApiClient.restProcessDeleteShipment(any())).thenReturn(response);

            TmsLoadProcessException ex = assertThrows(TmsLoadProcessException.class,
                    () -> orderService.deleteOrder(msg));
            assertTrue(ex.getMessage().contains("ORD-001"));
            assertTrue(ex.getMessage().contains("404"));
        }

        @Test
        void deleteOrder_nullResponseHeader_noException()  {
            OrderShipmentMessage msg = buildFullMessage();
            ProcessShipmentOrderDeleteResponse response = new ProcessShipmentOrderDeleteResponse();
            response.setResponseHeader(null);
            when(tmRestApiClient.restProcessDeleteShipment(any())).thenReturn(response);

            assertDoesNotThrow(() -> orderService.deleteOrder(msg));
        }

        @Test
        void deleteOrder_nullOrderId_setsShipmentNumberNull() {
            OrderShipmentMessage msg = buildFullMessage();
            msg.getOrder().setOrderId(null);
            ProcessShipmentOrderDeleteResponse response = successDeleteResponse();
            when(tmRestApiClient.restProcessDeleteShipment(any())).thenReturn(response);

            orderService.deleteOrder(msg);

            verify(tmRestApiClient).restProcessDeleteShipment(deleteCaptor.capture());
            assertNull(deleteCaptor.getValue().getShipment().getFirst().getShipmentNumber());
        }

        @Test
        void deleteOrder_failedWithNullErrorList_extractsEmptyString() {
            OrderShipmentMessage msg = buildFullMessage();
            ResponseHeaderType mockHeader = mock(ResponseHeaderType.class);
            when(mockHeader.isCompletedSuccessfully()).thenReturn(false);
            when(mockHeader.getError()).thenReturn(null);
            ProcessShipmentOrderDeleteResponse response = new ProcessShipmentOrderDeleteResponse();
            response.setResponseHeader(mockHeader);
            when(tmRestApiClient.restProcessDeleteShipment(any())).thenReturn(response);

            TmsLoadProcessException ex = assertThrows(TmsLoadProcessException.class,
                    () -> orderService.deleteOrder(msg));
            assertTrue(ex.getMessage().contains("ORD-001"));
        }

        @Test
        void deleteOrder_multipleErrors_extractsCommaSeparated()  {
            OrderShipmentMessage msg = buildFullMessage();
            ResponseHeaderType header = new ResponseHeaderType();
            header.setCompletedSuccessfully(false);
            ErrorType error1 = new ErrorType();
            error1.setCode(BigInteger.valueOf(100));
            ErrorType error2 = new ErrorType();
            error2.setCode(BigInteger.valueOf(200));
            header.setError(List.of(error1, error2));
            ProcessShipmentOrderDeleteResponse response = new ProcessShipmentOrderDeleteResponse();
            response.setResponseHeader(header);
            when(tmRestApiClient.restProcessDeleteShipment(any())).thenReturn(response);

            TmsLoadProcessException ex = assertThrows(TmsLoadProcessException.class,
                    () -> orderService.deleteOrder(msg));
            assertTrue(ex.getMessage().contains("100"));
            assertTrue(ex.getMessage().contains("200"));
        }
    }

    // ========================================================================================
    // Helper methods
    // ========================================================================================

    private OrderShipmentMessage buildFullMessage() {
        OrderShipmentMessage msg = new OrderShipmentMessage();
        msg.setTransCode("A");
        msg.setFbNbr("FB-001");

        Order order = new Order();
        order.setOrderId("ORD-001");

        LocationInfo origin = new LocationInfo();
        origin.setLocationId("ORIGIN1");
        order.setOrigin(origin);

        LocationInfo dest = new LocationInfo();
        dest.setLocationId("DEST1");
        order.setDestination(dest);

        order.setFreightTerms("COL");

        msg.setOrder(order);
        return msg;
    }

    private Reference ref(String type, String value) {
        Reference r = new Reference();
        r.setType(type);
        r.setValue(value);
        return r;
    }

    private ProcessShipmentOrderCreateResponse successCreateResponse() {
        ResponseHeaderType header = new ResponseHeaderType();
        header.setCompletedSuccessfully(true);
        ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
        response.setResponseHeader(header);
        return response;
    }

    private ProcessShipmentOrderCreateResponse failedCreateResponse(String errorCode) {
        ResponseHeaderType header = new ResponseHeaderType();
        header.setCompletedSuccessfully(false);
        ErrorType error = new ErrorType();
        error.setCode(new BigInteger(errorCode));
        header.setError(List.of(error));
        ProcessShipmentOrderCreateResponse response = new ProcessShipmentOrderCreateResponse();
        response.setResponseHeader(header);
        return response;
    }

    private ProcessShipmentOrderDeleteResponse successDeleteResponse() {
        ResponseHeaderType header = new ResponseHeaderType();
        header.setCompletedSuccessfully(true);
        ProcessShipmentOrderDeleteResponse response = new ProcessShipmentOrderDeleteResponse();
        response.setResponseHeader(header);
        return response;
    }
}
