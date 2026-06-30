package uk.gov.ons.census.fwmt.events.producer;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.census.fwmt.events.messaging.GatewayEventJsonCodec;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.provider", havingValue = "pubsub")
public class PubSubGatewayEventProducer implements GatewayEventProducer {

  private final PubSubTemplate pubSubTemplate;
  private final GatewayEventJsonCodec codec = new GatewayEventJsonCodec();

  @Value("${app.messaging.destinations.gatewayEvents:Gateway.Events.Exchange}")
  private String gatewayEventsTopic;

  @Override
  @Retryable
  public void sendEvent(GatewayEventDTO event) {
    publish(event);
  }

  @Override
  public void sendErrorEvent(GatewayErrorEventDTO errorEvent) {
    publish(errorEvent);
  }

  private void publish(Object payload) {
    try {
      PubsubMessage message = codec.toPubsubMessage(payload);
      log.debug("Publishing gateway event to topic {}", gatewayEventsTopic);
      pubSubTemplate.publish(gatewayEventsTopic, message);
    } catch (Exception e) {
      log.error("Failed to publish gateway event to Pub/Sub", e);
    }
  }
}
