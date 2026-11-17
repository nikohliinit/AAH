package com.macys.logistics.shipment.load.camel.processors;


import com.macys.logistics.shipment.load.util.Constants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.macys.logistics.shipment.load.service.PhotoGrabberService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Component
@Log4j2
public class PhotoGrabberProcessor implements Processor {
    
    @Autowired
    private PhotoGrabberService photoGrabberService;

    
    @Autowired
    private Tracer tracer;


    @Override
    public void process(Exchange exchange) throws Exception {
      Span camelContextSpan = MicrometerUtility.initializeSpan(exchange,tracer);
        String traceId = UUID.randomUUID().toString();
      try(Tracer.SpanInScope ws = this.tracer.withSpan(camelContextSpan.start())) {
          MDC.put(Constants.PHOTOGRABBER_TRACE_KEY, traceId);
        
          log.info("inside PhotoGrabberProcessor:process"); 
          photoGrabberService.uploadPhotoGrabberFiles();
          log.info("PhotoGrabberProcessor completed"); 
      }
      finally {
          MDC.clear();
        camelContextSpan.end();
    }

        
    }

}
