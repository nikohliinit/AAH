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
import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class TrailerImageAttachProcessor implements Processor {

    @Autowired
    private PhotoGrabberService photoGrabberService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private Tracer tracer;

    @Autowired
    CommonUtil commonUtil;

    @Override
    public void process(Exchange exchange) throws Exception {
        Span camelContextSpan = MicrometerUtility.initializeSpan(exchange,tracer);
        try(Tracer.SpanInScope ws = this.tracer.withSpan(camelContextSpan.start())) {

            log.info("Inside TrailerImageAttachProcessor : process");
            String requestMessage = exchange.getIn().getBody(String.class);
            TrailerEvent trailerEvent = objectMapper.readValue(requestMessage, TrailerEvent.class);
            if (trailerEvent == null || trailerEvent.getRequestId() == null) {
                log.error("Received an invalid trailer event. The event or its requestId is null. Discarding message. Body: {}", requestMessage);
                return;
            }
            log.info("Received Trailer event with request id :{}", trailerEvent.getRequestId());

            AuditTrailerFeedback auditFeedback = new AuditTrailerFeedback();
            List<Object> auditPayload = new ArrayList<>();
            auditPayload.add(Map.of("requestId", trailerEvent.getRequestId()));
            auditPayload.add(Map.of("success_trailer_details", trailerEvent));
            auditFeedback.setInputArguments(new Object[]{auditPayload});
            commonUtil.publishTrailerImageAuditFeedbackToPubSub(auditFeedback, trailerEvent.getRequestId(), Constants.CONSUME_IMAGE_PROCESS_ACTION, Constants.SHIPMENT_LOAD_SUCCESS_STATUS);

            photoGrabberService.attachImagesToLoad(trailerEvent);
            log.info("TrailerImageAttachProcessor completed for request Id : {}", trailerEvent.getRequestId());

        } catch (JsonProcessingException e) {
            log.error("Exception occurred in TrailerImageAttachProcessor while parsing Trailer Event {}", e.getMessage());
            AuditTrailerFeedback auditFeedback = new AuditTrailerFeedback();
            auditFeedback.setErrorCode(Constants.CONSUME_IMAGE_ERROR_CODE);
            auditFeedback.setErrorMessage(Constants.CONSUME_IMAGE_ERROR_DESC.replace("{responseCode}", "NA")
                    .replace("{errorMessage}", e.getMessage()));
            commonUtil.publishTrailerImageAuditFeedbackToPubSub(auditFeedback, "NA", Constants.CONSUME_IMAGE_PROCESS_ACTION, Constants.SHIPMENT_LOAD_FAILURE_STATUS);
            throw e;
        }catch(TmsAuditMessagePublishException e){
            log.warn("TmsAuditMessagePublishException caught in TrailerImageAttachProcessor while publishing audit messages : {} ",e.getMessage());
            throw e;
        }catch(TmsLoadProcessException e){
            log.warn("TmsLoadProcessException caught in TrailerImageAttachProcessor while publishing audit messages : {} ",e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.warn("An exception caught  in TrailerImageAttachProcessor while processing trailer images : {} ", e.getMessage());
            throw e;
        }
        finally {
            camelContextSpan.end();
        }
    }
}
