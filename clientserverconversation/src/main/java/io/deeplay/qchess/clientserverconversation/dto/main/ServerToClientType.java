package io.deeplay.qchess.clientserverconversation.dto.main;

import io.deeplay.qchess.clientserverconversation.dto.servertoclient.AcceptConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ActionDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.BadRequestDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ChatMessageDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.DisconnectedDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.EndGameDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.GameSettingsDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ResetGameDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.StartGameDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum ServerToClientType {
    INCORRECT_REQUEST(BadRequestDTO.class),
    ACCEPT_CONNECTION(AcceptConnectionDTO.class),
    DISCONNECTED(DisconnectedDTO.class),
    END_GAME(EndGameDTO.class),
    GAME_SETTINGS(GameSettingsDTO.class),
    GAME_ACTION(ActionDTO.class),
    CHAT_MESSAGE(ChatMessageDTO.class),
    START_GAME(StartGameDTO.class),
    RESET_GAME(ResetGameDTO.class);

    private static final Map<Class<? extends IServerToClientDTO>, ServerToClientType> type =
            new HashMap<>();

    static {
        for (final ServerToClientType t : ServerToClientType.values()) type.put(t.dto, t);
    }

    private final Class<? extends IServerToClientDTO> dto;

    ServerToClientType(final Class<? extends IServerToClientDTO> dto) {
        this.dto = dto;
    }

    public static <T extends IServerToClientDTO> ServerToClientType valueOf(
            final Class<T> dtoClass) {
        return Objects.requireNonNull(type.get(dtoClass), "DTO сервера не найдено");
    }

    public static ServerToClientType valueOf(final IServerToClientDTO dto) {
        return valueOf(dto.getClass());
    }

    public Class<? extends IServerToClientDTO> getDTO() {
        return dto;
    }
}
