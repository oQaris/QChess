package io.deeplay.qchess.nukebot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;

@FunctionalInterface
public interface EvaluationFunc {

    int MAX_ESTIMATION = Integer.MAX_VALUE;
    int MIN_ESTIMATION = -Integer.MAX_VALUE;

    int MG_PAWN_COST = 82;
    int MG_KNIGHT_COST = 337;
    int MG_BISHOP_COST = 365;
    int MG_ROOK_COST = 477;
    int MG_QUEEN_COST = 1025;
    int EG_PAWN_COST = 94;
    int EG_KNIGHT_COST = 281;
    int EG_BISHOP_COST = 297;
    int EG_ROOK_COST = 512;
    int EG_QUEEN_COST = 936;
    int KING_COST = 12000;

    int QUARTER_PAWN_COST = MG_PAWN_COST / 4;
    int DOUBLE_QUEEN_MINUS_PAWN_COST = 2 * (MG_QUEEN_COST - MG_PAWN_COST);

    /** @return значение, которое необходимо увеличивать игроку с цветом color */
    int getHeuristics(GameSettings gs, Color myColor) throws ChessError;
}
