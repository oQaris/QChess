package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.game.model.Move;

/** Игровой ход */
public class SendActionDTO extends ClientToServerDTO {
    @SerializedName("move")
    public final Move move;

    public SendActionDTO(final String sessionToken, final Move move) {
        super(ClientToServerType.valueOf(SendActionDTO.class), sessionToken);
        this.move = move;
    }
}
