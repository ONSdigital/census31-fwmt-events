package uk.gov.ons.census.fwmt.events.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;

/**
 * Pub/Sub adapter placeholder for gateway events (Stage 2).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.messaging.provider", havingValue = "pubsub")
public class PubSubGatewayEventProducer implements GatewayEventProducer {

  @Override
  public void sendEvent(GatewayEventDTO event) {
    throw new UnsupportedOperationException(
        "Pub/Sub gateway events publish is not implemented (Stage 2). Set app.messaging.provider=rabbit.");
  }

  @Override
  public void sendErrorEvent(GatewayErrorEventDTO errorEvent) {
    throw new UnsupportedOperationException(
        "Pub/Sub gateway events publish is not implemented (Stage 2). Set app.messaging.provider=rabbit.");
  }
}
