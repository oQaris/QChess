package io.deeplay.qchess.clientserverconversation.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class SerializationService {
    private static final Gson gson = new Gson();

    /** @return json */
    public static String serialize(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * @return десериализованный объект
     * @throws JsonSyntaxException если json некорректный
     */
    public static <T> T deserialize(String json, Class<T> clazz) throws JsonSyntaxException {
        return gson.fromJson(json, clazz);
    }
}
