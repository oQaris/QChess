package io.deeplay.qchess.nnnbot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;

public class FigureCountEvaluation {

    /** Оценка с нулевой суммой для укрепления защиты */
    public static double defenseWithZeroSum(GameSettings gs, Color color) {
        return gs.board.getFigureCount(color);
    }

    /** Оценка с нулевой суммой для атаки */
    public static double attackWithZeroSum(GameSettings gs, Color color) {
        return -gs.board.getFigureCount(color.inverse());
    }

    /** Оценка с нулевой суммой для атаки и защиты */
    public static double attackDefenseZeroSum(GameSettings gs, Color color) {
        return gs.board.getFigureCount(color) - gs.board.getFigureCount(color.inverse());
    }
}
