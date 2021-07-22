package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;

/** Отключение клиента */
public class DisconnectedDTO extends IServerToClientDTO {
    @SerializedName("reason")
    public final String reason;

    public DisconnectedDTO(final String reason) {
        this.reason = reason;
    }
}
