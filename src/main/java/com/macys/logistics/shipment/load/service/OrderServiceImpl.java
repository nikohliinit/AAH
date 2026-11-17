package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.inbound.RestProcessShipmentOrderDelete;
import com.macys.logistics.shipment.load.dto.inbound.RestShipmentDelete;
import com.macys.logistics.shipment.load.dto.inbound.orders.LineItem;
import com.macys.logistics.shipment.load.dto.inbound.orders.Order;
import com.macys.logistics.shipment.load.dto.inbound.orders.OrderShipmentMessage;
import com.macys.logistics.shipment.load.dto.inbound.orders.Reference;
import com.macys.logistics.shipment.load.dto.inbound.orders.SpecialRouteRequest;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.util.Constants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter INPUT_FORMAT_YYYYMMDDHHMM = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Autowired
    private TMRestApiClient tmRestApiClient;

    @Override
    public void createOrder(OrderShipmentMessage orderMessage) throws TmsLoadProcessException {
        Order order = orderMessage.getOrder();
        String orderId = order.getOrderId();
        log.info("OrderServiceImpl.createOrder: Starting order creation for orderId: {}, fbNbr: {}",
                orderId, orderMessage.getFbNbr());

        RestShipmentType shipment = buildShipmentType(orderMessage);
        RestProcessShipmentOrderCreate createRequest = new RestProcessShipmentOrderCreate();
        createRequest.setShipment(List.of(shipment));
        createRequest.setIgnoreAllShipmentReferenceNumbersFlag(Constants.ORDERS_FLAG_FALSE);
        createRequest.setExecuteAPRatingFlag(Constants.ORDERS_FLAG_FALSE);
        createRequest.setExecuteARRatingFlag(Constants.ORDERS_FLAG_FALSE);

        ProcessShipmentOrderCreateResponse response = tmRestApiClient.restProcessCreateShipment(createRequest);

        ResponseHeaderType responseHeader = response.getResponseHeader();
        if (Objects.nonNull(responseHeader) && !responseHeader.isCompletedSuccessfully()) {
            String errorCodes = extractErrorCodes(responseHeader);
            log.error(
                    "OrderServiceImpl.createOrder: TMS API returned CompletedSuccessfully=false for orderId: {}, errorCodes: {}",
                    orderId, errorCodes);
            throw new TmsLoadProcessException(
                    "TMS create shipment API failed for orderId: " + orderId + ", errorCodes: " + errorCodes);
        }

        log.info("OrderServiceImpl.createOrder: Order creation completed successfully for orderId: {}", orderId);
    }

    @Override
    public void updateOrder(OrderShipmentMessage orderMessage) throws TmsLoadProcessException {
        Order order = orderMessage.getOrder();
        String orderId = order.getOrderId();
        log.info("OrderServiceImpl.updateOrder: Starting order update for orderId: {}, fbNbr: {}",
                orderId, orderMessage.getFbNbr());
        RestShipmentType shipment = buildShipmentTypeForUpdate(orderMessage);

        RestProcessShipmentOrderCreate updateRequest = new RestProcessShipmentOrderCreate();
        updateRequest.setShipment(List.of(shipment));
        updateRequest.setIgnoreAllShipmentReferenceNumbersFlag(Constants.ORDERS_FLAG_FALSE);
        updateRequest.setExecuteAPRatingFlag(Constants.ORDERS_FLAG_FALSE);
        updateRequest.setExecuteARRatingFlag(Constants.ORDERS_FLAG_FALSE);
        ProcessShipmentOrderCreateResponse response = tmRestApiClient.processUpdateShipment(updateRequest);

        ResponseHeaderType responseHeader = response.getResponseHeader();
        if (Objects.nonNull(responseHeader) && !responseHeader.isCompletedSuccessfully()) {
            if (hasOnlyToleratedUpdateErrors(responseHeader)) {
                log.warn("OrderServiceImpl.updateOrder: TMS API returned CompletedSuccessfully=false "
                        + "with only tolerated error code(s) '3' for orderId: {}. Treating as success.", orderId);
            } else {
                String errorCodes = extractErrorCodes(responseHeader);
                log.error("OrderServiceImpl.updateOrder: TMS API returned CompletedSuccessfully=false "
                        + "for orderId: {}, errorCodes: {}", orderId, errorCodes);
                throw new TmsLoadProcessException(
                        "TMS update shipment API failed for orderId: " + orderId + errorCodes);
            }
        }
        log.info("OrderServiceImpl.updateOrder: Order update completed successfully for orderId: {}", orderId);
    }

    @Override
    public void deleteOrder(OrderShipmentMessage orderMessage) throws TmsLoadProcessException {
        String orderId = orderMessage.getOrder().getOrderId();
        log.info("OrderServiceImpl.deleteOrder: Starting order deletion for orderId: {}, fbNbr: {}",
                orderId, orderMessage.getFbNbr());

        RestShipmentDelete shipmentDelete = new RestShipmentDelete();
        shipmentDelete.setShipmentNumber(StringUtils.trim(orderId));
        RestProcessShipmentOrderDelete deleteRequest = new RestProcessShipmentOrderDelete();
        deleteRequest.setShipment(List.of(shipmentDelete));
        ProcessShipmentOrderDeleteResponse response = tmRestApiClient.restProcessDeleteShipment(deleteRequest);
        ResponseHeaderType responseHeader = response.getResponseHeader();
        if (Objects.nonNull(responseHeader) && !responseHeader.isCompletedSuccessfully()) {
            String errorCodes = extractErrorCodes(responseHeader);
            log.error(
                    "OrderServiceImpl.deleteOrder: TMS API returned CompletedSuccessfully=false for orderId: {}, errorCodes: {}",
                    orderId, errorCodes);
            throw new TmsLoadProcessException(
                    "TMS delete shipment API failed for orderId: " + orderId + errorCodes);
        }
        log.info("OrderServiceImpl.deleteOrder: Order deletion completed successfully for orderId: {}", orderId);
    }

    private RestShipmentType buildShipmentTypeForUpdate(OrderShipmentMessage orderMessage) {
        RestShipmentType shipment = buildShipmentType(orderMessage);

        String srr01Value = findReferenceValue(
                orderMessage.getOrder().getReferences(), Constants.ORDERS_SRR01_REF_CODE);
        if (Constants.ORDERS_DC_BYPASS_COLLECT.equals(srr01Value)
                || Constants.ORDERS_DC_BYPASS_PREPAID.equals(srr01Value)) {
            log.debug("OrderServiceImpl.buildShipmentTypeForUpdate: SRR01='{}' — setting MITCC strategy", srr01Value);
        } else {
            shipment.setMitccStrategyEnumVal(null);
        }
        shipment.setReferenceNumberStructure(buildReferenceNumbersForUpdate(orderMessage));

        return shipment;
    }

    private List<ReferenceNumberType> buildReferenceNumbersForUpdate(OrderShipmentMessage orderMessage) {
        Order order = orderMessage.getOrder();
        List<ReferenceNumberType> refs = new ArrayList<>();

        if (Objects.nonNull(order.getReferences())) {
            for (Reference ref : order.getReferences()) {
                if (StringUtils.isNotBlank(ref.getType()) && StringUtils.isNotBlank(ref.getValue())) {
                    refs.add(createReferenceNumber(ref.getType().trim(), ref.getValue().trim()));
                }
            }
        }

        return refs;
    }

    private boolean hasOnlyToleratedUpdateErrors(ResponseHeaderType responseHeader) {
        List<ErrorType> errors = responseHeader.getError();
        if (Objects.isNull(errors) || errors.isEmpty()) {
            return false;
        }
        return errors.stream()
                .map(ErrorType::getCode)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .allMatch(Constants.ORDERS_TOLERATED_UPDATE_ERROR_CODE::equals);
    }

    private RestShipmentType buildShipmentType(OrderShipmentMessage orderMessage) {
        Order order = orderMessage.getOrder();
        RestShipmentType shipment = new RestShipmentType();

        shipment.setShipmentNumber(StringUtils.trim(order.getOrderId()));
        shipment.setCustomerCode(StringUtils.trim(order.getCustomerCode()));
        shipment.setLogisticsGroupCode(Constants.DEFAULT_CUSTOMER_CODE);
        shipment.setDivisionCode(Constants.DEFAULT_CUSTOMER_CODE);
        if (StringUtils.isNotBlank(orderMessage.getPreferredAPCarrierCode())) {
            shipment.setPreferredAPCarrierCode(orderMessage.getPreferredAPCarrierCode());
        }
        shipment.setSystemPlanID(Constants.DEFAULT_SYSTEM_PLAN_ID);
        shipment.setFreightTermsEnumVal(mapFreightTerms(order.getFreightTerms()));
        String consolidationClass = determineConsolidationClassCode(orderMessage);
        if (Objects.nonNull(consolidationClass)) {
            shipment.setShipmentConsolidationClassCode(consolidationClass);
        }
        shipment.setMergeInTransitConsolidationCode(StringUtils.trim(order.getShipProject()));
        shipment.setShipFromLocationTypeEnumVal(Constants.SHIP_FROM_LOCATION_ENUM_VAL);
        if (StringUtils.isNotBlank(orderMessage.getPrimaryLoc())) {
            shipment.setShipFromLocationCode(orderMessage.getPrimaryLoc());
        } else {
            shipment.setShipFromLocationCode(StringUtils.trim(
                    Objects.nonNull(order.getOrigin()) ? order.getOrigin().getLocationId() : null));
        }
        shipment.setShipToLocationTypeEnumVal(Constants.SHIP_TO_LOCATION_TYPE_ENUM_VAL);
        shipment.setShipToLocationCode(StringUtils.trim(
                Objects.nonNull(order.getDestination()) ? order.getDestination().getLocationId() : null));
        setDateTimeFields(shipment, order);
        if (Objects.nonNull(order.getCommodity()) && StringUtils.isNotBlank(order.getCommodity().getCode())) {
            shipment.setCommodityCode(StringUtils.trim(order.getCommodity().getCode()));
        }
        shipment.setDeferAPRatingFlag(Constants.ORDERS_FLAG_TRUE);
        shipment.setDeferARRatingFlag(Constants.ORDERS_FLAG_TRUE);
        shipment.setUseOriginDefaultsFlag(Constants.ORDERS_FLAG_TRUE);
        shipment.setUseDestinationDefaultsFlag(Constants.ORDERS_FLAG_TRUE);
        shipment.setIgnoreReferenceNumbersFlag(Constants.ORDERS_FLAG_FALSE);
        shipment.setIgnoreContainersFlag(Constants.ORDERS_FLAG_FALSE);
        shipment.setIgnoreChargeOverridesFlag(Constants.ORDERS_FLAG_TRUE);
        shipment.setMitccStrategyEnumVal(Constants.ORDERS_MITCC_STRATEGY);
        shipment.setReferenceNumberStructure(buildReferenceNumbers(orderMessage));
        shipment.setContainer(buildContainers(order));

        return shipment;
    }

    private String mapFreightTerms(String freightTerms) {
        if (Objects.isNull(freightTerms)) {
            return Constants.ORDERS_FREIGHT_TERMS_NULL;
        }
        String trimmed = freightTerms.trim();
        if (Constants.ORDERS_FREIGHT_TERMS_COL.equalsIgnoreCase(trimmed)) {
            return Constants.ORDERS_FREIGHT_TERMS_COLLECT;
        } else if (Constants.ORDERS_FREIGHT_TERMS_PPD.equalsIgnoreCase(trimmed)) {
            return Constants.ORDERS_FREIGHT_TERMS_PRE_PAID;
        }
        return Constants.ORDERS_FREIGHT_TERMS_NULL;
    }

    private String determineConsolidationClassCode(OrderShipmentMessage orderMessage) {
        String spclRteNbr = orderMessage.getSpclRteNbr();
        if (StringUtils.isBlank(spclRteNbr)) {
            return null;
        }
        Order order = orderMessage.getOrder();
        String sourceId = Objects.nonNull(order.getOrigin()) ? order.getOrigin().getLocationId() : null;
        if (Constants.ORDERS_EXCLUDED_SOURCE_ID.equals(StringUtils.trim(sourceId))) {
            return null;
        }
        String trimmedRoute = spclRteNbr.trim();
        if (Constants.ORDERS_CONSOLIDATION_SRR02_ROUTES.contains(trimmedRoute)) {
            return findReferenceValue(order.getReferences(), Constants.ORDERS_SRR02_REF_CODE);
        } else if (Constants.ORDERS_CONSOLIDATION_SRR03_ROUTE.equals(trimmedRoute)) {
            return findReferenceValue(order.getReferences(), Constants.ORDERS_SRR03_REF_CODE);
        }

        return null;
    }

    private List<ReferenceNumberType> buildReferenceNumbers(OrderShipmentMessage orderMessage) {
        Order order = orderMessage.getOrder();
        List<ReferenceNumberType> refs = new ArrayList<>();

        if (Objects.nonNull(order.getReferences())) {
            for (Reference ref : order.getReferences()) {
                if (StringUtils.isNotBlank(ref.getType()) && StringUtils.isNotBlank(ref.getValue())) {
                    refs.add(createReferenceNumber(ref.getType().trim(), ref.getValue().trim()));
                }
            }
        }

        if (Objects.nonNull(order.getCommodity()) && StringUtils.isNotBlank(order.getCommodity().getCode())) {
            refs.add(createReferenceNumber(Constants.ORDERS_COMMODITY_REF_CODE,
                    StringUtils.trim(order.getCommodity().getCode())));
        }

        if (StringUtils.isNotBlank(orderMessage.getPrimaryLoc()) && Objects.nonNull(order.getOrigin())
                && StringUtils.isNotBlank(order.getOrigin().getLocationId())) {
            refs.add(createReferenceNumber(Constants.ORDERS_VNDR_ACCT_REF_CODE,
                    StringUtils.trim(order.getOrigin().getLocationId())));
        }

        if (StringUtils.isNotBlank(orderMessage.getVendorName())) {
            refs.add(createReferenceNumber(Constants.ORDERS_VENDOR_NAME_REF_CODE, orderMessage.getVendorName()));
        }

        return refs;
    }

    private List<ContainerType> buildContainers(Order order) {
        ContainerType container = new ContainerType();

        container.setContainerTypeCode(Constants.CONTAINER_TYPE_CODE);

        if (Objects.nonNull(order.getLineItems()) && !order.getLineItems().isEmpty()) {
            LineItem firstLineItem = order.getLineItems().getFirst();

            if (Objects.nonNull(firstLineItem.getPieces())) {
                container.setQuantity(BigDecimal.valueOf(firstLineItem.getPieces()));
            }

            if (Objects.nonNull(firstLineItem.getVolumeCf())) {
                ShippingInformationType shippingInfo = new ShippingInformationType();
                shippingInfo.setVolume(firstLineItem.getVolumeCf());
                container.setContainerShippingInformation(shippingInfo);
            }

            WeightByFreightClassType weightByFreightClass = new WeightByFreightClassType();
            if (Objects.nonNull(firstLineItem.getWeightLb())) {
                weightByFreightClass.setFreightClassNominalWeight(firstLineItem.getWeightLb());
            }
            weightByFreightClass.setFreightClassCode(Constants.CONTAINER_FREIGHT_CLASS_CODE);
            container.setWeightByFreightClass(List.of(weightByFreightClass));
        }

        container.setIgnoreContainerOrientationsFlag(Constants.ORDERS_FLAG_TRUE);
        container.setIgnoreWeightByFreightClassFlag(Constants.ORDERS_FLAG_FALSE);
        container.setIgnoreShipmentItemsFlag(Constants.ORDERS_FLAG_TRUE);
        container.setIgnoreReferenceNumbersFlag(Constants.ORDERS_FLAG_TRUE);

        return List.of(container);
    }

    private void setDateTimeFields(RestShipmentType shipment, Order order) {
        if (Objects.nonNull(order.getPickupWindow()) && StringUtils.isNotBlank(order.getPickupWindow().getEarliest())) {
            String earlyAvl = order.getPickupWindow().getEarliest();
            shipment.setPickupFromDateTime(earlyAvl);
            shipment.setDeliveryFromDateTime(addMinutes(earlyAvl, 1));
        }

        if (Objects.nonNull(order.getDeliveryWindow())
                && StringUtils.isNotBlank(order.getDeliveryWindow().getLatest())) {
            String lateDel = order.getDeliveryWindow().getLatest();
            shipment.setPickupToDateTime(addMinutes(lateDel, -1));
            shipment.setDeliveryToDateTime(lateDel);
        }
    }

    private String addMinutes(String isoDateTime, int minutes) {
        if (StringUtils.isBlank(isoDateTime)) {
            return isoDateTime;
        }
        try {
            if (isoDateTime.length() == 12 && isoDateTime.chars().allMatch(Character::isDigit)) {
                LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, INPUT_FORMAT_YYYYMMDDHHMM);
                return dateTime.plusMinutes(minutes).format(OUTPUT_FORMAT_ISO);
            }
            OffsetDateTime dateTime = OffsetDateTime.parse(isoDateTime);
            return dateTime.plusMinutes(minutes).format(OUTPUT_FORMAT_ISO);
        } catch (Exception e) {
            log.warn("OrderServiceImpl: Failed to parse/adjust dateTime '{}', returning as-is", isoDateTime);
            return isoDateTime;
        }
    }

    private String extractErrorCodes(ResponseHeaderType responseHeader) {
        if (Objects.isNull(responseHeader.getError())) {
            return "";
        }
        return responseHeader.getError().stream()
                .map(ErrorType::getCode)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    private ReferenceNumberType createReferenceNumber(String typeCode, String number) {
        ReferenceNumberType ref = new ReferenceNumberType();
        ref.setReferenceNumberTypeCode(typeCode);
        ref.setReferenceNumber(number);
        return ref;
    }

    private String findReferenceValue(List<Reference> references, String typeCode) {
        if (Objects.isNull(references)) {
            return null;
        }
        Optional<String> value = references.stream()
                .filter(r -> typeCode.equals(r.getType()) && StringUtils.isNotBlank(r.getValue()))
                .map(Reference::getValue)
                .map(String::trim)
                .findFirst();
        return value.orElse(null);
    }

}
