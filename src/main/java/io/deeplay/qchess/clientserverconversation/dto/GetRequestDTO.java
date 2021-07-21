package io.deeplay.qchess.clientserverconversation.dto;

import com.google.gson.annotations.SerializedName;

public class GetRequestDTO {
    @SerializedName("type")
    public final GetRequestType requestType;

    @SerializedName("request")
    public final String request;

    public GetRequestDTO(final GetRequestType requestType, final String request) {
        this.requestType = requestType;
        this.request = request;
    }
}
