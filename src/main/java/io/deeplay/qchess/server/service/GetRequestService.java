package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.GetGameSettingsDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.GameSettingsDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.server.dao.GameDAO;

public class GetRequestService {

    public static String getGameSettings(ClientToServerType type, String json, int clientID)
            throws SerializationException {
        assert type.getDTO() == GetGameSettingsDTO.class;
        GetGameSettingsDTO dto =
                SerializationService.clientToServerDTORequest(json, GetGameSettingsDTO.class);

        return SerializationService.makeMainDTOJsonToClient(
                new GameSettingsDTO(GameDAO.getColor(dto.sessionToken)));
    }
}
