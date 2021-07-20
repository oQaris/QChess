package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.other.GetRequestDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import java.io.IOException;

public class GetRequestService {

    public static String process(String getRequest, int clientID) {
        try {
            GetRequestDTO dto = SerializationService.deserialize(getRequest, GetRequestDTO.class);
            String response =
                    switch (dto.requestType) {
                        case GET_COLOR -> SerializationService.serialize(
                                GameService.getPlayerColor(clientID));
                    };
            return SerializationService.serialize(new GetRequestDTO(dto.requestType, response));
        } catch (IOException e) {
            return null;
        }
    }
}
