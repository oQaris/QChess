package io.deeplay.qchess.client.view;

import java.io.Closeable;

/** Управляет окружением клиента */
public interface IClientView extends Closeable {

    /** Запускает окружение */
    void startView();

    /** Выводит сообщение */
    void print(String message);
}
