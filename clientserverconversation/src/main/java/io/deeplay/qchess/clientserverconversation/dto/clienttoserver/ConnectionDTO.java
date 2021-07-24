package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IClientToServerDTO;

/** Запрос на подключение или отключение от сервера */
public class ConnectionDTO extends IClientToServerDTO {
    @SerializedName("connection")
    public final boolean connection;

    public ConnectionDTO(final String sessionToken, final boolean connection) {
        super(sessionToken);
        this.connection = connection;
    }
}
