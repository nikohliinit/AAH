package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentTrackingMessage;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.exception.TmsLoadValidationException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.util.Constants;
import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Log4j2
public class LTLTrackingMessageServiceImpl implements LTLTrackingMessageService{

    @Autowired
    LoadEnrichmentService loadEnrichmentService;

    @Autowired
    LoadMapper loadMapper;

    @Autowired
    TMRestApiClient tmRestApiClient;

    @Override
    public void addProNumberToTM(ShipmentTrackingMessage shipmentTrackingMessage) {
        try {
            if(null != shipmentTrackingMessage.getLoadId()) {
                FindEntitiesResponse loadDetails = loadEnrichmentService.fetchLoadDetails(String.valueOf(shipmentTrackingMessage.getLoadId()));
                LoadType load = loadMapper.getLoad(loadDetails);
                validateLoadNotNull(load, String.valueOf(shipmentTrackingMessage.getLoadId()));
                if(!checkIfProNumberExists(load)){
                    addOrUpdateProNumberInTM(shipmentTrackingMessage);
                } else {
                    log.info("Carrier Pro Number already exists in TM for loadId {} ", shipmentTrackingMessage.getLoadId());
                }
            }
        } catch (TmsLoadValidationException e) {
            log.error("addProNumberToTM | Load details are not available for loadId in TM {} ", e.getMessage());
        } catch (TmsLoadProcessException e) {
            log.error("addProNumberToTM | Exception while calling TM client to add proNumber for load {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception while processing proNumber for load {}", e.getMessage());
        }

    }

    private void addOrUpdateProNumberInTM(ShipmentTrackingMessage shipmentTrackingMessage) {
        log.debug("Started method addProNumberInTM for load {}", shipmentTrackingMessage.getLoadId());
        try {
            ProcessMaintainEntityReferenceNumber processMaintainEntityReferenceNumber = new ProcessMaintainEntityReferenceNumber();
            ReferenceNumberUpdateDataType referenceNumberUpdateDataType = new ReferenceNumberUpdateDataType();
            referenceNumberUpdateDataType.setReferenceNumberActionEnumVal(Constants.REF_NUM_ACTION_ENUM_VAL_ADD);
            referenceNumberUpdateDataType.setReferenceNumberCategoryEnumVal(Constants.DEFAULT_REF_NUM_SHP_CATEGORY_ENUM_VAL);
            referenceNumberUpdateDataType.setReferenceNumberEntityKey(String.valueOf(shipmentTrackingMessage.getLoadId()));
            referenceNumberUpdateDataType.setReferenceNumberTypeCode(Constants.REF_NUM_CARR_PRO_NUMBER_TYPE_CODE);
            referenceNumberUpdateDataType.setReferenceNumber(shipmentTrackingMessage.getProNumber());
            processMaintainEntityReferenceNumber.getReferenceNumberUpdateData().add(referenceNumberUpdateDataType);


            log.debug("addProNumberInTM using rest api call for loadId {}", shipmentTrackingMessage.getLoadId());
            tmRestApiClient.processMaintainEntityReferenceNumber(processMaintainEntityReferenceNumber);
            log.info("Added ProNumber {} for the load {} successfully ", shipmentTrackingMessage.getProNumber(), shipmentTrackingMessage.getLoadId());
            log.debug("Ended method addProNumberInTM for load {}", shipmentTrackingMessage.getLoadId());

        } catch (TmsLoadProcessException e) {
            log.error("addProNumberInTM | exception while adding pro number for load {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error calling tms to add pro number for load {}", e.getMessage());
        }
    }

    private boolean checkIfProNumberExists(LoadType load) {
        if(!CollectionUtils.isEmpty(load.getReferenceNumberStructure())) {
            return load.getReferenceNumberStructure().stream()
                    .anyMatch(ref ->
                            Constants.REF_NUM_CARR_PRO_NUMBER_TYPE_CODE.equalsIgnoreCase(ref.getReferenceNumberTypeCode()) &&
                                    StringUtils.isNotBlank(ref.getReferenceNumber())
                    );
        }
        return false;
    }

    private void validateLoadNotNull(LoadType load, String loadId){
        if(load == null) {
            log.error("Load details are not available for "+ loadId);
            throw new TmsLoadValidationException("Load details are not available for "+ loadId);
        }
    }
}
