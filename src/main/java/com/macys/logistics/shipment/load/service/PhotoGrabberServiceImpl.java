package com.macys.logistics.shipment.load.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.macys.logistics.shipment.load.exception.TmRestApiException;
import org.apache.commons.io.FilenameUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.macys.logistics.shipment.load.dto.AuditTrailerFeedback;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.request.Image;
import com.macys.logistics.shipment.load.dto.request.TrailerEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.exception.PhotoGrabberException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.util.CommonUtil;
import com.macys.logistics.shipment.load.util.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import static com.macys.logistics.shipment.load.service.LoadEnrichmentServiceImpl.SYSTEM_STOP_ID;
import static com.macys.logistics.shipment.load.util.Constants.SYSTEM_LOAD_ID;


@Component
@Log4j2
public class PhotoGrabberServiceImpl implements PhotoGrabberService {

    private static final String REQUEST_ID_KEY = "requestId";

    @Autowired
  TMRestApiClient tmRestApiClient;

  @Autowired
  LoadMapper loadMapper;

  @Autowired
  private Environment env;
  
  @Autowired
  CommonUtil commonUtil;

  @Autowired
  LoadEnrichmentService loadEnrichmentService;
  
  @Value("${photograbber.attachment.max.size}")
  private long photograbberImagesMaxSize;

    @Value("${app.excluded.file.types}")
    private String excludedFileTypes;

    @Value("${photograbber.batch.size:10}") // Add a fallback value
    private int photograbber_batch_size;

    @Value("${photograbber.thread.pool.size}")
    private int Photograbber_thread_pool_size;


    private ExecutorService executor;

