package com.macys.logistics.shipment.load.dto;

public enum SealCondition {
	INTACT("INTACT"), 
	MISMATCH("MISMATCH"), 
	TAMPERED("TAMPERED"), 
	NO_SEAL("NO SEAL");
	
	private final String value;
	
	SealCondition(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}

