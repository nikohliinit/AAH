package com.macys.logistics.shipment.load.rule.orchestrator;

import com.macys.logistics.shipment.load.dto.dockdoor.CleanupResult;
import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;
import com.macys.logistics.shipment.load.dto.dockdoor.LoadExecutionReport;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleInputDTO;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleResultDTO;
import com.macys.logistics.shipment.load.dto.dockdoor.DockDoorProcessResponse;

import com.macys.logistics.shipment.load.rule.engine.DockDoorRuleEngine;
import com.macys.logistics.shipment.load.rule.executor.DockDoorActionExecutor;
import com.macys.logistics.shipment.load.service.DockDoorCleanupProcessorService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.macys.logistics.shipment.load.util.Constants.*;

@Slf4j
@Component
public class DockDoorRuleOrchestrator {

    private final DockDoorCleanupProcessorService cleanupProcessor;
    private final DockDoorRuleEngine ruleEngine;
    private final DockDoorActionExecutor executor;

    public DockDoorRuleOrchestrator(DockDoorCleanupProcessorService cleanupProcessor, DockDoorRuleEngine ruleEngine, DockDoorActionExecutor executor) {
        this.cleanupProcessor = cleanupProcessor;
        this.ruleEngine = ruleEngine;
        this.executor = executor;
    }

    public DockDoorProcessResponse orchestrate(RuleInputDTO input, List<LoadDTO> trailerLoads, List<LoadDTO> doorLoads) {
        log.info("Starting DockDoorRuleOrchestrator for trailerNumber={}", input.getTrailerNumber());
        List<LoadExecutionReport> allReports = new ArrayList<>();
        List<LoadDTO> mergedLoads = LoadMerger.merge(trailerLoads, doorLoads);
        CleanupResult cleanupResult = cleanupProcessor.cleanup(input.getTrailerNumber(), mergedLoads);

        for (LoadDTO load : cleanupResult.getLoadsToDetach()) {
            allReports.addAll(executor.execute(RuleResultDTO.detachOnly(load.getLoadId(), load.getSystemReferenceNumberIdOfExistingDockDoor(), load.getDockDoor())));
            load.setDockDoor(null);
            load.setSystemReferenceNumberIdOfExistingDockDoor(null);
        }

        input.setLoads(mergedLoads);
        RuleResultDTO result = ruleEngine.evaluate(input);
        allReports.addAll(executor.execute(result));

        String finalStatus = determineFinalStatus(allReports);
        log.info("DockDoorRuleOrchestrator completed for trailerNumber={}, finalStatus={}", input.getTrailerNumber(), finalStatus);

        return DockDoorProcessResponse.builder()
                .trailerNumber(input.getTrailerNumber())
                .appointmentNumber(input.getAppointmentNumber())
                .yardType(input.getYardType())
                .finalStatus(finalStatus)
                .loadReports(allReports)
                .build();
    }

    private String determineFinalStatus(List<LoadExecutionReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return DOCK_DOOR_RULE_STATUS_FULL_SUCCESS;
        }

        long successCount = reports.stream()
                .filter(r -> DOCK_DOOR_RULE_STATUS_SUCCESS.equalsIgnoreCase(r.getStatus()) || DOCK_DOOR_RULE_STATUS_NO_ACTION.equalsIgnoreCase(r.getStatus()))
                .count();
        long failureCount = reports.stream()
                .filter(r -> DOCK_DOOR_RULE_STATUS_FAILURE.equalsIgnoreCase(r.getStatus()))
                .count();

        if (failureCount == 0) {
            return DOCK_DOOR_RULE_STATUS_FULL_SUCCESS;
        }

        if (successCount == 0) {
            return DOCK_DOOR_RULE_STATUS_FAILURE;
        }

        return DOCK_DOOR_RULE_STATUS_PARTIAL_SUCCESS;
    }
}
