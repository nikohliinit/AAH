package com.macys.logistics.shipment.load.camel.routes;

import com.macys.logistics.shipment.load.camel.processors.LTLLoadEventProcessor;
import com.macys.logistics.shipment.load.exception.TmsEntityServiceProcessException;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.exception.TmsLoadValidationException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class LTLEventRouteBuilder extends RouteBuilder {

    private static final String EXCEPTION_MESSAGE = "${exception.message}";

    @Value("${route.retry.attempts:1}")
    private String retryCount;

    @Value("${route.retry.delay:1000}")
    private String retryInterval;

    @Value("${supplychain.tmsintg.ltl.message.route}")
    private String ltlEventConsumerRoute;

    @Autowired
    LTLLoadEventProcessor ltlLoadEventProcessor;

    @Override
    public void configure()  {

        onException(TmsLoadValidationException.class)
                .log("Exception while file parsing processor:: " + simple(EXCEPTION_MESSAGE))
                .handled(true)
                .maximumRedeliveries(0)
                .redeliveryDelay(retryInterval)
                .end();

        onException(TmsLoadProcessException.class)
                .log("Exception occurred in FindTMSLoadEventProcessor:: " + simple(EXCEPTION_MESSAGE))
                .handled(true)
                .maximumRedeliveries(retryCount)
                .redeliveryDelay(retryInterval)
                .end();

        onException(Exception.class)
                .log("Exception occurred while publish audit message:: " + simple(EXCEPTION_MESSAGE))
                .handled(true)
                .maximumRedeliveries(retryCount)
                .redeliveryDelay(retryInterval)
                .end();


        log.info("LTLEventIntegrationRouteBuilder:: STARTED:: configure()");

        from(ltlEventConsumerRoute)
                .routeId("ibLoadConsumerRouteTMEvent")
                .log(LoggingLevel.DEBUG,"Processing LTL event")
                .process(ltlLoadEventProcessor)
                .choice()
                .end();

        log.info("LTLEventIntegrationRouteBuilder:: ENDED:: configure()");


    }
}
