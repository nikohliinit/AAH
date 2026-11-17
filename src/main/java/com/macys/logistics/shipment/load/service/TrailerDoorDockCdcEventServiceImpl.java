package com.macys.logistics.shipment.load.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.AuditTrailerFeedback;
import com.macys.logistics.shipment.load.dto.TrailerDoorDockCDCEvent;
import com.macys.logistics.shipment.load.dto.TrailerDoorDockMetaData;
import com.macys.logistics.shipment.load.dto.TrlrData;
import com.macys.logistics.shipment.load.dto.dockdoor.DockDoorProcessResponse;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleInputDTO;
import com.macys.logistics.shipment.load.rule.orchestrator.DockDoorRuleOrchestrator;
import com.macys.logistics.shipment.load.util.CommonUtil;
import com.macys.logistics.shipment.load.util.Constants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import static com.macys.logistics.shipment.load.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrailerDoorDockCdcEventServiceImpl implements TrailerDoorDockCdcEventService {

    @Value("${dockdoor.trailer.update.fields}")
    private String[] updateFieldsArray;

    private List<String> updateFields;
    private final Map<String, Function<TrlrData, Object>> fieldAccessors = new HashMap<>();

    @PostConstruct
    public void init() {

        updateFields = Arrays.asList(updateFieldsArray);

        // Map ALL fields from TrlrData DTO
        fieldAccessors.put("trlrNbr", TrlrData::getTrlrNbr);
        fieldAccessors.put("carrTrlrNbr", TrlrData::getCarrTrlrNbr);
        fieldAccessors.put("trlrClassNbr", TrlrData::getTrlrClassNbr);
        fieldAccessors.put("trlrTypNbr", TrlrData::getTrlrTypNbr);
        fieldAccessors.put("trlrStatNbr", TrlrData::getTrlrStatNbr);
        fieldAccessors.put("vndNbr", TrlrData::getVndNbr);
        fieldAccessors.put("locNbr", TrlrData::getLocNbr);
        fieldAccessors.put("createTs", TrlrData::getCreateTs);
        fieldAccessors.put("createUseridV", TrlrData::getCreateUseridV);
        fieldAccessors.put("lastUpdTs", TrlrData::getLastUpdTs);
        fieldAccessors.put("lastUpdUseridV", TrlrData::getLastUpdUseridV);
        fieldAccessors.put("versionNbr", TrlrData::getVersionNbr);
        fieldAccessors.put("currLocNbr", TrlrData::getCurrLocNbr);
        fieldAccessors.put("currYardNbr", TrlrData::getCurrYardNbr);
        fieldAccessors.put("currYardAreaNbr", TrlrData::getCurrYardAreaNbr);
    }
    private static final String TRLR_TABLE = "TRLR";
    private final ObjectMapper objectMapper;
    private final TrailerDockDoorService trailerDockDoorService;
    private final DockDoorRuleOrchestrator orchestrator;
    private final CommonUtil commonUtil;
    @Override
    public void processDockDoorCDCEvent(String rawPayload) {
        AuditTrailerFeedback auditFeedback = new AuditTrailerFeedback();
        List<Object> auditPayload = new ArrayList<>();
        String correlationId = java.util.UUID.randomUUID().toString();

        try {
            TrailerDoorDockCDCEvent event = objectMapper.readValue(rawPayload, TrailerDoorDockCDCEvent.class);

            if (event.getUserdata() != null && event.getUserdata().getUuid() != null) {
                correlationId = event.getUserdata().getUuid();
            }

            MDC.put(CORRELATION_KEY, correlationId);
            MDC.put(TRACE_KEY, correlationId);

            auditPayload.add(Map.of(CORRELATION_KEY, correlationId));
            auditPayload.add(Map.of(DOCK_DOOR_REQUEST_PAYLOAD, event));
            auditFeedback.setInputArguments(new Object[]{auditPayload});

            commonUtil.publishDockDoorAuditFeedbackToPubSub(auditFeedback, correlationId, Constants.DOCK_DOOR_RECEIVED_PAYLOAD_ACTION, Constants.SHIPMENT_LOAD_SUCCESS_STATUS);
            handleSingleEvent(event);

        } catch (Exception ex) {
            log.error("Failed to parse TRLR CDC payload. rawPayload={}", rawPayload);
            auditFeedback.setErrorMessage(DOCK_DOOR_CDC_INVALID_DATA_RECEIVED.concat(ex.getMessage()));
            auditFeedback.setErrorCode(DOCK_DDOR_CDC_INVALID_DATA_ERROR_CODE);
            auditPayload.add(Map.of(CORRELATION_KEY, correlationId));
            auditPayload.add(Map.of(DOCK_DOOR_REQUEST_PAYLOAD, rawPayload));
            auditFeedback.setInputArguments(new Object[]{auditPayload});
            commonUtil.publishDockDoorAuditFeedbackToPubSub(auditFeedback, correlationId, Constants.DOCK_DOOR_RECEIVED_PAYLOAD_WITH_EXCEPTION_ACTION, Constants.SHIPMENT_LOAD_FAILURE_STATUS);
            throw new IllegalStateException("Failed to process TRLR CDC payload", ex);
        }
    }

    private void handleSingleEvent(TrailerDoorDockCDCEvent event) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        TrailerDoorDockMetaData md = event.getMetadata();
        if (md == null) {
            log.warn("Skipping TRLR CDC event with null metadata: {}", event);
            return;
        }

        String tableName = Optional.ofNullable(md.getTableName())
                .orElse("")
                .toUpperCase(Locale.ROOT);
        if (!tableName.contains(TRLR_TABLE)) {
            log.debug("Ignoring CDC event for non-TRLR table: {}", tableName);
            return;
        }

        String op = Optional.ofNullable(md.getOperationName())
                .orElse(UNKNOWN_TRAILER_TYPE)
                .toUpperCase(Locale.ROOT);
        TrlrData after = event.getData();
        TrlrData before = event.getBefore();

        // Check for updates in the restricted fields
        boolean shouldRestrictFlow = shouldRestrictFlow(before, after);

        if (shouldRestrictFlow) {
            log.info("Update for restricted fields detected, skipping processing for trlrNbr={}", after != null ? after.getTrlrNbr() : null);
            AuditTrailerFeedback auditFeedback = new AuditTrailerFeedback();
            List<Object> auditPayload = new ArrayList<>();
            auditFeedback.setErrorMessage(DOCK_DOOR_CDC_INVALID_SCENARIO);
            auditPayload.add(Map.of(CORRELATION_KEY, MDC.get(CORRELATION_KEY)));
            auditPayload.add(Map.of(DOCK_DOOR_REQUEST_PAYLOAD, event));
            auditFeedback.setInputArguments(new Object[]{auditPayload});
            commonUtil.publishDockDoorAuditFeedbackToPubSub(auditFeedback, MDC.get(CORRELATION_KEY), Constants.DOCK_DOOR_NO_ACTION_REQUIRED, Constants.SHIPMENT_LOAD_SUCCESS_STATUS);
            return;
        }

        switch (op) {
            case "INSERT" -> handleInsert(md, after,event);
            case "UPDATE" -> handleUpdate(md, before, after,event);
            case "DELETE" -> log.debug("Ignoring TRLR DELETE CDC event for trlrNbr={}",
                    after != null ? after.getTrlrNbr() : null);
            default -> log.warn("Unknown OperationName '{}' for TRLR CDC event. metadata={}", op, md);
        }
    }

    private void handleInsert(TrailerDoorDockMetaData md, TrlrData after,TrailerDoorDockCDCEvent event) {
        if (after == null) {
            log.warn("INSERT TRLR CDC event with null data. metadata={}", md);
            return;
        }
        log.info(
                "[TRLR INSERT] trlrNbr={}, carrTrlrNbr={}, statNbr={}, currLocNbr={}, currYardNbr={}, currYardAreaNbr={}, csn={}",
                after.getTrlrNbr(),
                after.getCarrTrlrNbr(),
                after.getTrlrStatNbr(),
                after.getCurrLocNbr(),
                after.getCurrYardNbr(),
                after.getCurrYardAreaNbr(),
                md.getCsn()
        );
        performDockDoorProcess(after,event);

    }

    private void performDockDoorProcess(TrlrData currentTrlr,TrailerDoorDockCDCEvent event) {
        AuditTrailerFeedback auditFeedback = new AuditTrailerFeedback();
        List<Object> auditPayload = new ArrayList<>();
        RuleInputDTO ruleInput= RuleInputDTO.builder().build();
        try {
            log.info("Starting dock door process for trailerNumber={}",
                    currentTrlr != null ? currentTrlr.getTrlrNbr() : UNKNOWN_TRAILER_TYPE);

             ruleInput = trailerDockDoorService.performDockDoorProcess(currentTrlr,event);

            if (ObjectUtils.isNotEmpty(ruleInput) && ruleInput.getTrailerLoads()!=null
                    && ruleInput.getDoorLoads()!=null && ruleInput.getErrorMessage()==null) {

                log.debug("RuleInputDTO received: {}", ruleInput);

                DockDoorProcessResponse dockDoorAuditResponse= orchestrator.orchestrate(
                        ruleInput,
                        ruleInput.getTrailerLoads(),
                        ruleInput.getDoorLoads()
                );
                log.debug("Final Dock Door Audit Response {}",dockDoorAuditResponse);
                log.info("Rule evaluation completed for trailerNumber={}", ruleInput.getTrailerNumber());
                String ruleStatus=Constants.SHIPMENT_LOAD_SUCCESS_STATUS;
                if (!dockDoorAuditResponse.getFinalStatus().equalsIgnoreCase(DOCK_DOOR_RULE_STATUS_FULL_SUCCESS)) {
                    auditFeedback.setErrorMessage(DOCK_DOOR_DOOR_PROCESS_SCENARIO);
                    auditFeedback.setErrorCode(DOCK_DOOR_DOOR_PROCESS_SCENARIO_CODE);
                    ruleStatus=Constants.SHIPMENT_LOAD_FAILURE_STATUS;
                }
                if (dockDoorAuditResponse.getFinalStatus().equalsIgnoreCase(DOCK_DOOR_RULE_STATUS_PARTIAL_SUCCESS)) {
                    auditFeedback.setErrorCode(DOCK_DOOR_DOOR_PROCESS_PARTIAL_SUCCESS_SCENARIO_CODE);
                }
                auditPayload.add(Map.of(CORRELATION_KEY, MDC.get(CORRELATION_KEY)));
                auditPayload.add(Map.of(DOCK_DOOR_REQUEST_PAYLOAD, event));
                auditPayload.add(Map.of(DOCK_DOOR_FINAL_RESPONSE,dockDoorAuditResponse));
                auditPayload.add(Map.of(DOCK_DOOR_RULE_INPUT,ruleInput));
                auditFeedback.setInputArguments(new Object[]{auditPayload});
                commonUtil.publishDockDoorAuditFeedbackToPubSub(auditFeedback, MDC.get(CORRELATION_KEY), Constants.DOCK_DOOR_FINAL_STATUS.concat(dockDoorAuditResponse.getFinalStatus()), ruleStatus);

            } else {
                log.debug("Rule not executed due to ruleInputDTO is null or invalid for trailer or invalid location {}",
                        ruleInput);
            }

        } catch (Exception ex) {
            log.error("Exception occurred during dock door processing for trailerNumber={}. Error: {}",
                    currentTrlr != null ? currentTrlr.getTrlrNbr() : UNKNOWN_TRAILER_TYPE,
                    ex.getMessage(),
                    ex);
            auditFeedback.setErrorMessage(DOCK_DOOR_CDC_PROCESS_EXCEPTION.concat(ex.getMessage()));
            auditFeedback.setErrorCode(DOCK_DOOR_INVALID_RULE_INPUT_SCENARIO_CODE);
            auditPayload.add(Map.of(CORRELATION_KEY, MDC.get(CORRELATION_KEY)));
            auditPayload.add(Map.of(DOCK_DOOR_REQUEST_PAYLOAD, event));
            auditFeedback.setInputArguments(new Object[]{auditPayload});
            commonUtil.publishDockDoorAuditFeedbackToPubSub(auditFeedback, MDC.get(CORRELATION_KEY), Constants.DOCK_DOOR_INVALID_INPUT_TO_RULE_ACTION, Constants.SHIPMENT_LOAD_FAILURE_STATUS);
            throw new IllegalStateException("Failed to process TRLR CDC payload", ex);

        }
    }

    private void handleUpdate(TrailerDoorDockMetaData md, TrlrData before, TrlrData after,TrailerDoorDockCDCEvent event) {
        if (after == null) {
            log.warn("UPDATE TRLR CDC event with null data. metadata={}", md);
            return;
        }
        Long trlrNbr = after.getTrlrNbr();
        if (trlrNbr == null && before != null) {
            trlrNbr = before.getTrlrNbr();
        }
        log.info(
                "[TRLR UPDATE] trlrNbr={}, statNbr {} -> {}, currLocNbr {} -> {}, yardNbr {} -> {}, yardAreaNbr {} -> {}, csn={}",
                trlrNbr,
                before != null ? before.getTrlrStatNbr() : null,
                after.getTrlrStatNbr(),
                before != null ? before.getCurrLocNbr() : null,
                after.getCurrLocNbr(),
                before != null ? before.getCurrYardNbr() : null,
                after.getCurrYardNbr(),
                before != null ? before.getCurrYardAreaNbr() : null,
                after.getCurrYardAreaNbr(),
                md.getCsn()
        );
        performDockDoorProcess(after,event);

    }

    private boolean shouldRestrictFlow(TrlrData before, TrlrData after) {
        if (before == null) return after != null;
        if (after == null) return true;

        for (String field : updateFields) {
            Function<TrlrData, Object> getter = fieldAccessors.get(field);
            if (getter == null) {
                log.warn("No getter mapping found for field '{}'", field);
                continue; // Skip unknown fields
            }

            Object beforeValue = getter.apply(before);
            Object afterValue = getter.apply(after);

            if (!Objects.equals(beforeValue, afterValue)) {
                log.info("Field '{}' changed from '{}' to '{}'", field, beforeValue, afterValue);
                return false; // Change detected → continue processing
            }
        }
        return true; // No changes → restrict flow
    }



}