package com.macys.logistics.shipment.load.service;


import com.macys.logistics.shipment.load.dto.TrailerDoorDockCDCEvent;
import com.macys.logistics.shipment.load.dto.TrlrData;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleInputDTO;
import com.macys.logistics.shipment.load.dto.request.TrailerYardAppointmentInfo;

import java.util.List;

public interface TrailerDockDoorService {

    String processDockDoorAttach(String loadId,String dockDoor);
    String processDockDoorUpdate(String referenceNumber, String loadId,String dockDoor);
    String processDeleteDockDoor(List<String> oldDockDoorReferenceNumber, String loadId, String dockDoor);

    RuleInputDTO fetchLoadDetails(String trlrNbr,List<TrailerYardAppointmentInfo> trailerYardAppointmentInfo, TrailerDoorDockCDCEvent event);

    RuleInputDTO performDockDoorProcess(TrlrData currentTrlr, TrailerDoorDockCDCEvent event);
}
