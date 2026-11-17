package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.inbound.ErrorCode;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.APChargeType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ContainerType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ReferenceNumberType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestProcessShipmentOrderCreate;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.RestShipmentType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShippingInformationType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.WeightByFreightClassType;
import com.macys.logistics.shipment.load.dto.inbound.Division;
import com.macys.logistics.shipment.load.dto.inbound.RestProcessShipmentOrderDelete;
import com.macys.logistics.shipment.load.dto.inbound.RestShipmentDelete;
import com.macys.logistics.shipment.load.dto.inbound.SRRRequestDetails;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentDetails;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.dto.inbound.SpecialRoutingRequestDetails;
import com.macys.logistics.shipment.load.exception.NotFoundException;
import com.macys.logistics.shipment.load.exception.TMSCreateAPIFailureException;
import com.macys.logistics.shipment.load.exception.TMSDeleteAPIFailureException;
import com.macys.logistics.shipment.load.exception.TMSMappingException;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.util.Constants;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.threeten.bp.LocalDate;
import org.threeten.bp.chrono.ChronoLocalDate;
import org. threeten.bp.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeParseException;

@Service
@Log4j2
public class InBoundShipmentServiceImpl implements InBoundShipmentService {

  @Autowired
  private TMRestApiClient tmRestApiClient;
  @Value("${deliveryToDateTime.days}")
  private int deliveryAdditionalDays;
  @Value("${minus.minutes}")
  private int minusMinutes;
  @Value("${plus.minutes}")
  private int plusMinutes;

