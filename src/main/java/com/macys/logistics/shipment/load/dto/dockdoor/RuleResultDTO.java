package com.macys.logistics.shipment.load.dto.dockdoor;

import lombok.Data;

import java.util.List;

@Data
public class RuleResultDTO {
    private Action action;
    private String targetLoadId;
    private String dockDoor;
    private String reason;
    private List<String> oldDockDoorReference;
    private List<LoadDTO> loadsToDetach;

    public static RuleResultDTO detachOnly(
            String loadId,
            List<String> systemRefId, String dockDoor) {
        RuleResultDTO dto = new RuleResultDTO();
        dto.setAction(Action.DETACH);
        dto.setTargetLoadId(loadId);
        dto.setOldDockDoorReference(systemRefId);
        dto.setDockDoor(dockDoor);
        return dto;
    }
}
