package com.macys.logistics.shipment.load.util;

import java.sql.Timestamp;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.AuditFeedback;
import com.macys.logistics.shipment.load.dto.AuditTrailerFeedback;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.exception.TmsAuditMessagePublishException;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.macys.logistics.shipment.load.exception.PhotoGrabberException;
import lombok.extern.log4j.Log4j2;


@Component
@Log4j2
public class CommonUtil {
  
  @Value("${spring.cloud.gcp.bucket.id}")
  private String gcpBucketId;
  @Autowired
  private ObjectMapper objMapper;
  @Value("${gcp.pubsub.audit.feedback.publish.camel.route}")
  private String auditFeedbackPublishGCPRoute;

  @Value("${audit.feedback.trailer.image.publish.camel.route}")
  private String trailerImageAuditPublishGCPRoute;

  @Value("${audit.feedback.dock.door.audit.publish.camel.route}")
  private String dockDoorAuditPublishGCPRoute;

  private final CamelContext camelContext;

  public CommonUtil(CamelContext camelContext) {
    this.camelContext = camelContext;
  }

  @Retryable(retryFor = {
      PhotoGrabberException.class }, maxAttemptsExpression = "#{${max.retry.attempts}}", backoff = @Backoff(delayExpression = "${retry.backoff.delay}"))
  public void uploadToBucket(String base64FileData, String fileName, String contentType, String originalFileName)
      throws PhotoGrabberException {

    try {
      byte[] fileData = decodeBase64(base64FileData);
      Storage storage = StorageOptions.getDefaultInstance().getService();
      Bucket bucket = storage.get(gcpBucketId, Storage.BucketGetOption.fields());
      Blob blob = bucket.create(fileName, fileData, contentType);
      if (Optional.ofNullable(blob).isPresent()) {
        log.info(Constants.FILE_UPLOADED, originalFileName, fileName);
      } else {
        log.warn(Constants.BUCKET_MEDIA_LINK_NULL, originalFileName, fileName);
      }

    } catch (Exception e) {
      log.error(
          Constants.FILE_UPLOAD_ERROR,
          e.getMessage(), e.getCause());
      throw new PhotoGrabberException(Constants.FILE_UPLOAD_TO_BUCKET_EXCEPTION);
    }
  }

  @Recover
  public void recover(PhotoGrabberException e, String base64FileData, String fileName, String contentType) {
    log.error(Constants.MAX_RETRIES_EXCEEDED, fileName);
  }

  public byte[] decodeBase64(String base64FileData) {
    if (base64FileData == null) {
      throw new IllegalArgumentException(Constants.IMAGE_DATA_VALIDATION);
    }
    return Base64.decodeBase64(base64FileData);
  }

  public void publishAuditFeedbackToPubSub(AuditFeedback payload, String request) {
    try {
      ShipmentEnterpriseMessage shipment = objMapper.readValue(request, ShipmentEnterpriseMessage.class);
      payload.setCorrelationId(String.valueOf(shipment.getShipmentNumber()));
      payload.setAuditDetail(shipment);

      payload.setApplicationName(Constants.DEFAULT_APPLICATION_NAME);
      if(payload.getProcessName()== null) {
          payload.setProcessName(Constants.DEFAULT_PROCESS_NAME);
      }
      ZonedDateTime zonedDateTimeEST = ZonedDateTime.now(ZoneId.of(Constants.TIMEZONE_EST));
      payload.setAuditCreateTs(Timestamp.valueOf(zonedDateTimeEST.toLocalDateTime()));

      try (ProducerTemplate producerTemplate = camelContext.createProducerTemplate()) {
        String jsonPayload = objMapper.writeValueAsString(payload);
        producerTemplate.sendBody(dockDoorAuditPublishGCPRoute, jsonPayload);
        log.info("audit feedback published successfully to PubSub topic");
      }

    } catch (Exception e) {
      log.error("Exception occurred while publishing audit feedback to PubSub topic {}", e.getMessage());
      throw new TmsAuditMessagePublishException(e.getMessage(), e);
    }
  }

  public void createAndPublishFeedbackAuditMessage(String processAction, String request) {
    AuditFeedback auditFeedback = new AuditFeedback();
    auditFeedback.setStatus(Constants.SHIPMENT_LOAD_SUCCESS_STATUS);
    auditFeedback.setProcessAction(processAction);
    publishAuditFeedbackToPubSub(auditFeedback, request);
  }

  public void publishTrailerImageAuditFeedbackToPubSub(AuditTrailerFeedback payload, String correlationId,
      String processAction, String status) {
    try {

      payload.setTraceId(correlationId);
      payload.setActionName(processAction);
      payload.setStatus(status);
      payload.setApplicationName(Constants.DEFAULT_APPLICATION_NAME);
      payload.setProcessName(Constants.TRAILER_IMAGE_PROCESS_NAME);

      payload.setOperationSuccessful(status);
      ZonedDateTime zonedDateTimeEST = ZonedDateTime.now(ZoneId.of(Constants.TIMEZONE_EST));
      payload.setOccurredAt(Timestamp.valueOf(zonedDateTimeEST.toLocalDateTime()));

      try (ProducerTemplate producerTemplate = camelContext.createProducerTemplate()) {
        String jsonPayload = objMapper.writeValueAsString(payload);
        producerTemplate.sendBody(trailerImageAuditPublishGCPRoute, jsonPayload);
        log.info(
            "Processing auditable log - Trailer Request Payload: {}, ErrorCode: {}, ErrorMessage: {}, Action: {}, CorrelationId: {}",
            payload, payload.getErrorCode(), payload.getErrorMessage(), processAction, correlationId);
        log.info("Trailer Image Audit feedback published successfully to PubSub topic. Status: {}", status);
      }

    } catch (Exception e) {
      log.error("Exception occurred while publishing audit feedback to PubSub topic {}", e.getMessage());
      throw new TmsAuditMessagePublishException(e.getMessage(), e);
    }
  }

    public void publishDockDoorAuditFeedbackToPubSub(AuditTrailerFeedback payload, String correlationId, String processAction, String status) {
        try {

            ZonedDateTime zonedDateTimeEST = ZonedDateTime.now(ZoneId.of(Constants.TIMEZONE_EST));
            payload.setOccurredAt(Timestamp.valueOf(zonedDateTimeEST.toLocalDateTime()));
            payload.setTraceId(correlationId);
            payload.setActionName(processAction);
            payload.setStatus(status);
            payload.setApplicationName(Constants.DEFAULT_APPLICATION_NAME);
            payload.setProcessName(Constants.DEFAULT_PROCESS_NAME_OD_DOCK_DOOR);
            payload.setOperationSuccessful(status);

            try (ProducerTemplate producerTemplate = camelContext.createProducerTemplate()) {
                String jsonPayload = objMapper.writeValueAsString(payload);
                producerTemplate.sendBody(auditFeedbackPublishGCPRoute, jsonPayload);
                log.info("Processing auditable log - Dock Door Request Payload: {}, ErrorCode: {}, ErrorMessage: {}, Action: {}, CorrelationId: {}",
                        payload, payload.getErrorCode(), payload.getErrorMessage(), processAction, correlationId);
                log.info("Dock Door Audit feedback published successfully to PubSub topic. Status: {}", status);
            }

        } catch (Exception e) {
            log.error("Exception occurred for Dock Door while publishing audit feedback to PubSub topic {}", e.getMessage());
            throw new TmsAuditMessagePublishException(e.getMessage(), e);
        }
    }


}
