package com.macys.logistics.shipment.load.rule.executor;

import com.macys.logistics.shipment.load.dto.dockdoor.Action;

import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;

import com.macys.logistics.shipment.load.dto.dockdoor.LoadExecutionReport;

import com.macys.logistics.shipment.load.dto.dockdoor.RuleResultDTO;

import com.macys.logistics.shipment.load.service.TrailerDockDoorService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ObjectUtils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DockDoorActionExecutor {
    private final TrailerDockDoorService tmClient;
    /**
     * Executes TM operation based on rule engine decision
     * and returns execution report per load.
     */
    public List<LoadExecutionReport> execute(RuleResultDTO result) {
        List<LoadExecutionReport> reports = new ArrayList<>();
        if (result == null || result.getAction() == null) {
            return reports;
        }
        switch (result.getAction()) {
            case ATTACH:
                handleAttach(result, reports);
                break;
            case UPDATE:
                handleUpdate(result, reports);
                break;
            case DETACH:
                handleDetach(result, reports);
                break;
            case NO_ACTION:
                handleNoAction(result, reports);
                break;
            default:
                log.info("Rule result processed");
                break;
        }
        return reports;
    }
    // ============================================================
    // ATTACH
    // ============================================================
    private void handleAttach(RuleResultDTO result,
                              List<LoadExecutionReport> reports) {
        log.info("ATTACH dock door. LoadId={}, DockDoor={}",
                result.getTargetLoadId(),
                result.getDockDoor());
        String response =
                tmClient.processDockDoorAttach(
                        result.getTargetLoadId(),
                        result.getDockDoor());
        reports.add(buildReport(
                result.getTargetLoadId(),
                result.getDockDoor(),
                Action.ATTACH,
                response
        ));
    }
    // ============================================================
    // UPDATE
    // ============================================================
    private void handleUpdate(RuleResultDTO result,
                              List<LoadExecutionReport> reports) {
        log.info("UPDATE dock door. LoadId={}, DockDoor={}",
                result.getTargetLoadId(),
                result.getDockDoor());
        String response;
        if (ObjectUtils.isNotEmpty(result.getOldDockDoorReference())
                && result.getOldDockDoorReference().size() > 1) {
            response = tmClient.processDeleteDockDoor(
                    result.getOldDockDoorReference(),
                    result.getTargetLoadId(),
                    result.getDockDoor());
        } else {
            response = tmClient.processDockDoorUpdate(
                    result.getOldDockDoorReference().get(0),
                    result.getTargetLoadId(),
                    result.getDockDoor());
        }
        reports.add(buildReport(
                result.getTargetLoadId(),
                result.getDockDoor(),
                Action.UPDATE,
                response
        ));
    }
    // ============================================================
    // DETACH
    // ============================================================
    private void handleDetach(RuleResultDTO result,
                              List<LoadExecutionReport> reports) {
        // MULTIPLE LOAD DETACH
        if (result.getLoadsToDetach() != null &&
                !result.getLoadsToDetach().isEmpty()) {
            for (LoadDTO load : result.getLoadsToDetach()) {
                log.info("DETACH dock door. LoadId={}", load.getLoadId());
                String response =
                        tmClient.processDeleteDockDoor(
                                load.getSystemReferenceNumberIdOfExistingDockDoor(),
                                load.getLoadId(),
                                load.getDockDoor());
                reports.add(buildReport(
                        load.getLoadId(),
                        load.getDockDoor(),
                        Action.DETACH,
                        response
                ));
            }
            return;
        }
        // SINGLE LOAD DETACH
        log.info("DETACH dock door. LoadId={}, DockDoor={}",
                result.getTargetLoadId(),
                result.getDockDoor());
        String response =
                tmClient.processDeleteDockDoor(
                        result.getOldDockDoorReference(),
                        result.getTargetLoadId(),
                        result.getDockDoor());
        reports.add(buildReport(
                result.getTargetLoadId(),
                result.getDockDoor(),
                Action.DETACH,
                response
        ));
    }
    private void handleNoAction(RuleResultDTO result,List<LoadExecutionReport> reports) {
        log.info("NO_ACTION required for loadId={}",
                result.getTargetLoadId());
        reports.add(
                LoadExecutionReport.builder()
                        .loadId(result.getTargetLoadId())
                        .dockDoor(result.getDockDoor())
                        .action(Action.NO_ACTION)
                        .status("NO_ACTION")
                        .message("No action required as per rule evaluation")
                        .build()
        );
    }
    // ============================================================
    // COMMON REPORT BUILDER
    // ============================================================
    private LoadExecutionReport buildReport(
            String loadId,
            String dockDoor,
            Action action,
            String response) {
        boolean success = isSuccess(response);
        return LoadExecutionReport.builder()
                .loadId(loadId)
                .dockDoor(dockDoor)
                .action(action)
                .status(success ? "SUCCESS" : "FAILURE")
                .message(response)
                .build();
    }
    /**
     * Determines success based on TM response.
     * Safer enterprise logic: treat anything not starting with FAILURE as success.
     */
    private boolean isSuccess(String response) {
        if (response == null) {
            return false;
        }
        return !response.trim()
                .toUpperCase()
                .startsWith("FAILURE");
    }
}
