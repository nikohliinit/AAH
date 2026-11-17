package com.macys.logistics.shipment.load.camel.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.BeginningSegment;
import com.macys.logistics.shipment.load.dto.inbound.Edi990Data;
import com.macys.logistics.shipment.load.dto.inbound.Edi990PubSubMessage;
import com.macys.logistics.shipment.load.exception.Edi990ProcessingException;
import com.macys.logistics.shipment.load.exception.Edi990ValidationException;
import com.macys.logistics.shipment.load.service.Edi990Service;
import com.macys.logistics.shipment.load.util.Constants;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for Edi990Processor.
 * Tests all processing paths including acceptance, decline, and various
 * exception scenarios.
 * Target: ≥98% method, line, and branch coverage.
 */
@ExtendWith(SpringExtension.class)
class Edi990ProcessorTest {

    @InjectMocks
    private Edi990Processor processor;

    @Mock
    private Tracer tracer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Edi990Service edi990Service;

    @Mock
    private Exchange exchange;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Message message;

    @Mock
    private Span span;

    @Mock
    private Span.Builder spanBuilder;

    private MockedStatic<MicrometerUtility> micrometerUtilityMock;

    private static final String SHIPMENT_ID = "12345678901";
    private static final String CARRIER_CODE = "ABCD";
    private static final String MESSAGE_ID = "msg-12345";
    private static final String SPAN_ID = "span-67890";

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(processor, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(processor, "tracer", tracer);
        ReflectionTestUtils.setField(processor, "edi990Service", edi990Service);

        when(exchange.getIn()).thenReturn(message);

        micrometerUtilityMock = Mockito.mockStatic(MicrometerUtility.class);
        micrometerUtilityMock.when(() -> MicrometerUtility.initializeSpan(exchange, tracer))
                .thenReturn(span);

        when(span.start()).thenReturn(span);

        // Mock TraceContext to avoid NullPointerException
        TraceContext traceContext = Mockito.mock(TraceContext.class);
        when(traceContext.spanId()).thenReturn(SPAN_ID);
        when(span.context()).thenReturn(traceContext);

        Tracer.SpanInScope spanInScope = Mockito.mock(Tracer.SpanInScope.class);
        when(tracer.withSpan(span)).thenReturn(spanInScope);
    }

    @AfterEach
    void cleanup() {
        if (micrometerUtilityMock != null) {
            micrometerUtilityMock.close();
        }
        MDC.clear();
    }

    // ==================== ACCEPTANCE TESTS ====================

    @Test
    void testProcess_Success_AcceptanceMessage() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act
        processor.process(exchange);

