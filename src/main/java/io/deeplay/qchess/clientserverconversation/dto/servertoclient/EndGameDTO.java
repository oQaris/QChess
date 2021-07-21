package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;

/** Причина конца игры */
public class EndGameDTO extends ServerToClientDTO {
    @SerializedName("reason")
    public final String reason;

    public EndGameDTO(final String reason) {
        super(ServerToClientType.valueOf(EndGameDTO.class));
        this.reason = reason;
    }
}
