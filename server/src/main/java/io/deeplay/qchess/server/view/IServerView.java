package io.deeplay.qchess.server.view;

import java.io.Closeable;

/** Управляет окружением сервера */
public interface IServerView extends Closeable {

    /** Запускает окружение */
    void startView();

    /** Выводит сообщение */
    void print(String message);
}