  public void createShipment(ShipmentEnterpriseMessage shipmentEnterpriseMessage) {
    log.info("Creating shipment for shipment number: {}", shipmentEnterpriseMessage.getShipmentNumber());
    RestProcessShipmentOrderCreate shipmentOrderCreate = null;
    ShipmentDetails shipmentDetails = shipmentEnterpriseMessage.getShipmentDetails();
    try {
      shipmentOrderCreate = new RestProcessShipmentOrderCreate();
      RestShipmentType restShipmentType = new RestShipmentType();
      restShipmentType.setBillToCustomerCode(Constants.DEFAULT_CUSTOMER_CODE);
      restShipmentType.setBillToCustomerName(Constants.BILL_TO_CUSTOMER_NAME);
      restShipmentType.setComponentTypeGroupCode(Constants.DEFAULT_CUSTOMER_CODE);
      restShipmentType.setShipmentNumber(
          String.valueOf(shipmentEnterpriseMessage.getShipmentNumber()));
      restShipmentType.setCustomerCode(String.format("%09d",shipmentDetails.getVendorDunsNumber()));
      restShipmentType.setLogisticsGroupCode(Division.getDivAbbreviationByDivNumber(shipmentDetails.getPurchaseOrders().getFirst().getDivisionNumber()));
      restShipmentType.setDivisionCode(Constants.DEFAULT_CUSTOMER_CODE);
      restShipmentType.setPreferredAPCarrierCode(null);
      restShipmentType.setSystemPlanID(Constants.DEFAULT_SYSTEM_PLAN_ID);
      restShipmentType.setFreightTermsEnumVal(evaluateFreightType(shipmentDetails.getSpecialRoutingRequestDetails().getSrrTypeDescription()));
      restShipmentType.setMergeInTransitConsolidationCode(null);
      restShipmentType.setShipFromLocationTypeEnumVal(Constants.SHIP_FROM_LOCATION_ENUM_VAL);
      restShipmentType.setShipFromLocationCode(shipmentDetails.getOrigin().getLocationId());
      restShipmentType.setShipToLocationTypeEnumVal(Constants.SHIP_TO_LOCATION_TYPE_ENUM_VAL);
      restShipmentType.setShipToLocationCode(
          shipmentEnterpriseMessage.getShipmentDetails().getDestination()
              .getLocationAbbreviation() + String.format("%06d",shipmentDetails.getDestination().getLocationNbr()));

      String shipDate = shipmentDetails.getReadyToShipDate();
      LocalDateTime readyToShipDate = LocalDate.parse(shipDate).atStartOfDay();
      setCalculatedDates(restShipmentType,readyToShipDate,shipmentEnterpriseMessage);

      restShipmentType.setCommodityCode(shipmentDetails.getCommodityDesc());
      restShipmentType.setDeferAPRatingFlag(Boolean.TRUE);
      restShipmentType.setDeferARRatingFlag(Boolean.TRUE);
      restShipmentType.setUseOriginDefaultsFlag(Boolean.TRUE);
      restShipmentType.setUseDestinationDefaultsFlag(Boolean.TRUE);
      restShipmentType.setIgnoreReferenceNumbersFlag(Boolean.FALSE);
      restShipmentType.setIgnoreChargeOverridesFlag(Boolean.TRUE);
      if(Constants.COLLECT_PRE_ASSIGNED_CARRIER.equalsIgnoreCase(shipmentDetails.getSpecialRoutingRequestDetails().getSrrTypeDescription())
          && shipmentDetails.getSpecialRoutingRequestDetails().getCarrierScac()!=null){
        restShipmentType.setPreferredAPCarrierCode(shipmentDetails.getSpecialRoutingRequestDetails().getCarrierScac());
      }
      List<ReferenceNumberType> referenceNumberTypeList = getReferenceNumberTypes(shipmentDetails);
      restShipmentType.setIgnoreContainersFlag(Boolean.FALSE);
      restShipmentType.setReferenceNumberStructure(referenceNumberTypeList);

      List<ContainerType> containerTypeList = new ArrayList<>();
      ContainerType containerType = new ContainerType();
      ContainerType palletContainerType = new ContainerType();
      containerType.setContainerTypeCode(Constants.CONTAINER_TYPE_CODE);
      containerType.setQuantity(BigDecimal.valueOf(shipmentDetails.getTotalCarton()));

      containerType.setIgnoreContainerOrientationsFlag(Boolean.TRUE);
      containerType.setIgnoreWeightByFreightClassFlag(Boolean.FALSE);
      containerType.setIgnoreShipmentItemsFlag(Boolean.TRUE);
      containerType.setIgnoreReferenceNumbersFlag(Boolean.TRUE);
      containerTypeList.add(containerType);

      if(shipmentEnterpriseMessage.getShipmentDetails().getTotalPallet()>0) {
        palletContainerType.setContainerTypeCode(Constants.PALLET_CONTAINER_TYPE_CODE);
        palletContainerType.setQuantity(BigDecimal.valueOf(shipmentDetails.getTotalPallet()));
        containerTypeList.add(palletContainerType);
      }

      restShipmentType.setContainer(containerTypeList);
      WeightByFreightClassType weightByFreightClassType = new WeightByFreightClassType();
      weightByFreightClassType.setFreightClassNominalWeight(BigDecimal.valueOf(shipmentDetails.getTotalWeight()));
      if(shipmentDetails.getFreightClassName().equalsIgnoreCase(Constants.VSP_FREIGHT_CLASS_TYPE)) {
        weightByFreightClassType.setFreightClassCode(Constants.CONTAINER_FREIGHT_CLASS_CODE);
      } else {
        weightByFreightClassType.setFreightClassCode(shipmentDetails.getFreightClassName());
      }
      containerType.setWeightByFreightClass(Collections.singletonList(weightByFreightClassType));
      palletContainerType.setWeightByFreightClass(Collections.singletonList(weightByFreightClassType));

      mapShipmentConsolidationCode(shipmentDetails, restShipmentType);

      ShippingInformationType containerShippingInformation = new ShippingInformationType();
      containerShippingInformation.setVolume(BigDecimal.valueOf(shipmentDetails.getTotalCube()));
      containerShippingInformation.setSkids(BigDecimal.valueOf(shipmentDetails.getTotalPallet()));
      containerType.setContainerShippingInformation(containerShippingInformation);

      APChargeType apChargeType = new APChargeType();
      apChargeType.setFreightClassCode(null);
      restShipmentType.setArCharge(Collections.singletonList(apChargeType));

      shipmentOrderCreate.setIgnoreAllShipmentReferenceNumbersFlag(Boolean.FALSE);
      shipmentOrderCreate.setExecuteAPRatingFlag(Boolean.FALSE);
      shipmentOrderCreate.setExecuteARRatingFlag(Boolean.FALSE);

      shipmentOrderCreate.setShipment(Collections.singletonList(restShipmentType));
      log.debug("Update shipment create details using rest api call {}", shipmentOrderCreate);
      tmRestApiClient.restProcessCreateShipment(shipmentOrderCreate);
    }
    catch (TmsLoadProcessException ex) {
      ErrorCode errorCode = ErrorCode.Create_Shipment_Exception;
      log.error("ErrorCode: {} {} for shipment number : {} with error message:{}",errorCode.getCode(),errorCode.getMessage(),shipmentEnterpriseMessage.getShipmentNumber(),ex.getMessage(), ex);
      throw new TMSCreateAPIFailureException("TM API Failed to create shipment number " + shipmentEnterpriseMessage.getShipmentNumber() + ". Failure Reason - " + ex.getMessage() + "API Payload - " + shipmentOrderCreate, ex);
    }
    catch (Exception ex) {
     ErrorCode errorCode = ErrorCode.Create_Shipment_Exception;
      log.error("ErrorCode: {} {} for shipment number : {} with error message:{}",errorCode.getCode(),errorCode.getMessage(),shipmentEnterpriseMessage.getShipmentNumber(),ex.getMessage(), ex);
      throw new TMSMappingException(
          "CreateShipment|Error Occurred while mapping shipment in TM " + ex.getMessage() + "API Payload - " + shipmentOrderCreate, ex);
    }
  }

