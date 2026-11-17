package com.macys.logistics.shipment.load.logging;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AppMetrics {
	private List<Metric> metrics;

	public AppMetrics() {
		metrics = new ArrayList<>();
	}

	public void addMetric(Metric metric) {
		metrics.add(metric);
	}

	public AppMetrics add(String metric, String value) {
		metrics.add(new Metric(metric, value, "String", true));
		return this;
	}
	
	@Override
	public String toString() {
		return metrics + "";
	}

}
