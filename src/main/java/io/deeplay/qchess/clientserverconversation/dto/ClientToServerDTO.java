package io.deeplay.qchess.clientserverconversation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
