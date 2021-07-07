package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.figures.*;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Bot implements IPlayer {

    private final Board board;
    private final boolean color;

    private static Map<Class<?>, Integer> grades = preparedGrades();

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

    public Bot(Board board, boolean isWhite) {
        this.board = board;
        this.color = isWhite;
    }

    @Override
    public Move getNextMove() {
        var topMoves = new ArrayList<Move>();
        int maxGrade = 0;
        for (Move move : board.getAllMoves(color)) {
            var fig = board.getFigure(move.getTo());
            if (fig == null) {
                continue;
            }
            var curGrade = grades.get(fig.getClass());
            if (curGrade > maxGrade) {
                maxGrade = curGrade;
                topMoves.clear();
            }
            if (curGrade >= maxGrade) {
                topMoves.add(move);
            }
        }
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }
}
