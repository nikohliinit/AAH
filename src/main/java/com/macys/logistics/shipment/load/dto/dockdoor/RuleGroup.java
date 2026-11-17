package com.macys.logistics.shipment.load.dto.dockdoor;

import lombok.Data;

import java.util.List;

@Data
public class RuleGroup {
    private String groupName;
    private List<RuleDefinition> rules;
}
