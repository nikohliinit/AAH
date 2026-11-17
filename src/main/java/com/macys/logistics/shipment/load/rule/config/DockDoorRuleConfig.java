package com.macys.logistics.shipment.load.rule.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.macys.logistics.shipment.load.rule.engine.DockDoorRuleEngine;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleDefinition;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Configuration
public class DockDoorRuleConfig {
    @Bean
    public RuleGroup dockDoorRules() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        InputStream is = new ClassPathResource(
                "rules/dock-door-rules.yml").getInputStream();
        Map<String, List<RuleDefinition>> raw =
                mapper.readValue(is, new TypeReference<>() {
                });
        RuleGroup group = new RuleGroup();
        group.setGroupName("rules");
        group.setRules(raw.get("rules"));
        return group;
    }

    @Bean
    public DockDoorRuleEngine dockDoorRuleEngine(RuleGroup dockDoorRules) {
        return new DockDoorRuleEngine(dockDoorRules);
    }
}
