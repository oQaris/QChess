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

    void closeGame(String reason);

    /**
     * Вызывается при отключении от сервера
     *
     * @param reason причина отключения
     */
    void disconnect(String reason);

    /** Изменяет цвет и перерисовывает доску (снизу теперь будет выбранный цвет) */
    void changeMyColorOnBoard(boolean color);
}
