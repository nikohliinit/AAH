package com.macys.logistics.shipment.load.dto.dockdoor;


import lombok.Data;
import java.util.List;
@Data
public class DockDoorRequest {
    private String trailerNumber;
    private String yardType;  // DOOR / YARD
    private String dockDoor;
    private List<LoadDTO> trailerLoads;
    private List<LoadDTO> doorLoads;
}
