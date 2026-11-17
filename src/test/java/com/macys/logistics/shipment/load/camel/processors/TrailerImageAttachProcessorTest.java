package com.macys.logistics.shipment.load.camel.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.AuditTrailerFeedback;
import com.macys.logistics.shipment.load.dto.request.TrailerEvent;
import com.macys.logistics.shipment.load.exception.TmsAuditMessagePublishException;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.service.PhotoGrabberService;
import com.macys.logistics.shipment.load.util.CommonUtil;
import com.macys.logistics.shipment.load.util.Constants;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TrailerImageAttachProcessorTest {

    @InjectMocks
    TrailerImageAttachProcessor imageAttachProcessor;
    @Mock
    Exchange exchange;
    @Mock
    Message message;

    @Mock
    Tracer tracer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Span span;
    @Mock
    private TraceContext traceContext;
    @Mock
    private Tracer.SpanInScope spanInScope;

    @Mock
    PhotoGrabberService photoGrabberService;

    @Mock
    CommonUtil commonUtil;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(imageAttachProcessor, "objectMapper", objectMapper);
        MockitoAnnotations.openMocks(this);
        when(tracer.nextSpan()).thenReturn(span);
        when(span.name(anyString())).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.spanId()).thenReturn("mockSpanId");
        when(tracer.withSpan(any(Span.class))).thenReturn(spanInScope);
        CamelContext camelContext = mock(CamelContext.class);
        when(span.start()).thenReturn(span);
        when(camelContext.getName()).thenReturn("mockCamelContext");
        when(exchange.getContext()).thenReturn(camelContext);
        when(exchange.getIn()).thenReturn(message);
    }

    @Test
    void test_process_valid_body() throws Exception {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setAppointmentId("52763553");
        trailerEvent.setTrailerId("TR47864");
        trailerEvent.setRequestId("5ddfbf77-a4db-49da-8f69-ad966bd90403");
        when(exchange.getIn().getBody(String.class)).thenReturn("valid");
        when(objectMapper.readValue(anyString(), eq(TrailerEvent.class))).thenReturn(
                trailerEvent);
        imageAttachProcessor.process(exchange);
        verify(photoGrabberService, times(1)).attachImagesToLoad(any(TrailerEvent.class));
    }

    @Test
    void test_process_whenTrailerEventIsNull_shouldReturnGracefully() throws Exception {
        when(exchange.getIn().getBody(String.class)).thenReturn("valid");
        when(objectMapper.readValue(anyString(), eq(TrailerEvent.class))).thenReturn(null);
        assertDoesNotThrow(() -> imageAttachProcessor.process(exchange));
        verify(photoGrabberService, never()).attachImagesToLoad(any());
    }

    @Test
    void test_process_whenRequestIdIsNull_shouldReturnGracefully() throws Exception {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setRequestId(null);
        when(exchange.getIn().getBody(String.class)).thenReturn("valid");
        when(objectMapper.readValue(anyString(), eq(TrailerEvent.class))).thenReturn(trailerEvent);
        assertDoesNotThrow(() -> imageAttachProcessor.process(exchange));
        verify(photoGrabberService, never()).attachImagesToLoad(any());
    }

    @Test
    void test_process_JsonProcessingException() throws Exception {
        String invalidJson = "invalid json";
        when(exchange.getIn().getBody(String.class)).thenReturn(invalidJson);
        when(objectMapper.readValue(invalidJson, TrailerEvent.class)).thenThrow(new JsonProcessingException("test") {});

        assertThrows(JsonProcessingException.class, () -> imageAttachProcessor.process(exchange));

        verify(commonUtil).publishTrailerImageAuditFeedbackToPubSub(
                any(AuditTrailerFeedback.class),
                eq("NA"),
                eq(Constants.CONSUME_IMAGE_PROCESS_ACTION),
                eq(Constants.SHIPMENT_LOAD_FAILURE_STATUS)
        );
    }

    @Test
    void test_process_TmsAuditMessagePublishException() throws Exception {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setRequestId("test-request-id");
        when(exchange.getIn().getBody(String.class)).thenReturn("valid");
        when(objectMapper.readValue(anyString(), eq(TrailerEvent.class))).thenReturn(trailerEvent);
        doThrow(new TmsAuditMessagePublishException("PubSub Error")).when(commonUtil).publishTrailerImageAuditFeedbackToPubSub(any(), any(), any(), any());

        assertThrows(TmsAuditMessagePublishException.class, () -> imageAttachProcessor.process(exchange));
    }

    @Test
    void test_process_TmsLoadProcessException() throws Exception {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setRequestId("test-request-id");
        when(exchange.getIn().getBody(String.class)).thenReturn("valid");
        when(objectMapper.readValue(anyString(), eq(TrailerEvent.class))).thenReturn(trailerEvent);
        when(photoGrabberService.attachImagesToLoad(any(TrailerEvent.class))).thenThrow(new TmsLoadProcessException("TMS Load Error"));

        assertThrows(TmsLoadProcessException.class, () -> imageAttachProcessor.process(exchange));
    }

    @Test
    void test_process_GenericException() throws Exception {
        TrailerEvent trailerEvent = new TrailerEvent();
        trailerEvent.setRequestId("test-request-id");
        when(exchange.getIn().getBody(String.class)).thenReturn("valid");
        when(objectMapper.readValue(anyString(), eq(TrailerEvent.class))).thenReturn(trailerEvent);
        when(photoGrabberService.attachImagesToLoad(any(TrailerEvent.class))).thenThrow(new RuntimeException("Generic Error"));

        assertThrows(RuntimeException.class, () -> imageAttachProcessor.process(exchange));
    }

}
