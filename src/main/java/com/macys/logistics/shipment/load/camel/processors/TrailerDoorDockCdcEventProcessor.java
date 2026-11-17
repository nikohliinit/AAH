package com.macys.logistics.shipment.load.camel.processors;

import com.macys.logistics.shipment.load.service.TrailerDoorDockCdcEventService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TrailerDoorDockCdcEventProcessor implements Processor {

    @Autowired
    private TrailerDoorDockCdcEventService trailerDoorDockCdcEventService;

    @Autowired
    private Tracer tracer;

    @Override
    public void process(Exchange exchange) throws Exception {
        Span camelContextSpan = MicrometerUtility.initializeSpan(exchange,tracer);
        try(Tracer.SpanInScope ws = this.tracer.withSpan(camelContextSpan.start())) {

            log.info("Inside TrailerDoorDockCdcEventProcessor : process");
            String requestMessage = exchange.getIn().getBody(String.class);
            trailerDoorDockCdcEventService.processDockDoorCDCEvent(requestMessage);

        }
         finally {
            camelContextSpan.end();
        }
    }
}