package io.deeplay.qchess.clientserverconversation.dto.main;

import com.google.gson.annotations.SerializedName;

/** Запрос от сервера к клиенту */
public abstract class ServerToClientDTO {
    @SerializedName("type")
    public final ServerToClientType type;

    protected ServerToClientDTO(final ServerToClientType type) {
        this.type = type;
    }
}
