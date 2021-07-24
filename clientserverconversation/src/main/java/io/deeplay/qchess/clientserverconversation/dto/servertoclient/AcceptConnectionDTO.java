package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;

/** Подтверждение соединения с клиентом */
public class AcceptConnectionDTO extends IServerToClientDTO {
    @SerializedName("sessionToken")
    public final String sessionToken;

    public AcceptConnectionDTO(final String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
