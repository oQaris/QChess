package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;

/** Подтверждение соединения с клиентом */
public class AcceptConnectionDTO extends ServerToClientDTO {
    @SerializedName("sessionToken")
    public final String sessionToken;

    public AcceptConnectionDTO(final String sessionToken) {
        super(ServerToClientType.valueOf(AcceptConnectionDTO.class));
        this.sessionToken = sessionToken;
    }
}
