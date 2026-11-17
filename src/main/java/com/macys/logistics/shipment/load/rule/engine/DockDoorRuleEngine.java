package com.macys.logistics.shipment.load.rule.engine;

import com.macys.logistics.shipment.load.dto.dockdoor.*;
import com.macys.logistics.shipment.load.rule.config.RuleApplier;
import com.macys.logistics.shipment.load.rule.config.RuleMatcher;

import java.util.List;

import static com.macys.logistics.shipment.load.util.Constants.*;

public class DockDoorRuleEngine {

    private final RuleGroup ruleGroup;

    public DockDoorRuleEngine(RuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    public RuleResultDTO evaluate(RuleInputDTO input) {
        RuleContext ctx = buildContext(input);
        for (RuleDefinition rule : ruleGroup.getRules()) {
            if (RuleMatcher.match(rule.getWhen(), ctx)) {
                RuleApplier.apply(rule.getThen(), ctx);
                break;
            }
        }
        return buildResult(ctx, input);
    }

    // ============================================================

    // BUILD CONTEXT

    // ============================================================

    private RuleContext buildContext(RuleInputDTO input) {
        RuleContext ctx = new RuleContext();
        List<LoadDTO> loads = input.getLoads();
        ctx.setTrailerPresentAt(input.getYardType());
        int size = loads.size();
        String trailerLoadState = switch (size) {
            case 0 -> DOCK_DOOR_NO_LOAD_MATCHED;
            case 1 -> DOCK_DOOR_ONE_LOAD_MATCHED;
            default -> DOCK_DOOR_MULTIPLE_LOAD_MATCHED;
        };

        ctx.setNumberOfLoadsPresentInTM(trailerLoadState);
        ctx.setDockDoorExistsWithLoad(loads.stream().anyMatch(l -> l.getDockDoor() != null));
        // SINGLE load
        if (size == 1) {
            LoadDTO load = loads.get(0);
            setDockDoorMatch(ctx, load.getDockDoor(), input.getDockDoor());
            ctx.setAppointmentExistsWithLoad(DOCK_DOOR_NO_LOAD_MATCHED);
        }
        // MULTIPLE load
        if (size > 1) {
            if (input.getAppointmentNumber() != null) {
                long matchCount = loads.stream().filter(l -> input.getAppointmentNumber().equals(l.getAppointmentNumber())).count();
                if (matchCount == 0) {
                    ctx.setAppointmentExistsWithLoad(DOCK_DOOR_NO_LOAD_MATCHED);
                    ctx.setSameDockDoorExistsWithLoad(null);
                } else if (matchCount == 1) {
                    ctx.setAppointmentExistsWithLoad("UNIQUE");
                    LoadDTO correctLoad = loads.stream().filter(l -> input.getAppointmentNumber().equals(l.getAppointmentNumber())).findFirst().orElse(null);
                    if (correctLoad != null) {
                        setDockDoorMatch(ctx, correctLoad.getDockDoor(), input.getDockDoor());
                    }
                } else {
                    ctx.setAppointmentExistsWithLoad(DOCK_DOOR_MULTIPLE_LOAD_MATCHED);
                    ctx.setSameDockDoorExistsWithLoad(null);
                }
            } else {
                ctx.setAppointmentExistsWithLoad(DOCK_DOOR_NO_LOAD_MATCHED);
                ctx.setSameDockDoorExistsWithLoad(null);
            }
        }
        return ctx;
    }

    private void setDockDoorMatch(RuleContext ctx, String existingDoor, String eventDoor) {
        if (existingDoor == null) {
            ctx.setSameDockDoorExistsWithLoad(DOCK_DOOR_NO_LOAD_MATCHED);
        } else if (existingDoor.equalsIgnoreCase(eventDoor)) {
            ctx.setSameDockDoorExistsWithLoad("SAME");
        } else {
            ctx.setSameDockDoorExistsWithLoad("DIFFERENT");
        }
    }

    // ============================================================

    // BUILD RESULT

    // ============================================================

    private RuleResultDTO buildResult(RuleContext ctx, RuleInputDTO input) {
        RuleResultDTO result = new RuleResultDTO();
        result.setAction(ctx.getAction());
        List<LoadDTO> loads = input.getLoads();
        if (loads == null || loads.isEmpty()) {
            return result;
        }
        // ============================================================
        // MULTIPLE + UNIQUE → DETACH stale + ATTACH/UPDATE correct
        // ============================================================
        if (DOCK_DOOR_MULTIPLE_LOAD_MATCHED.equals(ctx.getNumberOfLoadsPresentInTM()) && "UNIQUE".equals(ctx.getAppointmentExistsWithLoad())) {
            LoadDTO correctLoad = loads.stream().filter(l -> input.getAppointmentNumber().equals(l.getAppointmentNumber())).findFirst().orElse(null);
            if (correctLoad == null) {
                result.setAction(Action.NO_ACTION);
                return result;
            }
            // Always populate evaluated load context
            result.setTargetLoadId(correctLoad.getLoadId());
            result.setDockDoor(input.getDockDoor());
            result.setOldDockDoorReference(correctLoad.getSystemReferenceNumberIdOfExistingDockDoor());
            // Detach stale loads
            List<LoadDTO> staleLoads = loads.stream().filter(l -> l.getDockDoor() != null).filter(l -> !l.getLoadId().equals(correctLoad.getLoadId())).toList();
            result.setLoadsToDetach(staleLoads);
            // Decide action
            String existingDoor = correctLoad.getDockDoor();
            String eventDoor = input.getDockDoor();
            if (existingDoor == null) {
                result.setAction(Action.ATTACH);
            } else if (existingDoor.equalsIgnoreCase(eventDoor)) {
                result.setAction(Action.NO_ACTION);
            } else {
                result.setAction(Action.UPDATE);
            }
            return result;
        }
        // ============================================================
        // SINGLE CASE
        // ============================================================
        LoadDTO load = loads.get(0);
        //  ALWAYS populate load context (even NO_ACTION)
        result.setTargetLoadId(load.getLoadId());
        result.setDockDoor(input.getDockDoor());
        result.setOldDockDoorReference(load.getSystemReferenceNumberIdOfExistingDockDoor());
        // DETACH case
        if (ctx.getAction() == Action.DETACH) {
            List<LoadDTO> loadsToDetach = loads.stream().filter(l -> l.getDockDoor() != null).toList();
            result.setLoadsToDetach(loadsToDetach);
        }
        return result;
    }

}