  private static void mapShipmentConsolidationCode(ShipmentDetails shipmentDetails,
      RestShipmentType restShipmentType) {
    if(shipmentDetails.getSpecialRoutingRequestDetails()!=null){
      SpecialRoutingRequestDetails spclRoutingDetails = shipmentDetails.getSpecialRoutingRequestDetails();
      if(isContainerRouting(spclRoutingDetails.getSrrTypeDescription())){
        setShipmentConsolidationClassCode(spclRoutingDetails.getSrrRequestDetails(),Constants.CONTAINER_NUMBER,restShipmentType);
      } else if(Constants.COLLECT_PRE_ASSIGNED_CARRIER.equalsIgnoreCase(spclRoutingDetails.getSrrTypeDescription())){
        setShipmentConsolidationClassCode(spclRoutingDetails.getSrrRequestDetails(),Constants.TRAILER_NUMBER,restShipmentType);
      }
    }
  }

  private static boolean isContainerRouting(String srrType) {
    return Constants.DC_BY_PASS_PRE_PAID_CONTAINER.equalsIgnoreCase(srrType) ||
        Constants.POE_PREPAID_CONTAINER.equalsIgnoreCase(srrType) ||
        Constants.DC_BY_PASS_COLLECT_CONTAINER.equalsIgnoreCase(srrType);
  }

  private static void setShipmentConsolidationClassCode(List<SRRRequestDetails> srrRequestDetails,
      String labelName,
      RestShipmentType restShipmentType
  ) {
    srrRequestDetails.stream()
        .filter(detail -> labelName.equalsIgnoreCase(detail.getLabelName()))
        .findFirst()
        .ifPresent(detail -> restShipmentType.setShipmentConsolidationClassCode(detail.getUserInputValue()));
  }

