package io.deeplay.qchess.nukebot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;

public class FigureCountEvaluation {

    /**
     * Эвристика защиты
     *
     * @param myColor цвет игрока, который укрепляет свою позицию
     */
    public static int defenseHeuristics(GameSettings gs, Color myColor) {
        return gs.board.getFigureCount(myColor);
    }

    /**
     * Эвристика атаки
     *
     * @param myColor цвет игрока, который атакует соперника
     */
    public static int attackHeuristics(GameSettings gs, Color myColor) {
        return -defenseHeuristics(gs, myColor.inverse());
    }

    /**
     * Эвристика атаки и защиты
     *
     * @param myColor цвет игрока, который укрепляет свою позицию и пытается атаковать противника
     */
    public static int attackDefenseHeuristics(GameSettings gs, Color myColor) {
        return 5 * defenseHeuristics(gs, myColor) + 4 * attackHeuristics(gs, myColor);
    }
}
