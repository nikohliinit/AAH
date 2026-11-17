package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import lombok.Data;

import java.util.List;

@Data
public class RestFilterType {
    protected String name;
    protected String op;
    protected List<String> value;
}
