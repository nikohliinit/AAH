package com.macys.logistics.shipment.load.rule.orchestrator;

import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
public class LoadMerger {


    private LoadMerger() {
        throw new UnsupportedOperationException("Utility class");
    }
    public static List<LoadDTO> merge(
            List<LoadDTO> trailerApiLoads,
            List<LoadDTO> doorApiLoads) {
        Map<String, LoadDTO> merged = new HashMap<>();
        for (LoadDTO l : trailerApiLoads) {
            merged.put(l.getLoadId(), l);
        }
        for (LoadDTO l : doorApiLoads) {
            merged.putIfAbsent(l.getLoadId(), l);
        }
        return new ArrayList<>(merged.values());
    }
}