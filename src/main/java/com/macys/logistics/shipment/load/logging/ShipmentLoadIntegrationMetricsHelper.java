package com.macys.logistics.shipment.load.logging;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ProcessShipmentOrderUpdate;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.ShipmentType;

@Service("metricsHelper")
public class ShipmentLoadIntegrationMetricsHelper {
	public static final Marker PAYLOAD_MARKER = MarkerManager.getMarker("PAYLOAD_LOG");
	public void initiateMetrics() {
		String localDateTimeString = LocalDateTime.now().toString();
		ThreadContext.put(CommonMetrics.CORRELATION_ID.value(),
				String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		ThreadContext.put(CommonMetrics.TRANSACTION_ID.value(),CommonMetrics.EXTRACT_PHOTO.name() +
				"-" + localDateTimeString);
		AppMetrics metrics = new AppMetrics();
		ThreadContext.put(CommonMetrics.PROCESS_METRICS.value(), metrics.toString());
	}

	public void initiateMetricsShipmentEnrichment(String shipmentNumber) {
		ThreadContext.put(CommonMetrics.CORRELATION_ID.value(),
				shipmentNumber.trim() + "-" + String.valueOf(UUID.randomUUID()));
		ThreadContext.put(CommonMetrics.TRANSACTION_DOMAIN.value(),"ShipmentEnrichmentIntegration");
		AppMetrics metrics = new AppMetrics();
		metrics.add(CommonMetrics.SHIPMENT_NUMBER.value(), shipmentNumber.trim());
		ThreadContext.put(CommonMetrics.PROCESS_METRICS.value(), metrics.toString());
	}

	public void populateShipmentEnrichmentDetailsMetrics(ProcessShipmentOrderUpdate request) {
		AppMetrics metrics = new AppMetrics();
		ShipmentType shipmentType = request.getShipment().get(0);
		setShipmentEnrichmentDetailsMetrics(shipmentType, metrics);
		ThreadContext.put(CommonMetrics.PROCESS_METRICS.value(), metrics.toString());
	}

	private void setShipmentEnrichmentDetailsMetrics(ShipmentType request, AppMetrics metrics) {
		metrics.add(CommonMetrics.PICKUP_FROM_DATE_TIME.value(),  String.valueOf(request.getPickupFromDateTime()));
		metrics.add(CommonMetrics.PICKUP_TO_DATE_TIME.value(),  String.valueOf(request.getPickupToDateTime()));

		metrics.add(CommonMetrics.DELIVERY_FROM_DATE_TIME.value(),  String.valueOf(request.getDeliveryFromDateTime()));
		metrics.add(CommonMetrics.DELIVERY_TO_DATE_TIME.value(),  String.valueOf(request.getDeliveryToDateTime()));
	}

	public void populateImageProcessingMetrics(String imageName) {
		AppMetrics metrics = new AppMetrics();
		metrics.add(CommonMetrics.IMAGE_NAME.value(), imageName);
		metrics.add(CommonMetrics.TRANSACTION_TYPE.value(), CommonMetrics.IMAGE_UPLOAD_PROCESS.name());
		ThreadContext.put(CommonMetrics.PROCESS_METRICS.value(), metrics.toString());
	}
	
	public void populateUpdateTMProcessorMetrics(Map<String, Object> headers) {
		
		String correlationId= new String((byte[]) headers.get(CommonMetrics.CORRELATION_ID.value()));
		ThreadContext.put(CommonMetrics.CORRELATION_ID.value(),
				correlationId.trim() + "-" + String.valueOf(UUID.randomUUID()));
		ThreadContext.put(CommonMetrics.TRANSACTION_DOMAIN.value(),CommonMetrics.FLO_TO_TM_INTEGRATION.name());
		
	}

	public void clearThreadContext() {
		ThreadContext.clearAll();
	}

	
}
