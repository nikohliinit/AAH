package com.macys.logistics.shipment.load.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentContainerInfo {
	
	private String cntr20digitNbr;

	private int xpctdUnitQty;
	
	private int actlUnitQty;

    private Long destLocNbr;

	private Long departmentNum;

	private int cntrStatNbr;

}