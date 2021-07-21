package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;

/** Получение данных игры */
public class GetGameSettingsDTO extends ClientToServerDTO {

    public GetGameSettingsDTO(final String sessionToken) {
        super(ClientToServerType.valueOf(GetGameSettingsDTO.class), sessionToken);
    }
}
