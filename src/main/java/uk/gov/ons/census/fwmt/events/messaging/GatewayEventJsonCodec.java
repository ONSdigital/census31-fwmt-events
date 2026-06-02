package uk.gov.ons.census.fwmt.events.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;

public final class GatewayEventJsonCodec {

  public static final String TYPE_ID_HEADER = "__TypeId__";

  private final ObjectMapper objectMapper;
  private final Map<String, Class<?>> typeIds;

  public GatewayEventJsonCodec() {
    objectMapper = new ObjectMapper();
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    typeIds = gatewayEventTypeIds();
  }

  public PubsubMessage toPubsubMessage(Object payload) {
    try {
      String typeId = payload.getClass().getName();
      String json = objectMapper.writeValueAsString(payload);
      return PubsubMessage.newBuilder()
          .setData(ByteString.copyFromUtf8(json))
          .putAttributes(TYPE_ID_HEADER, typeId)
          .build();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to encode gateway event message", e);
    }
  }

  private static Map<String, Class<?>> gatewayEventTypeIds() {
    Map<String, Class<?>> mapping = new HashMap<>();
    mapping.put(GatewayEventDTO.class.getName(), GatewayEventDTO.class);
    mapping.put(GatewayErrorEventDTO.class.getName(), GatewayErrorEventDTO.class);
    return mapping;
  }
}
