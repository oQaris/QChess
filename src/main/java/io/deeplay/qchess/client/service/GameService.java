package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.dao.SessionDAO;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ActionDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.model.Move;

public class GameService {

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
        return null;
    }

    public static void sendMove(Move move) throws ClientException {
        ClientController.sendIfNotNull(
                SerializationService.makeMainDTOJsonToServer(
                        new io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ActionDTO(
                                SessionDAO.getSessionToken(), move)));
    }
}
