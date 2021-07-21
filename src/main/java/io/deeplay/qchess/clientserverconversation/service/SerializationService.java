package io.deeplay.qchess.clientserverconversation.service;

import com.google.gson.Gson;
import java.io.IOException;

public class SerializationService {
    private static final Gson gson = new Gson();

    /** @return json */
    public static String serialize(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * @return десериализованный объект
     * @throws IOException если json некорректный
     */
    public static <T> T deserialize(String json, Class<T> clazz) throws IOException {
        return gson.fromJson(json, clazz);
    }
}
