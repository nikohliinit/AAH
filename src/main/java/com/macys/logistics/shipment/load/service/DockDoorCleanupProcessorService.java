package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;
import com.macys.logistics.shipment.load.dto.dockdoor.CleanupResult;

import java.util.List;

public interface DockDoorCleanupProcessorService {
    CleanupResult cleanup(
            String eventTrailer,
            List<LoadDTO> mergedLoads);
}
