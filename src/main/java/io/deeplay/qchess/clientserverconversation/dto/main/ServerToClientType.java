package io.deeplay.qchess.clientserverconversation.dto.main;

import io.deeplay.qchess.clientserverconversation.dto.servertoclient.AcceptConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.BadRequestDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.DisconnectedDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.EndGameDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.GameSettingsDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum ServerToClientType {
    INCORRECT_REQUEST(BadRequestDTO.class),
    ACCEPT_CONNECTION(AcceptConnectionDTO.class),
    DISCONNECTED(DisconnectedDTO.class),
    END_GAME(EndGameDTO.class),
    GAME_SETTINGS(GameSettingsDTO.class);

    private static final Map<Class<? extends ServerToClientDTO>, ServerToClientType> type =
            new HashMap<>();

    static {
        for (ServerToClientType t : ServerToClientType.values()) type.put(t.dto, t);
    }

    private final Class<? extends ServerToClientDTO> dto;

    ServerToClientType(Class<? extends ServerToClientDTO> dto) {
        this.dto = dto;
    }

    public static ServerToClientType valueOf(Class<? extends ServerToClientDTO> dtoClass) {
        return Objects.requireNonNull(type.get(dtoClass), "DTO сервера не найдено");
    }

    public Class<? extends ServerToClientDTO> getDTO() {
        return dto;
    }
}
