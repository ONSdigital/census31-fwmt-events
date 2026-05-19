package uk.gov.ons.census.fwmt.events.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;

@Slf4j
@Component
public class GatewayLoggingEventProducer implements GatewayEventProducer {

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  @Override
  public void sendEvent(GatewayEventDTO event) {
    log.info("{} event {}", event.getSource(), toJson(event));
  }

  @Override
  public void sendErrorEvent(GatewayErrorEventDTO errorEvent) {
    if (errorEvent.getMetadata() != null && errorEvent.getMetadata()
        .containsKey(GatewayEventProducer.INVALID_ERROR_TYPE)) {
      log.error("Invalid event type: {}", errorEvent.getMetadata().get(GatewayEventProducer.INVALID_ERROR_TYPE));
    }
    log.info("{} {} error event {}", errorEvent.getSource(), errorEvent.getErrorEventType(), toJson(errorEvent));
  }

  private static String toJson(Object value) {
    try {
      return MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      log.warn("Failed to serialise gateway event payload: {}", e.getMessage());
      return String.valueOf(value);
    }
  }
}
