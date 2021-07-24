package io.deeplay.qchess.clientserverconversation.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.clientserverconversation.dto.main.IClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;

public class SerializationService {
    private static final Gson gson = new Gson();

    /** @return json */
    private static String serialize(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Создает ClientToServerDTO из type и json, затем сериализует
     *
     * @return json основного объекта, хранящий запрос серверу
     */
    public static String makeMainDTOJsonToServer(IClientToServerDTO dto) {
        return serialize(new ClientToServerDTO(ClientToServerType.valueOf(dto), serialize(dto)));
    }

    /**
     * Создает ServerToClientDTO из mainRequestType и json, затем сериализует
     *
     * @return json основного объекта, хранящий запрос клиенту
     */
    public static String makeMainDTOJsonToClient(IServerToClientDTO dto) {
        return serialize(new ServerToClientDTO(ServerToClientType.valueOf(dto), serialize(dto)));
    }

    /**
     * @return десериализованный объект
     * @throws SerializationException если json некорректный
     */
    private static <T> T deserialize(String json, Class<T> clazz) throws SerializationException {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new SerializationException();
        }
    }

    /**
     * @return основной объект, хранящий запрос от сервера к клиенту
     * @throws SerializationException если json некорректный
     */
    public static ServerToClientDTO serverToClientDTOMain(String json)
            throws SerializationException {
        return deserialize(json, ServerToClientDTO.class);
    }

    /**
     * @return основной объект, хранящий запрос от клиента к серверу
     * @throws SerializationException если json некорректный
     */
    public static ClientToServerDTO clientToServerDTOMain(String json)
            throws SerializationException {
        return deserialize(json, ClientToServerDTO.class);
    }

    /**
     * @return запрос от сервера к клиенту
     * @throws SerializationException если json некорректный
     */
    public static <T extends IServerToClientDTO> T serverToClientDTORequest(
            String json, Class<T> clazz) throws SerializationException {
        return deserialize(json, clazz);
    }

    /**
     * @return запрос от клиента к серверу
     * @throws SerializationException если json некорректный
     */
    public static <T extends IClientToServerDTO> T clientToServerDTORequest(
            String json, Class<T> clazz) throws SerializationException {
        return deserialize(json, clazz);
    }
}
