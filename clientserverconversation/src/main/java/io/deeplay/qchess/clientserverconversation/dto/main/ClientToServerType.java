package io.deeplay.qchess.clientserverconversation.dto.main;

import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ActionDTO;
import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ChatMessageDTO;
import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.FindGameDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum ClientToServerType {
    GAME_ACTION(ActionDTO.class),
    SET_CONNECTION(ConnectionDTO.class),
    FIND_GAME(FindGameDTO.class),
    CHAT_MESSAGE(ChatMessageDTO.class);

    private static final Map<Class<? extends IClientToServerDTO>, ClientToServerType> type =
            new HashMap<>();

    static {
        for (final ClientToServerType t : ClientToServerType.values()) type.put(t.dto, t);
    }

    private final Class<? extends IClientToServerDTO> dto;

    ClientToServerType(final Class<? extends IClientToServerDTO> dto) {
        this.dto = dto;
    }

    public static <T extends IClientToServerDTO> ClientToServerType valueOf(
            final Class<T> dtoClass) {
        return Objects.requireNonNull(type.get(dtoClass), "DTO клиента не найдено");
    }

    public static ClientToServerType valueOf(final IClientToServerDTO dto) {
        return valueOf(dto.getClass());
    }

    public Class<? extends IClientToServerDTO> getDTO() {
        return dto;
    }
}
