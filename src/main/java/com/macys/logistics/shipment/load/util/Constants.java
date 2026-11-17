package com.macys.logistics.shipment.load.util;

import java.math.BigDecimal;
import java.util.Set;
import java.util.regex.Pattern;

import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ReferenceNumberType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShippingInformationType;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.WeightByFreightClassType;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class Constants {

    private Constants() {
    }

    static {

        ReferenceNumberType loadTypeRefNum = new ReferenceNumberType();
        loadTypeRefNum.setReferenceNumberTypeCode(Constants.MCY_LOAD_TYP_REF_KEY);
        loadTypeRefNum.setReferenceNumber(Constants.OB_THROUGH_MERGE_REF_VALUE);
        MCY_LOAD_TYPE_REF_NUM = loadTypeRefNum;

        ReferenceNumberType shipTypeRefNum = new ReferenceNumberType();
        loadTypeRefNum.setReferenceNumberTypeCode(Constants.MCY_SHP_TYP_REF_KEY);
        loadTypeRefNum.setReferenceNumber(Constants.OB_THROUGH_MERGE_REF_VALUE);
        MCY_SHP_TYPE_REF_NUM = shipTypeRefNum;

        ShippingInformationType shippingInformationType = new ShippingInformationType();
        shippingInformationType.setVolume(BigDecimal.valueOf(1000));
        DEFAULT_SHIPPING_INFORMATION_TYPE = shippingInformationType;

        WeightByFreightClassType weightByFreightClassType = new WeightByFreightClassType();
        weightByFreightClassType.setFreightClassNominalWeight(BigDecimal.valueOf(20000L));
        weightByFreightClassType.setFreightClassCode(Constants.CONTAINER_FREIGHT_CLASS_CODE);
        DEFAULT_WEIGHT_BY_FREIGHT_CLASS_TYPE = weightByFreightClassType;
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String FOM = "FOM_";

    public static final String ACTION = "action";

    public static final String CREATE = "CREATE";

    public static final String UPDATE = "UPDATE";

    public static final String CANCEL = "CANCEL";

    public static final String DELETE = "DELETE";

    public static final String TYPE = "type";

    public static final String INBOUND = "INBOUND";

    public static final String APPT_TYPE_CARR_TO_DC = "Carrier to DC";

    public static final String APPT_TYPE_DC_TO_DC = "DC to DC";

    public static final String FRIEGHTTYPE_BACKSTAGE_TO_SDC = "Backstage to SDC";

    public static final String FRIEGHTTYPE_CF_TO_SDC = "CF to SDC";

    public static final String APPT_STATUS = "ARRIVED";
    
    public static final String DEFAULT_FREIGHT_CLASS_CODE = "ST92.5_IMOK";
    public static final String CONTAINER_FREIGHT_CLASS_CODE = "92.5";
    
    public static final String MCY_LOAD_TYP_REF_KEY = "MCY_LOAD_TYP";
    public static final String DEFAULT_CUSTOMER_CODE = "MCY";
    public static final String DEFAULT_SHIPMENT_SOURCE_ENUMVAL = "IS_EXTERNAL_API";
    public static final String DEFAULT_FREIGHT_TERMS_ENUMVAL = "FT_COLLECT";
    public static final String DEFAULT_SHIPMENT_CONSOLIDATION_CODE = "GENERAL";
    public static final String CNTR_NUM_REF_KEY = "cntr20DigNbr";
    public static final String CNTR_NUM_REF_VALUE = "00000000000000000000";
    public static final String DEFAULT_PLAN_ID = "2022";
    public static final String LDS_OPEN_STATUS = "LDS_OPEN";
    public static final String MCY_SHP_TYP_REF_KEY = "MCY_SHPT_TYP";
    public static final String OB_THROUGH_MERGE_REF_VALUE = "OB_THROUGH_MERGE";
    public static final String DEFAULT_CONTAINER_TYPE_CODE = "CASE";
    public static final String DEFAULT_CONTAINER_TYPE_CODE_OUTBOUND = "PIECE";
    public static final ReferenceNumberType MCY_SHP_TYPE_REF_NUM;
    public static final ReferenceNumberType MCY_LOAD_TYPE_REF_NUM;
    public static final ShippingInformationType DEFAULT_SHIPPING_INFORMATION_TYPE;
    public static final WeightByFreightClassType DEFAULT_WEIGHT_BY_FREIGHT_CLASS_TYPE;
    public static final String DEFAULT_REF_NUM_CATEGORY_ENUM_VAL = "RNC_LD_LEG";
    public static final String REF_NUM_ACTION_ENUM_VAL_ADD = "AT_ADD";
    public static final String REF_NUM_ACTION_ENUM_VAL_UPDATE = "AT_UPDATE";
    public static final String TRIP_ID_KEY = "Trip ID";

    public static final String READY_FOR_UNLOAD = "READY FOR UNLOAD";
    public static final String UNLOADING = "UNLOADING";

    public static final String SHIPMENT_LEG_RESULT_TYPE_ENUM_VAL = "RES_SHIPMENTLEG_HEADER";
    public static final String DROP = "Drop";

    public static final String UNLOAD_COMPLETE = "COMPLETE";
    public static final String LOAD_COMPLETE = "LOAD COMPLETE";
    public static final String LOCN_TYPE_CODE = "STR";
    public static final String DOCK_DOOR_NUMBER_MOVE_OUT_OF_DOOR = "Moved out of the door..";

    public static final String CANCELLED = "CANCELLED";
    public static final String TENDERED = "Tendered";
    public static final String TENDERED_ACCEPTED = "Tender Accepted";

    public static final String OUBOUND_APPOINTMENT = "DC";

    public static final String CANCELLED_LOAD = "Canceled";

    public static final String PLANNED = "Planned";

    public static final String OPEN = "Open";

    public static final String DISPATCH_FLAG = "Y";
    public static final String DROP_FLAG_N = "N";
    public static final String DROP_FLAG_Y = "Y";

    public static final String LOAD_STATUS = "  cannot be tender because the status is  ";

    public static final String LOAD_ID = "Current LoadId ";

    public static final String IGNORE_CHANGE_ORIGIN = "XFLO323";
    public static final String IGNORE_LAST_UPDATED_USERIDV = "XFLOB151-A";
    public static final String ARRIVED = "ARRIVED";
    public static final String DEPARTED = "DEPARTED";
    public static final String PICKUP_AND_DELIVERY_DATE_TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String EQ = "Eq";
    public static final String PICKUP_AND_DELIVERY_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TM_API_CALL = "TM_REST_API_CALL-";

    public static final String AT_RETRIEVE = "AT_RETRIEVE";
    public static final String LOAD_TYPE = "LoadType";
    public static final String UPDATED_DATE_TIME = "UpdatedDateTime";
    public static final String PHOTOGRABBER_SAMPLE_FILE_TYPE = "png";

    public static final String NESTED_COLLECTION_NAME_SUFFIX = "].name";
    public static final String NESTED_COLLECTION_OPERATOR_SUFFIX = "].operator";
    public static final String NESTED_COLLECTION_VALUE_SUFFIX = "].value";
    public static final String NESTED_COLLECTION_FIELDS_SUFFIX = "].fields";
    public static final String NESTED_FILTER_COLLECTION_PREFIX = ".filter[";
    public static final String NESTED_COLLECTION_PREFIX = ".nested.collection[";

    public static final String PHOTOGRABBER_FILES_UPLOADED_SUCCESS_MESSAGE = "Files uploaded successfully!";
    public static final String PHOTOGRABBER_NO_ATTACHMENTS_FOUND_RESPONSE = "No attachment data found in response.";
    public static final String PHOTOGRABBER_NO_ATTACHMENTS_FOR_THE_GIVEN_LOAD = "No attachments found for the given load info.";
    public static final String PHOTOGRABBER_NO_PENDING_LOADS_FOUND_WITH_ATTACHMENTS = "No pending loads found with attachments!";
    public static final String PHOTOGRABBER_UNABLE_TO_UPLOAD_EXCEPTION = "Photograbber Service |UploadFiles| Unable to upload files to bucket with exception: ";
    public static final String PHOTOGRABBER_ERROR_OCCURED_WHILE_UPLOAD_TO_BUCKET_EXCEPTION = "An error occurred while uploading files to bucket";
    public static final String PHOTOGRABBER_LOAD_SEARCH_CONFIG = "load.search.for.photograbber.config";
    public static final String PHOTOGRABBER_LOAD_SEARCH_CONFIG_MISSING = "Photograbber Service |UploadFiles| Missing configuration for load search.";
    public static final String PHOTOGRABBER_LOAD_SEARCH_CONFIG_MISSING_EXCEPTION = "Configuration missing for Photograbber load search.";

    public static final String FILE_UPLOADED = "event file :{} changed to {} successfully uploaded to GCS";
    public static final String BUCKET_MEDIA_LINK_NULL = "Bucket media link is null while uploading event file: {} changed to {} to GCS";
    public static final String FILE_UPLOAD_ERROR = "An error occurred while uploading data to GCS. Exception: {} Additional details: {}";
    public static final String FILE_UPLOAD_TO_BUCKET_EXCEPTION = "An error occurred while storing data to GCS";
    public static final String MAX_RETRIES_EXCEEDED = "Maximum retries to upload file:{} to bucket exceeded.";

    public static final String IMAGE_DATA_VALIDATION = "Image data cannot be null";
    public static final String PHOTOGRABBER_ATTACHMENT_IS_TOO_BIG_FOR_THE_IMAGE = "Photograbber image size is too big for the image: {}";
    public static final String PHOTOGRABBER_ATTACHMENT_IS_TOO_BIG = "the size of photograbber image is too big";
    public static final String PHOTOGRABBER_ATTACHMENT_TYPE_IS_NOT_ALLOWED_TO_UPLOAD = "This file type {} is not allowed for upload and the allowed extensions are  {}";
    public static final String PHOTOGRABBER_ATTACHMENT_TYPE_IS_NOT_ALLOWED = "Photograbber image is not a allowed extension";

    public static final String IN = "In";

    public static final String IN_TRANSIT = "In Transit";

    public static final String COMPLETED = "Completed";

    public static final String OB_SHIP_APPT = "OB_SHIP_APPT";

    public static final String DMY_TRLR_NBR = "DMY_TRLR_NBR";

    public static final String LOAD = "Load: ";

    public static final String LOAD_STATUS_TM = "is in the status ";
    public static final String DELETE_REFERENCE = "deleteReferenceNumberFromShipment methods removes the reference number";

    public static final String PROCESS_LOAD = "processLoadRate  details using rest api call for loadId {}";

    public static final String SEARCH_SHIPMENT = "searchShipment based on {}";

    public static final String SEARCH_SHIPMENT_WITH_SHIPMENT_NUMBER = "searchShipment details using rest api call based on shipmentNumber {}";
    public static final String SEARCH_SHIPMENT_WITH_OLD_APPOINTMENT_NUMBER = "searchShipment details using rest api call based on old appointment number {}";
    public static final String SEARCH_SHIPMENT_EXCEPTION = "searchShipment| exception while searching the shipment details";

    public static final String DELETE_REFERENCE_METHODS_STARTS = "Start deleteReferenceNumber method for the shipment {}";
    public static final String DELETE_REFERENCE_API_CALL = "deleteshipmentRefernceNumberInTM details using rest api call for shipmentNumber {} ";

    public static final String DELETE_REFERENCE_METHODS_EXCEPTION = "Ended method deleteReferenceNumber for shipmentNumber {}";
    public static final String DELETE_REFERENCE_METHODS_ENDS = "Ended method deleteReferenceNumber for shipmentNumber {}";

    public static final String SEARCH_SHIPMENT_API = "searchShipment| calling rest api to search the shipment details {}";

    public static final String SEARCH_SHIPMENT_API_ERROR = "Error calling rest api to serach the shipment details";

    public static final String SHIPMENT_CONFIG = "shipment.search.config";
    public static final String SHIPMENT_APPOINTMENT_REFERENCE_CONFIG = "shipment.Appointment.reference.search.config";

    public static final String SHIPMENT_KEY = "shipmentNumber";
    public static final String OLD_APPOINTMENT_REFERENCE_KEY = "referenceNumber23";

    public static final String LOCN_ENTITY_METHODS_STARTS = "Started method findLocnEntityByLocnAbbr to get  DC code  {}";
    public static final String RESPONSE_ENTITY = "Reponse Entity - ";
    public static final String LOCATION = "Location - ";
    public static final String LOCATION_INFO = "Location Info - ";
    public static final String LOCN_ENTITY_METHODS_ENDS = "Ended  method findLocnEntityByLocnAbbr to get  DC code  {}";
    public static final String SERVER_EXCEPTION = "Http server error occurred while getting location by locnNbr {}";
    public static final String RESOURCE_EXCEPTION = "Resource access error occurred while getting location by locnNbr {}";
    public static final String CLIENT_EXCEPTION = "Http client error occurred while getting location by locnNbr{}: status code {} and response body {}";
    public static final String EXCEPTION = "Exception occurred while getting location by locnNbr {}";
    public static final String RESOURCE_ACCESS_EXCEPTION = "An error occurred while getting location by locnNbr";

    public static final String REMOVE_SHIPMENT_METHODS_STARTS = "remove shipment methods starts {}";

    public static final String REMOVE_SHIPMENT_METHODS_ENDS = "remove shipment methods Ends {}";

    public static final String SHIPMENT_REMOVE_METHODS_EXCEPTION = "An error occurred while removing the shipment";
    public static final String LOAD_TENDERED_STATUS_LOG = "The status of load is  Tendered Or Tendered Accepect {} ";
    public static final String PROCESS_LOAD_RETURN_TO_OPEN_STARTS = "Stared the processLoadReturnToOpen()  {} : ";
    public static final String PROCESS_LOAD_RETURN_TO_OPEN_ENDS = "Eneded the processLoadReturnToOpen()  {} : ";
    public static final String PROCESS_LOAD_RETURN_TO_OPEN_LOAD_INFO = "ProcessReturnToOpen using rest api call for loadId {} ";
    public static final String PROCESS_LOAD_RETURN_TO_OPEN_EXCEPTION = "processLoadReturnToOpen| exception while return to open {} ";
    public static final String ERROR = "Error";

    public static final String FREIGHT_TERMS_ENUM_VAL = "FT_PRE_PAID";
    public static final String SHIP_FROM_LOCATION_ENUM_VAL = "SFT_DC";
    public static final String DEFAULT_SYSTEM_PLAN_ID = "1011";
    public static final String SHIP_TO_LOCATION_TYPE_ENUM_VAL = "STT_DC";
    public static final String CONTAINER_TYPE_CODE = "PIECE";
    public static final String DEPT_REFERENCE_TYPE_CODE = "DEPT_NBR";
    public static final String READY_TO_SHIP_REFERENCE_TYPE_CODE = "RDY_TO_SHIP";
    public static final String IN_DC_DATE_REFERENCE_TYPE_CODE = "NDC_DATE";
    public static final String OTYPE_REFERENCE_TYPE_CODE = "OTYPE";
    public static final String OTYPE_REFERENCE_NUMBER = "HOST";
    public static final String VENDOR_REFERENCE_TYPE_CODE = "Vendor_Number";
    public static final String PO_REFERENCE_TYPE_CODE = "CUST_PO1";
    public static final String PO_TYPE_REFERENCE_CODE = "PO_TYPE";
    public static final String DEFAULT_SRR_REFERENCE_TYPE_CODE = "SRR0";
    public static final String DEFAULT_SRR_REFERENCE = "SRR";
    public static final String SOURCE_SYSTEM_REFERENCE_NUMBER = "VSP-18";
    public static final String POE_PREPAID_CONTAINER = "POE - Prepaid Container";
    public static final String NEED_APPOINTMENT_ONLY_PREPAID = "Need Appointment Only-Prepaid";
    public static final String DC_BY_PASS_PRE_PAID_CONTAINER = "DC By-Pass Pre-Paid Container";
    public static final String DC_BY_PASS_COLLECT_CONTAINER = "DC By-Pass Collect Container";
    public static final String COLLECT_PRE_ASSIGNED_CARRIER = "Collect Pre-Assigned Carrier";
    public static final String OUTBOUND_LOAD_PREFIX = "OB-";
    public static final String PALLET_CONTAINER_TYPE_CODE = "PALLET";
    public static final String DEFAULT_APPLICATION_NAME = "tms-shipment-load";
    public static final String DEFAULT_PROCESS_NAME = "VSP Shipment Integration";
    public static final String TIMEZONE_EST = "America/New_York";
    public static final String BILL_TO_CUSTOMER_NAME = "Macy's Organization";
    public static final String FIXED_FORMAT_TIME = " 00:01";
    public static final String VENDOR_NAME_REFERENCE_CODE = "VendorName";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String SHIP_CONFIRM_ROUTE_ID = "shipConfirmRoute";
    public static final String ORDER_TYPE = "orderType";
    public static final String EVENT_TYPE = "eventType";
    public static final String OBTM_TRANSFER_TYPE = "HAF";
    public static final String OBTM_TRANSFER_TYPE_HEADER = "sourceApplicationFunctionType";
    public static final String VSP_FREIGHT_CLASS_TYPE = "Don't Know";
    public static final String CONTAINER_NUMBER = "Container Number";
    public static final String TRAILER_NUMBER = "Trailer Number";
    public static final String SHIPMENT_LOAD_DATA_VALIDATION_ERROR_CODE = "SLIVAL001";
    public static final String SHIPMENT_LOAD_CREATE_API_ERROR_CODE = "SLIAPI003";
    public static final String SHIPMENT_LOAD_DELETE_API_ERROR_CODE = "SLIAPI004";
    public static final String SHIPMENT_LOAD_PROCESSING_EXCEPTION_ERROR_CODE = "SLIEXC002";
    public static final String SHIPMENT_LOAD_CREATE_API_ERROR_PROCESS_ACTION = "Shipment Creation: API Call";
    public static final String SHIPMENT_LOAD_DELETE_API_ERROR_PROCESS_ACTION = "Shipment Deletion: API Call";
    public static final String SHIPMENT_LOAD_DATA_VALIDATION_PROCESS_ACTION = "Shipment Creation: Data Validation";
    public static final String SHIPMENT_LOAD_CREATE_SUCCESS_ACTION = "VSP Shipment Creation";
    public static final String SHIPMENT_LOAD_DELETE_SUCCESS_ACTION = "VSP Shipment Deletion";
    public static final String SHIPMENT_LOAD_FAILURE_STATUS = "Failure";
    public static final String SHIPMENT_LOAD_SUCCESS_STATUS = "Success";
    public static final String SHIPMENT_LOAD_PROCESSING_EXCEPTION_PROCESS_ACTION = "Processing Exception";
    public static final String SHIPMENT_LOAD_WARNING_STATUS = "Warning";
    public static final String TRAILER_IMAGE_PROCESS_NAME = "Photo Integration";
    public static final String CONSUME_IMAGE_PROCESS_ACTION = "Consume Images";
    public static final String BY_TMS_LOOKUP_PROCESS_ACTION = "BY TMS Lookup API";
    public static final String IMAGE_ATTACH_PROCESS_ACTION = "Attach Images to Load";
    public static final String CONSUME_IMAGE_ERROR_CODE = "TPIPST002";
    public static final String BY_TMS_LOAD_ERROR_CODE = "TPILOAD001";
    public static final String IMAGE_ATTACH_ERROR_CODE = "TPIATC001";
    public static final String CONSUME_IMAGE_ERROR_DESC = "Failed to consume image from pubsub with response code: {responseCode} with error: {errorMessage}";
    public static final String BY_TMS_LOAD_ERROR_DESC = "Failed to get Load Id for apptNum : {responseCode} with error: {errorMessage}";
    public static final String IMAGE_ATTACH_ERROR_DESC = "Failed to publish image for load Id  : {responseCode} with error: {errorMessage}";

    public static final String CHARGE_BACK_VENDOR = "VNDRCHRGE";
    public static final String NO_CHARGE = "NOVNDRCHRGE";
    public static final Pattern PALLET_PATTERN = Pattern.compile("pallet(s)?", Pattern.CASE_INSENSITIVE);
    public static final String PHOTOGRABBER_TIMEZONE_TIMEZONE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String OPERATOR_GREATER_THAN_EQUAL = "Ge";
    public static final String OPERATOR_LESS_THAN_EQUAL = "Le";
    public static final String TIMEZONE_UTC = "UTC";
    public static final String PHOTOGRABBER_TRACE_KEY = "traceId";
    public static final Long DEFAULT_PHOTOGRABBER_POOL_TIME_FRAME = 1L;
    public static final String GCP_PROJECT_NAME = "mtech-supplychain-tmsintg-prod";

    public static final String DEFAULT_REF_NUM_SHP_CATEGORY_ENUM_VAL = "RNC_LD_LEG";
    public static final String REF_NUM_CARR_PRO_NUMBER_TYPE_CODE = "CARR_PRO_NBR";
    public static final String DOOR_DOCK_NBR = "DOOR_DOCK_NBR";
    public static final String REF_NUM_ACTION_ENUM_VAL_DELETE = "AT_DELETE";
    public static final String API_SOURCE_ENUMVALUE = "apiSourceEnumVal";
    public static final String THREEPL_TRAILER_SERVICE = "threepl-trailer-service-integration";
    public static final String CLIENTID = "clientID";

    // Orders Hub Check Constants
    public static final String ORDERS_HUB_DESTINATION = "HUB_DESTINATION";
    public static final String ORDERS_HUB_CHECKER = "hub-checker";
    public static final String ORDERS_COMPONENT = "component";
    public static final String ORDERS_DEFAULT_ORDER_ID = "N/A";
    public static final String TRANS_CODE_CREATE = "A";
    public static final String TRANS_CODE_UPDATE = "M";
    public static final String TRANS_CODE_DELETE = "D";

    // Orders Service Constants
    public static final String ORDERS_FREIGHT_TERMS_COLLECT = "FT_COLLECT";
    public static final String ORDERS_FREIGHT_TERMS_PRE_PAID = "FT_PRE_PAID";
    public static final String ORDERS_FREIGHT_TERMS_NULL = "FT_NULL";
    public static final String ORDERS_MITCC_STRATEGY = "MITCC_DIFF_DESTLOC";
    public static final String ORDERS_VENDOR_NAME_REF_CODE = "VendorName";
    public static final String ORDERS_VNDR_ACCT_REF_CODE = "VNDR_ACCT";
    public static final String ORDERS_COMMODITY_REF_CODE = "COMMODITY";
    public static final Set<String> ORDERS_CONSOLIDATION_SRR02_ROUTES = Set.of("180", "190", "111", "134");
    public static final String ORDERS_CONSOLIDATION_SRR03_ROUTE = "122";
    public static final String ORDERS_EXCLUDED_SOURCE_ID = "50957364 295";
    public static final String ORDERS_SRR01_REF_CODE = "SRR01";
    public static final String ORDERS_DC_BYPASS_COLLECT = "DC By-Pass Collect Container";
    public static final String ORDERS_DC_BYPASS_PREPAID = "DC By-Pass Pre-Paid Container";
    public static final String ORDERS_TOLERATED_UPDATE_ERROR_CODE = "3";
    public static final boolean ORDERS_FLAG_TRUE = true;
    public static final boolean ORDERS_FLAG_FALSE = false;
    public static final String ORDERS_SRR02_REF_CODE = "SRR02";
    public static final String ORDERS_SRR03_REF_CODE = "SRR03";
    public static final String ORDERS_FREIGHT_TERMS_COL = "COL";
    public static final String ORDERS_FREIGHT_TERMS_PPD = "PPD";

    // EDI 990 related constants
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String SPAN_ID = "spanId";
    public static final String ERROR_CLASSIFICATION = "errorClassification";
    public static final String TIMESTAMP = "timestamp";
    public static final String EDI990_MESSAGE_ID = "edi990MessageId";
    public static final String EDI990_SHIPMENT_ID = "edi990ShipmentId";
    public static final String EDI990_RESERVATION_ACTION_CODE = "edi990ReservationActionCode";
    public static final String TENDER_ACCEPT = "A";
    public static final String TENDER_DECLINE = "D";
    public static final Integer SHIPMENT_MIN_LEN = 0;
    public static final Integer SHIPMENT_MAX_LEN = 11;
    public static final String LOAD_OPERATIONAL_STATUS = "S_TENDERED";
    public static final String TENDER_ACCEPT_OPERATION_NAME = "processTenderAccept";
    public static final String TENDER_REJECT_OPERATION_NAME = "processTenderReject";
    public static final String API_SOURCE_CLIENT_ID = "shipment-load-integration";
    public static final String TM_REST_API_FAILURE_PREFIX = "TM Rest API failure for ";
    public static final String DUE_TO_SEPARATOR = " due to :";

    public static final String SYSTEM_LOAD_ID = "systemLoadID";
    public static final String DOCK_DOOR_ID = "referenceNumber6";

    public static final String DOCK_DOOR_SEARCH_CONFIG = "load.search.dockdoor.config";
    public static final String DOOR_REFRENCE= "DOOR";
    public static final String YARD_REFRENCE= "YARD";
    public static final String UNKNOWN_TRAILER_TYPE= "UNKNOWN";
    public static final String CORRELATION_KEY= "correlationId";
    public static final String TRACE_KEY = "traceId";
    public static final String SUCCESS_PROCESS_STATUS = "SUCCESS";
    public static final String FAILURE_PROCESS_STATUS = "FAILURE";
    public static final String DEFAULT_PROCESS_NAME_OD_DOCK_DOOR = "Dock Door Update to TMS";
    public static final String DOCK_DOOR_REQUEST_PAYLOAD = "dock_door_request_payload";
    public static final String DOCK_DOOR_RULE_INPUT = "dock_door_rule_input";
    public static final String DOCK_DOOR_FINAL_RESPONSE = "dock_door_rule_output";
    public static final String DOCK_DOOR_RECEIVED_PAYLOAD_ACTION = "Dock Door Received Payload";
    public static final String DOCK_DOOR_RECEIVED_PAYLOAD_WITH_EXCEPTION_ACTION = "Data Validation on Pub Sub";
    public static final String DOCK_DOOR_NO_ACTION_REQUIRED = "No Dock Door Updates Required to TMS Load";
    public static final String DOCK_DOOR_CDC_INVALID_DATA_RECEIVED= "Invalid Data Received And Got Exception ";
    public static final String DOCK_DDOR_CDC_INVALID_DATA_ERROR_CODE ="SDIVAL001";
    public static final String DOCK_DOOR_CDC_INVALID_SCENARIO = "Skipping update due to unwanted field update.";
    public static final String DOCK_DOOR_INVALID_INPUT_TO_RULE_ACTION = "Dock Door Processing";
    public static final String DOCK_DOOR_CDC_INVALID_RULE_INPUT_SCENARIO = "Skipping rule process due to invalid input received or No valid Trailer Found.";
    public static final String DOCK_DOOR_INVALID_RULE_INPUT_SCENARIO_CODE = "SDIGEX002";
    public static final String DOCK_DOOR_CDC_PROCESS_EXCEPTION = "Exception occurred while processing dock door due to  ";
    public static final String DOCK_DOOR_RULE_STATUS_PARTIAL_SUCCESS= "PARTIAL_SUCCESS";
    public static final String DOCK_DOOR_RULE_STATUS_SUCCESS= "SUCCESS";
    public static final String DOCK_DOOR_RULE_STATUS_FAILURE= "FAILURE";
    public static final String DOCK_DOOR_RULE_STATUS_FULL_SUCCESS ="SUCCESS";
    public static final String DOCK_DOOR_RULE_STATUS_NO_ACTION="NO_ACTION";
    public static final String DOCK_DOOR_FINAL_STATUS = "Dock Door Update ";
    public static final String DOCK_DOOR_DOOR_PROCESS_SCENARIO = "Exception occurred while processing dock door";
    public static final String DOCK_DOOR_DOOR_PROCESS_SCENARIO_CODE = "SDIAPE003";
    public static final String DOCK_DOOR_DOOR_PROCESS_PARTIAL_SUCCESS_SCENARIO_CODE = "SDIAPE008";
    public static final String DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_ACTION = "Enrich Trailer Information with Trailer Service";
    public static final String DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_ERROR_MESSAGE= "Exception occurred while enrichment data from trailer service ";
    public static final String DOCK_DOOR_TRAILER_SERVICE_ENRICHMENT_EXCEPTION_CODE ="SDIAPE004";
    public static final String DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_ERROR_MESSAGE= "Exception occurred while getting information from location service ";
    public static final String DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_EXCEPTION_CODE ="SDIAPE005";
    public static final String DOCK_DOOR_LOCATION_SERVICE_ENRICHMENT_ACTION = "Enrich Trailer Information with Location Service";
    public static final String DOCK_DOOR_LOCATION_SERVICE_PAYLOAD = "dock_door_location_service_payload";
    public static final String DOCK_DOOR_TRAILER_SERVICE_PAYLOAD = "dock_door_trailer_service_payload";
    public static final String DOCK_DOOR_TMS_SERVICE_BY_DOCK_DOOR = "dock_door_tms_service_payload_by_dockdoor";
    public static final String DOCK_DOOR_TMS_SERVICE_BY_APPOINTMENT = "dock_door_tms_service_payload_by_appointment";
    public static final String DOCK_DOOR_TMS_SERVICE_BY_DOCK_DOOR_ERROR_MESSAGE= "Exception occurred while getting information from tms service by dock door ";
    public static final String DOCK_DOOR_TMS_SERVICE_BY_DOCK_DOOR_EXCEPTION_CODE ="SDIAPE006";
    public static final String DOCK_DOOR_TMS_SERVICE_BY_DOCK_DOOR_ACTION_BY_DOCK_DOOR = "Search Dock Door Information from TMS Load By Dock Door";
    public static final String DOCK_DOOR_TMS_SERVICE_BY_DOCK_DOOR_ACTION_BY_APPOINTMENT = "Search Dock Door Information from TMS Load By Appointment ";
    public static final String DOCK_DOOR_TMS_SERVICE_BY_APPOINTMENT_ERROR_MESSAGE= " Exception occurred while getting information from tms service by appointment ";
    public static final String DOCK_DOOR_TMS_SERVICE_BY_APPOINTMENT_EXCEPTION_CODE ="SDIAPE007";
    public static final String DOCK_DOOR_LOCATION_SERVICE_NO_LOCATION_FOUND="No location found with ";
    public static final String DOCK_DOOR_LOCATION_SERVICE_ERROR = "LOCATION_SERVICE_ERROR";
    public static final String DOCK_DOOR_TRAILER_SERVICE_ERROR = "TRAILER_SERVICE_ERROR";
    public static final String LOAD_FIND_ENTITY_RESPONSE="FindEntitiesResponse";
    public static final String DOCK_DOOR_MULTIPLE_LOAD_MATCHED = "MULTIPLE";
    public static final String DOCK_DOOR_ONE_LOAD_MATCHED = "SINGLE";
    public static final String DOCK_DOOR_NO_LOAD_MATCHED = "NONE";
}
