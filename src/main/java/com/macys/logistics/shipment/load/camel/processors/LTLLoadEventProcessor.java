package com.macys.logistics.shipment.load.camel.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentTrackingMessage;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.exception.TmsLoadValidationException;
import com.macys.logistics.shipment.load.service.LTLTrackingMessageService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class LTLLoadEventProcessor implements Processor {
    @Autowired
    private Tracer tracer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    LTLTrackingMessageService ltlTrackingMessageService;

    @Override
    public void process(Exchange exchange) throws Exception {
        Span camelContextSpan = MicrometerUtility.initializeSpan(exchange,tracer);
        try(Tracer.SpanInScope ws = this.tracer.withSpan(camelContextSpan.start())) {
            log.info("Inside LTLLoadEventProcessor :process");
            String request = exchange.getIn().getBody(String.class);
            ShipmentTrackingMessage shipmentTracking = objectMapper.readValue(request, ShipmentTrackingMessage.class);
            if(null != shipmentTracking && null != shipmentTracking.getLoadId()){
                ltlTrackingMessageService.addProNumberToTM(shipmentTracking);
            } else {
                log.warn("Shipment Tracking message is invalid or missing loadId");
            }
        } catch (TmsLoadValidationException e) {
            log.error("Load details are not available for loadId in TM {} ", e.getMessage());
        } catch (TmsLoadProcessException e) {
            log.error("Exception while calling TM client to add proNumber for load {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception while processing proNumber for load {}", e.getMessage());
        }
        finally {
            MDC.clear();
            camelContextSpan.end();
        }
    }
}