        // Assert
        verify(message).setHeader("spanId", SPAN_ID);
        verify(message).setHeader(Constants.EDI990_MESSAGE_ID, MESSAGE_ID);
        verify(message).setHeader(Constants.EDI990_SHIPMENT_ID, SHIPMENT_ID);
        verify(message).setHeader(Constants.EDI990_RESERVATION_ACTION_CODE, "A");
        verify(edi990Service, times(1)).processAcceptance(pubSubMessage.getData(), SHIPMENT_ID);
        verify(span, times(1)).start();
        verify(span, times(1)).end();
        verify(tracer, times(1)).withSpan(span);
    }

    // ==================== DECLINE TESTS ====================

    @Test
    void testProcess_Success_DeclineMessage() throws Exception {
        // Arrange
        String requestJson = createDeclineJsonPayload();
        Edi990PubSubMessage pubSubMessage = createDeclinePubSubMessage();

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act
        processor.process(exchange);

        // Assert
        verify(message).setHeader("spanId", SPAN_ID);
        verify(message).setHeader(Constants.EDI990_MESSAGE_ID, MESSAGE_ID);
        verify(message).setHeader(Constants.EDI990_SHIPMENT_ID, SHIPMENT_ID);
        verify(message).setHeader(Constants.EDI990_RESERVATION_ACTION_CODE, "D");
        verify(edi990Service, times(1)).processDecline(pubSubMessage.getData(), SHIPMENT_ID);
        verify(span, times(1)).start();
        verify(span, times(1)).end();
    }

    // ==================== VALIDATION EXCEPTION TESTS ====================

    @Test
    void testProcess_ThrowsValidationException_WhenMessageIsNull() throws Exception {
        // Arrange
        String requestJson = "{}";
        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(null);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertEquals("EDI 990 message is null", thrown.getMessage());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenMessageIdIsMissing() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();
        pubSubMessage.setMessageId(null);

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertEquals("MessageId is missing in EDI 990 message", thrown.getMessage());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenMessageIdIsBlank() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();
        pubSubMessage.setMessageId("   ");

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertEquals("MessageId is missing in EDI 990 message", thrown.getMessage());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenDataIsNull() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = new Edi990PubSubMessage();
        pubSubMessage.setMessageId(MESSAGE_ID);
        pubSubMessage.setData(null);

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertEquals("Beginning segment is missing in EDI 990 message", thrown.getMessage());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenBeginningSegmentIsNull() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();
        pubSubMessage.getData().setBeginningSegment(null);

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertEquals("Beginning segment is missing in EDI 990 message", thrown.getMessage());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenReservationActionCodeIsMissing() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();
        pubSubMessage.getData().getBeginningSegment().setReservationActionCode(null);

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertEquals("ReservationActionCode is missing in beginning segment", thrown.getMessage());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenReservationActionCodeIsBlank() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();
        pubSubMessage.getData().getBeginningSegment().setReservationActionCode("  ");

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertEquals("ReservationActionCode is missing in beginning segment", thrown.getMessage());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenShipmentIdIsMissing() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();
        pubSubMessage.getData().getBeginningSegment().setShipmentIdentification(null);

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertEquals("ShipmentIdentification is missing in beginning segment", thrown.getMessage());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenShipmentIdIsBlank() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();
        pubSubMessage.getData().getBeginningSegment().setShipmentIdentification("   ");

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertEquals("ShipmentIdentification is missing in beginning segment", thrown.getMessage());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenInvalidReservationActionCode() throws Exception {
        // Arrange
        String requestJson = createInvalidReservationActionCodePayload();
        Edi990PubSubMessage pubSubMessage = createInvalidReservationActionCodePubSubMessage();

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertTrue(thrown.getMessage().contains("Invalid reservationActionCode"));
        assertTrue(thrown.getMessage().contains("X"));
        assertTrue(thrown.getMessage().contains(SHIPMENT_ID));
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_WhenJsonParsingFails() throws Exception {
        // Arrange
        String malformedJson = "{invalid json}";
        when(message.getBody(String.class)).thenReturn(malformedJson);
        when(objectMapper.readValue(malformedJson, Edi990PubSubMessage.class))
                .thenThrow(new RuntimeException("JSON parse error"));

        // Act & Assert
        Edi990ProcessingException thrown = assertThrows(Edi990ProcessingException.class,
                () -> processor.process(exchange));

        assertEquals("Failed to process EDI 990 message", thrown.getMessage());
        assertNotNull(thrown.getCause());
        verify(span, times(1)).end();
    }

    // ==================== PROCESSING EXCEPTION TESTS ====================

    @Test
    void testProcess_ThrowsProcessingException_WhenAcceptServiceThrowsException() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();
        RuntimeException serviceException = new RuntimeException("Database connection failed");

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);
        doThrow(serviceException).when(edi990Service).processAcceptance(pubSubMessage.getData(), SHIPMENT_ID);

        // Act & Assert
        Edi990ProcessingException thrown = assertThrows(Edi990ProcessingException.class,
                () -> processor.process(exchange));

        assertEquals("Failed to process EDI 990 message", thrown.getMessage());
        assertEquals(serviceException, thrown.getCause());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsProcessingException_WhenDeclineServiceFails() throws Exception {
        // Arrange
        String requestJson = createDeclineJsonPayload();
        Edi990PubSubMessage pubSubMessage = createDeclinePubSubMessage();
        RuntimeException serviceException = new RuntimeException("API call failed");

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);
        doThrow(serviceException).when(edi990Service).processDecline(pubSubMessage.getData(), SHIPMENT_ID);

        // Act & Assert
        Edi990ProcessingException thrown = assertThrows(Edi990ProcessingException.class,
                () -> processor.process(exchange));

        assertEquals("Failed to process EDI 990 message", thrown.getMessage());
        assertEquals(serviceException, thrown.getCause());
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_RethrowsValidationException() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(null);

        // Act & Assert
        assertThrows(Edi990ValidationException.class, () -> processor.process(exchange));
        verify(span, times(1)).end();
    }

    // ==================== CASE SENSITIVITY TESTS ====================

    @Test
    void testProcess_ThrowsValidationException_LowercaseAcceptanceCode() throws Exception {
        // Arrange
        String requestJson = createAcceptJsonPayload();
        Edi990PubSubMessage pubSubMessage = createAcceptPubSubMessage();
        pubSubMessage.getData().getBeginningSegment().setReservationActionCode("a"); // lowercase

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertTrue(thrown.getMessage().contains("Invalid reservationActionCode"));
        assertTrue(thrown.getMessage().contains("a"));
        verify(span, times(1)).end();
    }

    @Test
    void testProcess_ThrowsValidationException_LowercaseDeclineCode() throws Exception {
        // Arrange
        String requestJson = createDeclineJsonPayload();
        Edi990PubSubMessage pubSubMessage = createDeclinePubSubMessage();
        pubSubMessage.getData().getBeginningSegment().setReservationActionCode("d"); // lowercase

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(requestJson, Edi990PubSubMessage.class)).thenReturn(pubSubMessage);

        // Act & Assert
        Edi990ValidationException thrown = assertThrows(Edi990ValidationException.class,
                () -> processor.process(exchange));

        assertTrue(thrown.getMessage().contains("Invalid reservationActionCode"));
        assertTrue(thrown.getMessage().contains("d"));
        verify(span, times(1)).end();
    }

    // ==================== HELPER METHODS ====================

    private String createAcceptJsonPayload() {
        return "{\"messageId\":\"" + MESSAGE_ID + "\",\"data\":{\"beginningSegment\":{\"reservationActionCode\":\"A\"}}}";
    }

    private String createDeclineJsonPayload() {
        return "{\"messageId\":\"" + MESSAGE_ID + "\",\"data\":{\"beginningSegment\":{\"reservationActionCode\":\"D\"}}}";
    }

    private String createInvalidReservationActionCodePayload() {
        return "{\"messageId\":\"" + MESSAGE_ID + "\",\"data\":{\"beginningSegment\":{\"reservationActionCode\":\"X\"}}}";
    }

    private Edi990PubSubMessage createAcceptPubSubMessage() {
        Edi990PubSubMessage msg = new Edi990PubSubMessage();
        msg.setMessageId(MESSAGE_ID);
        Edi990Data data = createEdi990Data("A");
        msg.setData(data);
        return msg;
    }

    private Edi990PubSubMessage createDeclinePubSubMessage() {
        Edi990PubSubMessage msg = new Edi990PubSubMessage();
        msg.setMessageId(MESSAGE_ID);
        Edi990Data data = createEdi990Data("D");
        data.getBeginningSegment().setReservationDeclinedReasonCode("CPU");
        msg.setData(data);
        return msg;
    }

    private Edi990PubSubMessage createInvalidReservationActionCodePubSubMessage() {
        Edi990PubSubMessage msg = new Edi990PubSubMessage();
        msg.setMessageId(MESSAGE_ID);
        Edi990Data data = createEdi990Data("X");
        msg.setData(data);
        return msg;
    }

    private Edi990Data createEdi990Data(String reservationActionCode) {
        Edi990Data data = new Edi990Data();
        BeginningSegment beginSegment = new BeginningSegment();
        beginSegment.setShipmentIdentification(SHIPMENT_ID);
        beginSegment.setStandardCarrierAlphaCode(CARRIER_CODE);
        beginSegment.setReservationActionCode(reservationActionCode);
        data.setBeginningSegment(beginSegment);
        return data;
    }
}
