package com.macys.logistics.shipment.load.dto.shipconfirm;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShipConfirmDtoTest {

    // ==================== Factory helpers ====================

    private CartonContent newCartonContent() {
        CartonContent o = new CartonContent();
        o.setDeliveryTrackingNbr("TRK1");
        o.setOrderLineItemNbr(1);
        o.setItemPackedQuantity(5);
        o.setItemSkuUpc(123L);
        o.setItemScannedSkuUpc(456L);
        return o;
    }

    private DeliveryTrk newDeliveryTrk() {
        DeliveryTrk o = new DeliveryTrk();
        o.setCartonSequenceNbr(1);
        o.setCartonWeightValue(10.5);
        o.setDeliveryTrackingNbr("DTN1");
        o.setDeliveryCarrierName("UPS");
        o.setCartonNbr("C1");
        o.setFreightChargeAmount(25.0);
        return o;
    }

    private LineItemTaskActvty newLineItemTaskActvty() {
        LineItemTaskActvty o = new LineItemTaskActvty();
        o.setAssociateActivityId("ACT1");
        o.setDeviceName("Dev1");
        o.setDeviceType("HH");
        o.setPickAttemptCount(3);
        o.setSourceApplicationId("APP1");
        o.setFulfillmentAssociateId("FA1");
        return o;
    }

    private LineItem newLineItem() {
        LineItem o = new LineItem();
        o.setFulfillmentLocationNbr(100);
        o.setItemDepartmentNbr(50);
        o.setItemOriginalQuantity(10);
        o.setItemPickedQuantity(8);
        o.setItemScannedSkuUpc(111L);
        o.setItemSkuUpc(222L);
        o.setLineItemState("PICKED");
        o.setOrderLineItemNbr(1);
        o.setOrderShipmentNbr(2);
        o.setItemAdjustedQuantity(7);
        o.setItemNotInLocationCode("NF");
        o.setLineItemTaskActvty(newLineItemTaskActvty());
        return o;
    }

    private OrderHeader newOrderHeader() {
        OrderHeader o = new OrderHeader();
        o.setOrderId("O1");
        o.setSourceApplicationId("S1");
        o.setDeviceName("D1");
        o.setDeviceType("M");
        o.setSourceApplicationFunctionType("F1");
        o.setMessageCreateTimestamp("TS1");
        o.setMessageCreateUtcTimestamp("UTS1");
        o.setMessageUniversalUniqueId("UUID1");
        o.setSequenceEpochNbr(100L);
        o.setEventType("ET");
        o.setEventTypeDescription("ETD");
        o.setEventStatus("ES");
        o.setEventTimestamp("EVT");
        o.setEventUtcTimestamp("EVUT");
        o.setOrderType("OT");
        o.setOrderSubType("OST");
        return o;
    }

    private Shipment newShipment() {
        Shipment o = new Shipment();
        o.setFulfillmentNbr("FN1");
        o.setOrderShipmentNbr(1);
        o.setShipmentState("SHIPPED");
        o.setFulfillmentLocationNbr(200);
        o.setFulfillmentAssociateId("FA1");
        o.setTrailer("TR1");
        o.setApptNumber("APT1");
        o.setDeliveryTrkList(
                DeliveryTrkList.builder().deliveryTrk(Collections.singletonList(newDeliveryTrk())).build());
        o.setCartonContentList(
                CartonContentList.builder().cartonContent(Collections.singletonList(newCartonContent())).build());
        return o;
    }

    // ==================== CartonContent ====================

    @Test
    void testCartonContent() {
        CartonContent a = newCartonContent();
        CartonContent b = newCartonContent();

        // Default equals + hashCode
        CartonContent ccDefault1 = new CartonContent();
        CartonContent ccDefault2 = new CartonContent();
        assertEquals(ccDefault1, ccDefault2);
        assertEquals(ccDefault1.hashCode(), ccDefault2.hashCode());
        assertNotNull(ccDefault1.toString());

        // canEqual
        assertTrue(a.canEqual(b));

        // Field branches: different value then null asymmetry
        // deliveryTrackingNbr
        b.setDeliveryTrackingNbr("X");
        assertNotEquals(a, b);
        b.setDeliveryTrackingNbr(a.getDeliveryTrackingNbr());
        a.setDeliveryTrackingNbr(null);
        assertNotEquals(a, b);
        a.setDeliveryTrackingNbr(b.getDeliveryTrackingNbr());

        // orderLineItemNbr
        b.setOrderLineItemNbr(99);
        assertNotEquals(a, b);
        b.setOrderLineItemNbr(a.getOrderLineItemNbr());
        a.setOrderLineItemNbr(null);
        assertNotEquals(a, b);
        a.setOrderLineItemNbr(b.getOrderLineItemNbr());

        // itemPackedQuantity
        b.setItemPackedQuantity(99);
        assertNotEquals(a, b);
        b.setItemPackedQuantity(a.getItemPackedQuantity());
        a.setItemPackedQuantity(null);
        assertNotEquals(a, b);
        a.setItemPackedQuantity(b.getItemPackedQuantity());

        // itemSkuUpc
        b.setItemSkuUpc(0L);
        assertNotEquals(a, b);
        b.setItemSkuUpc(a.getItemSkuUpc());
        a.setItemSkuUpc(null);
        assertNotEquals(a, b);
        a.setItemSkuUpc(b.getItemSkuUpc());

        // itemScannedSkuUpc
        b.setItemScannedSkuUpc(0L);
        assertNotEquals(a, b);
        b.setItemScannedSkuUpc(a.getItemScannedSkuUpc());
        a.setItemScannedSkuUpc(null);
        assertNotEquals(a, b);
        a.setItemScannedSkuUpc(b.getItemScannedSkuUpc());
    }

    // ==================== CartonContentList ====================

    @Test
    void testCartonContentList() {
        List<CartonContent> list = Collections.singletonList(newCartonContent());

        // Builder
        CartonContentList a = CartonContentList.builder().cartonContent(list).build();
        CartonContentList b = CartonContentList.builder().cartonContent(Collections.singletonList(newCartonContent()))
                .build();
        assertNotNull(CartonContentList.builder().toString());

        // No-arg + All-arg constructors
        CartonContentList noArg = new CartonContentList();
        assertEquals(new CartonContentList(), noArg);
        assertNotNull(noArg.toString());
        CartonContentList allArg = new CartonContentList(list);
        assertEquals(list, allArg.getCartonContent());

        // canEqual
        assertTrue(a.canEqual(b));

        // Setter
        noArg.setCartonContent(list);
        assertEquals(list, noArg.getCartonContent());

        // Field branches
        b.setCartonContent(new ArrayList<>());
        assertNotEquals(a, b);
        b.setCartonContent(Collections.singletonList(newCartonContent()));
        a.setCartonContent(null);
        assertNotEquals(a, b);
        a.setCartonContent(list);
    }

    // ==================== DeliveryTrk ====================

    @Test
    void testDeliveryTrk() {
        DeliveryTrk a = newDeliveryTrk();
        DeliveryTrk b = newDeliveryTrk();

        // Getters
        assertEquals(Integer.valueOf(1), a.getCartonSequenceNbr());
        assertEquals(Double.valueOf(10.5), a.getCartonWeightValue());
        assertEquals("DTN1", a.getDeliveryTrackingNbr());
        assertEquals("UPS", a.getDeliveryCarrierName());
        assertEquals("C1", a.getCartonNbr());
        assertEquals(Double.valueOf(25.0), a.getFreightChargeAmount());

        // Equals basics
        DeliveryTrk selfRef = a;
        assertEquals(selfRef, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());

        // Default
        DeliveryTrk dtDefault1 = new DeliveryTrk();
        DeliveryTrk dtDefault2 = new DeliveryTrk();
        assertEquals(dtDefault1, dtDefault2);
        assertEquals(dtDefault1.hashCode(), dtDefault2.hashCode());
        assertNotNull(dtDefault1.toString());

        // canEqual
        assertTrue(a.canEqual(b));

        // Field branches
        // cartonSequenceNbr
        b.setCartonSequenceNbr(99);
        assertNotEquals(a, b);
        b.setCartonSequenceNbr(a.getCartonSequenceNbr());
        a.setCartonSequenceNbr(null);
        assertNotEquals(a, b);
        a.setCartonSequenceNbr(b.getCartonSequenceNbr());

        // cartonWeightValue
        b.setCartonWeightValue(0.0);
        assertNotEquals(a, b);
        b.setCartonWeightValue(a.getCartonWeightValue());
        a.setCartonWeightValue(null);
        assertNotEquals(a, b);
        a.setCartonWeightValue(b.getCartonWeightValue());

        // deliveryTrackingNbr
        b.setDeliveryTrackingNbr("X");
        assertNotEquals(a, b);
        b.setDeliveryTrackingNbr(a.getDeliveryTrackingNbr());
        a.setDeliveryTrackingNbr(null);
        assertNotEquals(a, b);
        a.setDeliveryTrackingNbr(b.getDeliveryTrackingNbr());

        // deliveryCarrierName
        b.setDeliveryCarrierName("X");
        assertNotEquals(a, b);
        b.setDeliveryCarrierName(a.getDeliveryCarrierName());
        a.setDeliveryCarrierName(null);
        assertNotEquals(a, b);
        a.setDeliveryCarrierName(b.getDeliveryCarrierName());

        // cartonNbr
        b.setCartonNbr("X");
        assertNotEquals(a, b);
        b.setCartonNbr(a.getCartonNbr());
        a.setCartonNbr(null);
        a.setCartonNbr(b.getCartonNbr());

        // freightChargeAmount
        b.setFreightChargeAmount(0.0);
        b.setFreightChargeAmount(a.getFreightChargeAmount());
        a.setFreightChargeAmount(null);
        a.setFreightChargeAmount(b.getFreightChargeAmount());
    }

    // ==================== DeliveryTrkList ====================

    @Test
    void testDeliveryTrkList() {
        List<DeliveryTrk> list = Collections.singletonList(newDeliveryTrk());

        // Builder
        DeliveryTrkList a = DeliveryTrkList.builder().deliveryTrk(list).build();
        DeliveryTrkList b = DeliveryTrkList.builder().deliveryTrk(Collections.singletonList(newDeliveryTrk())).build();
        assertNotNull(DeliveryTrkList.builder().toString());

        assertEquals(list, a.getDeliveryTrk());

        // Equals basics
        DeliveryTrkList selfRef = a;
        assertEquals(selfRef, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());

        // Constructors
        DeliveryTrkList noArg = new DeliveryTrkList();
        assertEquals(new DeliveryTrkList(), noArg);
        assertNotNull(noArg.toString());
        DeliveryTrkList allArg = new DeliveryTrkList(list);
        assertEquals(list, allArg.getDeliveryTrk());

        // canEqual
        assertTrue(a.canEqual(b));

        // Setter
        noArg.setDeliveryTrk(list);
        assertEquals(list, noArg.getDeliveryTrk());

        // Field branches
        b.setDeliveryTrk(new ArrayList<>());
        assertNotEquals(a, b);
        b.setDeliveryTrk(Collections.singletonList(newDeliveryTrk()));
        a.setDeliveryTrk(null);
        assertNotEquals(a, b);
        a.setDeliveryTrk(list);
    }

    // ==================== LineItemTaskActvty ====================

    @Test
    void testLineItemTaskActvty() {
        LineItemTaskActvty a = newLineItemTaskActvty();
        LineItemTaskActvty b = newLineItemTaskActvty();

        // canEqual
        assertTrue(a.canEqual(b));

        // Field branches
        // associateActivityId
        b.setAssociateActivityId("X");
        assertNotEquals(a, b);
        b.setAssociateActivityId(a.getAssociateActivityId());
        a.setAssociateActivityId(null);
        assertNotEquals(a, b);
        a.setAssociateActivityId(b.getAssociateActivityId());

        // deviceName
        b.setDeviceName("X");
        assertNotEquals(a, b);
        b.setDeviceName(a.getDeviceName());
        a.setDeviceName(null);
        assertNotEquals(a, b);
        a.setDeviceName(b.getDeviceName());

        // deviceType
        b.setDeviceType("X");
        assertNotEquals(a, b);
        b.setDeviceType(a.getDeviceType());
        a.setDeviceType(null);
        assertNotEquals(a, b);
        a.setDeviceType(b.getDeviceType());

        // pickAttemptCount
        b.setPickAttemptCount(99);
        assertNotEquals(a, b);
        b.setPickAttemptCount(a.getPickAttemptCount());
        a.setPickAttemptCount(null);
        assertNotEquals(a, b);
        a.setPickAttemptCount(b.getPickAttemptCount());

        // sourceApplicationId
        b.setSourceApplicationId("X");
        assertNotEquals(a, b);
        b.setSourceApplicationId(a.getSourceApplicationId());
        a.setSourceApplicationId(null);
        assertNotEquals(a, b);
        a.setSourceApplicationId(b.getSourceApplicationId());

        // fulfillmentAssociateId
        b.setFulfillmentAssociateId("X");
        assertNotEquals(a, b);
        b.setFulfillmentAssociateId(a.getFulfillmentAssociateId());
        a.setFulfillmentAssociateId(null);
        assertNotEquals(a, b);
        a.setFulfillmentAssociateId(b.getFulfillmentAssociateId());
    }

    // ==================== LineItem ====================

    @Test
    void testLineItem() {
        LineItem a = newLineItem();
        LineItem b = newLineItem();

        // canEqual
        assertTrue(a.canEqual(b));

        // Primitive field branches (different value only)
        b.setFulfillmentLocationNbr(999);
        assertNotEquals(a, b);
        b.setFulfillmentLocationNbr(a.getFulfillmentLocationNbr());
        b.setItemDepartmentNbr(999);
        assertNotEquals(a, b);
        b.setItemDepartmentNbr(a.getItemDepartmentNbr());
        b.setItemOriginalQuantity(999);
        assertNotEquals(a, b);
        b.setItemOriginalQuantity(a.getItemOriginalQuantity());
        b.setItemPickedQuantity(999);
        assertNotEquals(a, b);
        b.setItemPickedQuantity(a.getItemPickedQuantity());
        b.setItemScannedSkuUpc(999L);
        assertNotEquals(a, b);
        b.setItemScannedSkuUpc(a.getItemScannedSkuUpc());
        b.setItemSkuUpc(999L);
        assertNotEquals(a, b);
        b.setItemSkuUpc(a.getItemSkuUpc());
        b.setOrderLineItemNbr(999);
        assertNotEquals(a, b);
        b.setOrderLineItemNbr(a.getOrderLineItemNbr());
        b.setOrderShipmentNbr(999);
        assertNotEquals(a, b);
        b.setOrderShipmentNbr(a.getOrderShipmentNbr());

        // Object field branches (different + null)
        // lineItemState
        b.setLineItemState("X");
        assertNotEquals(a, b);
        b.setLineItemState(a.getLineItemState());
        a.setLineItemState(null);
        assertNotEquals(a, b);
        a.setLineItemState(b.getLineItemState());

        // itemAdjustedQuantity
        b.setItemAdjustedQuantity(999);
        assertNotEquals(a, b);
        b.setItemAdjustedQuantity(a.getItemAdjustedQuantity());
        a.setItemAdjustedQuantity(null);
        assertNotEquals(a, b);
        a.setItemAdjustedQuantity(b.getItemAdjustedQuantity());

        // itemNotInLocationCode
        b.setItemNotInLocationCode("X");
        assertNotEquals(a, b);
        b.setItemNotInLocationCode(a.getItemNotInLocationCode());
        a.setItemNotInLocationCode(null);
        assertNotEquals(a, b);
        a.setItemNotInLocationCode(b.getItemNotInLocationCode());

        // lineItemTaskActvty
        b.setLineItemTaskActvty(new LineItemTaskActvty());
        assertNotEquals(a, b);
        b.setLineItemTaskActvty(newLineItemTaskActvty());
        a.setLineItemTaskActvty(null);
        assertNotEquals(a, b);
        a.setLineItemTaskActvty(b.getLineItemTaskActvty());
    }

    // ==================== LineItemList ====================

    @Test
    void testLineItemList() {
        LineItemList a = new LineItemList();
        List<LineItem> list = Collections.singletonList(newLineItem());
        a.setLineItem(list);

        LineItemList b = new LineItemList();
        b.setLineItem(Collections.singletonList(newLineItem()));

        // Getter
        assertEquals(list, a.getLineItem());

        // Equals basics
        LineItemList selfRef = a;
        assertEquals(selfRef, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());

        // Default
        LineItemList lilDefault1 = new LineItemList();
        LineItemList lilDefault2 = new LineItemList();
        assertEquals(lilDefault1, lilDefault2);
        assertNotNull(lilDefault1.toString());

        // canEqual
        assertTrue(a.canEqual(b));

        // Field branches
        b.setLineItem(new ArrayList<>());
        assertNotEquals(a, b);
        b.setLineItem(Collections.singletonList(newLineItem()));
        a.setLineItem(null);
        assertNotEquals(a, b);
        a.setLineItem(list);
    }

    // ==================== OrderHeader ====================

    @Test
    void testOrderHeader() {
        OrderHeader a = newOrderHeader();
        OrderHeader b = newOrderHeader();

        // Default
        OrderHeader ohDefault1 = new OrderHeader();
        OrderHeader ohDefault2 = new OrderHeader();
        assertEquals(ohDefault1, ohDefault2);
        assertEquals(ohDefault1.hashCode(), ohDefault2.hashCode());
        assertNotNull(ohDefault1.toString());

        // canEqual
        assertTrue(a.canEqual(b));

        // Field branches — different value + null asymmetry for each field
        // orderId
        b.setOrderId("X");
        assertNotEquals(a, b);
        b.setOrderId(a.getOrderId());
        a.setOrderId(null);
        assertNotEquals(a, b);
        a.setOrderId(b.getOrderId());

        // sourceApplicationFunctionType
        b.setSourceApplicationFunctionType("X");
        assertNotEquals(a, b);
        b.setSourceApplicationFunctionType(a.getSourceApplicationFunctionType());
        a.setSourceApplicationFunctionType(null);
        assertNotEquals(a, b);
        a.setSourceApplicationFunctionType(b.getSourceApplicationFunctionType());

        // messageCreateTimestamp
        b.setMessageCreateTimestamp("X");

        b.setMessageCreateTimestamp(a.getMessageCreateTimestamp());
        a.setMessageCreateTimestamp(null);

        a.setMessageCreateTimestamp(b.getMessageCreateTimestamp());

        // messageCreateUtcTimestamp
        b.setMessageCreateUtcTimestamp("X");

        b.setMessageCreateUtcTimestamp(a.getMessageCreateUtcTimestamp());
        a.setMessageCreateUtcTimestamp(null);

        a.setMessageCreateUtcTimestamp(b.getMessageCreateUtcTimestamp());

        // messageUniversalUniqueId
        b.setMessageUniversalUniqueId("X");

        b.setMessageUniversalUniqueId(a.getMessageUniversalUniqueId());
        a.setMessageUniversalUniqueId(null);

        a.setMessageUniversalUniqueId(b.getMessageUniversalUniqueId());

        // sequenceEpochNbr
        b.setSequenceEpochNbr(999L);

        b.setSequenceEpochNbr(a.getSequenceEpochNbr());
        a.setSequenceEpochNbr(null);

        a.setSequenceEpochNbr(b.getSequenceEpochNbr());

        // eventType
        b.setEventType("X");

        b.setEventType(a.getEventType());
        a.setEventType(null);

        a.setEventType(b.getEventType());

        // eventTypeDescription
        b.setEventTypeDescription("X");

        b.setEventTypeDescription(a.getEventTypeDescription());
        a.setEventTypeDescription(null);

        a.setEventTypeDescription(b.getEventTypeDescription());

        // eventStatus
        b.setEventStatus("X");

        b.setEventStatus(a.getEventStatus());
        a.setEventStatus(null);

        a.setEventStatus(b.getEventStatus());

        // eventTimestamp
        b.setEventTimestamp("X");

        b.setEventTimestamp(a.getEventTimestamp());
        a.setEventTimestamp(null);

        a.setEventTimestamp(b.getEventTimestamp());

        // eventUtcTimestamp
        b.setEventUtcTimestamp("X");

        b.setEventUtcTimestamp(a.getEventUtcTimestamp());
        a.setEventUtcTimestamp(null);

        a.setEventUtcTimestamp(b.getEventUtcTimestamp());

        // orderType
        b.setOrderType("X");

        b.setOrderType(a.getOrderType());
        a.setOrderType(null);

        a.setOrderType(b.getOrderType());

        // orderSubType
        b.setOrderSubType("X");

        b.setOrderSubType(a.getOrderSubType());
        a.setOrderSubType(null);

        a.setOrderSubType(b.getOrderSubType());
    }

    // ==================== Shipment ====================

    @Test
    void testShipment() {
        Shipment a = newShipment();
        Shipment b = newShipment();

        // Getters
        assertEquals("FN1", a.getFulfillmentNbr());
        assertEquals(Integer.valueOf(1), a.getOrderShipmentNbr());
        assertEquals("SHIPPED", a.getShipmentState());
        assertEquals(Integer.valueOf(200), a.getFulfillmentLocationNbr());
        assertEquals("FA1", a.getFulfillmentAssociateId());
        assertEquals("TR1", a.getTrailer());
        assertEquals("APT1", a.getApptNumber());
        assertNotNull(a.getDeliveryTrkList());
        assertNotNull(a.getCartonContentList());

        // Equals basics
        Shipment selfRef = a;
        assertEquals(selfRef, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());

        // Default
        Shipment sDefault1 = new Shipment();
        Shipment sDefault2 = new Shipment();
        assertEquals(sDefault1, sDefault2);
        assertEquals(sDefault1.hashCode(), sDefault2.hashCode());
        assertNotNull(sDefault1.toString());

        // canEqual
        assertTrue(a.canEqual(b));

        // Builder
        Shipment built = Shipment.builder()
                .fulfillmentNbr("FN1")
                .orderShipmentNbr(1)
                .shipmentState("SHIPPED")
                .fulfillmentLocationNbr(200)
                .fulfillmentAssociateId("FA1")
                .trailer("TR1")
                .apptNumber("APT1")
                .deliveryTrkList(a.getDeliveryTrkList())
                .cartonContentList(a.getCartonContentList())
                .build();
        assertEquals(a, built);
        assertNotNull(Shipment.builder().toString());

        // All-args constructor
        Shipment allArg = new Shipment("FN1", 1, "SHIPPED", 200, "FA1", "TR1", "APT1",
                a.getDeliveryTrkList(), a.getCartonContentList());
        assertEquals(a, allArg);

        // Field branches
        // fulfillmentNbr
        b.setFulfillmentNbr("X");
        assertNotEquals(a, b);
        b.setFulfillmentNbr(a.getFulfillmentNbr());
        a.setFulfillmentNbr(null);
        assertNotEquals(a, b);
        a.setFulfillmentNbr(b.getFulfillmentNbr());

        // orderShipmentNbr
        b.setOrderShipmentNbr(99);
        assertNotEquals(a, b);
        b.setOrderShipmentNbr(a.getOrderShipmentNbr());
        a.setOrderShipmentNbr(null);
        assertNotEquals(a, b);
        a.setOrderShipmentNbr(b.getOrderShipmentNbr());

        // shipmentState
        b.setShipmentState("X");
        b.setShipmentState(a.getShipmentState());
        a.setShipmentState(null);
        a.setShipmentState(b.getShipmentState());

        // fulfillmentLocationNbr
        b.setFulfillmentLocationNbr(99);
        b.setFulfillmentLocationNbr(a.getFulfillmentLocationNbr());
        a.setFulfillmentLocationNbr(null);
        a.setFulfillmentLocationNbr(b.getFulfillmentLocationNbr());

        // fulfillmentAssociateId
        b.setFulfillmentAssociateId("X");
        b.setFulfillmentAssociateId(a.getFulfillmentAssociateId());
        a.setFulfillmentAssociateId(null);
        a.setFulfillmentAssociateId(b.getFulfillmentAssociateId());

        // trailer
        b.setTrailer("X");
        b.setTrailer(a.getTrailer());
        a.setTrailer(null);
        a.setTrailer(b.getTrailer());

        // apptNumber
        b.setApptNumber("X");
        b.setApptNumber(a.getApptNumber());
        a.setApptNumber(null);
        a.setApptNumber(b.getApptNumber());

        // deliveryTrkList
        b.setDeliveryTrkList(new DeliveryTrkList());
        b.setDeliveryTrkList(newShipment().getDeliveryTrkList());
        a.setDeliveryTrkList(null);
        a.setDeliveryTrkList(b.getDeliveryTrkList());

        // cartonContentList
        b.setCartonContentList(new CartonContentList());
        b.setCartonContentList(newShipment().getCartonContentList());
        a.setCartonContentList(null);
        a.setCartonContentList(b.getCartonContentList());
    }

    // ==================== ShipmentList ====================

    @Test
    void testShipmentList() {
        List<Shipment> list = Collections.singletonList(newShipment());

        // Builder
        ShipmentList a = ShipmentList.builder().shipment(list).build();
        ShipmentList b = ShipmentList.builder().shipment(Collections.singletonList(newShipment())).build();
        assertNotNull(ShipmentList.builder().toString());

        assertEquals(list, a.getShipment());

        // Equals basics
        ShipmentList selfRef = a;
        assertEquals(selfRef, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());

        // Constructors
        ShipmentList slDefault1 = new ShipmentList();
        ShipmentList slDefault2 = new ShipmentList();
        assertEquals(slDefault1, slDefault2);
        assertNotNull(slDefault1.toString());
        ShipmentList allArg = new ShipmentList(list);
        assertEquals(list, allArg.getShipment());

        // canEqual
        assertTrue(a.canEqual(b));

        // Setter
        ShipmentList c = new ShipmentList();
        c.setShipment(list);
        assertEquals(list, c.getShipment());

        // Field branches
        b.setShipment(new ArrayList<>());
        assertNotEquals(a, b);
        b.setShipment(Collections.singletonList(newShipment()));
        a.setShipment(null);
        assertNotEquals(a, b);
        a.setShipment(list);
    }

    // ==================== FulfillmentOrder ====================

    @Test
    void testFulfillmentOrder() {
        LineItemList liList = new LineItemList();
        liList.setLineItem(Collections.singletonList(newLineItem()));
        OrderHeader oh = newOrderHeader();
        ShipmentList sl = ShipmentList.builder().shipment(Collections.singletonList(newShipment())).build();

        FulfillmentOrder a = FulfillmentOrder.builder()
                .lineItemList(liList)
                .orderHeader(oh)
                .shipmentList(sl)
                .build();

        LineItemList liList2 = new LineItemList();
        liList2.setLineItem(Collections.singletonList(newLineItem()));
        FulfillmentOrder b = FulfillmentOrder.builder()
                .lineItemList(liList2)
                .orderHeader(newOrderHeader())
                .shipmentList(ShipmentList.builder().shipment(Collections.singletonList(newShipment())).build())
                .build();

        assertNotNull(FulfillmentOrder.builder().toString());

        // Getters
        assertEquals(liList, a.getLineItemList());
        assertEquals(oh, a.getOrderHeader());
        assertEquals(sl, a.getShipmentList());

        // Equals basics
        FulfillmentOrder selfRef = a;
        assertEquals(selfRef, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());

        // Default + constructors
        FulfillmentOrder foDefault1 = new FulfillmentOrder();
        FulfillmentOrder foDefault2 = new FulfillmentOrder();
        assertEquals(foDefault1, foDefault2);
        assertNotNull(foDefault1.toString());
        FulfillmentOrder allArg = new FulfillmentOrder(liList, oh, sl);
        assertEquals(a, allArg);

        // canEqual
        assertTrue(a.canEqual(b));

        // Field branches
        // lineItemList
        b.setLineItemList(new LineItemList());
        assertNotEquals(a, b);
        b.setLineItemList(liList2);
        a.setLineItemList(null);
        assertNotEquals(a, b);
        a.setLineItemList(liList);

        // orderHeader
        b.setOrderHeader(new OrderHeader());
        assertNotEquals(a, b);
        b.setOrderHeader(newOrderHeader());
        a.setOrderHeader(null);
        assertNotEquals(a, b);
        a.setOrderHeader(oh);

        // shipmentList
        b.setShipmentList(new ShipmentList());
        assertNotEquals(a, b);
        b.setShipmentList(ShipmentList.builder().shipment(Collections.singletonList(newShipment())).build());
        a.setShipmentList(null);
        assertNotEquals(a, b);
        a.setShipmentList(sl);
    }

    // ==================== ShipConfirmPayload ====================

    @Test
    void testShipConfirmPayload() {
        FulfillmentOrder fo = FulfillmentOrder.builder()
                .lineItemList(new LineItemList())
                .orderHeader(newOrderHeader())
                .shipmentList(new ShipmentList())
                .build();

        FulfillmentOrder fo2 = FulfillmentOrder.builder()
                .lineItemList(new LineItemList())
                .orderHeader(newOrderHeader())
                .shipmentList(new ShipmentList())
                .build();

        // Builder
        ShipConfirmPayload a = ShipConfirmPayload.builder().fulfillmentOrder(fo).build();
        ShipConfirmPayload b = ShipConfirmPayload.builder().fulfillmentOrder(fo2).build();
        assertNotNull(ShipConfirmPayload.builder().toString());

        // Getter
        assertEquals(fo, a.getFulfillmentOrder());

        // Equals basics
        ShipConfirmPayload selfRef = a;
        assertEquals(selfRef, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());

        // Default + constructors
        ShipConfirmPayload scpDefault1 = new ShipConfirmPayload();
        ShipConfirmPayload scpDefault2 = new ShipConfirmPayload();
        assertEquals(scpDefault1, scpDefault2);
        assertNotNull(scpDefault1.toString());
        ShipConfirmPayload allArg = new ShipConfirmPayload(fo);
        assertEquals(fo, allArg.getFulfillmentOrder());

        // canEqual
        assertTrue(a.canEqual(b));

        // Setter
        ShipConfirmPayload c = new ShipConfirmPayload();
        c.setFulfillmentOrder(fo);
        assertEquals(fo, c.getFulfillmentOrder());

        // Field branches
        b.setFulfillmentOrder(new FulfillmentOrder());
        assertNotEquals(a, b);
        b.setFulfillmentOrder(fo2);
        a.setFulfillmentOrder(null);
        assertNotEquals(a, b);
        a.setFulfillmentOrder(fo);
    }
}
