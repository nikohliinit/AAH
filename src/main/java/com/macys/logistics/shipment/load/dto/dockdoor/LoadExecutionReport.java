package com.macys.logistics.shipment.load.dto.dockdoor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadExecutionReport {
    private String loadId;
    private String dockDoor;
    private Action action;
    private String status;
    private String message;
}