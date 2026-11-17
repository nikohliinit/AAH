package com.macys.logistics.shipment.load.camel.processors;

import com.macys.logistics.shipment.load.service.TrailerDoorDockCdcEventService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrailerDoorDockCdcEventProcessorTest {

    @InjectMocks
    private TrailerDoorDockCdcEventProcessor processor;

    @Mock
    private TrailerDoorDockCdcEventService service;

    @Mock
    private Tracer tracer;

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @Mock
    private Span span;

    @Mock
    private Tracer.SpanInScope spanInScope;

    @BeforeEach
    void setUp() {
        lenient().when(exchange.getIn()).thenReturn(message);
        lenient().when(tracer.withSpan(any())).thenReturn(spanInScope);
    }

    @Test
    void testProcess_Success() throws Exception {
        // Arrange
        String payload = "[{\"metadata\":{\"OperationName\":\"INSERT\"}}]";
        when(message.getBody(String.class)).thenReturn(payload);

        // Mock static MicrometerUtility
        try (MockedStatic<MicrometerUtility> utilities = Mockito.mockStatic(MicrometerUtility.class)) {
            utilities.when(() -> MicrometerUtility.initializeSpan(exchange, tracer)).thenReturn(span);
            
            // Act
            processor.process(exchange);
            
            // Assert
            verify(service, times(1)).processDockDoorCDCEvent(payload);
        }
    }

    @Test
    void testProcess_ServiceThrowsException_Rethrows() throws Exception {
        // Arrange
        String payload = "invalid";
        when(message.getBody(String.class)).thenReturn(payload);
        doThrow(new RuntimeException("Processing failed")).when(service).processDockDoorCDCEvent(payload);

        try (MockedStatic<MicrometerUtility> utilities = Mockito.mockStatic(MicrometerUtility.class)) {
            utilities.when(() -> MicrometerUtility.initializeSpan(exchange, tracer)).thenReturn(span);
            
            // Act & Assert
            assertThrows(RuntimeException.class, () -> processor.process(exchange));
            
            verify(service, times(1)).processDockDoorCDCEvent(payload);
        }
    }
}