package io.deeplay.qchess.nnnbot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;

public class FigureCountEvaluation {

    /**
     * Эвристика защиты
     *
     * @param myColor цвет игрока, который укрепляет свою позицию
     */
    public static double defenseHeuristics(GameSettings gs, Color myColor) {
        return gs.board.getFigureCount(myColor);
    }

    /**
     * Эвристика атаки
     *
     * @param myColor цвет игрока, который атакует соперника
     */
    public static double attackHeuristics(GameSettings gs, Color myColor) {
        return -gs.board.getFigureCount(myColor.inverse());
    }

    /**
     * Эвристика атаки и защиты
     *
     * @param myColor цвет игрока, который укрепляет свою позицию и пытается атаковать противника
     */
    public static double attackDefenseHeuristics(GameSettings gs, Color myColor) {
        return defenseHeuristics(gs, myColor) + 0.5 * attackHeuristics(gs, myColor);
    }
}
