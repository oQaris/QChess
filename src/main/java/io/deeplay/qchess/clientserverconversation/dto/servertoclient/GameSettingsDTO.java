package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;

/** Настройки игры для комнаты клиента */
public class GameSettingsDTO extends ServerToClientDTO {
    // TODO: добавить GameSettings (?)

    public GameSettingsDTO() {
        super(ServerToClientType.valueOf(GameSettingsDTO.class));
    }
}
