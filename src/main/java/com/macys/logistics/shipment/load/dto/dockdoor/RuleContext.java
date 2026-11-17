package com.macys.logistics.shipment.load.dto.dockdoor;

import lombok.Data;

@Data
public class RuleContext {
    private String trailerPresentAt;        // DOOR / YARD
    private String numberOfLoadsPresentInTM;   // NONE / SINGLE / MULTIPLE
    private Boolean dockDoorExistsWithLoad;    // YES / NO
    private Action action; //actionToLoad
    private String sameDockDoorExistsWithLoad;
    private String appointmentExistsWithLoad;

}
