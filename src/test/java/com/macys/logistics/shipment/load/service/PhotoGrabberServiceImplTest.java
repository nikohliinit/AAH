package com.macys.logistics.shipment.load.service;


import com.fasterxml.jackson.core.JsonProcessingException;

import com.macys.logistics.shipment.load.DataFactory;
import com.macys.logistics.shipment.load.client.TMRestApiClient;
import com.macys.logistics.shipment.load.dto.gen.transportationmanager.*;
import com.macys.logistics.shipment.load.dto.request.Image;
import com.macys.logistics.shipment.load.dto.request.TrailerEvent;
import com.macys.logistics.shipment.load.exception.PhotoGrabberException;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.mapper.LoadMapper;
import com.macys.logistics.shipment.load.util.CommonUtil;
import com.macys.logistics.shipment.load.util.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PhotoGrabberServiceImplTest {

    @InjectMocks
    PhotoGrabberServiceImpl photoGrabberService;

    @Mock
    LoadMapper loadMapper;
    @Mock
    TMRestApiClient tmRestApiClient;
    @Mock
    LoadEnrichmentService loadEnrichmentService;

    @Mock
    Environment env;
    @Mock
    CommonUtil commonUtil;

    @Mock
    private ExecutorService executor;

    private long photograbberImagesMaxSize = 100000;
    private String excludedFileTypes = "pdf,doc,docx,xlsx,txt";

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(photoGrabberService, "commonUtil", commonUtil);
        ReflectionTestUtils.setField(photoGrabberService, "photograbberImagesMaxSize", photograbberImagesMaxSize);
        ReflectionTestUtils.setField(photoGrabberService, "excludedFileTypes", excludedFileTypes);
        ReflectionTestUtils.setField(photoGrabberService, "photograbber_batch_size", 10);
        ReflectionTestUtils.setField(photoGrabberService, "Photograbber_thread_pool_size", 10);
        ReflectionTestUtils.setField(photoGrabberService, "executor", executor);
    }

    @AfterEach
    void tearDown() {
        photoGrabberService.shutdownExecutor();
    }



    @Test
    void uploadPhotoGrabberFiles_success() throws Exception {
        when(env.getProperty(anyString())).thenReturn("testdata");

        RestFindEntity mockRestFindEntity = DataFactory.getRestFindEntity();
        when(loadMapper.createRestFindEntity(anyString(), any(), any(), any())).thenReturn(mockRestFindEntity);

        FindEntitiesResponse mockFindEntitiesResponse = DataFactory.getEntitiesResponse();
        when(tmRestApiClient.restFindEntities(mockRestFindEntity)).thenReturn(mockFindEntitiesResponse);

        RestAttachmentMaintainDataType attachment = new RestAttachmentMaintainDataType();
        attachment.setAttachmentEntityKey("52091765");
        attachment.setSystemAttachmentID("9017");
        attachment.setFileData("/9j/4AAQSkZJRgABAQAASABIAAD/");
        attachment.setFileType("png");
        attachment.setAttachmentCategoryCode("NOVNDRCHRGE");
        attachment.setDescription("test description pallet");
        attachment.setName("original-file-name.png");

        RestAttachmentMaintainDataType attachment2 = new RestAttachmentMaintainDataType();
        attachment2.setAttachmentEntityKey("52091766");
        attachment2.setSystemAttachmentID("9018");
        attachment2.setFileData(null);

        RestProcessMaintainEntityAttachmentResponse mockAttachmentDataResponse = new RestProcessMaintainEntityAttachmentResponse();
        mockAttachmentDataResponse.setAttachmentMaintainData(List.of(attachment, attachment2));

        when(tmRestApiClient.getAttachmentsInformationFromRest(any(RestProcessMaintainEntityAttachment.class)))
                .thenReturn(mockAttachmentDataResponse);

        doNothing().when(commonUtil).uploadToBucket(anyString(), anyString(), anyString(),  anyString());

        doAnswer(invocation -> {
            Runnable task = (Runnable) invocation.getArgument(0);
            task.run();
            return null;
        }).when(executor).submit(any(Runnable.class));

        photoGrabberService.uploadPhotoGrabberFiles();

        verify(commonUtil, times(1)).uploadToBucket(anyString(), eq("52091765_9017.png"), anyString(), anyString());
    }

    @Test
    void uploadPhotoGrabberFiles_executionException() throws Exception {
        when(env.getProperty(anyString())).thenReturn("testdata");
        when(loadMapper.createRestFindEntity(anyString(), any(), any(), any())).thenReturn(new RestFindEntity());
        when(tmRestApiClient.restFindEntities(any())).thenReturn(DataFactory.getEntitiesResponse());

        RestAttachmentMaintainDataType attachment = new RestAttachmentMaintainDataType();
        attachment.setFileData("data");
        attachment.setFileType("jpg");
        attachment.setAttachmentEntityKey("key");
        attachment.setSystemAttachmentID("id");
        attachment.setAttachmentCategoryCode("VNDRCHRGE");
        attachment.setDescription("some description Pallets");

        RestProcessMaintainEntityAttachmentResponse mockAttachmentResponse = new RestProcessMaintainEntityAttachmentResponse();
        mockAttachmentResponse.setAttachmentMaintainData(List.of(attachment));

        when(tmRestApiClient.getAttachmentsInformationFromRest(any(RestProcessMaintainEntityAttachment.class)))
                .thenReturn(mockAttachmentResponse)
                .thenReturn(mockAttachmentResponse);

        @SuppressWarnings("unchecked")
        Future<Void> mockFuture = (Future<Void>) mock(Future.class);
        lenient().when(mockFuture.get()).thenThrow(new ExecutionException(new RuntimeException("task failed")));

        when(executor.submit(any(Runnable.class))).thenReturn(null);

        photoGrabberService.uploadPhotoGrabberFiles();

        verify(executor).submit(any(Runnable.class));
    }

    @Test
    void uploadPhotoGrabberFiles_interruptedException() throws Exception {

        ExecutorService customMockExecutor = mock(ExecutorService.class);
        ReflectionTestUtils.setField(photoGrabberService, "executor", customMockExecutor);
        when(customMockExecutor.awaitTermination(any(Long.class), any())).thenThrow(new InterruptedException());

        when(env.getProperty(Constants.PHOTOGRABBER_LOAD_SEARCH_CONFIG)).thenReturn("dummy-config");
        when(loadMapper.createRestFindEntity(anyString(), any(), any(), any())).thenReturn(DataFactory.getRestFindEntity());
        when(tmRestApiClient.restFindEntities(any())).thenReturn(DataFactory.getEntitiesResponse());
        when(tmRestApiClient.getAttachmentsInformationFromRest(any())).thenReturn(new RestProcessMaintainEntityAttachmentResponse());

        photoGrabberService.uploadPhotoGrabberFiles();
        assertDoesNotThrow(() -> photoGrabberService.shutdownExecutor());
        ReflectionTestUtils.setField(photoGrabberService, "executor", executor);
    }

    @Test
    void uploadPhotoGrabberFiles_getEntitiesResponse_null() throws PhotoGrabberException {
        when(env.getProperty(anyString())).thenReturn("testdata");
        RestFindEntity mockRestFindEntity = new RestFindEntity();
        when(loadMapper.createRestFindEntity(anyString(),any(),any(),any())).thenReturn(mockRestFindEntity);

        photoGrabberService.uploadPhotoGrabberFiles();
        verify(tmRestApiClient, times(1)).restFindEntities(mockRestFindEntity);
        verify(tmRestApiClient, times(0)).getAttachmentsInformationFromRest(any(RestProcessMaintainEntityAttachment.class));
        verify(commonUtil, times(0)).uploadToBucket(any(),any(),any(), any());

    }

    @Test
    void uploadPhotoGrabberFiles_getAttachemtnInfo_null() throws PhotoGrabberException {
        when(env.getProperty(anyString())).thenReturn("testdata");
        RestFindEntity mockRestFindEntity = DataFactory.getRestFindEntity();
        when(loadMapper.createRestFindEntity(anyString(),any(),any(),any())).thenReturn(mockRestFindEntity);
        FindEntitiesResponse mockFindEntitiesResponse = DataFactory.getEntitiesResponse();
        when(tmRestApiClient.restFindEntities(mockRestFindEntity)).thenReturn(mockFindEntitiesResponse);

        photoGrabberService.uploadPhotoGrabberFiles();
        verify(tmRestApiClient, times(1)).restFindEntities(mockRestFindEntity);
        verify(tmRestApiClient, times(1)).getAttachmentsInformationFromRest(any(RestProcessMaintainEntityAttachment.class));
        verify(commonUtil, times(0)).uploadToBucket(any(),any(),any(),any());

    }

    @Test
    void uploadPhotoGrabberFiles_getAttachemtnData_null() throws PhotoGrabberException {
        when(env.getProperty(anyString())).thenReturn("testdata");
        RestFindEntity mockRestFindEntity = DataFactory.getRestFindEntity();
        when(loadMapper.createRestFindEntity(anyString(),any(),any(),any())).thenReturn(mockRestFindEntity);
        FindEntitiesResponse mockFindEntitiesResponse = DataFactory.getEntitiesResponse();
        when(tmRestApiClient.restFindEntities(mockRestFindEntity)).thenReturn(mockFindEntitiesResponse);
        RestProcessMaintainEntityAttachmentResponse attachmentsData = new RestProcessMaintainEntityAttachmentResponse();
        when(tmRestApiClient.getAttachmentsInformationFromRest(any(RestProcessMaintainEntityAttachment.class))).thenReturn(attachmentsData);

        photoGrabberService.uploadPhotoGrabberFiles();
        verify(tmRestApiClient, times(1)).restFindEntities(mockRestFindEntity);
        verify(tmRestApiClient, times(1)).getAttachmentsInformationFromRest(any(RestProcessMaintainEntityAttachment.class));
        verify(commonUtil, times(0)).uploadToBucket(any(),any(),any(),any());

    }

    @Test
    void uploadPhotoGrabberFiles_throws_exception_with_interrupted_cause() {
        when(env.getProperty(anyString())).thenReturn("testdata");
        when(loadMapper.createRestFindEntity(anyString(), any(), any(), any())).thenThrow(new RuntimeException(new InterruptedException()));

        PhotoGrabberException thrown = assertThrows(PhotoGrabberException.class, () -> photoGrabberService.uploadPhotoGrabberFiles());
        assertTrue(thrown.getMessage().contains("Error occurred while uploading PhotoGrabber files"));
    }

    @Test
    void uploadPhotoGrabberFiles_findEntitiesResponse_null() throws PhotoGrabberException {
        when(env.getProperty(anyString())).thenReturn("testdata");
        RestFindEntity mockRestFindEntity = DataFactory.getRestFindEntity();
        when(loadMapper.createRestFindEntity(anyString(),any(),any(),any())).thenReturn(mockRestFindEntity);
        when(tmRestApiClient.restFindEntities(mockRestFindEntity)).thenReturn(null);
        photoGrabberService.uploadPhotoGrabberFiles();
        assertEquals("SYSTEM_STOP_ID", mockRestFindEntity.getFilter().get(0).getName());
    }

    @Test
    void uploadPhotoGrabberFiles() throws PhotoGrabberException {
        when(env.getProperty(anyString())).thenReturn("testdata");
        RestFindEntity mockRestFindEntity = DataFactory.getRestFindEntity();
        when(loadMapper.createRestFindEntity(anyString(),any(),any(),any())).thenReturn(mockRestFindEntity);
        FindEntitiesResponse mockFindEntitiesResponse = DataFactory.getEntitiesResponse();
        when(tmRestApiClient.restFindEntities(mockRestFindEntity)).thenReturn(mockFindEntitiesResponse);
        when(tmRestApiClient.getAttachmentsInformationFromRest(any(RestProcessMaintainEntityAttachment.class))).thenReturn(null);
        photoGrabberService.uploadPhotoGrabberFiles();
        assertEquals("5432123", mockFindEntitiesResponse.getEntity().get(0).getStop().get(0).getSystemLoadID());
    }


    @Test
    void validateImageData_throwsException_fileTooLarge() {
        RestAttachmentMaintainDataType data = new RestAttachmentMaintainDataType();
        data.setFileData("a".repeat((int) (photograbberImagesMaxSize + 1)));
        data.setAttachmentEntityKey("testKey");
        data.setSystemAttachmentID("123");
        data.setFileType("jpeg");
        data.setAttachmentCategoryCode("VNDRCHRGE");
        data.setDescription("Not a pallet");
        data.setName("originalFile.jpeg");
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> photoGrabberService.validateImagesDataAndUpload(data)
        );
        assertEquals(Constants.PHOTOGRABBER_ATTACHMENT_IS_TOO_BIG, thrown.getMessage());
    }

    @Test
    void validateImageData_success_validData() throws PhotoGrabberException {
        RestAttachmentMaintainDataType data = new RestAttachmentMaintainDataType();
        data.setFileData("/9j/4AAQSkZJRgABAQAASABIAAD/");
        data.setAttachmentEntityKey("testKey");
        data.setSystemAttachmentID("123");
        data.setFileType("jpeg");
        data.setAttachmentCategoryCode("NOVNDRCHRGE");
        data.setDescription("pallet");
        data.setName("originalFile.jpeg");
        photoGrabberService.validateImagesDataAndUpload(data);
        verify(commonUtil, times(1)).uploadToBucket(
            "/9j/4AAQSkZJRgABAQAASABIAAD/",
            "testKey_123.jpeg",
            "jpeg",
                "originalFile.jpeg"
        );

    }

    @Test
    void testUploadLogic_WithMixedScenarios() throws Exception {
        List<RestAttachmentMaintainDataType> testData = DataFactory.createTestData();
        testData.forEach(data ->
                data.setName("original-file-" + data.getSystemAttachmentID() + ".png")
        );

        when(env.getProperty(Constants.PHOTOGRABBER_LOAD_SEARCH_CONFIG)).thenReturn("dummy-config");
        when(loadMapper.createRestFindEntity(anyString(), any(), any(), any())).thenReturn(new RestFindEntity());

        LoadType load = new LoadType();
        load.setSystemLoadID("LOAD-123");
        UIEntityType uiEntityType = new UIEntityType();
        uiEntityType.getLoad().add(load);
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        findEntitiesResponse.getEntity().add(uiEntityType);

        when(tmRestApiClient.restFindEntities(any(RestFindEntity.class))).thenReturn(findEntitiesResponse);

        RestProcessMaintainEntityAttachmentResponse metadataResponse = new RestProcessMaintainEntityAttachmentResponse();
        metadataResponse.setAttachmentMaintainData(testData);

        RestProcessMaintainEntityAttachmentResponse dataResponse = new RestProcessMaintainEntityAttachmentResponse();
        dataResponse.setAttachmentMaintainData(testData);

        when(tmRestApiClient.getAttachmentsInformationFromRest(any())).thenReturn(metadataResponse).thenReturn(dataResponse);

        lenient().doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(executor).submit(any(Runnable.class));

        photoGrabberService.uploadPhotoGrabberFiles();

        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> fileTypeCaptor = ArgumentCaptor.forClass(String.class);

        verify(commonUtil, times(4)).uploadToBucket(anyString(), fileNameCaptor.capture(), fileTypeCaptor.capture(),anyString());

        List<String> uploadedFileNames = fileNameCaptor.getAllValues();

        assertEquals(4, uploadedFileNames.size(), "Exactly 4 files should have been uploaded.");
        assertTrue(uploadedFileNames.contains("LOAD-123_1.png"), "File with category 'NOVNDRCHRGE' and description 'Pallet' should be uploaded.");
        assertTrue(uploadedFileNames.contains("LOAD-123_2.png"), "File with category 'NOVNDRCHRGE' and description 'PALLETS' should be uploaded.");
        assertTrue(uploadedFileNames.contains("LOAD-123_4.png"), "File with category 'VNDRCHRGE' and null description should be uploaded.");
        assertTrue(uploadedFileNames.contains("LOAD-123_7.png"), "File with category 'VNDRCHRGE' and description 'pallets' should be uploaded.");
        assertFalse(uploadedFileNames.contains("LOAD-123_3.png"), "File with null category and description should NOT be uploaded.");
        assertFalse(uploadedFileNames.contains("LOAD-123_5.png"), "File with category 'NOVNDRCHRGE' and null description should NOT be uploaded.");
        assertFalse(uploadedFileNames.contains("LOAD-123_6.png"), "File with description 'Not a pallet' should NOT be uploaded.");
    }

    @Test
    void testUploadLogic_WithNoLoadsFound() throws Exception {
        when(env.getProperty(Constants.PHOTOGRABBER_LOAD_SEARCH_CONFIG)).thenReturn("dummy-config");
        when(loadMapper.createRestFindEntity(anyString(), any(), any(), any())).thenReturn(new RestFindEntity());

        UIEntityType uiEntityType = new UIEntityType();
        uiEntityType.setLoad(null);
        FindEntitiesResponse findEntitiesResponse = new FindEntitiesResponse();
        findEntitiesResponse.getEntity().add(uiEntityType);

        when(tmRestApiClient.restFindEntities(any(RestFindEntity.class))).thenReturn(findEntitiesResponse);

        photoGrabberService.uploadPhotoGrabberFiles();


        verify(tmRestApiClient, never()).getAttachmentsInformationFromRest(any());
    }

    @Test
    void validateImageData_failure_palletViolation() throws PhotoGrabberException {
        RestAttachmentMaintainDataType data = new RestAttachmentMaintainDataType();
        data.setFileData("/9j/4AAQSkZJRgABAQAASABIAAD/");
        data.setAttachmentEntityKey("testKey");
        data.setSystemAttachmentID("123");
        data.setFileType("txt");
        data.setAttachmentCategoryCode("UNAUTHPALLET");
        data.setName("originalFile.txt");
        photoGrabberService.validateImagesDataAndUpload(data);
        verify(commonUtil, times(0)).uploadToBucket(
                "/9j/4AAQSkZJRgABAQAASABIAAD/",
                "testKey_123.txt",
                "txt" ,"originalFile.txt"
        );

    }

    @Test
    void validateImageData_success_chargeBackVendor() throws PhotoGrabberException {
        RestAttachmentMaintainDataType data = new RestAttachmentMaintainDataType();
        data.setFileData("/9j/4AAQSkZJRgABAQAASABIAAD/");
        data.setAttachmentEntityKey("testKey");
        data.setSystemAttachmentID("123");
        data.setFileType("png");
        data.setAttachmentCategoryCode("VNDRCHRGE");
        data.setDescription("Not a pallet");
        data.setName("originalFile.png");
        photoGrabberService.validateImagesDataAndUpload(data);
        verify(commonUtil, times(1)).uploadToBucket(
                "/9j/4AAQSkZJRgABAQAASABIAAD/",
                "testKey_123.png",
                "png",
                "originalFile.png"
        );

    }

    @Test
    void validateImageData_isPalletImage() throws PhotoGrabberException {
        RestAttachmentMaintainDataType data = new RestAttachmentMaintainDataType();
        data.setFileData("/9j/4AAQSkZJRgABAQAASABIAAD/");
        data.setAttachmentEntityKey("testKey");
        data.setSystemAttachmentID("123");
        data.setFileType("png");
        data.setDescription("pallets");
        data.setName("originalFile.png");
        photoGrabberService.validateImagesDataAndUpload(data);
        verify(commonUtil, never()).uploadToBucket(anyString(), anyString(), anyString(),anyString());
    }
    @Test
    void attachImagesToLoad() throws Exception {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setAppointmentId("52627356");
        List<Image> imageList = new ArrayList<>();
        Image image = new Image();
        image.setType("Front");
        image.setUrl("https://console.cloud.google.com/storage/browser/mtech-supplychain-tmsintg-3pl-trailer-images-dev/d8b3db30-03b4-48af-9ab3-576d0d4e45c0-467167-f9e38000-f32e-4430-8ad9-c5f11e902787.png");
        imageList.add(image);
        trailerEvent.setImages(imageList);
        FindEntitiesResponse mockFindEntitiesResponse = DataFactory.getEntitiesResponse();
        when(loadEnrichmentService.fetchLoadId(any(),anyString())).thenReturn(mockFindEntitiesResponse);

        PhotoGrabberServiceImpl spiedService = spy(photoGrabberService);
        doThrow(new PhotoGrabberException("File not found in bucket")).when(spiedService).downloadImage(anyString(), anyString());
        PhotoGrabberException thrown = assertThrows(PhotoGrabberException.class,
                () -> spiedService.attachImagesToLoad(trailerEvent));

        assertTrue(thrown.getMessage().contains("Exception occurred while processing trailer images to load"));
        assertTrue(thrown.getCause().getMessage().contains("File not found in bucket"));
        assertEquals("5432123", mockFindEntitiesResponse.getEntity().get(0).getStop().get(0).getSystemLoadID());
    }

    @Test
    void attachImagesToLoad_noAppointmentId() throws PhotoGrabberException, JsonProcessingException {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setAppointmentId(null);
        trailerEvent.setImages(Collections.singletonList(new Image()));

        List<String> response = photoGrabberService.attachImagesToLoad(trailerEvent);

        assertTrue(response.isEmpty());
        verify(loadEnrichmentService, times(0)).fetchLoadId(any(),anyString());
    }

    @Test
    void attachImagesToLoad_noImages() throws PhotoGrabberException, JsonProcessingException {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setAppointmentId("12345");
        trailerEvent.setImages(Collections.emptyList());
        when(loadEnrichmentService.fetchLoadId(any(),anyString())).thenReturn(DataFactory.getEntitiesResponse());

        List<String> response = photoGrabberService.attachImagesToLoad(trailerEvent);

        assertTrue(response.isEmpty());
        verify(loadEnrichmentService, times(1)).fetchLoadId(any(),anyString());
    }

    @Test
    void attachImagesToLoad_apiCallFails() throws PhotoGrabberException, JsonProcessingException {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setAppointmentId("52627356");
        trailerEvent.setRequestId("req-123");
        Image image = new Image();
        image.setUrl("https://console.cloud.google.com/storage/browser/mtech-supplychain-tmsintg-3pl-trailer-images-dev/test.png");
        trailerEvent.setImages(Collections.singletonList(image));

        when(loadEnrichmentService.fetchLoadId(any(),anyString())).thenReturn(DataFactory.getEntitiesResponse());

        PhotoGrabberServiceImpl spiedService = spy(photoGrabberService);

        doReturn("base64ImageData").when(spiedService).downloadImage(anyString(), anyString());

        doThrow(new RuntimeException("API call failed")).when(tmRestApiClient).getAttachmentsInformationFromRest(any());

        PhotoGrabberException thrown = assertThrows(PhotoGrabberException.class,
                () -> spiedService.attachImagesToLoad(trailerEvent));

        assertTrue(thrown.getMessage().contains("Exception occurred while processing trailer images to load"));
        verify(commonUtil).publishTrailerImageAuditFeedbackToPubSub(any(), anyString(), anyString(), anyString());
    }

    @Test
    void attachImagesToLoad_apiCallFailsWithUnprocessableEntity() throws PhotoGrabberException, JsonProcessingException {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setAppointmentId("52627356");
        trailerEvent.setRequestId("req-123");
        Image image = new Image();
        image.setUrl("https://console.cloud.google.com/storage/browser/mtech-supplychain-tmsintg-3pl-trailer-images-dev/test.png");
        trailerEvent.setImages(Collections.singletonList(image));

        when(loadEnrichmentService.fetchLoadId(any(),anyString())).thenReturn(DataFactory.getEntitiesResponse());

        PhotoGrabberServiceImpl spiedService = spy(photoGrabberService);

        doReturn("base64ImageData").when(spiedService).downloadImage(anyString(), anyString());

        HttpClientErrorException unprocessableEntityException = new HttpClientErrorException(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY);
        doThrow(new RuntimeException(unprocessableEntityException)).when(tmRestApiClient).getAttachmentsInformationFromRest(any());

        PhotoGrabberException thrown = assertThrows(PhotoGrabberException.class,
                () -> spiedService.attachImagesToLoad(trailerEvent));

        assertTrue(thrown.getMessage().contains("Exception occurred while processing trailer images to load"));
        verify(commonUtil).publishTrailerImageAuditFeedbackToPubSub(any(), anyString(), anyString(), anyString());
    }

    @Test
    void attachImagesToInvalidLoad() throws PhotoGrabberException {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setAppointmentId("52627356");
        trailerEvent.setRequestId("1324323442-gtyt56567g");
        List<Image> imageList = new ArrayList<>();
        Image image = new Image();
        image.setType("Front");
        image.setUrl("https://console.cloud.google.com/storage/browser/mtech-supplychain-tmsintg-3pl-trailer-images-dev/d8b3db30-03b4-48af-9ab3-576d0d4e45c0-467167-f9e38000-f32e-4430-8ad9-c5f11e902787.png");
        imageList.add(image);
        trailerEvent.setImages(imageList);
        FindEntitiesResponse mockFindEntitiesResponse = DataFactory.getEntitiesResponse();
        mockFindEntitiesResponse.setEntity(null);
        when(loadEnrichmentService.fetchLoadId(any(),anyString())).thenReturn(mockFindEntitiesResponse);
        RestProcessMaintainEntityAttachmentResponse response = new RestProcessMaintainEntityAttachmentResponse();
        assertThrows(PhotoGrabberException.class,
                () ->photoGrabberService.attachImagesToLoad(trailerEvent));
    }

    @Test
    void attachImagesToLoadException()  {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setAppointmentId("52627356");
        trailerEvent.setRequestId("test-request-id");
        List<Image> imageList = new ArrayList<>();
        Image image = new Image();
        image.setType("Front");
        image.setUrl("https://console.cloud.google.com/storage/browser/mtech-supplychain-tmsintg-photograbber-attachments-dev/51729089.jpeg");
        imageList.add(image);
        trailerEvent.setImages(imageList);
        when(loadEnrichmentService.fetchLoadId(any(),anyString())).thenThrow(new TmsLoadProcessException("Error fetching load ID"));
        assertThrows(PhotoGrabberException.class, () -> {
            photoGrabberService.attachImagesToLoad(trailerEvent);
        });
    }

    @Test
    void testShutdownExecutor_NoInterruption() throws InterruptedException {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        ReflectionTestUtils.setField(photoGrabberService, "executor", mockExecutor);
        when(mockExecutor.awaitTermination(60, TimeUnit.SECONDS)).thenReturn(true);

        photoGrabberService.shutdownExecutor();

        verify(mockExecutor).shutdown();
        verify(mockExecutor).awaitTermination(60, TimeUnit.SECONDS);
        verify(mockExecutor, never()).shutdownNow();
    }

    @Test
    void testShutdownExecutor_Timeout() throws InterruptedException {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        ReflectionTestUtils.setField(photoGrabberService, "executor", mockExecutor);
        when(mockExecutor.awaitTermination(60, TimeUnit.SECONDS)).thenReturn(false);

        photoGrabberService.shutdownExecutor();

        verify(mockExecutor).shutdown();
        verify(mockExecutor).awaitTermination(60, TimeUnit.SECONDS);
        verify(mockExecutor).shutdownNow();
    }

    @Test
    void attachImageToLoad_generatesSequentialNames() throws Exception {
        PhotoGrabberServiceImpl spiedService = spy(photoGrabberService);
        List<String> existingNames = new ArrayList<>(List.of("load1-trailer1", "load1-trailer1-2"));
        RestAttachmentMaintainDataType attachmentData = new RestAttachmentMaintainDataType();


        java.lang.reflect.Method method = PhotoGrabberServiceImpl.class.getDeclaredMethod("addAttachmentMainData", String.class, String.class, String.class, RestAttachmentMaintainDataType.class, List.class);
        method.setAccessible(true);
        doReturn("base64").when(spiedService).downloadImage(anyString(), anyString());
        method.invoke(spiedService, "http://a.com/b.png", "load1", "trailer1", attachmentData, existingNames);
        assertEquals("load1-trailer1-1", attachmentData.getName());
    }

}
