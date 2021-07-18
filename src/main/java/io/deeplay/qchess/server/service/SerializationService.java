package io.deeplay.qchess.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringReader;

public class SerializationService {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * @return json
     * @throws IllegalArgumentException если объект нельзя сериализовать
     */
    public static String serialize(Object obj) throws IllegalArgumentException {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Некорректный объект для сериализации", e);
        }
    }

    /**
     * @return десериализованный объект
     * @throws IOException если json некорректный
     */
    public static <T> T deserialize(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(new StringReader(json), clazz);
    }
}
