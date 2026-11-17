package com.macys.logistics.shipment.load.camel.routes;

import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;
import com.macys.logistics.shipment.load.exception.TmsLoadValidationException;
import com.macys.logistics.shipment.load.logging.ShipmentLoadIntegrationMetricsHelper;
import com.macys.logistics.shipment.load.camel.processors.UpdateTMProcessor;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "feature.seal.update.enabled", havingValue = "true", matchIfMissing = false)
public class UpdateTMRouteBuilder extends RouteBuilder {

  private static final String EXCEPTION_MESSAGE = "${exception.message}";
  private static final String UPDATE_TM_ROUTE_ID = "updateTMRouteId";
  private static final int VALIDATION_REDELIVERY = 0;

  @Value("${route.retry.attempts:1}")
  private String retryCount;

  @Value("${route.retry.delay:1000}")
  private String retryInterval;

  @Value("${update.tm.load.consumer.route}")
  private String updateTMConsumerRoute;
  
  @Value("${spring.kafka.enterprise.appointment.topic}")
  private String kafkaTopic;
  
  @Autowired
  private final UpdateTMProcessor updateTMProcessor;
  
  @Autowired
  ShipmentLoadIntegrationMetricsHelper metricsHelper;

  public UpdateTMRouteBuilder(UpdateTMProcessor updateTMProcessor) {
    this.updateTMProcessor = updateTMProcessor;
  }

  /**
   * @throws Exception - Exception in case of an error
   */
  @Override
  public void configure() throws Exception {

    onException(TmsLoadProcessException.class)
        .log("Exception while processing Update TM event:: " + simple(EXCEPTION_MESSAGE))
        .handled(true)
        .maximumRedeliveries(retryCount)
        .redeliveryDelay(retryInterval)
        .process(this::resetKafkaOffset)
        .process(exchange -> metricsHelper.clearThreadContext());
    onException(TmsLoadValidationException.class)
    	.log("Validation exception while processing Update TM event:: " + simple(
    			EXCEPTION_MESSAGE))
    	.handled(true)
    	.maximumRedeliveries(VALIDATION_REDELIVERY)
    	.process(exchange -> metricsHelper.clearThreadContext())
    	.end();
    onException(Exception.class)
        .log("Generic exception caught while processing Update TM event:: " + simple(
            EXCEPTION_MESSAGE))
        .handled(true)
        .maximumRedeliveries(retryCount)
        .redeliveryDelay(retryInterval)
        .process(exchange -> metricsHelper.clearThreadContext())
        .end();
    log.info("STARTED:: configure()");
    from(updateTMConsumerRoute)
        .routeId(UPDATE_TM_ROUTE_ID)
        .process(updateTMProcessor)
        .log(LoggingLevel.DEBUG, "Received appointment payload event :${body}")
        .process(exchange -> metricsHelper.clearThreadContext())
        .end();
    log.info("ENDED:: configure()");
  }
  
  private void resetKafkaOffset(Exchange exchange) {
	  Consumer<?, ?> consumer = exchange.getIn().getHeader("kafka.consumer", Consumer.class);
	  if(consumer != null) {
		  consumer.seek(new TopicPartition(kafkaTopic, 0), consumer.position(new TopicPartition(kafkaTopic, 0)));
	  }
  }
}