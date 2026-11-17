package com.macys.logistics.shipment.load.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.macys.logistics.shipment.load.camel.processors.PhotoGrabberProcessor;
import com.macys.logistics.shipment.load.exception.PhotoGrabberException;

@Component
public class PhotoGrabberRouteBuilder extends RouteBuilder {
    
    
    @Autowired 
    PhotoGrabberProcessor photoGrabberProcessor;
    
    
    @Value("${photo.grabber.route}")
    private String photoGrabberRoute;

    @Value("${photo.extract.retry.attempts}")
    private String retryCount;

    @Value("${photo.extract.retry.delay}")
    private String retryInterval;

    private static final String EXCEPTION_MESSAGE = "${exception.message}";

    public static final String METRICS_HELPER_BEAN = "metricsHelper";

    public static final String CLEAR_THREAD_CONTEXT_METHOD = "clearThreadContext";

    @Override
    public void configure() throws Exception {

        onException(PhotoGrabberException.class)
                .log("Photo Grabber exception while calling Photo Grabber service:" + simple(EXCEPTION_MESSAGE))
                .bean(METRICS_HELPER_BEAN, CLEAR_THREAD_CONTEXT_METHOD)
                .handled(true)
                .maximumRedeliveries(retryCount)
                .redeliveryDelay(retryInterval)
                .end();

                
         
        from(photoGrabberRoute)
                .routeId("photoGrabberProcessorRoute")
                .process(photoGrabberProcessor)
                .end();
        
    }

}

