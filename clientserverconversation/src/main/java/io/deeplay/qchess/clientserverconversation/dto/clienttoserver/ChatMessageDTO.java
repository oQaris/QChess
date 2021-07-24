package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IClientToServerDTO;

/** Сообщение серверу */
public class ChatMessageDTO extends IClientToServerDTO {
    @SerializedName("message")
    public final String message;

    public ChatMessageDTO(final String sessionToken, final String message) {
        super(sessionToken);
        this.message = message;
    }
}
