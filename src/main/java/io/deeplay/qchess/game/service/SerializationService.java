package io.deeplay.qchess.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringReader;

public final class SerializationService {
    private static final ObjectMapper mapper = new ObjectMapper();

    private SerializationService() {}

    public static String serialize(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public static <T> T deserialize(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(new StringReader(json), clazz);
    }
}
