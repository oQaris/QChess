package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;
import io.deeplay.qchess.game.model.Move;

/** Игровой ход */
public class ActionDTO extends IServerToClientDTO {
    @SerializedName("move")
    public final Move move;

    public ActionDTO(final Move move) {
        this.move = move;
    }
}
