package io.deeplay.qchess.clientserverconversation.dto;

public enum MainRequestType {
    INCORRECT_REQUEST,
    // Для запросов без данных
    GET,
    CHAT_MESSAGE,
    // TODO: rename to ACTION when Action is done
    MOVE
}
