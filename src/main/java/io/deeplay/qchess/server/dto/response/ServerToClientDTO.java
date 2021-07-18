package io.deeplay.qchess.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.deeplay.qchess.server.dto.RequestType;

public class ServerToClientDTO {
    @JsonProperty("type")
    public RequestType requestType;

    @JsonProperty("request")
    public String request;

    public ServerToClientDTO(RequestType requestType, String request) {
        this.requestType = requestType;
        this.request = request;
    }

    public ServerToClientDTO() {}
}
