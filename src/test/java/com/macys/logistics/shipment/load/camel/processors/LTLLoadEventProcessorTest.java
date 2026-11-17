package com.macys.logistics.shipment.load.camel.processors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentTrackingMessage;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.exception.TmsLoadValidationException;
import com.macys.logistics.shipment.load.service.LTLTrackingMessageService;
import io.micrometer.tracing.Span;
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

@ExtendWith(SpringExtension.class)
class LTLLoadEventProcessorTest {

    @InjectMocks
    private LTLLoadEventProcessor processor;

    @Mock
    private Tracer tracer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private LTLTrackingMessageService ltlTrackingMessageService;

    @Mock
    private Exchange exchange;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Message message;

    @Mock
    private Span span;

    private MockedStatic<MicrometerUtility> micrometerUtilityMock;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(processor, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(processor, "tracer", tracer);
        ReflectionTestUtils.setField(processor, "ltlTrackingMessageService", ltlTrackingMessageService);

        when(exchange.getIn()).thenReturn(message);

        micrometerUtilityMock = Mockito.mockStatic(MicrometerUtility.class);
        micrometerUtilityMock.when(() -> MicrometerUtility.initializeSpan(exchange, tracer))
                .thenReturn(span);

        when(span.start()).thenReturn(span);
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

    @Test
    void testProcess_callsService_whenValidLoadId() throws Exception {
        String requestJson = "{\"loadId\":\"L123456\",\"proNumber\":\"PRO789\"}";
        ShipmentTrackingMessage dto = new ShipmentTrackingMessage();
        dto.setLoadId(123456);
        dto.setProNumber("PRO789");

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(anyString(), eq(ShipmentTrackingMessage.class))).thenReturn(dto);
        processor.process(exchange);

        verify(ltlTrackingMessageService).addProNumberToTM(dto);
    }

    @Test
    void testProcess_doesNotCallService_whenMissingLoadId() throws Exception {
        String requestJson = "{\"proNumber\":\"PRO789\"}";
        ShipmentTrackingMessage dto = new ShipmentTrackingMessage();
        dto.setProNumber("PRO789");
        dto.setLoadId(null);

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(anyString(), eq(ShipmentTrackingMessage.class))).thenReturn(dto);
        processor.process(exchange);

        verify(ltlTrackingMessageService, never()).addProNumberToTM(any(ShipmentTrackingMessage.class));
    }

    @Test
    void testProcess_handlesTmsLoadValidationException() throws Exception {
        String requestJson = "{\"loadId\":\"L223344\",\"proNumber\":\"PX001\"}";
        ShipmentTrackingMessage dto = new ShipmentTrackingMessage();
        dto.setLoadId(223344);
        dto.setProNumber("PX001");

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(anyString(), eq(ShipmentTrackingMessage.class))).thenReturn(dto);

        doThrow(new TmsLoadValidationException("Invalid loadId in TM"))
                .when(ltlTrackingMessageService)
                .addProNumberToTM(dto);
        processor.process(exchange);

        verify(ltlTrackingMessageService).addProNumberToTM(dto);
    }

    @Test
    void testProcess_handlesTmsLoadProcessException() throws Exception {
        String requestJson = "{\"loadId\":\"L998877\",\"proNumber\":\"ABC123\"}";
        ShipmentTrackingMessage dto = new ShipmentTrackingMessage();
        dto.setLoadId(998877);
        dto.setProNumber("ABC123");

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(anyString(), eq(ShipmentTrackingMessage.class))).thenReturn(dto);

        doThrow(new TmsLoadProcessException("TM client error"))
                .when(ltlTrackingMessageService)
                .addProNumberToTM(dto);
        processor.process(exchange);

        verify(ltlTrackingMessageService).addProNumberToTM(dto);
    }

    @Test
    void testProcess_handlesGenericException() throws Exception {
        String requestJson = "{\"loadId\":\"L445566\",\"proNumber\":\"ZZZ999\"}";
        ShipmentTrackingMessage dto = new ShipmentTrackingMessage();
        dto.setLoadId(445566);
        dto.setProNumber("ZZZ999");

        when(message.getBody(String.class)).thenReturn(requestJson);
        when(objectMapper.readValue(anyString(), eq(ShipmentTrackingMessage.class))).thenReturn(dto);

        doThrow(new RuntimeException("Unexpected exception"))
                .when(ltlTrackingMessageService)
                .addProNumberToTM(dto);
        processor.process(exchange);

        verify(ltlTrackingMessageService).addProNumberToTM(dto);
    }

    @Test
    void testProcess_malformedJson_doesNotCallService() throws Exception {
        String badJson = "{\"loadId\":\"L0001\",,}";
        when(message.getBody(String.class)).thenReturn(badJson);
        when(objectMapper.readValue(anyString(), eq(ShipmentTrackingMessage.class)))
                .thenThrow(new RuntimeException("JSON parse error")); // simulate parsing failure
        processor.process(exchange);

        verify(ltlTrackingMessageService, never()).addProNumberToTM(any());
    }
}
