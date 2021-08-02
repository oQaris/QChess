package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.dao.GameDAO;
import io.deeplay.qchess.client.dao.SessionDAO;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.view.gui.PlayerType;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ActionDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.EndGameDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.GameSettingsDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.nnnbot.bot.NNNBotFactory;
import java.util.List;

public class GameService {

    public static String setGameSettings(ServerToClientType type, String json)
            throws SerializationException {
        assert type.getDTO() == GameSettingsDTO.class;
        GameSettingsDTO dto =
                SerializationService.serverToClientDTORequest(json, GameSettingsDTO.class);

        initGame(dto.color == Color.WHITE);
        if (dto.botMove != null) {
            try {
                GameDAO.getGame().move(dto.botMove);
            } catch (ChessError chessError) {
                // TODO: Ошибка в игре
            }
        }
        ClientController.resetMyColorOnBoard(dto.color);
        return null;
    }

    public static String endGame(ServerToClientType type, String json)
            throws SerializationException {
        assert type.getDTO() == EndGameDTO.class;
        EndGameDTO dto = SerializationService.serverToClientDTORequest(json, EndGameDTO.class);

        ClientController.showMessage(dto.reason);
        return null;
    }

    public static String resetGame(ServerToClientType type, String json) {
        try {
            ClientController.sendFindGameRequest();
        } catch (ClientException e) {
            // Сервис работает при запущенном клиенте
        }
        return null;
    }

    public static void chooseEnemy(PlayerType playerType) {
        GameDAO.setEnemy(playerType);
    }

    public static void initGame(boolean color) {
        GameSettings gs = new GameSettings(BoardFilling.STANDARD);
        try {
            Player player1;
            Player player2;
            if (color) {
                player1 = getRemotePlayer(GameDAO.getMyType(), gs, Color.WHITE);
                player2 = getRemotePlayer(GameDAO.getEnemyType(), gs, Color.BLACK);
            } else {
                player1 = getRemotePlayer(GameDAO.getEnemyType(), gs, Color.WHITE);
                player2 = getRemotePlayer(GameDAO.getMyType(), gs, Color.BLACK);
            }
            Selfplay game = new Selfplay(gs, player1, player2);
            GameDAO.newGame(gs, game, color ? Color.WHITE : Color.BLACK);
        } catch (ChessError ignore) {
            // Стандартная расстановка доски верна всегда
        }
    }

    private static RemotePlayer getRemotePlayer(PlayerType pt, GameSettings gs, Color color) {
        return switch (pt) {
            case USER -> new RemotePlayer(gs, color, "user");
            case EASYBOT -> new RandomBot(gs, color);
            case MEDIUMBOT -> new AttackBot(gs, color);
            // TODO: использовать своего бота
            case HARDBOT -> NNNBotFactory.getNNNBot(gs, color);
        };
    }

    public static String startGame(ServerToClientType type, String json) {
        GameDAO.startGame();
        return null;
    }

    public static String action(ServerToClientType type, String json)
            throws SerializationException {
        assert type.getDTO() == ActionDTO.class;
        ActionDTO dto = SerializationService.serverToClientDTORequest(json, ActionDTO.class);

        makeMove(
                dto.move.getFrom().row,
                dto.move.getFrom().column,
                dto.move.getTo().row,
                dto.move.getTo().column,
                dto.move.getTurnInto());

        ClientController.drawBoard();
        return null;
    }

    /**
     * Делает ход
     *
     * @return сделанный ход или null TODO: null ? why ????????????
     */
    public static Move makeMove(
            int rowFrom, int columnFrom, int rowTo, int columnTo, FigureType figureType) {
        Cell from = new Cell(columnFrom, rowFrom);
        Cell to = new Cell(columnTo, rowTo);
        List<Move> set;
        try {
            set = GameDAO.getGameSettings().moveSystem.getAllCorrectMoves(from);
        } catch (ChessError e) {
            e.printStackTrace();
            return null;
        }
        for (Move move : set) {
            if (to.equals(move.getTo())) {
                try {
                    move.setTurnInto(figureType);
                    GameDAO.getGame().move(move);
                } catch (ChessError e) {
                    e.printStackTrace();
                    return null;
                }
                // TODO: удалить
                System.err.println(GameDAO.getGameSettings().board.toString());
                return move;
            }
        }

        return null;
    }

    /**
     * Делает ход ботом. Гарантируется, что клиент выбрал бота при выборе КЕМ играть
     */
    public static void botMove() {
        try {
            Move move = GameDAO.getGame().getCurrentPlayerToMove().getNextMove();
            GameDAO.getGame().move(move);
            ClientController.drawBoard();
            sendMove(move);
        } catch (ChessError | ClientException ignore) {
            // В боте нет ошибок (?)
        }
    }

    public static void sendMove(Move move) throws ClientException {
        ClientController.sendIfNotNull(
                SerializationService.makeMainDTOJsonToServer(
                        new io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ActionDTO(
                                SessionDAO.getSessionToken(), move)));
    }
}
