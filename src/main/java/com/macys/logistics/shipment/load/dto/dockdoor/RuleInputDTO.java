package com.macys.logistics.shipment.load.dto.dockdoor;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class RuleInputDTO {
    private String trailerNumber;
    private String yardType;
    private String dockDoor;
    private List<LoadDTO> trailerLoads = new ArrayList<>();
    private List<LoadDTO> doorLoads = new ArrayList<>();
    private List<LoadDTO> loads;
    private String appointmentNumber;
    private String errorType;
    private String errorMessage;
    private String errorCode;
}
