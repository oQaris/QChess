package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;

/** Запрос на подключение к серверу */
public class GetConnectionDTO extends ClientToServerDTO {

    public GetConnectionDTO(String sessionToken) {
        super(ClientToServerType.valueOf(GetConnectionDTO.class), sessionToken);
    }
}
