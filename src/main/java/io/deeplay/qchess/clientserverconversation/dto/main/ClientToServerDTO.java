package io.deeplay.qchess.clientserverconversation.dto.main;

import com.google.gson.annotations.SerializedName;

/** Запрос от клиента к серверу */
public final class ClientToServerDTO {
    @SerializedName("type")
    public final ClientToServerType type;

    @SerializedName("request")
    public final String json;

    public ClientToServerDTO(final ClientToServerType type, final String json) {
        this.type = type;
        this.json = json;
    }
}
