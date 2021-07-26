package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.dao.GameDAO;
import io.deeplay.qchess.client.dao.SessionDAO;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.view.gui.EnemyType;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ActionDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.EndGameDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.game.player.MinimaxBot;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.game.player.RemotePlayer;
import java.util.List;

public class GameService {

    public static String endGame(ServerToClientType type, String json)
            throws SerializationException {
        assert type.getDTO() == EndGameDTO.class;
        EndGameDTO dto = SerializationService.serverToClientDTORequest(json, EndGameDTO.class);
        ClientController.closeGame(dto.reason);
        return null;
    }

    public static String resetGame(ServerToClientType type, String json) {
        try {
            ClientController.resetGame();
        } catch (ClientException e) {
            // Сервис работает при запущенном клиенте
        }
        return null;
    }

    public static void chooseEnemy(EnemyType enemyType) {
        GameDAO.setEnemy(enemyType);
    }

    public static void initGame(boolean color) {
        GameSettings gs = new GameSettings(BoardFilling.STANDARD);
        Player enemy =
                switch (GameDAO.getEnemyType()) {
                    case USER -> new RemotePlayer(gs, color ? Color.BLACK : Color.WHITE, "enemy");
                    case EASYBOT -> new RandomBot(gs, color ? Color.BLACK : Color.WHITE);
                    case MEDIUMBOT -> new AttackBot(gs, color ? Color.BLACK : Color.WHITE);
                    case HARDBOT -> new MinimaxBot(gs, color ? Color.BLACK : Color.WHITE, 1);
                };
        try {
            Player player1;
            Player player2;
            if (color) {
                player1 = new RemotePlayer(gs, Color.WHITE, "me");
                player2 = enemy;
            } else {
                player1 = enemy;
                player2 = new RemotePlayer(gs, Color.BLACK, "me");
            }
            Selfplay game = new Selfplay(gs, player1, player2);
            GameDAO.newGame(gs, game, color ? Color.WHITE : Color.BLACK);
        } catch (ChessError ignore) {
            // Стандартная расстановка доски верна всегда
        }
    }

    /** Возвращает статус конца игры для следующего игрока или null, если игра еще не окончена */
    public static String getEndGameStatus() {
        Color color = GameDAO.getGame().getCurrentPlayerToMove().getColor();
        if (GameDAO.getGameSettings().endGameDetector.isDraw()) {
            if (GameDAO.getGameSettings().endGameDetector.isDrawWithPeaceMoves()) {
                return String.format(
                        "Ничья: %d ходов без взятия и хода пешки",
                        EndGameDetector.END_PEACE_MOVE_COUNT);
            } else if (GameDAO.getGameSettings().endGameDetector.isDrawWithRepetitions()) {
                return String.format(
                        "Ничья: %d повторений позиций доски",
                        EndGameDetector.END_REPETITIONS_COUNT);
            } else if (GameDAO.getGameSettings()
                    .endGameDetector
                    .isDrawWithNotEnoughMaterialForCheckmate()) {
                return "Ничья: недостаточно фигур, чтобы поставить мат";
            }
        } else if (GameDAO.getGameSettings().endGameDetector.isCheckmate(color)) {
            return "Мат " + (color == Color.BLACK ? "черным" : "белым");
        } else if (GameDAO.getGameSettings().endGameDetector.isStalemate(color)) {
            return "Пат " + (color == Color.BLACK ? "черным" : "белым");
        }
        return null;
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
                dto.move.getFrom().getRow(),
                dto.move.getFrom().getColumn(),
                dto.move.getTo().getRow(),
                dto.move.getTo().getColumn(),
                dto.move.getTurnInto());

        ClientController.drawBoard();
        checkEndGame();
        return null;
    }

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

    /** Проверяет и заканчивает игру, если игрок, чей ход текущий, проиграл */
    public static void checkEndGame() {
        if (GameService.getEndGameStatus() != null) {
            // TODO: показать сообщение в Table
            ClientController.closeGame(GameService.getEndGameStatus());
        }
    }

    public static void sendMove(Move move) throws ClientException {
        ClientController.sendIfNotNull(
                SerializationService.makeMainDTOJsonToServer(
                        new io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ActionDTO(
                                SessionDAO.getSessionToken(), move)));
    }
}
