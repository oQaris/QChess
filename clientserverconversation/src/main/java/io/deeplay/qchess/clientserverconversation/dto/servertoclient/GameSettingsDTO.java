package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;

/** Настройки игры для комнаты клиента */
public class GameSettingsDTO extends IServerToClientDTO {
    // TODO: добавить GameSettings (?)
    @SerializedName("color")
    public final Color color;

    @SerializedName("botMove")
    public final Move botMove;

    public GameSettingsDTO(final Color color, final Move botMove) {
        this.color = color;
        this.botMove = botMove;
    }
}
