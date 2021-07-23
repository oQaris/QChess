package io.deeplay.qchess.clientserverconversation.dto.servertoclient;

import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;

/** Уведомление ПЕРВОГО игрока о начале игры. Второй игрок узнает при получении хода от первого */
public class StartGameDTO extends IServerToClientDTO {}