    @PostConstruct
    public void initExecutor() {
        this.executor = Executors.newFixedThreadPool(Photograbber_thread_pool_size);
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void uploadPhotoGrabberFiles() throws PhotoGrabberException {
        log.info("Starting PhotoGrabber upload job.");

        try {
            String loadSearchConfig = validateLoadSearchPhotoGrabberConfigExists();
            RestFindEntity restFindEntity = loadMapper.createRestFindEntity(loadSearchConfig, null, null, null);
            FindEntitiesResponse findEntitiesResponse = tmRestApiClient.restFindEntities(restFindEntity);

            if (!validateFindEntityResponse(findEntitiesResponse)) {
                log.info("No pending loads found with attachments.");
                return;
            }

            processAttachmentsInBatches(findEntitiesResponse);

        } catch (Exception e) {
            log.error("Error occurred while uploading PhotoGrabber files", e);
            throw new PhotoGrabberException("Error occurred while uploading PhotoGrabber files", e);
        }
    }

    /**
     * Main batching + streaming logic
     */
    private void processAttachmentsInBatches(FindEntitiesResponse findEntitiesResponse) throws PhotoGrabberException {
        List<LoadType> loads = findEntitiesResponse.getEntity().get(0).getLoad();

        if (loads == null || loads.isEmpty()) {
            log.info("No loads found for attachment retrieval.");
            return;
        }


        List<List<LoadType>> loadChunks = chunkListSafely(loads, photograbber_batch_size);

        for (List<LoadType> loadChunk : loadChunks) {
            RestProcessMaintainEntityAttachmentResponse metadataResponse = getAttachmentInformationForChunk(loadChunk);

            if (!validateAttachmentsInfo(metadataResponse)) {
                log.info("No attachments found for this batch of loads.");
                continue;
            }

            extractImageData(metadataResponse);

        }

        log.info("PhotoGrabber upload job submitted for all attachments.");
    }

    private void extractImageData(RestProcessMaintainEntityAttachmentResponse metadataResponse) {
        List<RestAttachmentMaintainDataType> metaList = metadataResponse.getAttachmentMaintainData();
        Map<String, String> descriptionMap = metaList.stream()
            .filter(meta -> meta.getSystemAttachmentID() != null && meta.getDescription() != null)
            .collect(Collectors.toMap(
                RestAttachmentMaintainDataType::getSystemAttachmentID,
                RestAttachmentMaintainDataType::getDescription,
                (existing, replacement) -> existing
            ));
        List<List<RestAttachmentMaintainDataType>> metaChunks = chunkListSafely(metaList, photograbber_batch_size);

        for (List<RestAttachmentMaintainDataType> metaChunk : metaChunks) {

            RestProcessMaintainEntityAttachmentResponse dataResponse = getAttachmentDataForChunk(metaChunk);

            if (!validateAttachmentsInfo(dataResponse)) {
                log.debug("No attachment data returned for this batch.");
                continue;
            }

            for (RestAttachmentMaintainDataType data : dataResponse.getAttachmentMaintainData()) {
                data.setDescription(descriptionMap.get(data.getSystemAttachmentID()));
                executor.submit(() -> {
                    try {
                        validateImagesDataAndUpload(data);
                    } catch (Exception ex) {
                        log.error("Error uploading attachment {}: {}", data.getFileName(), ex.getMessage());
                    }
                });
            }

            metaChunk.clear();
        }
    }

    /**
     * Batch-level metadata retrieval
     */
    private RestProcessMaintainEntityAttachmentResponse getAttachmentInformationForChunk(List<LoadType> loadChunk) {
        RestProcessMaintainEntityAttachment req = new RestProcessMaintainEntityAttachment();
        List<RestAttachmentMaintainDataType> dataList = new ArrayList<>();

        for (LoadType load : loadChunk) {
            RestAttachmentMaintainDataType data = new RestAttachmentMaintainDataType();
            data.setAttachmentActionEnumVal(Constants.AT_RETRIEVE);
            data.setAttachmentEntityKey(load.getSystemLoadID());
            data.setAttachmentEntityEnumVal(Constants.LOAD_TYPE);
            dataList.add(data);
        }

        req.setAttachmentMaintainData(dataList);
        return tmRestApiClient.getAttachmentsInformationFromRest(req);
    }

    /**
     * Batch-level file data retrieval
     */
    private RestProcessMaintainEntityAttachmentResponse getAttachmentDataForChunk(List<RestAttachmentMaintainDataType> metaChunk) {
        RestProcessMaintainEntityAttachment req = new RestProcessMaintainEntityAttachment();
        List<RestAttachmentMaintainDataType> dataList = new ArrayList<>();

        for (RestAttachmentMaintainDataType meta : metaChunk) {
            RestAttachmentMaintainDataType data = new RestAttachmentMaintainDataType();
            data.setAttachmentActionEnumVal(Constants.AT_RETRIEVE);
            data.setSystemAttachmentID(meta.getSystemAttachmentID());
            dataList.add(data);
        }

        req.setAttachmentMaintainData(dataList);
        return tmRestApiClient.getAttachmentsInformationFromRest(req);
    }

    private <T> List<List<T>> chunkListSafely(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        int totalSize = list.size();
        for (int i = 0; i < totalSize; i += chunkSize) {
            chunks.add(new ArrayList<>(list.subList(i, Math.min(totalSize, i + chunkSize))));
        }
        return chunks;
    }



    @Override
    public List<String> attachImagesToLoad(TrailerEvent trailerEvent) throws PhotoGrabberException, TmRestApiException, JsonProcessingException {
        log.info("Starting to attach images for request Id: {}, appointment Id: {}", trailerEvent.getRequestId(), trailerEvent.getAppointmentId());
      FindEntitiesResponse findEntitiesResponse = null;
      List<RestProcessMaintainEntityAttachmentResponse> response = null;
      String loadId = null;
      try {
          if(trailerEvent.getAppointmentId() != null) {
              log.info("Fetching load ID for appointment Id: {} requestId:{}", trailerEvent.getRequestId(), trailerEvent.getAppointmentId());
              findEntitiesResponse = loadEnrichmentService.fetchLoadId(Arrays.asList(trailerEvent.getAppointmentId()),Constants.EQ);
          }
          if(findEntitiesResponse != null) {
              validateFindEntitiesResponse(trailerEvent.getRequestId(),findEntitiesResponse, trailerEvent.getAppointmentId());
              loadId = findEntitiesResponse.getEntity().get(0).getStop().get(0).getSystemLoadID();
              log.info("Successfully found load Id: {} for appointment Id: {}  requestId:{}", loadId, trailerEvent.getAppointmentId() ,trailerEvent.getRequestId());
          }

          if(loadId != null && !CollectionUtils.isEmpty(trailerEvent.getImages())) {
              log.info("Attaching {} trailer image(s) to load Id: {}", trailerEvent.getImages().size(), loadId);
              return attachImageToLoad(trailerEvent.getRequestId(),loadId, trailerEvent.getImages(), trailerEvent.getTrailerId());
          }
          log.info("No images to attach or load Id not found. Skipping attachment. Request Id: {}", trailerEvent.getRequestId());
          return Collections.emptyList();
      } catch (Exception e) {
          log.error("Exception occurred while processing trailer images to load with exception {} for request Id {} ", e.getMessage(), trailerEvent.getRequestId());
          if(e instanceof TmRestApiException){
              throw e;
          }
          throw new PhotoGrabberException("Exception occurred while processing trailer images to load " + trailerEvent.getRequestId(), e);
      }
    }


  private boolean validateAttachmentsInfo(
      RestProcessMaintainEntityAttachmentResponse attachmentResponse1) {
    return attachmentResponse1 != null && attachmentResponse1.getAttachmentMaintainData() != null;
  }



  private boolean validateFindEntityResponse(FindEntitiesResponse findEntitiesResponse) {
    return findEntitiesResponse != null && findEntitiesResponse.getEntity() != null 
            && !findEntitiesResponse.getEntity().isEmpty() 
            && findEntitiesResponse.getEntity().get(0).getLoad() != null 
            && !findEntitiesResponse.getEntity().get(0).getLoad().isEmpty();
  }



  private String validateLoadSearchPhotoGrabberConfigExists() throws PhotoGrabberException {
    String loadSearchConfig = env.getProperty(Constants.PHOTOGRABBER_LOAD_SEARCH_CONFIG);
    if (loadSearchConfig == null) {
        log.error(Constants.PHOTOGRABBER_LOAD_SEARCH_CONFIG_MISSING);
        throw new PhotoGrabberException(Constants.PHOTOGRABBER_LOAD_SEARCH_CONFIG_MISSING_EXCEPTION);
    }
    return loadSearchConfig;
  }



  public void validateImagesDataAndUpload(RestAttachmentMaintainDataType data)
      throws PhotoGrabberException {
      List<String> excludedFileTypeList = Arrays.asList(excludedFileTypes.split(","));
    if (data != null && data.getFileData() != null && data.getAttachmentEntityKey() != null && data.getFileType() != null) {
        String originalFileName = data.getName();
        String fileName = data.getAttachmentEntityKey() + "_" + data.getSystemAttachmentID() + "." + data.getFileType();
        String categoryCode = data.getAttachmentCategoryCode();
        String description = data.getDescription();
      if (!excludedFileTypeList.contains(data.getFileType()) &&
          (Constants.CHARGE_BACK_VENDOR.equals(categoryCode) ||
            (Constants.NO_CHARGE.equals(categoryCode) && description != null && Constants.PALLET_PATTERN.matcher(description).find())
          )
    ) {
                String base64FileData = data.getFileData();
                String fileType = data.getFileType();
                long len = data.getFileData().getBytes(StandardCharsets.UTF_8).length;
                if (len > photograbberImagesMaxSize) {
                    log.error(Constants.PHOTOGRABBER_ATTACHMENT_IS_TOO_BIG_FOR_THE_IMAGE, fileName);
                    throw new IllegalArgumentException(Constants.PHOTOGRABBER_ATTACHMENT_IS_TOO_BIG);
                }

            commonUtil.uploadToBucket(base64FileData, fileName, fileType,originalFileName);
        } else {
            log.error("File {} changed to {} is not uploaded to GCS due to failed precondition of category code {} and fileType {}",
                    originalFileName,fileName, data.getAttachmentCategoryCode(), data.getFileType());
        }

    }
  }

    private List<String> attachImageToLoad(
        String requestId, String loadId, List<Image> imageList, String trailerId) throws PhotoGrabberException {
      List<String> finalAttachmentNames = new ArrayList<>();
      log.info("Request Id: {}. Starting to attach {} image(s) for loadId: {}", requestId, imageList.size(), loadId);
      List<String> existingAttachmentNames = getExistingAttachmentNames(loadId);
        for (Image image : imageList) {

            String imageUrl = image.getUrl();
            RestProcessMaintainEntityAttachment attachmentRequest = new RestProcessMaintainEntityAttachment();
            RestAttachmentMaintainDataType attachmentData = new RestAttachmentMaintainDataType();
            addAttachmentMainData(imageUrl, loadId, trailerId, attachmentData, existingAttachmentNames);

            String finalName = attachmentData.getName();
            finalAttachmentNames.add(finalName);
            existingAttachmentNames.add(finalName);

            updateImageUrl(image, finalName);

            attachmentData.setAttachmentActionEnumVal(Constants.REF_NUM_ACTION_ENUM_VAL_ADD);
            attachmentData.setAttachmentEntityKey(loadId);
            attachmentData.setAttachmentEntityEnumVal(Constants.LOAD_TYPE);
            attachmentRequest.setAttachmentMaintainData(List.of(attachmentData));

            try {
                log.info("Request Id: {}. Attaching image '{}' to loadId: {}", requestId, finalName, loadId);
                tmRestApiClient.getAttachmentsInformationFromRest(attachmentRequest);
                log.info("Request Id: {}. Successfully attached image '{}' to loadId: {}", requestId, finalName, loadId);
            } catch (Exception e ){
                log.warn("Request Id: {}. Exception while attaching image '{}' to loadId: {}", requestId, finalName, loadId, e);
                AuditTrailerFeedback auditFeedback = new AuditTrailerFeedback();
                auditFeedback.setErrorCode(Constants.IMAGE_ATTACH_ERROR_CODE);
                auditFeedback.setErrorMessage(Constants.IMAGE_ATTACH_ERROR_DESC.replace("{responseCode}", String.valueOf(loadId))
                        .replace("{errorMessage}", e.getMessage()));
                List<Object> auditPayload = new ArrayList<>();
                auditPayload.add(Map.of(REQUEST_ID_KEY, requestId));
                auditPayload.add(Map.of("failure_image_attach_details", attachmentRequest));
                auditFeedback.setInputArguments(new Object[]{auditPayload});
                commonUtil.publishTrailerImageAuditFeedbackToPubSub(auditFeedback, requestId, Constants.IMAGE_ATTACH_PROCESS_ACTION, Constants.SHIPMENT_LOAD_FAILURE_STATUS);
                if (e.getCause() instanceof HttpClientErrorException && ((HttpClientErrorException) e.getCause()).getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                   throw e;
                }else {
                    throw new PhotoGrabberException("Exception while attaching the image for the loadId {} " + loadId, e);
                }
            }

            AuditTrailerFeedback auditFeedback = new AuditTrailerFeedback();
            List<Object> auditPayload = new ArrayList<>();
            auditPayload.add(Map.of(REQUEST_ID_KEY, requestId));
            auditPayload.add(Map.of("success_image_attach_details", attachmentRequest));
            auditFeedback.setInputArguments(new Object[]{auditPayload});
            commonUtil.publishTrailerImageAuditFeedbackToPubSub(auditFeedback, requestId, Constants.IMAGE_ATTACH_PROCESS_ACTION, Constants.SHIPMENT_LOAD_SUCCESS_STATUS);
      }
      log.info("Request Id: {}. Finished attaching {} images for loadId: {}. Final names: {}", requestId, finalAttachmentNames.size(), loadId, finalAttachmentNames);
      return finalAttachmentNames;
    }

    private void updateImageUrl(Image image, String finalName) {
      String originalUrl = image.getUrl();
      String path = FilenameUtils.getPath(originalUrl);
      String extension = FilenameUtils.getExtension(originalUrl);
      if (!path.isEmpty() && !extension.isEmpty()) {
        String newImageUrl = path + finalName + "." + extension;
        image.setUrl(newImageUrl);
        log.debug("New image URL with imageName as loadId-imageId: {}", newImageUrl);
      } else {
        log.debug("Could not update the image URL for original URL: {}", originalUrl);
      }
    }

    private List<String> getExistingAttachmentNames(String loadId) {
      RestProcessMaintainEntityAttachment req = new RestProcessMaintainEntityAttachment();
      RestAttachmentMaintainDataType data = new RestAttachmentMaintainDataType();
      data.setAttachmentActionEnumVal(Constants.AT_RETRIEVE);
      data.setAttachmentEntityKey(loadId);
      data.setAttachmentEntityEnumVal(Constants.LOAD_TYPE);
      req.setAttachmentMaintainData(List.of(data));

      try {
        RestProcessMaintainEntityAttachmentResponse response = tmRestApiClient.getAttachmentsInformationFromRest(req);
        if (response != null && response.getAttachmentMaintainData() != null) {
          return response.getAttachmentMaintainData().stream()
              .map(RestAttachmentMaintainDataType::getName)
              .collect(Collectors.toCollection(ArrayList::new));
        }
      } catch (Exception e) {
        log.warn("Could not retrieve existing attachments for loadId {}. Proceeding without duplicate check. Error: {}", loadId, e.getMessage());
      }
      return new ArrayList<>();
    }

    private void addAttachmentMainData(String imageUrl, String loadId, String trailerId, RestAttachmentMaintainDataType attachmentMaintainData, List<String> existingAttachmentNames) throws PhotoGrabberException {
        String extension = FilenameUtils.getExtension(imageUrl);
        String baseName = loadId + "-" + trailerId;
        String finalImageName = baseName;
        int sequence = 1;

        while (existingAttachmentNames.contains(finalImageName)) {
          finalImageName = baseName + "-" + sequence;
          sequence++;
        }

        String objectName = FilenameUtils.getName(imageUrl);
        String base64String = downloadImage(imageUrl, objectName);

        attachmentMaintainData.setFileType(extension);
        attachmentMaintainData.setName(finalImageName);
        attachmentMaintainData.setFileData(base64String);
    }

    public String downloadImage(String urlString, String objectName) throws PhotoGrabberException {
        String bucketName = null;
        String regex = "\\/browser\\/([^\\/]+)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(urlString);

        if (matcher.find()) {
            bucketName = matcher.group(1);
            log.debug("Extracted Bucket Name: {}", bucketName);
        }
        Storage storage = StorageOptions.getDefaultInstance().getService();

        Blob blob = storage.get(bucketName, objectName);
        if (blob == null) {
            throw new PhotoGrabberException("File not found in bucket: " + bucketName + "/" + objectName);
        }

        byte[] content = blob.getContent();
        return Base64.getEncoder().encodeToString(content);
    }

    private void validateFindEntitiesResponse(String requestId, FindEntitiesResponse findEntitiesResponse, String appointmentNumber) throws PhotoGrabberException, JsonProcessingException {

        boolean isInvalidResponse = Optional.ofNullable(findEntitiesResponse)
                .map(FindEntitiesResponse::getEntity)
                .filter(entities -> !entities.isEmpty())
                .map(entities -> entities.get(0).getStop())
                .filter(stops -> !stops.isEmpty())
                .isEmpty();

        if (isInvalidResponse) {
            log.warn("No load Id found for appointment {}", appointmentNumber);
            RestFindEntity restFindEntity = getRequestPayload(appointmentNumber);
            AuditTrailerFeedback auditFeedback = new AuditTrailerFeedback();
            auditFeedback.setErrorCode(Constants.BY_TMS_LOAD_ERROR_CODE);
            auditFeedback.setErrorMessage(Constants.BY_TMS_LOAD_ERROR_DESC.replace("{responseCode}", String.valueOf(appointmentNumber))
                    .replace("{errorMessage}", "No load Id found for appointment"));
            List<Object> auditPayload = new ArrayList<>();
            auditPayload.add(Map.of(REQUEST_ID_KEY, requestId));
            auditPayload.add(Map.of("failure_load_details", restFindEntity));
            auditFeedback.setInputArguments(new Object[]{auditPayload});
            commonUtil.publishTrailerImageAuditFeedbackToPubSub(auditFeedback, requestId, Constants.BY_TMS_LOOKUP_PROCESS_ACTION, Constants.SHIPMENT_LOAD_FAILURE_STATUS);
            throw new PhotoGrabberException("No load Id found for appointment " + appointmentNumber);
        }
    }

    private RestFindEntity getRequestPayload(String appointmentNumber) {

        RestFindEntity restFindEntity = new RestFindEntity();
        List<RestFilterType> filter = new ArrayList<>();
        RestFilterType restFilterType1 = new RestFilterType();
        restFilterType1.setName(SYSTEM_STOP_ID);
        restFilterType1.setOp(Constants.EQ);
        List<String> apps = Arrays.asList(appointmentNumber);
        restFilterType1.setValue(apps);
        filter.add(restFilterType1);
        restFindEntity.setFilter(filter);
        RestSelectClauseType select=new RestSelectClauseType();
        List<String> names = Arrays.asList(SYSTEM_LOAD_ID);
        select.setName(names);
        restFindEntity.setSelect(select);
        return restFindEntity;
    }

}
