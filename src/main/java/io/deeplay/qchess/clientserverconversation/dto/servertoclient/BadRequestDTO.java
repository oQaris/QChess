package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;

/** Для некорректных запросов */
public class BadRequestDTO extends ServerToClientDTO {

    public BadRequestDTO() {
        super(ServerToClientType.valueOf(BadRequestDTO.class));
    }
}
