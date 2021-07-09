package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Bot extends Player {

    private static final Map<Class<?>, Integer> grades = preparedGrades();

    public Bot() {
        super();
    }

    private static Map<Class<?>, Integer> preparedGrades() {
        var res = new HashMap<Class<?>, Integer>();
        res.put(Pawn.class, 1);
        res.put(Knight.class, 3);
        res.put(Bishop.class, 3);
        res.put(Rook.class, 5);
        res.put(Queen.class, 9);
        res.put(King.class, 100);
        return res;
    }

    @Override
    public Move getNextMove() throws ChessError {
        var topMoves = new ArrayList<Move>();
        int maxGrade = 0;
        for (Move move : ms.getAllCorrectMoves(color)) {
            try {
                var fig = board.getFigure(move.getTo());

                var curGrade = fig != null ? grades.get(fig.getClass()) : 0;
                if (curGrade > maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                }
                if (curGrade >= maxGrade) {
                    topMoves.add(move);
                }
            } catch (ChessException e) {
                throw new ChessError("В боте возникло исключение", e);
            }
        }
        var move = topMoves.get(new Random().nextInt(topMoves.size()));
        if (move.getMoveType() == MoveType.TURN_INTO) {
            move.setTurnInto(new Queen(board, color, move.getTo()));
        }
        return move;
    }
}