package com.macys.logistics.shipment.load.rule.orchestrator;

import com.macys.logistics.shipment.load.dto.dockdoor.*;
import com.macys.logistics.shipment.load.rule.engine.DockDoorRuleEngine;
import com.macys.logistics.shipment.load.rule.executor.DockDoorActionExecutor;
import com.macys.logistics.shipment.load.service.DockDoorCleanupProcessorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DockDoorRuleOrchestratorTest {

    private DockDoorCleanupProcessorService cleanupProcessor;
    private DockDoorRuleEngine ruleEngine;
    private DockDoorActionExecutor executor;

    private DockDoorRuleOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        cleanupProcessor = mock(DockDoorCleanupProcessorService.class);
        ruleEngine = mock(DockDoorRuleEngine.class);
        executor = mock(DockDoorActionExecutor.class);

        orchestrator = new DockDoorRuleOrchestrator(cleanupProcessor, ruleEngine, executor);
    }

    @Test
    void testOrchestrate_AttachfullFlow() {
        // ------- Input DTO -------
        RuleInputDTO input = RuleInputDTO.builder()
                .trailerNumber("T1")
                .yardType("DOOR")
                .dockDoor("DOCKDOOR")
                .build();

        LoadDTO trailerLoad = LoadDTO.builder().loadId("L1").trailerNumber("T1").build();
        LoadDTO doorLoad = LoadDTO.builder().loadId("L2").trailerNumber("X").build();

        List<LoadDTO> trailerLoads = List.of(trailerLoad);
        List<LoadDTO> doorLoads = List.of(doorLoad);

        LoadDTO loadToDetach = LoadDTO.builder()
                .loadId("LD1")
                .dockDoor("D1")
                .systemReferenceNumberIdOfExistingDockDoor(Collections.singletonList("SRN1"))
                .build();

        CleanupResult cleanupResult = new CleanupResult();


        when(cleanupProcessor.cleanup(eq("T1"), anyList()))
                .thenReturn(cleanupResult);

        // Rule engine final result
        RuleResultDTO finalResult = new RuleResultDTO();
        finalResult.setTargetLoadId("LD1");
        finalResult.setDockDoor("D1");
        finalResult.setAction(Action.valueOf("ATTACH"));

        when(ruleEngine.evaluate(any(RuleInputDTO.class)))
                .thenReturn(finalResult);

        // ------- Execute -------
        orchestrator.orchestrate(input, trailerLoads, doorLoads);


        ArgumentCaptor<RuleResultDTO> captor = ArgumentCaptor.forClass(RuleResultDTO.class);
        verify(executor, times(1)).execute(captor.capture());

        RuleResultDTO detachCall = captor.getAllValues().get(0);

        assertEquals("LD1", detachCall.getTargetLoadId());
        assertEquals("D1", detachCall.getDockDoor());


    }

    @Test
    void testOrchestrate_noDetachLoads() {
        RuleInputDTO input = RuleInputDTO.builder()
                .trailerNumber("T1")
                .yardType("DOOR")
                .dockDoor("DOCKDOOR")
                .build();

        LoadDTO trailerLoad = LoadDTO.builder().loadId("L1").trailerNumber("T1").build();

        when(cleanupProcessor.cleanup(eq("T1"), anyList()))
                .thenReturn(new CleanupResult()); // No detach loads

        RuleResultDTO finalResult = new RuleResultDTO();
        finalResult.setTargetLoadId("L1");
        finalResult.setDockDoor("D2");
        finalResult.setAction(Action.ATTACH);

        when(ruleEngine.evaluate(any(RuleInputDTO.class)))
                .thenReturn(finalResult);

        orchestrator.orchestrate(input, List.of(trailerLoad), List.of());

        ArgumentCaptor<RuleResultDTO> captor = ArgumentCaptor.forClass(RuleResultDTO.class);
        verify(executor, times(1)).execute(captor.capture()); // Only final call

        RuleResultDTO finalCall = captor.getValue();
        assertEquals("L1", finalCall.getTargetLoadId());
        assertEquals("D2", finalCall.getDockDoor());
    }



}