package com.macys.logistics.shipment.load.camel.processors;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
@Log4j2
public class MicrometerUtility {
    private MicrometerUtility(){}
    public static Span initializeSpan(Exchange exchange, Tracer tracer) {
        Span camelContextSpan = tracer.nextSpan().name(exchange.getContext().getName());
        exchange.setProperty("spanId", camelContextSpan.context().spanId());
        camelContextSpan= tracer.nextSpan().name("customSpan").start();
        return camelContextSpan;
    }
}
