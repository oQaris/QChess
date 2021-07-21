package io.deeplay.qchess.clientserverconversation.dto.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.deeplay.qchess.clientserverconversation.dto.GetRequestType;

public class GetRequestDTO {
    @JsonProperty("type")
    public GetRequestType requestType;

    @JsonProperty("request")
    public String request;

    public GetRequestDTO(final GetRequestType requestType, final String request) {
        this.requestType = requestType;
        this.request = request;
    }

    public GetRequestDTO() {}
}
