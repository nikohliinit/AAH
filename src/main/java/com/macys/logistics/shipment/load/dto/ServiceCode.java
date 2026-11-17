package com.macys.logistics.shipment.load.dto;

public enum ServiceCode {
	TL("TL"), 
	IM("IM"), 
	LTL("LTL"), 
	PKG("PKG");
	
	private final String value;
	
	ServiceCode(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}


