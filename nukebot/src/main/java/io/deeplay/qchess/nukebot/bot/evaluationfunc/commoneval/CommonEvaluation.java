package io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import java.util.List;

/** Общая функция оценки для алгоритма поиска, вызывает функцию оценивания доски */
public interface CommonEvaluation {
    /**
     * @param isCheckToMe поставлен ли шах максимизирующему игроку
     * @param isCheckToEnemy поставлен ли шах минимизирующему игроку
     * @param allMoves возможно все ходы текущего игрока
     * @param isMyMove true, если сейчас ход максимизирующего игрока
     * @param depth текущая глубина
     * @return оценка доски для максимизирующего игрока
     */
    int getEvaluation(
            final boolean isCheckToMe,
            final boolean isCheckToEnemy,
            final List<Move> allMoves,
            final boolean isMyMove,
            final int alfa,
            final int beta,
            final int depth)
            throws ChessError;
}
