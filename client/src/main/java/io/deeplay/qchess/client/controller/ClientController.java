package io.deeplay.qchess.client.controller;

import io.deeplay.qchess.client.IClient;
import io.deeplay.qchess.client.LocalClient;
import io.deeplay.qchess.client.dao.GameDAO;
import io.deeplay.qchess.client.dao.SessionDAO;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.service.GameGUIAdapterService;
import io.deeplay.qchess.client.service.GameService;
import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.gui.EnemyType;
import io.deeplay.qchess.client.view.gui.ViewCell;
import io.deeplay.qchess.client.view.model.ViewFigure;
import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.FindGameDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.AcceptConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.GameSettingsDTO;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.PlayerType;

import java.util.Set;

public class ClientController {
    private static final IClient client = LocalClient.getInstance();
    private static IClientView view;

    private ClientController() {}

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

    /** Отправляет сообщение View, если view и message не null */
    public static void print(String message) {
        if (view != null && message != null) view.print(message);
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

    // TODO: javadoc
    public static void waitForAcceptConnection() throws ClientException {
        client.waitForResponse(new ConnectionDTO(null, true), AcceptConnectionDTO.class);
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
        if (move != null) {
            if (move.getMoveType() == MoveType.TURN_INTO
                    || move.getMoveType() == MoveType.TURN_INTO_ATTACK) {
                return 2;
            } else if (move.getMoveType() == MoveType.LONG_CASTLING
                    || move.getMoveType() == MoveType.SHORT_CASTLING) {
                return 3;
            } else if (move.getMoveType() == MoveType.EN_PASSANT) {
                return 4;
            }
            return 1;
        }
        return 0;
    }

    /** @throws ClientException если клиент не подключен к серверу */
    public static void makeMove(
            int rowFrom, int columnFrom, int rowTo, int columnTo, Object turnFigure)
            throws ClientException {
        Move move =
                GameService.makeMove(
                        rowFrom, columnFrom, rowTo, columnTo, getFigureType(turnFigure));
        view.drawBoard();
        GameService.sendMove(move);
        GameService.checkEndGame();
    }

    // TODO: добавить javadoc

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

    /** @return true, если сейчас ход клиента */
    public static boolean isMyStep() {
        return GameDAO.isGameStarted()
                && GameDAO.getGame().getCurrentPlayerToMove().getColor() == GameDAO.getMyColor();
    }

    public static void drawBoard() {
        view.drawBoard();
    }

    public static void chooseEnemy(EnemyType enemyType) {
        GameService.chooseEnemy(enemyType);
    }

    public static void closeGame(String reason) {
        view.closeGame(reason);
    }

    /**
     * Эта операция блокирует поток, пока не будет получен цвет или не возникнет исключение
     *
     * @return true, если цвет игрока белый
     * @throws ClientException если клиент не подключен к серверу или во время ожидания соединение
     *     было разорвано
     */
    public static boolean resetGame() throws ClientException {
        boolean color = waitForGameSettings();
        GameService.initGame(color);
        // TODO: менять цвет у View и перерисовывать доску
        return color;
    }

    /**
     * Эта операция блокирует поток, пока не будет получен цвет или не возникнет исключение
     *
     * @return true, если цвет игрока белый
     * @throws ClientException если клиент не подключен к серверу или во время ожидания соединение
     *     было разорвано
     */
    private static boolean waitForGameSettings() throws ClientException {
        GameSettingsDTO dto =
                client.waitForResponse(
                        new FindGameDTO(
                                SessionDAO.getSessionToken(),
                                switch (GameDAO.getEnemyType()) {
                                    case USER -> PlayerType.REMOTE_PLAYER;
                                    case EASYBOT -> PlayerType.RANDOM_BOT;
                                    case MEDIUMBOT -> PlayerType.ATTACK_BOT;
                                    case HARDBOT -> PlayerType.MINIMAX_BOT;
                                }, 2),
                        GameSettingsDTO.class);
        return dto.color == Color.WHITE;
    }
}
