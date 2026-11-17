package com.macys.logistics.shipment.load.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.service.InBoundShipmentService;
import com.macys.logistics.shipment.load.util.CommonUtil;
import com.macys.logistics.shipment.load.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@Slf4j
@ConditionalOnProperty(name = "create.shipment.load.controller.enable", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class ShipmentRepublishController {

    private final InBoundShipmentService inBoundShipmentService;
    private final CommonUtil commonUtil;
    private final ObjectMapper objectMapper;

    @PostMapping(path = "/createShipment")
    public ResponseEntity<String> createShipmentInTM(@RequestBody ShipmentEnterpriseMessage shipmentPayload) {
        try {
            String payload = objectMapper.writeValueAsString(shipmentPayload);
            inBoundShipmentService.createShipment(shipmentPayload);
            commonUtil.createAndPublishFeedbackAuditMessage(Constants.SHIPMENT_LOAD_CREATE_SUCCESS_ACTION, payload);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success");
        } catch (JsonProcessingException e) {
            log.error("Error serializing shipment payload to JSON", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request payload format.");
        } catch (Exception e) {
            log.error("An unexpected error occurred while creating the shipment in TM via /createShipment API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create shipment due to an internal error.");
        }
    }
}
