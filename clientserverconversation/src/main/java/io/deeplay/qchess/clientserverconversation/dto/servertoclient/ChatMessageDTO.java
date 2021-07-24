package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;

public class ChatMessageDTO extends IServerToClientDTO {
    @SerializedName("message")
    public final String message;

    public ChatMessageDTO(final String message) {
        this.message = message;
    }
}