  private static List<ReferenceNumberType> getReferenceNumberTypes(ShipmentDetails shipmentDetails) {
    ReferenceNumberType deptNumberReferenceType = new ReferenceNumberType();
    ReferenceNumberType readyToShipReferenceType = new ReferenceNumberType();
    ReferenceNumberType oTypeReferenceType = new ReferenceNumberType();
    ReferenceNumberType inDcDateReferenceType = new ReferenceNumberType();
    ReferenceNumberType sourceReferenceType = new ReferenceNumberType();
    ReferenceNumberType vendorReferenceType = new ReferenceNumberType();
    ReferenceNumberType vendorNameReferenceType = new ReferenceNumberType();
    ReferenceNumberType poReferenceType = new ReferenceNumberType();
    ReferenceNumberType poTypeReference = new ReferenceNumberType();
    List<ReferenceNumberType> referenceNumberTypeList = new ArrayList<>();

    if (shipmentDetails.getSpecialRoutingRequestDetails() != null) {
      SpecialRoutingRequestDetails srrDetails = shipmentDetails.getSpecialRoutingRequestDetails();
      String srrTypeDescription = srrDetails.getSrrTypeDescription();
      List<SRRRequestDetails> requestDetails = srrDetails.getSrrRequestDetails();

      int srrTypeCode = 1;
      if (srrTypeDescription != null) {
        ReferenceNumberType srrTypeDescRef = new ReferenceNumberType();
        srrTypeDescRef.setReferenceNumberTypeCode(Constants.DEFAULT_SRR_REFERENCE_TYPE_CODE.concat(String.valueOf(srrTypeCode)));
        srrTypeDescRef.setReferenceNumber(srrTypeDescription);
        referenceNumberTypeList.add(srrTypeDescRef);
        srrTypeCode++;
      }
      if (Constants.COLLECT_PRE_ASSIGNED_CARRIER.equalsIgnoreCase(srrTypeDescription) && srrDetails.getCarrierScac() != null) {
          ReferenceNumberType srrCarrierScacRef = new ReferenceNumberType();
          srrCarrierScacRef.setReferenceNumberTypeCode(Constants.DEFAULT_SRR_REFERENCE.concat(String.format("%02d", srrTypeCode)));
          srrCarrierScacRef.setReferenceNumber(srrDetails.getCarrierScac());
          referenceNumberTypeList.add(srrCarrierScacRef);
          srrTypeCode++;
        if (requestDetails != null && !requestDetails.isEmpty()) {
          ReferenceNumberType srrUserInputValueRef = new ReferenceNumberType();
          srrUserInputValueRef.setReferenceNumberTypeCode(Constants.DEFAULT_SRR_REFERENCE.concat(String.format("%02d", srrTypeCode)));
          srrUserInputValueRef.setReferenceNumber(requestDetails.get(1).getUserInputValue());
          referenceNumberTypeList.add(srrUserInputValueRef);
        }
      } else {
        for (SRRRequestDetails detail : requestDetails) {
          ReferenceNumberType srrReferenceType = new ReferenceNumberType();
          String userInputValue = detail.getUserInputValue();
          String referenceNumberTypeCode = Constants.DEFAULT_SRR_REFERENCE.concat(String.format("%02d", srrTypeCode));
          srrReferenceType.setReferenceNumberTypeCode(referenceNumberTypeCode);
          srrReferenceType.setReferenceNumber(userInputValue);
          referenceNumberTypeList.add(srrReferenceType);
          srrTypeCode++;
        }
      }
    }
    deptNumberReferenceType.setReferenceNumberTypeCode(Constants.DEPT_REFERENCE_TYPE_CODE);
    deptNumberReferenceType.setReferenceNumber(String.valueOf(shipmentDetails.getPurchaseOrders().getFirst().getDepartmentNumber()));
    readyToShipReferenceType.setReferenceNumberTypeCode(Constants.READY_TO_SHIP_REFERENCE_TYPE_CODE);
    readyToShipReferenceType.setReferenceNumber(setReadyToShipDate(shipmentDetails.getReadyToShipDate()));
    inDcDateReferenceType.setReferenceNumberTypeCode(Constants.IN_DC_DATE_REFERENCE_TYPE_CODE);
    inDcDateReferenceType.setReferenceNumber(shipmentDetails.getPurchaseOrders().getFirst().getInDcDate()+ Constants.FIXED_FORMAT_TIME);
    oTypeReferenceType.setReferenceNumberTypeCode(Constants.OTYPE_REFERENCE_TYPE_CODE);
    oTypeReferenceType.setReferenceNumber(Constants.OTYPE_REFERENCE_NUMBER);
    sourceReferenceType.setReferenceNumberTypeCode(Constants.MCY_SHP_TYP_REF_KEY);
    sourceReferenceType.setReferenceNumber(Constants.SOURCE_SYSTEM_REFERENCE_NUMBER);
    vendorReferenceType.setReferenceNumberTypeCode(Constants.VENDOR_REFERENCE_TYPE_CODE);
    vendorReferenceType.setReferenceNumber(String.valueOf(shipmentDetails.getVendorNumber()));
    vendorNameReferenceType.setReferenceNumberTypeCode(Constants.VENDOR_NAME_REFERENCE_CODE);
    vendorNameReferenceType.setReferenceNumber(shipmentDetails.getVendorName());
    poReferenceType.setReferenceNumberTypeCode(Constants.PO_REFERENCE_TYPE_CODE);
    poReferenceType.setReferenceNumber(String.valueOf(shipmentDetails.getPurchaseOrders().getFirst().getPoNumber()));
    poTypeReference.setReferenceNumberTypeCode(Constants.PO_TYPE_REFERENCE_CODE);
    poTypeReference.setReferenceNumber(shipmentDetails.getPurchaseOrders().getFirst().getPoType());
    referenceNumberTypeList.add(deptNumberReferenceType);
    referenceNumberTypeList.add(readyToShipReferenceType);
    referenceNumberTypeList.add(oTypeReferenceType);
    referenceNumberTypeList.add(inDcDateReferenceType);
    referenceNumberTypeList.add(sourceReferenceType);
    referenceNumberTypeList.add(vendorReferenceType);
    referenceNumberTypeList.add(vendorNameReferenceType);
    referenceNumberTypeList.add(poReferenceType);
    referenceNumberTypeList.add(poTypeReference);
    return referenceNumberTypeList;
  }

