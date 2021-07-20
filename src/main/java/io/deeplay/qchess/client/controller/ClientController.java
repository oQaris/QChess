package io.deeplay.qchess.client.controller;

import io.deeplay.qchess.client.IClient;
import io.deeplay.qchess.client.LocalClient;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.service.GameGUIAdapterService;
import io.deeplay.qchess.client.service.GameService;
import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.gui.ViewCell;
import io.deeplay.qchess.client.view.model.ViewFigure;
import io.deeplay.qchess.client.view.model.ViewFigureType;
import io.deeplay.qchess.clientserverconversation.dto.GetRequestType;
import io.deeplay.qchess.clientserverconversation.dto.other.GetRequestDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.Figure;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class ClientController {
    private static final IClient client = LocalClient.getInstance();
    public static boolean repaint = false;
    private static IClientView view;

    /** @return окружение клиента */
    public static Optional<IClientView> getView() {
        return Optional.ofNullable(view);
    }

    /**
     * Устанавливает окружение клиента
     *
     * <p>Необходимо использовать рабочее окружение перед использованием клиента, иначе клиент может
     * работать некорректно либо вообще не работать. Если окружение равно null, оно будет отключено
     * и не будет использоваться клиентом. По умолчанию окружение стоит null
     *
     * @param view окружение клиента
     */
    public static void setView(IClientView view) {
        ClientController.view = view;
    }

    /**
     * Подключается к серверу
     *
     * @throws ClientException если клиент уже подключен к серверу или возникла ошибка при
     *     подключении
     */
    public static void connect(String ip, int port) throws ClientException {
        client.connect(ip, port);
    }

    /**
     * Отключается от сервера
     *
     * @throws ClientException если клиент не подключен к серверу
     */
    public static void disconnect() throws ClientException {
        client.disconnect();
    }

    /** @return true, если клиент подключен к серверу, false иначе */
    public static boolean isConnected() {
        return client.isConnected();
    }

    /** @return порт сервера, к которому подключен клиент */
    public static int getPort() {
        return client.getPort();
    }

    /**
     * Устанавливает порт сервера, к которому будет подключен клиент
     *
     * @throws ClientException если клиент уже подключен к серверу
     */
    public static void setPort(int port) throws ClientException {
        client.setPort(port);
    }

    /** @return IP сервера, к которому подключен клиент */
    public static String getIp() {
        return client.getIp();
    }

    /**
     * Устанавливает IP сервера, к которому будет подключен клиент
     *
     * @throws ClientException если клиент уже подключен к серверу
     */
    public static void setIp(String ip) throws ClientException {
        client.setIp(ip);
    }

    /**
     * Эта операция блокирует поток, пока не будет получен цвет или не возникнет исключение
     *
     * @return true, если цвет игрока белый
     * @throws ClientException если клиент не подключен к серверу или во время ожидания соединение
     *     было разорвано
     */
    public static boolean waitForColor() throws ClientException {
        GetRequestDTO dto = client.waitForResponse(GetRequestType.GET_COLOR);
        Color color = null;
        try {
            color = SerializationService.deserialize(dto.request, Color.class);
        } catch (IOException ignore) {
        }
        return color == Color.WHITE;
    }

    /**
     * Выполняет команду клиента
     *
     * @throws ClientException если при выполнении команды возникла ошибка
     */
    public static void executeCommand(String command) throws ClientException {
        client.executeCommand(command);
    }

    /**
     * Отправляет серверу строку, если она не null
     *
     * @throws ClientException если клиент не подключен к серверу
     */
    public static void sendIfNotNull(String json) throws ClientException {
        client.sendIfNotNull(json);
    }

    // TODO: добавить javadoc
    public static Set<ViewCell> getAllMoves(int row, int column) {
        return GameGUIAdapterService.getAllMoves(row, column);
    }

    // TODO: добавить javadoc
    public static boolean checkFigure(int row, int column) {
        return GameGUIAdapterService.checkFigure(row, column);
    }

    // TODO: добавить javadoc
    public static boolean checkFigure(int row, int column, boolean isWhite) {
        return GameGUIAdapterService.checkFigure(row, column, isWhite);
    }

    // TODO: добавить javadoc
    public static ViewFigure getFigure(int row, int column) {
        return GameGUIAdapterService.getFigure(row, column);
    }

    // TODO: добавить javadoc
    public static boolean makeMove(int rowFrom, int columnFrom, int rowTo, int columnTo) {
        Move move = GameGUIAdapterService.makeMove(rowFrom, columnFrom, rowTo, columnTo);
        GameService.sendMove(move);
        return move != null;
    }

    // TODO: добавить javadoc
    public static boolean isWhiteStep() {
        return GameGUIAdapterService.isWhiteStep();
    }

    public static void drawBoard() {
        for (int column = 0; column < 8; ++column) {
            for (int row = 0; row < 8; ++row) {
                Figure f = GameGUIAdapterService.getBoard().getFigureUgly(new Cell(column, row));
                view.getBoard()
                        .setFigure(
                                column,
                                row,
                                new ViewFigure(
                                        f.getColor().name(),
                                        ViewFigureType.valueOf(f.getType().name())));
            }
        }
    }
}
