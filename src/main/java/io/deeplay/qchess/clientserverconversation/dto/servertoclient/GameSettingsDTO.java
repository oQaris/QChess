package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;
import io.deeplay.qchess.game.model.Color;

/** Настройки игры для комнаты клиента */
public class GameSettingsDTO extends IServerToClientDTO {
    // TODO: добавить GameSettings (?)
    public final Color color;

    public GameSettingsDTO(final Color color) {
        this.color = color;
    }
}
