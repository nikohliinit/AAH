package com.macys.logistics.shipment.load.dto.request;

import lombok.Data;

@Data
public class ShipFromVendorLocation {

    private String locationId;

    private String Street;

    private String city;

    private Integer postalCode;

    private String stateCode;

    private String countryCode;
}
