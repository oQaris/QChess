package io.deeplay.qchess.client.service;

import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.MOVE;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.handlers.TrafficRequestHandler;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.model.Move;
import java.io.IOException;

public class GameService {

    public static String action(String json) {
        try {
            Move move = SerializationService.deserialize(json, Move.class);
            GameGUIAdapterService.makeMove(
                    move.getFrom().getRow(),
                    move.getFrom().getColumn(),
                    move.getTo().getRow(),
                    move.getTo().getColumn());
            //            GameGUIAdapterService.changeIsWhiteStep();
            ClientController.drawBoard();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendMove(Move move) {
        try {
            ClientController.sendIfNotNull(
                    TrafficRequestHandler.convertToClientToServerDTO(
                            MOVE, SerializationService.serialize(move)));
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
