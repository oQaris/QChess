package io.deeplay.qchess.clientserverconversation.dto.main;

import com.google.gson.annotations.SerializedName;

/** Запрос от клиента к серверу */
public abstract class ClientToServerDTO {
    @SerializedName("type")
    public final ClientToServerType type;

    @SerializedName("sessionToken")
    public final String sessionToken;

    protected ClientToServerDTO(final ClientToServerType type, final String sessionToken) {
        this.type = type;
        this.sessionToken = sessionToken;
    }
}
