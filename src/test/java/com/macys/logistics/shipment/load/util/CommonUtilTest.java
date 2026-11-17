package com.macys.logistics.shipment.load.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.macys.logistics.shipment.load.dto.AuditTrailerFeedback;
import com.macys.logistics.shipment.load.dto.request.TrailerEvent;
import com.macys.logistics.shipment.load.exception.TmsAuditMessagePublishException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.macys.logistics.shipment.load.exception.PhotoGrabberException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.AuditFeedback;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

@ExtendWith(MockitoExtension.class)
 class CommonUtilTest {
  
  private final String fileData = "/9j/4AAQSkZJRgABAQAASABIAAD/";
  private final String fileName = "test-file.xml";
  private final String contentType = "jpeg";
  private final String originalFileName = "original-file.xml";
  @Mock
  private Bucket bucket;

  @Mock
  private Blob mockBlob;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private CamelContext camelContext;

  @Mock
  private ProducerTemplate producerTemplate;

  private CommonUtil commonUtil;

  @BeforeEach
  public void setup() {
      commonUtil = new CommonUtil(camelContext);
      ReflectionTestUtils.setField(commonUtil, "gcpBucketId", "mock-gcpBucketId");
      ReflectionTestUtils.setField(commonUtil, "objMapper", objectMapper);
      ReflectionTestUtils.setField(commonUtil, "auditFeedbackPublishGCPRoute", "mockRoute");
      ReflectionTestUtils.setField(commonUtil, "gcpBucketId",
              "mock-gcpBucketId");

  }

  @Test
  void testUploadToBucketSuccess() {
      Storage mockStorage = Mockito.mock(Storage.class);
      MockedStatic<StorageOptions> storageOptionsMock =
              Mockito.mockStatic(StorageOptions.class, Mockito.RETURNS_DEEP_STUBS);

      storageOptionsMock.when(() ->
                      StorageOptions.getDefaultInstance()
                              .getService())
              .thenReturn(mockStorage);
      when(mockStorage.get(anyString(), any(Storage.BucketGetOption.class))).thenReturn(bucket);
      when(bucket.create(anyString(), any(byte[].class), anyString())).thenReturn(mockBlob);
      assertDoesNotThrow(() -> commonUtil.uploadToBucket(fileData, fileName, contentType,originalFileName));
      storageOptionsMock.close();
  }

  @Test
  void testUploadToBucketFailure() {
      Storage mockStorage = Mockito.mock(Storage.class);
      MockedStatic<StorageOptions> storageOptionsMock =
              Mockito.mockStatic(StorageOptions.class, Mockito.RETURNS_DEEP_STUBS);

      storageOptionsMock.when(() ->
                      StorageOptions.getDefaultInstance()
                              .getService())
              .thenReturn(mockStorage);
      when(mockStorage.get(anyString(), any(Storage.BucketGetOption.class))).thenReturn(bucket);
      when(bucket.create(anyString(), any(byte[].class), anyString())).thenReturn(null);
      assertDoesNotThrow(() -> commonUtil.uploadToBucket(fileData, fileName, contentType,originalFileName));
      storageOptionsMock.close();
  }

  @Test
  void testUploadToBucketException() {

      assertThrows(PhotoGrabberException.class,
              () -> commonUtil.uploadToBucket(fileData, fileName, contentType,originalFileName));
  }
  
  
  @Test
  void testRecovery() {

      assertDoesNotThrow(() -> commonUtil.recover(new PhotoGrabberException(), fileData, fileName, contentType));

  }
  
  @Test
  void testUploadToBucketWithNullImageData() {
    String fileDataNull= null;
      assertThrows(IllegalArgumentException.class,
              () -> commonUtil.decodeBase64(fileDataNull));
  }

    @Test
    void testPublishAuditFeedbackToPubSubSuccess() throws Exception {

        AuditFeedback feedback = new AuditFeedback();
        ShipmentEnterpriseMessage shipmentMessage = new ShipmentEnterpriseMessage();
        shipmentMessage.setShipmentNumber(12345L);

        String requestJson = "{\"shipmentNumber\": \"12345\"}";
        String feedbackJson = "{\"mock\":\"json\"}";

        when(objectMapper.readValue(requestJson, ShipmentEnterpriseMessage.class))
                .thenReturn(shipmentMessage);
        when(camelContext.createProducerTemplate()).thenReturn(producerTemplate);
        when(objectMapper.writeValueAsString(any(AuditFeedback.class))).thenReturn(feedbackJson);

        ReflectionTestUtils.setField(commonUtil, "auditFeedbackPublishGCPRoute", "mockRoute");


        assertDoesNotThrow(() -> commonUtil.publishAuditFeedbackToPubSub(feedback, requestJson));
    }

    @Test
    void testPublishAuditFeedbackToPubSubException() throws Exception {
        AuditFeedback feedback = new AuditFeedback();
        String requestJson = "{\"shipmentNumber\": \"12345\"}";
        when(objectMapper.readValue(requestJson , ShipmentEnterpriseMessage.class ))
                .thenThrow(new RuntimeException("Parsing error"));

        assertThrows(TmsAuditMessagePublishException.class,() -> commonUtil.publishAuditFeedbackToPubSub(feedback, requestJson));
    }

    @Test
    void testPublishAuditFeedbackForTrailerImage() throws Exception {

        AuditTrailerFeedback feedback = new AuditTrailerFeedback();
        TrailerEvent event = new TrailerEvent();
        event.setRequestId("123344");
        feedback.setTraceId("12345");
        feedback.setInputArguments(new Object[]{event});
        String feedbackJson = "{\"mock\":\"json\"}";
        when(camelContext.createProducerTemplate()).thenReturn(producerTemplate);
        when(objectMapper.writeValueAsString(any(AuditTrailerFeedback.class))).thenReturn(feedbackJson);

        ReflectionTestUtils.setField(commonUtil, "auditFeedbackPublishGCPRoute", "mockRoute");


        assertDoesNotThrow(() -> commonUtil.publishTrailerImageAuditFeedbackToPubSub(feedback, feedback.getTraceId(), Constants.CONSUME_IMAGE_PROCESS_ACTION, Constants.SHIPMENT_LOAD_SUCCESS_STATUS));
    }



}
