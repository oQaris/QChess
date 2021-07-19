package io.deeplay.qchess.clientserverconversation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
