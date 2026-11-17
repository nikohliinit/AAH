package com.macys.logistics.shipment.load.logging;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import com.macys.logistics.shipment.load.dto.request.Load;

import java.time.LocalDateTime;
import java.util.UUID;


@Service("loadMetricsHelper")
public class TmsLoadMetricsHelper {
	public static final Marker PAYLOAD_MARKER = MarkerManager.getMarker("PAYLOAD_LOG");
	private static final String REST_API = "E";

	public void initiateMetricsLoadRest(String loadId, String operationType) {
		String localDateTimeString = LocalDateTime.now().toString();
		ThreadContext.put(CommonMetrics.CORRELATION_ID.value(),
				String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		ThreadContext.put(CommonMetrics.TRANSACTION_ID.value(), loadId + "-" + localDateTimeString);
		AppMetrics metrics = new AppMetrics();
		metrics.add(CommonMetrics.LOAD_REQUEST_ID.value(), loadId);
		metrics.add(CommonMetrics.REQUEST_CHANNEL.value(), REST_API);
		metrics.add(CommonMetrics.REQUEST_TYPE.value(), operationType);

		ThreadContext.put(CommonMetrics.PROCESS_METRICS.value(), metrics.toString());
	}
	
	public void initiateMetricsLoad(Load load, String operationType) {
		String localDateTimeString = LocalDateTime.now().toString();
		ThreadContext.put(CommonMetrics.CORRELATION_ID.value(),
				String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		ThreadContext.put(CommonMetrics.TRANSACTION_ID.value(), localDateTimeString);
		AppMetrics metrics = new AppMetrics();
		metrics.add(CommonMetrics.LOAD_REQUEST_ID.value(), load.getLoadId());
		metrics.add(CommonMetrics.LOAD_CARRIER_CODE.value(), load.getCarrierCode());
		metrics.add(CommonMetrics.LOAD_CARRIER_TYPE.value(), load.getCarrierType());
		metrics.add(CommonMetrics.LOAD_TRAILER_NUMBER.value(), load.getTrailerNumber());
		metrics.add(CommonMetrics.REQUEST_CHANNEL.value(), REST_API);
		metrics.add(CommonMetrics.REQUEST_TYPE.value(), operationType);

		ThreadContext.put(CommonMetrics.PROCESS_METRICS.value(), metrics.toString());
	}
	
	public void populateMetricsLoad(Load load, String operationType) {
		AppMetrics metrics = new AppMetrics();
		metrics.add(CommonMetrics.LOAD_REQUEST_ID.value(), load.getLoadId());
		metrics.add(CommonMetrics.LOAD_CARRIER_CODE.value(), load.getCarrierCode());
		metrics.add(CommonMetrics.LOAD_CARRIER_TYPE.value(), load.getCarrierType());
		metrics.add(CommonMetrics.LOAD_TRAILER_NUMBER.value(), load.getTrailerNumber());
		metrics.add(CommonMetrics.REQUEST_CHANNEL.value(), REST_API);
		metrics.add(CommonMetrics.REQUEST_TYPE.value(), operationType);

		ThreadContext.put(CommonMetrics.PROCESS_METRICS.value(), metrics.toString());
	}
	
	public void clearThreadContext() {
		ThreadContext.clearAll();
	}
	
	public void populateTransactionDomainAndType(String transactionDomain, String transactionType) {
		ThreadContext.put(CommonMetrics.TRANSACTION_DOMAIN.value(), transactionDomain);
		ThreadContext.put(CommonMetrics.TRANSACTION_TYPE.value(), transactionType);
	}
}
