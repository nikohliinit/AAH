package com.macys.logistics.shipment.load.camel.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.macys.logistics.shipment.load.camel.processors.ShipConfirmProcessor;
import com.macys.logistics.shipment.load.enums.Event;
import com.macys.logistics.shipment.load.enums.Order;
import com.macys.logistics.shipment.load.util.Constants;
import org.apache.camel.LoggingLevel;
import org.apache.camel.support.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.macys.logistics.shipment.load.util.Constants.OBTM_TRANSFER_TYPE;
import static com.macys.logistics.shipment.load.util.Constants.OBTM_TRANSFER_TYPE_HEADER;

/**
 * Defines the Camel routing and the respective processors to be called for each endpoint defined in
 * application property file.
 */
@Component
@ConditionalOnProperty(name = "feature.ship.confirm.processing.enabled", havingValue = "true", matchIfMissing = false)
public class ShipConfirmRouteBuilder extends RouteBuilder {

    private static final String EXCEPTION_MESSAGE = "${exception.message}";

    @Value("${route.retry.attempts:1}")
    private String retryCount;

    @Value("${route.retry.delay:1000}")
    private String retryInterval;

    @Value("${shipconfirm.appointment.route}")
    private String updateAppointmentShipConfirmRoute;

    @Autowired
    ShipConfirmProcessor shipConfirmProcessor;


    /**
     * @throws Exception - Exception in case of an error
     */
    @Override
    public void configure() throws Exception {

        onException(JsonProcessingException.class)
                .log("Exception while processing Ship Confirm Message :: " + simple(EXCEPTION_MESSAGE))
                .handled(true)
                .end();

        onException(Exception.class)
                .log("Generic exception caught while processing Ship Confirm event :: " + simple(EXCEPTION_MESSAGE))
                .handled(true)
                .maximumRedeliveries(retryCount)
                .redeliveryDelay(retryInterval)
                .end();

        from(updateAppointmentShipConfirmRoute)
                .routeId(Constants.SHIP_CONFIRM_ROUTE_ID)
                .choice()
                .when(
                        PredicateBuilder.and(
                                header(Constants.ORDER_TYPE).isEqualTo(Order.DIGITAL.getType()),
                                header(Constants.EVENT_TYPE).isEqualTo(Event.SHIP_CONFIRM.getType()),
                                header(OBTM_TRANSFER_TYPE_HEADER).isEqualTo(OBTM_TRANSFER_TYPE)))
                    .log(LoggingLevel.DEBUG,"Processing started for ShipConfirm message for headers: ${headers}, body:${body}")
                    .process(shipConfirmProcessor)
                    .log(LoggingLevel.DEBUG,"Successfully processed update appointment ShipConfirm message for headers:${headers}")
                .otherwise()
                    .log(LoggingLevel.DEBUG,"Cannot process message due to header mismatch for headers:${headers}, body:${body}")
                .end();

        log.info("ENDED:: configure()");
    }
}
