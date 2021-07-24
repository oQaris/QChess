package io.deeplay.qchess.clientserverconversation.dto.main;

import com.google.gson.annotations.SerializedName;

public abstract class IClientToServerDTO {
    @SerializedName("sessionToken")
    public final String sessionToken;

    protected IClientToServerDTO(final String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
