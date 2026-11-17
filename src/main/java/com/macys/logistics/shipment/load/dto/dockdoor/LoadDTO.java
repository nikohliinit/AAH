package com.macys.logistics.shipment.load.dto.dockdoor;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoadDTO {
    private String loadId;
    private String trailerNumber;
    private String dockDoor;
    private String status;
    private String appointmentNumber;
    private List<String> systemReferenceNumberIdOfExistingDockDoor;
}
