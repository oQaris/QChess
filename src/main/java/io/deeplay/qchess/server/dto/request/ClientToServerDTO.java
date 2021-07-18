package io.deeplay.qchess.server.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.deeplay.qchess.server.dto.RequestType;

public class ClientToServerDTO {
    @JsonProperty("type")
    public RequestType requestType;

    @JsonProperty("request")
    public String request;

    public ClientToServerDTO(RequestType requestType, String request) {
        this.requestType = requestType;
        this.request = request;
    }

    public ClientToServerDTO() {}
}
