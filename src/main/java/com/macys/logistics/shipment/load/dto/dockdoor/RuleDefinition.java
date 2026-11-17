package com.macys.logistics.shipment.load.dto.dockdoor;

import lombok.Data;

import java.util.Map;

@Data
public class RuleDefinition {
    private String name;
    private Map<String, Object> when;
    private Map<String, Object> then;
}
