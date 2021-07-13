package io.deeplay.qchess.game.player;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.BOT_ERROR;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AttackBot extends Bot {

    public AttackBot(GameSettings roomSettings, Color color) {
        super(roomSettings, color);
    }

    @Override
    public Move getNextMove() throws ChessError {
        List<Move> topMoves = new ArrayList<>();
        int maxGrade = 0;
        for (Move move : ms.getAllCorrectMoves(color))
            try {
                Figure fig = board.getFigure(move.getTo());

                int curGrade = fig != null ? Bot.grades.get(fig.getType()) : 0;
                if (curGrade > maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                }
                if (curGrade >= maxGrade) topMoves.add(move);
            } catch (ChessException e) {
                throw new ChessError(BOT_ERROR, e);
            }
        Move move = topMoves.get(new Random().nextInt(topMoves.size()));
        turnIntoInQueen(move);
        return move;
    }
}
