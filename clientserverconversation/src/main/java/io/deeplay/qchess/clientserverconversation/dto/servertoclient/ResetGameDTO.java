package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;

/** Уведомляет клиента, что игра окончено, нужно сменить сторону цвета */
public class ResetGameDTO extends IServerToClientDTO {

    public ResetGameDTO() {}
}
