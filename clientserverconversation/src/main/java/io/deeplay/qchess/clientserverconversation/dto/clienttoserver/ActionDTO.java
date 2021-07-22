package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IClientToServerDTO;
import io.deeplay.qchess.game.model.Move;

/** Игровой ход */
public class ActionDTO extends IClientToServerDTO {
    @SerializedName("move")
    public final Move move;

    public ActionDTO(final String sessionToken, final Move move) {
        super(sessionToken);
        this.move = move;
    }
}
