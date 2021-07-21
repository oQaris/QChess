package io.deeplay.qchess.client.controller;

import io.deeplay.qchess.client.IClient;
import io.deeplay.qchess.client.LocalClient;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.service.GameGUIAdapterService;
import io.deeplay.qchess.client.service.GameService;
import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.gui.EndGame;
import io.deeplay.qchess.client.view.gui.ViewCell;
import io.deeplay.qchess.client.view.model.ViewFigure;
import io.deeplay.qchess.clientserverconversation.dto.GetRequestType;
import io.deeplay.qchess.clientserverconversation.dto.other.GetRequestDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class ClientController {
    private static final IClient client = LocalClient.getInstance();
    private static IClientView view;

    /**
     * @deprecated Не безопасное использование View. Если необходимо использовать логику View, не
     *     считая простого вывода, лучше создать здесь метод и использовать его
     * @return окружение клиента
     */
    @Deprecated
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
    public static void disconnect(String reason) throws ClientException {
        client.disconnect();
        view.disconnect(reason);
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
    public static int tryMakeMove(int rowFrom, int columnFrom, int rowTo, int columnTo) {
        Move move = GameGUIAdapterService.tryMakeMove(rowFrom, columnFrom, rowTo, columnTo);
        if (move != null
                && (move.getMoveType() == MoveType.TURN_INTO
                        || move.getMoveType() == MoveType.TURN_INTO_ATTACK)) {
            return 2;
        } else if (move != null) {
            return 1;
        }
        return 0;
    }

    // TODO: добавить javadoc
    public static void makeMove(
            int rowFrom, int columnFrom, int rowTo, int columnTo, Object turnFigure) {
        Move move =
                GameGUIAdapterService.makeMove(
                        rowFrom, columnFrom, rowTo, columnTo, getFigureType(turnFigure));
        GameService.sendMove(move);
    }

    public static void checkEndGame() {
        getView().ifPresent(IClientView::endGame);
    }

    // TODO: добавить javadoc
    public static boolean isMyStep() {
        return GameGUIAdapterService.isMyStep();
    }

    // TODO: добавить javadoc
    public static EndGame getEndGame(boolean color) {
        return new EndGame(GameGUIAdapterService.getStatus(color), GameGUIAdapterService.getEnd(color));
    }

    public static void drawBoard() {
        view.drawBoard();
    }

    private static FigureType getFigureType(Object figure) {
        String strFigure = (String) figure;
        if ("Ферзь".equals(strFigure)) {
            return FigureType.QUEEN;
        } else if ("Ладья".equals(strFigure)) {
            return FigureType.ROOK;
        } else if ("Конь".equals(strFigure)) {
            return FigureType.KNIGHT;
        } else if ("Слон".equals(strFigure)) {
            return FigureType.BISHOP;
        }
        return null;
    }
}
