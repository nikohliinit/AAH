package com.macys.logistics.shipment.load.dto.dockdoor;

import java.util.ArrayList;
import java.util.List;
public class CleanupResult {
    private final List<LoadDTO> loadsToDetach = new ArrayList<>();
    public void addDetach(LoadDTO load) {
        loadsToDetach.add(load);
    }
    public List<LoadDTO> getLoadsToDetach() {
        return loadsToDetach;
    }
    public boolean hasDetaches() {
        return !loadsToDetach.isEmpty();
    }
}