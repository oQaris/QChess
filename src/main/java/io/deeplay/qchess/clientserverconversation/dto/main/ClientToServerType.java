package io.deeplay.qchess.clientserverconversation.dto.main;

import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.GetConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.GetGameSettingsDTO;
import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.SendActionDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum ClientToServerType {
    SEND_GAME_ACTION(SendActionDTO.class),
    GET_CONNECTION(GetConnectionDTO.class),
    GET_GAME_SETTINGS(GetGameSettingsDTO.class);

    private static final Map<Class<? extends ClientToServerDTO>, ClientToServerType> type =
            new HashMap<>();

    static {
        for (ClientToServerType t : ClientToServerType.values()) type.put(t.dto, t);
    }

    private final Class<? extends ClientToServerDTO> dto;

    ClientToServerType(Class<? extends ClientToServerDTO> dto) {
        this.dto = dto;
    }

    public static ClientToServerType valueOf(Class<? extends ClientToServerDTO> dtoClass) {
        return Objects.requireNonNull(type.get(dtoClass), "DTO клиента не найдено");
    }

    public Class<? extends ClientToServerDTO> getDTO() {
        return dto;
    }
}
