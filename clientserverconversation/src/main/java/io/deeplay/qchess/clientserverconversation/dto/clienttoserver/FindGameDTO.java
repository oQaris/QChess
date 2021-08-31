package io.deeplay.qchess.clientserverconversation.dto.clienttoserver;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.clientserverconversation.dto.main.IClientToServerDTO;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.PlayerType;

/** Поиск игры по предпочитаемым настройкам */
public class FindGameDTO extends IClientToServerDTO {
    @SerializedName("gameEnemyType")
    public final PlayerType enemyType;

    @SerializedName("gameCount")
    public final int gameCount;

    @SerializedName("myPreferColor")
    public final Color myPreferColor;

    public FindGameDTO(
            final String sessionToken,
            final PlayerType enemyType,
            final int gameCount,
            final Color myPreferColor) {
        super(sessionToken);
        this.enemyType = enemyType;
        this.gameCount = gameCount;
        this.myPreferColor = myPreferColor;
    }
}
