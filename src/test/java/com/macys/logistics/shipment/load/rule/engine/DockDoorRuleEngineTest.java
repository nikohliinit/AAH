package com.macys.logistics.shipment.load.rule.engine;


import com.macys.logistics.shipment.load.dto.dockdoor.*;
import com.macys.logistics.shipment.load.rule.config.RuleApplier;
import com.macys.logistics.shipment.load.rule.config.RuleMatcher;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DockDoorRuleEngineTest {

    // -------------------------------
    // ATTACH / SINGLE / SAME DOOR
    // -------------------------------

    @Test
    void evaluate_shouldAttach_whenRuleMatches() {
        RuleDefinition rule = mock(RuleDefinition.class);
        RuleGroup ruleGroup = mock(RuleGroup.class);

        when(ruleGroup.getRules()).thenReturn(List.of(rule));
        when(rule.getWhen()).thenReturn(Map.of("any", "value"));
        when(rule.getThen()).thenReturn(Map.of());

        DockDoorRuleEngine engine = new DockDoorRuleEngine(ruleGroup);

        LoadDTO load = mockLoad("L1", "D1");
        RuleInputDTO input = input("D1", List.of(load));

        try (MockedStatic<RuleMatcher> matcher = mockStatic(RuleMatcher.class);
             MockedStatic<RuleApplier> applier = mockStatic(RuleApplier.class)) {

            matcher.when(() -> RuleMatcher.match(any(), any()))
                    .thenAnswer(inv -> {
                        RuleContext ctx = inv.getArgument(1);
                        ctx.setAction(Action.ATTACH);
                        return true;
                    });

            RuleResultDTO result = engine.evaluate(input);

            assertEquals(Action.ATTACH, result.getAction());
            assertEquals("L1", result.getTargetLoadId());
            assertEquals("D1", result.getDockDoor());
        }
    }

    // -------------------------------
    // UPDATE / SINGLE / DIFFERENT
    // -------------------------------

    @Test
    void evaluate_shouldUpdate_whenRuleMatches() {
        RuleDefinition rule = mock(RuleDefinition.class);
        RuleGroup ruleGroup = mock(RuleGroup.class);

        when(ruleGroup.getRules()).thenReturn(List.of(rule));
        when(rule.getWhen()).thenReturn(Map.of("x", "y"));

        DockDoorRuleEngine engine = new DockDoorRuleEngine(ruleGroup);

        LoadDTO load = mockLoad("L2", "OLD");
        RuleInputDTO input = input("NEW", List.of(load));

        try (MockedStatic<RuleMatcher> matcher = mockStatic(RuleMatcher.class)) {

            matcher.when(() -> RuleMatcher.match(any(), any()))
                    .thenAnswer(inv -> {
                        RuleContext ctx = inv.getArgument(1);
                        ctx.setAction(Action.UPDATE);
                        return true;
                    });

            RuleResultDTO result = engine.evaluate(input);

            assertEquals(Action.UPDATE, result.getAction());
            assertEquals("L2", result.getTargetLoadId());
            assertEquals("NEW", result.getDockDoor());
        }
    }

    // -------------------------------
    // DETACH / MULTIPLE LOADS
    // -------------------------------

    @Test
    void evaluate_shouldDetachMultipleLoads() {
        RuleDefinition rule = mock(RuleDefinition.class);
        RuleGroup ruleGroup = mock(RuleGroup.class);

        when(ruleGroup.getRules()).thenReturn(List.of(rule));
        when(rule.getWhen()).thenReturn(Map.of());

        DockDoorRuleEngine engine = new DockDoorRuleEngine(ruleGroup);

        LoadDTO l1 = mockLoad("L1", "D1");
        LoadDTO l2 = mockLoad("L2", null);
        LoadDTO l3 = mockLoad("L3", "D2");

        RuleInputDTO input = input("X", List.of(l1, l2, l3));

        try (MockedStatic<RuleMatcher> matcher = mockStatic(RuleMatcher.class)) {

            matcher.when(() -> RuleMatcher.match(any(), any()))
                    .thenAnswer(inv -> {
                        RuleContext ctx = inv.getArgument(1);
                        ctx.setAction(Action.DETACH);
                        return true;
                    });

            RuleResultDTO result = engine.evaluate(input);

            assertEquals(Action.DETACH, result.getAction());
            assertEquals(2, result.getLoadsToDetach().size());
        }
    }

    // -------------------------------
    // NO RULE MATCH
    // -------------------------------

    @Test
    void evaluate_shouldReturnEmptyResult_whenNoRuleMatches() {
        RuleDefinition rule = mock(RuleDefinition.class);
        RuleGroup ruleGroup = mock(RuleGroup.class);

        when(ruleGroup.getRules()).thenReturn(List.of(rule));
        when(rule.getWhen()).thenReturn(Map.of());

        DockDoorRuleEngine engine = new DockDoorRuleEngine(ruleGroup);

        RuleInputDTO input = input("D1", List.of());

        try (MockedStatic<RuleMatcher> matcher = mockStatic(RuleMatcher.class)) {
            matcher.when(() -> RuleMatcher.match(any(), any())).thenReturn(false);

            RuleResultDTO result = engine.evaluate(input);

            assertNull(result.getAction());
        }
    }

    // -------------------------------
    // TEST HELPERS
    // -------------------------------

    private LoadDTO mockLoad(String id, String dockDoor) {
        LoadDTO load = mock(LoadDTO.class);
        when(load.getLoadId()).thenReturn(id);
        when(load.getDockDoor()).thenReturn(dockDoor);
        when(load.getSystemReferenceNumberIdOfExistingDockDoor())
                .thenReturn(Collections.singletonList("REF-" + id));
        return load;
    }

    private RuleInputDTO input(String eventDoor, List<LoadDTO> loads) {
        RuleInputDTO input = mock(RuleInputDTO.class);
        when(input.getDockDoor()).thenReturn(eventDoor);
        when(input.getLoads()).thenReturn(loads);
        when(input.getYardType()).thenReturn("YARD");
        return input;
    }
}
