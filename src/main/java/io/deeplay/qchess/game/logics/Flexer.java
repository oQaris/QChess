package io.deeplay.qchess.game.logics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringReader;

public class Flexer {
    final ObjectMapper mapper = new ObjectMapper();

    String serialize(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    <T> T deserialize(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(new StringReader(json), clazz);
    }
}
