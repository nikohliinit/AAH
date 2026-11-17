package com.macys.logistics.shipment.load.dto.dockdoor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DockDoorProcessResponse {
    private String trailerNumber;
    private String appointmentNumber;
    private String yardType;
    private String finalStatus;
    private List<LoadExecutionReport> loadReports;
}
