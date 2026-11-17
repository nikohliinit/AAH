package com.macys.logistics.shipment.load.camel.routes;


import com.macys.logistics.shipment.load.camel.processors.CreateShipmentProcessor;
import com.macys.logistics.shipment.load.camel.processors.DeleteShipmentProcessor;
import com.macys.logistics.shipment.load.camel.processors.ShipmentEventProcessor;
import com.macys.logistics.shipment.load.dto.AuditFeedback;
import com.macys.logistics.shipment.load.dto.inbound.EventType;
import com.macys.logistics.shipment.load.exception.*;
import com.macys.logistics.shipment.load.util.CommonUtil;
import com.macys.logistics.shipment.load.util.Constants;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "feature.shipment.enabled", havingValue = "true", matchIfMissing = false)
public class InboundShipmentRouteBuilder extends RouteBuilder {

  private static final int VALIDATION_REDELIVERY = 0;

  private static final String EXCEPTION_MESSAGE = "${exception.message}";
  private static final String EVENT_TYPE = "eventType";
  public static final String CONFLUENT = "confluent";

  @Value("${route.retry.attempts:3}")
  private String retryCount;
  @Value("${route.retry.delay:1000}")
  private String retryInterval;
  @Value("${shipment.request.consumer.topic}")
  private String kafkaShipmentTopic;
  @Value("${kafka.mode}")
  private String kafkaMode;
  @Value("${shipment.consumer.route}")
  private String shipmentManagedKafkaRoute;
  @Value("${confluent.shipment.consumer.route}")
  private String shipmentConfluentKafkaRoute;
  @Value("${vsp.shipment.integration.route}")
  private String vspShipmentIntegrationRoute;

  @Autowired
  CreateShipmentProcessor createShipmentProcessor;
  @Autowired
  DeleteShipmentProcessor deleteShipmentProcessor;
  @Autowired
  ShipmentEventProcessor findShipmentEventProcessor;
  @Autowired
  CommonUtil commonUtil;

  @Override
  public void configure() throws Exception {
    onException(TMSMappingException.class)
        .log("Exception while Processing Shipment Enterprise Message:: " + simple(EXCEPTION_MESSAGE))
        .handled(true)
        .maximumRedeliveries(retryCount)
        .redeliveryDelay(retryInterval)
        .process(exchange -> {
          String payload = exchange.getIn().getBody(String.class);
          AuditFeedback feedback = new AuditFeedback();
          feedback.setErrorCode(Constants.SHIPMENT_LOAD_DATA_VALIDATION_ERROR_CODE);
          feedback.setStatus(Constants.SHIPMENT_LOAD_FAILURE_STATUS);
          feedback.setProcessAction(Constants.SHIPMENT_LOAD_DATA_VALIDATION_PROCESS_ACTION);
          commonUtil.publishAuditFeedbackToPubSub(feedback, payload);
        })
        .end();

    onException(TMSDataValidationException.class)
        .log("Validation Failure for Shipment Enterprise Message:: " + simple(EXCEPTION_MESSAGE))
        .handled(true)
        .process(exchange -> {
          String payload = exchange.getIn().getBody(String.class);
          AuditFeedback feedback = new AuditFeedback();
          feedback.setErrorCode(Constants.SHIPMENT_LOAD_DATA_VALIDATION_ERROR_CODE);
          feedback.setStatus(Constants.SHIPMENT_LOAD_FAILURE_STATUS);
          feedback.setProcessAction(Constants.SHIPMENT_LOAD_DATA_VALIDATION_PROCESS_ACTION);
          commonUtil.publishAuditFeedbackToPubSub(feedback, payload);
        })
        .maximumRedeliveries(VALIDATION_REDELIVERY)
        .end();

    onException(TMSCreateAPIFailureException.class)
        .log("TM API Processing Failed :: " + simple(EXCEPTION_MESSAGE))
        .handled(true)
        .process(exchange -> {
          String payload = exchange.getIn().getBody(String.class);
          AuditFeedback feedback = new AuditFeedback();
          feedback.setErrorCode(Constants.SHIPMENT_LOAD_CREATE_API_ERROR_CODE);
          feedback.setStatus(Constants.SHIPMENT_LOAD_FAILURE_STATUS);
          feedback.setProcessAction(Constants.SHIPMENT_LOAD_CREATE_API_ERROR_PROCESS_ACTION);
          commonUtil.publishAuditFeedbackToPubSub(feedback, payload);
        })
        .maximumRedeliveries(VALIDATION_REDELIVERY)
        .end();
    onException(TMSDeleteAPIFailureException.class)
        .log("TM API Processing Failed :: " + simple(EXCEPTION_MESSAGE))
        .handled(true)
        .process(exchange -> {
          String payload = exchange.getIn().getBody(String.class);
          AuditFeedback feedback = new AuditFeedback();
          feedback.setErrorCode(Constants.SHIPMENT_LOAD_DELETE_API_ERROR_CODE);
          feedback.setStatus(Constants.SHIPMENT_LOAD_FAILURE_STATUS);
          feedback.setProcessAction(Constants.SHIPMENT_LOAD_DELETE_API_ERROR_PROCESS_ACTION);
          commonUtil.publishAuditFeedbackToPubSub(feedback, payload);
        })
        .maximumRedeliveries(VALIDATION_REDELIVERY)
        .end();
    onException(TmsAuditMessagePublishException.class)
        .log("Exception occurred while publish audit message:: " + simple(EXCEPTION_MESSAGE))
        .handled(true)
        .maximumRedeliveries(retryCount)
        .redeliveryDelay(retryInterval)
        .end();
    onException(Exception.class)
        .log("General Exception occurred:: " + simple(EXCEPTION_MESSAGE))
        .handled(true)
        .process(exchange -> {
          String payload = exchange.getIn().getBody(String.class);
          AuditFeedback feedback = new AuditFeedback();
          feedback.setErrorCode(Constants.SHIPMENT_LOAD_PROCESSING_EXCEPTION_ERROR_CODE);
          feedback.setStatus(Constants.SHIPMENT_LOAD_FAILURE_STATUS);
          feedback.setProcessAction(Constants.SHIPMENT_LOAD_PROCESSING_EXCEPTION_PROCESS_ACTION);
          commonUtil.publishAuditFeedbackToPubSub(feedback, payload);
        })
        .maximumRedeliveries(retryCount)
        .redeliveryDelay(retryInterval)
        .end();

    log.info("STARTED:: configure()");
    configureKafkaRoute(); //dynamically builds kafka consumer route
    from(vspShipmentIntegrationRoute)
        .routeId("shipmentMessageRoute")
        .process(findShipmentEventProcessor)
        .choice()
        .when(header(EVENT_TYPE).isEqualTo(EventType.CREATE))
        .log("Entering create shipment flow")
        .process(createShipmentProcessor)
        .when(header(EVENT_TYPE).isEqualTo(EventType.DELETE))
        .log("Entering delete shipment flow")
        .process(deleteShipmentProcessor)
        .end();
    log.info("ENDED:: configure()");

  }

  private void configureKafkaRoute() {
    if (CONFLUENT.equalsIgnoreCase(kafkaMode)) {
      from(shipmentConfluentKafkaRoute)
          .routeId("confluentKafkaRoute")
          .log("Entered confluent kafka route")
          .to(vspShipmentIntegrationRoute);
    } else {
      from(shipmentManagedKafkaRoute)
          .routeId("managedKafkaRoute")
          .log("Entered managed kafka route")
          .to(vspShipmentIntegrationRoute);
    }
  }

}
