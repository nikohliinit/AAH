package com.macys.logistics.shipment.load.logging;

import lombok.Data;

/**
 * This class is the replacement for IAPI metric
 * @author B009047
 *
 */
@Data
public class Metric {


	private String name;
	private String value;
	private String type;
	private boolean searchable;
	
	public Metric() {
		super();
	}
	
	public Metric(String name, String value, String type, boolean searchable) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.searchable = searchable;
	}

	public Metric(String name, String value) {
		this.name = name;
		this.value = value;
		this.type = "String";
		this.searchable = true;
	}
	@Override
    public String toString() {
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(this.name);
        strBuff.append('#');
        strBuff.append(this.value);
        strBuff.append('#');
        strBuff.append(this.type);
        strBuff.append('#');
        strBuff.append(this.searchable);
        return strBuff.toString();
    }
	

}