  private void setCalculatedDates(RestShipmentType restShipmentType, LocalDateTime readyToShipDate,
      ShipmentEnterpriseMessage shipmentEnterpriseMessage) {

    OffsetDateTime pickupFrom = readyToShipDate.atOffset(ZoneOffset.UTC);
    OffsetDateTime deliveryTo = calculateDeliveryTo(readyToShipDate, shipmentEnterpriseMessage);
    OffsetDateTime pickupTo = deliveryTo.minusMinutes(minusMinutes);
    OffsetDateTime deliveryFrom = calculateDeliveryFrom(readyToShipDate, shipmentEnterpriseMessage);

    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    restShipmentType.setPickupFromDateTime(pickupFrom.format(formatter));
    restShipmentType.setPickupToDateTime(pickupTo.format(formatter));
    restShipmentType.setDeliveryFromDateTime(Objects.requireNonNull(deliveryFrom).format(formatter));
    restShipmentType.setDeliveryToDateTime(deliveryTo.format(formatter));
  }

  private OffsetDateTime calculateDeliveryFrom(LocalDateTime readyToShipDate, ShipmentEnterpriseMessage shipmentEnterpriseMessage) {
    LocalDate inDcDate = parseInDcDate(shipmentEnterpriseMessage);
    if (inDcDate != null) {
      return inDcDate.isAfter(ChronoLocalDate.from(readyToShipDate)) ? inDcDate.atStartOfDay().atOffset(ZoneOffset.UTC): readyToShipDate.plusMinutes(plusMinutes).atOffset(ZoneOffset.UTC);
    }
    return readyToShipDate.plusMinutes(plusMinutes).atOffset(ZoneOffset.UTC);
  }

