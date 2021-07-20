package io.deeplay.qchess.client.view;

import io.deeplay.qchess.client.view.model.ViewBoard;
import java.io.Closeable;

/** Управляет окружением клиента */
public interface IClientView extends Closeable {

    /** Запускает окружение */
    void startView();

    /** Выводит сообщение */
    void print(String message);

    /** Перерисовывает доску */
    void drawBoard();

    /** @return доска */
    ViewBoard getBoard();

    void endGame();
}
