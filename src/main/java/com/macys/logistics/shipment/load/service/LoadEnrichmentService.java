package com.macys.logistics.shipment.load.service;


import javax.xml.datatype.DatatypeConfigurationException;

import com.macys.logistics.shipment.load.dto.AppointmentPayload;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import java.util.List;

public interface LoadEnrichmentService {
	
	 void addOrUpdateSealCondition(AppointmentPayload apptPayload, String loadId,LoadType load) throws TmsLoadProcessException;

    void addOrUpdateSealType(AppointmentPayload apptPayload, String loadId,LoadType load,String sealType) throws TmsLoadProcessException;

	 FindEntitiesResponse fetchLoadId(List<String> apptNums,String filterOperation);
	 
	 FindEntitiesResponse fetchLoadDetails(String loadId);

	void updateStopAppointment(AppointmentPayload apptPayload, LoadType load) throws TmsLoadProcessException, DatatypeConfigurationException;

	ProcessLoadRetrieveResponse fetchLoadRetrieveDetails(String loadId);

	void processCancelLoad(ProcessLoadCancel processLoadCancel);

	void processCancleTender(ProcessTenderCancel processTenderCancel);

	void processLoadUpdate(String loadId, String trailerNbr);

	void dispatchFlagTrigger(AppointmentPayload apptPayload, LoadType load);

	ShipmentResponse searchShipment(String shipmentNumber);

	void deleteReferenceNumber(String shipmentNumber, ShipmentResponse shipmentResponse);

	void processLoadRate(ProcessLoadRate processLoadRate);

	void removeShipmentFromLoad(String shipmentNumber, String systemLoadID);

	void processLoadReturnToOpen(ProcessLoadReturnToOpen processLoadReturnToOpen);
	
	ShipmentResponse searchShipmentWithOldAppointmentRef(String oldAppointmentNumber);

    void triggerLoadCompleteForObtmAppointments(String loadId, LoadType load, String apptNumber, int pieces);

	void updateCartonCountForShipment(String loadId, LoadType load, Integer appointmentNumberOfCartons);

	void updateFlexibleQ1andFlexibleQ2andReRate(String loadId, LoadType load, AppointmentPayload appointmentPayload, ShipmentLegType shipmentLegType);

    FindEntitiesResponse fetchLoadDetailsForDockDoor(String config,String filterField,List<String> filterValue,String filterOperation);
    FindEntitiesResponse fetchLoadByAppointment(List<String> apptNums,String filterOperation);
}
