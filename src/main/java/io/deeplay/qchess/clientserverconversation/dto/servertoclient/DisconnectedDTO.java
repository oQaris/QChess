package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;

/** Отключение клиента */
public class DisconnectedDTO extends ServerToClientDTO {
    @SerializedName("reason")
    public final String reason;

    public DisconnectedDTO(final String reason) {
        super(ServerToClientType.valueOf(DisconnectedDTO.class));
        this.reason = reason;
    }
}
