package io.deeplay.qchess.clientserverconversation.dto.main;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.MainRequestType;

public class ServerToClientDTO {
    @SerializedName("type")
    public final MainRequestType mainRequestType;

    @SerializedName("request")
    public final String request;

    public ServerToClientDTO(final MainRequestType mainRequestType, final String request) {
        this.mainRequestType = mainRequestType;
        this.request = request;
    }
}
