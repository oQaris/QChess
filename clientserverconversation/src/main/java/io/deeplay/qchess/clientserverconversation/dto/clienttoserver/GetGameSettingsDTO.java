package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import io.deeplay.qchess.clientserverconversation.dto.main.IClientToServerDTO;

/** Получение данных игры */
public class GetGameSettingsDTO extends IClientToServerDTO {

    public GetGameSettingsDTO(final String sessionToken) {
        super(sessionToken);
    }
}
