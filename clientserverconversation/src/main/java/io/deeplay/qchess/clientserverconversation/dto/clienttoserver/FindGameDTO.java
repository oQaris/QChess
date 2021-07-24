package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IClientToServerDTO;
import io.deeplay.qchess.game.player.PlayerType;

/** Поиск игры по предпочитаемым настройкам */
public class FindGameDTO extends IClientToServerDTO {
    @SerializedName("gameEnemyType")
    public final PlayerType enemyType;

    public FindGameDTO(final String sessionToken, final PlayerType enemyType) {
        super(sessionToken);
        this.enemyType = enemyType;
    }
}
