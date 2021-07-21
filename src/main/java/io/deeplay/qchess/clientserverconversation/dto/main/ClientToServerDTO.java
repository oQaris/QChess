package io.deeplay.qchess.clientserverconversation.dto.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.deeplay.qchess.clientserverconversation.dto.MainRequestType;

public class ClientToServerDTO {
    @JsonProperty("type")
    public MainRequestType mainRequestType;

    @JsonProperty("request")
    public String request;

    public ClientToServerDTO(final MainRequestType mainRequestType, final String request) {
        this.mainRequestType = mainRequestType;
        this.request = request;
    }

    public ClientToServerDTO() {}
}
