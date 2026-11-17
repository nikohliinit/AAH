package com.macys.logistics.shipment.load.camel.routes;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.macys.logistics.shipment.load.camel.processors.TrailerImageAttachProcessor;
import com.macys.logistics.shipment.load.exception.PhotoGrabberException;
import com.macys.logistics.shipment.load.exception.TmsAuditMessagePublishException;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TrailerImageAttachRouteBuilder extends RouteBuilder {

    @Autowired
    TrailerImageAttachProcessor imageAttachProcessor;

    @Value("${trailer.image.attach.route}")
    private String trailerImageAttachRoute;

    @Value("${photo.extract.retry.attempts}")
    private String retryCount;

    @Value("${photo.extract.retry.delay}")
    private String retryInterval;

    private static final String EXCEPTION_MESSAGE = "${exception.message}";

    public static final String METRICS_HELPER_BEAN = "metricsHelper";

    public static final String CLEAR_THREAD_CONTEXT_METHOD = "clearThreadContext";

    @Override
    public void configure() throws Exception {

        onException(TmsAuditMessagePublishException.class)
                .log(LoggingLevel.WARN,"TmsAuditMessagePublishException while calling Trailer Image AttachProcessor:" + simple(EXCEPTION_MESSAGE))
                .handled(true)
                .end();

        onException(JsonProcessingException.class)
                .log(LoggingLevel.WARN,"JsonProcessingException while processing Trailer ImageAttach Message :: " + simple(EXCEPTION_MESSAGE))
                .handled(true)
                .end();

        onException(TmsLoadProcessException.class)
                .log(LoggingLevel.WARN,"TmsLoadProcessException while processing Trailer ImageAttach Message :: " + simple(EXCEPTION_MESSAGE))
                .handled(true)
                .end();

        onException(PhotoGrabberException.class)
                .log(LoggingLevel.WARN,"Photo Grabber exception while calling Photo Grabber service:" + simple(EXCEPTION_MESSAGE))
                .bean(METRICS_HELPER_BEAN, CLEAR_THREAD_CONTEXT_METHOD)
                .handled(true)
                .maximumRedeliveries(retryCount)
                .redeliveryDelay(retryInterval)
                .end();


        log.info("STARTED:: configure()");
        from(trailerImageAttachRoute)
                .routeId("trailerImageAttachProcessorRoute")
                .process(imageAttachProcessor)
                .end();
        log.info("ENDED:: configure()");
    }
}
