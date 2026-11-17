package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;
import com.macys.logistics.shipment.load.dto.dockdoor.CleanupResult;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class DockDoorCleanupProcessorServiceImpl implements DockDoorCleanupProcessorService{
    @Override
    public CleanupResult cleanup(
            String eventTrailer,
            List<LoadDTO> loads) {
        CleanupResult result = new CleanupResult();
        Iterator<LoadDTO> it = loads.iterator();
        while (it.hasNext()) {
            LoadDTO load = it.next();
            if(load.getSystemReferenceNumberIdOfExistingDockDoor().size()>1){
                result.addDetach(load);
            }else if (load.getDockDoor() != null &&
                    load.getAppointmentNumber() == null) {
                result.addDetach(load);
                it.remove();
            }
        }
        return result;
    }

}
