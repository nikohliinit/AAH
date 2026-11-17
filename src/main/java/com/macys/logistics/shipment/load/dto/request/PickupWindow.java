package com.macys.logistics.shipment.load.dto.request;

import lombok.Data;

@Data
public class PickupWindow {

    private String startDateTime;

    private String endDateTime;
}
