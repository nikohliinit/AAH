package com.macys.logistics.shipment.load.camel.processors;


import com.google.cloud.storage.Blob;
import com.macys.logistics.shipment.load.service.PhotoGrabberService;
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
import org.mockito.Spy;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PhotoGrabberProcessorTest {

    @InjectMocks
    @Spy
    PhotoGrabberProcessor photoGrabberProcessor;
    @Mock
    private Exchange exchange;
    @Mock
    private Message message;
    @Mock
    private Blob mockBlob;
    
    @Mock
    private Tracer tracer;
    @Mock
    private Span span;
    @Mock
    private TraceContext traceContext;
    @Mock
    private Tracer.SpanInScope spanInScope;

    
    @Mock
    private PhotoGrabberService photoGrabberService;

    @BeforeEach
    void init() {
      lenient().when(exchange.getIn()).thenReturn(message);
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

    }

    @Test
    void photoGrabberProcessorSuccess() throws Exception {
        doNothing().when(photoGrabberService).uploadPhotoGrabberFiles();
        photoGrabberProcessor.process(exchange);
        verify(photoGrabberService, times(1)).uploadPhotoGrabberFiles();
        
    }

    
}
