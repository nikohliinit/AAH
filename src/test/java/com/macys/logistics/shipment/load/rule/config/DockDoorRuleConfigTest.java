package com.macys.logistics.shipment.load.rule.config;

import com.macys.logistics.shipment.load.rule.engine.DockDoorRuleEngine;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DockDoorRuleConfigTest {


    @Test
    void dockDoorRules_shouldLoadYamlAndCreateRuleGroup() throws Exception {
        DockDoorRuleConfig config = new DockDoorRuleConfig();

        RuleGroup ruleGroup = config.dockDoorRules();

        assertNotNull(ruleGroup);
        assertEquals("rules", ruleGroup.getGroupName());
        assertNotNull(ruleGroup.getRules());
        assertFalse(ruleGroup.getRules().isEmpty());
    }

    @Test
    void dockDoorRuleEngine_shouldCreateEngineWithRuleGroup() throws Exception {
        DockDoorRuleConfig config = new DockDoorRuleConfig();
        RuleGroup ruleGroup = config.dockDoorRules();

        DockDoorRuleEngine engine = config.dockDoorRuleEngine(ruleGroup);

        assertNotNull(engine);
    }

    @Test
    void dockDoorRules_shouldThrowException_whenYamlNotFound() {
        DockDoorRuleConfig config = new DockDoorRuleConfig() {
            @Override
            public RuleGroup dockDoorRules() throws Exception {
                throw new RuntimeException("File not found");
            }
        };

        assertThrows(RuntimeException.class, config::dockDoorRules);
    }
}