  private OffsetDateTime calculateDeliveryTo(LocalDateTime readyToShipDate, ShipmentEnterpriseMessage shipmentEnterpriseMessage) {
    LocalDate inDcDate = parseInDcDate(shipmentEnterpriseMessage);
    if (inDcDate != null) {
      return inDcDate.isAfter(ChronoLocalDate.from(readyToShipDate)) ? inDcDate.plusDays(deliveryAdditionalDays).atStartOfDay().atOffset(ZoneOffset.UTC) : readyToShipDate.plusDays(deliveryAdditionalDays).atOffset(ZoneOffset.UTC);
    }
    return readyToShipDate.plusDays(deliveryAdditionalDays).atOffset(ZoneOffset.UTC);
  }

  private LocalDate parseInDcDate(ShipmentEnterpriseMessage shipmentEnterpriseMessage) {
    String inDcDateStr = shipmentEnterpriseMessage.getShipmentDetails().getPurchaseOrders().getFirst().getInDcDate();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    try {
      return LocalDate.parse(inDcDateStr, dateFormatter);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  private static String setReadyToShipDate(String readyToShipDate) {
    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
    String shipDate = "";
    try {
      if(readyToShipDate!=null) {
        return LocalDate.parse(readyToShipDate, inputFormatter).format(outputFormatter);
      }
    } catch (DateTimeParseException e) {
      return null;
    }
    return shipDate;
  }

  private String evaluateFreightType(String ssrTypeDescription) {
    return (Objects.equals(ssrTypeDescription, Constants.POE_PREPAID_CONTAINER)
        || Objects.equals(ssrTypeDescription, Constants.NEED_APPOINTMENT_ONLY_PREPAID)
        || Objects.equals(ssrTypeDescription,Constants.DC_BY_PASS_PRE_PAID_CONTAINER))
        ? Constants.FREIGHT_TERMS_ENUM_VAL : Constants.DEFAULT_FREIGHT_TERMS_ENUMVAL;
  }

  public void deleteShipment(ShipmentEnterpriseMessage shipmentEnterpriseMessage) {
    log.info("Deleting shipment for shipment number: {}", shipmentEnterpriseMessage.getShipmentNumber());
    RestProcessShipmentOrderDelete shipmentOrderDelete = null;
    try {
      shipmentOrderDelete = new RestProcessShipmentOrderDelete();

      RestShipmentDelete restShipmentDelete = new RestShipmentDelete();
      restShipmentDelete.setShipmentNumber(String.valueOf(shipmentEnterpriseMessage.getShipmentNumber()));
      shipmentOrderDelete.setShipment(Collections.singletonList(restShipmentDelete));
      log.debug("Update shipment delete details using rest api call {}", shipmentOrderDelete);
      tmRestApiClient.restProcessDeleteShipment(shipmentOrderDelete);
    }
    catch (TmsLoadProcessException ex) {
      ErrorCode errorCode = ErrorCode.Delete_Shipment_Exception;
      log.error("ErrorCode: {} {} for shipment number: {} error message:{}",errorCode.getCode(),errorCode.getMessage(),shipmentEnterpriseMessage.getShipmentNumber(),ex.getMessage() ,ex);
      throw new TMSDeleteAPIFailureException("TM API Failed to delete shipment number " + shipmentEnterpriseMessage.getShipmentNumber() + ". Failure Reason - " + ex.getMessage() + " API Payload - " + shipmentOrderDelete, ex);
    }
    catch (Exception ex) {
     ErrorCode errorCode = ErrorCode.Delete_Shipment_Exception;
      log.error("ErrorCode: {} {} for shipment number: {} error message:{}",errorCode.getCode(),errorCode.getMessage(),shipmentEnterpriseMessage.getShipmentNumber(),ex.getMessage());
      throw new NotFoundException(
          "DeleteShipment|Error Occurred while deleting shipment in TM " + ex.getMessage() + " API Payload - " + shipmentOrderDelete, ex);
    }
  }
}
