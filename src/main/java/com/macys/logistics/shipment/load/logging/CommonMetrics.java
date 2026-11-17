package com.macys.logistics.shipment.load.logging;

public enum CommonMetrics {
    CORRELATION_ID("CorrelationID"),
    TRANSACTION_ID("TransactionID"),
    TRANSACTION_TYPE("TransactionType"),
    ELAPSED_TIME("ElapsedTime"),
    TRANSACTION_DOMAIN("TransactionDomain"),
    PROCESS_METRICS("Metrics"),
    IMAGE_UPLOAD_PROCESS("ImageUploadProcess"),
    IMAGE_NAME("ImageName"),
    EXTRACT_PHOTO("ExtractPhoto"),
    PICKUP_FROM_DATE_TIME("PickupFromDateTime"),
    PICKUP_TO_DATE_TIME("PickupToDateTime"),
    DELIVERY_FROM_DATE_TIME("DeliveryFromDateTime"),
    DELIVERY_TO_DATE_TIME("DeliveryToDateTime"),
    SHIPMENT_NUMBER("ShipmentNumber"),
    SHIPMENT_ENRICHMENT_PROCESSOR("ShipmentEnrichmentProcessor"),
    SHIPMENT_PUBLISH_PROCESSOR("ShipmentPublishProcessor"),
    UPDATE_TM_PROCESSOR("UpdateTMEventProcessor"),
	FLO_TO_TM_INTEGRATION("FloToTmIntegration"),
	LOAD_REQUEST_ID("LoadId"),
	REQUEST_CHANNEL("RequestChannel"),
	REQUEST_TYPE("RequestType"),
	LOAD_CARRIER_CODE("CarrierCode"),
	LOAD_CARRIER_TYPE("CarrierType"),
	LOAD_TRAILER_NUMBER("TrailerNumber"),
	X_CLIENT_ID( "X-Client-Id"),
	X_REQUEST_ID("X-Request-Id"),
	X_SUB_CLIENT_ID("X-Sub-Client-Id"),
	TMS("TMS");
	
    private String value;

    private CommonMetrics(String name) {
        this.value = name;
    }

    public String value() {
        return this.value;
    }
}