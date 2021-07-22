package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IClientToServerDTO;
import io.deeplay.qchess.game.player.PlayerType;

/** Получение данных игры */
public class GetGameSettingsDTO extends IClientToServerDTO {
    @SerializedName("gameEnemyType")
    public final PlayerType enemyType;

    public GetGameSettingsDTO(final String sessionToken, final PlayerType enemyType) {
        super(sessionToken);
        this.enemyType = enemyType;
    }
}
