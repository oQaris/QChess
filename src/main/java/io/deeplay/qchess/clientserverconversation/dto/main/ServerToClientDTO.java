package io.deeplay.qchess.clientserverconversation.dto.main;

import com.google.gson.annotations.SerializedName;

/** Запрос от сервера к клиенту */
public final class ServerToClientDTO {
    @SerializedName("type")
    public final ServerToClientType type;

    @SerializedName("request")
    public final String json;

    public ServerToClientDTO(final ServerToClientType type, final String json) {
        this.type = type;
        this.json = json;
    }
}
