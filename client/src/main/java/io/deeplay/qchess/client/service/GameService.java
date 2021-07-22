package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.dao.GameDAO;
import io.deeplay.qchess.client.dao.SessionDAO;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.view.gui.EnemyType;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ActionDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.game.player.MinimaxBot;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.game.player.RemotePlayer;

public class GameService {

    public static void chooseEnemy(EnemyType enemyType) {
        GameDAO.setEnemy(enemyType);
    }

    public static void initGame(boolean color) {
        GameSettings gs = new GameSettings(BoardFilling.STANDARD);
        Player enemy =
                switch (GameDAO.getEnemyType()) {
                    case USER -> new RemotePlayer(gs, color ? Color.WHITE : Color.BLACK, "enemy");
                    case EASYBOT -> new RandomBot(gs, color ? Color.WHITE : Color.BLACK);
                    case MEDIUMBOT -> new AttackBot(gs, color ? Color.WHITE : Color.BLACK);
                    case HARDBOT -> new MinimaxBot(gs, color ? Color.WHITE : Color.BLACK, 3);
                };
        try {
            Selfplay game =
                    new Selfplay(
                            gs,
                            new RemotePlayer(gs, color ? Color.BLACK : Color.WHITE, "me"),
                            enemy);
            GameDAO.newGame(gs, game);
        } catch (ChessError ignore) {
            // Стандартная расстановка доски верна всегда
        }
    }

    public static String startGame(ServerToClientType type, String json) {
        GameDAO.changeIsMyStep();
        return null;
    }

    public static String action(ServerToClientType type, String json)
            throws SerializationException {
        assert type.getDTO() == ActionDTO.class;
        ActionDTO dto = SerializationService.serverToClientDTORequest(json, ActionDTO.class);

        GameGUIAdapterService.makeMove(
                dto.move.getFrom().getRow(),
                dto.move.getFrom().getColumn(),
                dto.move.getTo().getRow(),
                dto.move.getTo().getColumn(),
                dto.move.getTurnInto());

        ClientController.drawBoard();
        ClientController.endGameInverse();
        return null;
    }

    public static void sendMove(Move move) throws ClientException {
        ClientController.sendIfNotNull(
                SerializationService.makeMainDTOJsonToServer(
                        new io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ActionDTO(
                                SessionDAO.getSessionToken(), move)));
    }
}
