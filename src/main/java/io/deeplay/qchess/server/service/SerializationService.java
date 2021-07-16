package io.deeplay.qchess.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringReader;

public class SerializationService {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String serialize(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public static <T> T deserialize(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(new StringReader(json), clazz);
    }
}
