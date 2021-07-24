package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;

/** Причина конца игры */
public class EndGameDTO extends IServerToClientDTO {
    @SerializedName("reason")
    public final String reason;

    public EndGameDTO(final String reason) {
        this.reason = reason;
    }
}
