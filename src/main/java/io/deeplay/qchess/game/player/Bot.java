package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Queen;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.*;

public class Bot extends Player {

    private static final Map<TypeFigure, Integer> grades = preparedGrades();

    public Bot(GameSettings roomSettings, Color color) {
        super(roomSettings, color);
    }

    private static Map<TypeFigure, Integer> preparedGrades() {
        Map<TypeFigure, Integer> res = new HashMap<>();
        res.put(TypeFigure.PAWN, 1);
        res.put(TypeFigure.KNIGHT, 3);
        res.put(TypeFigure.BISHOP, 3);
        res.put(TypeFigure.ROOK, 5);
        res.put(TypeFigure.QUEEN, 9);
        res.put(TypeFigure.KING, 100);
        return res;
    }

    @Override
    public Move getNextMove() throws ChessError {
        List<Move> topMoves = new ArrayList<>();
        int maxGrade = 0;
        for (Move move : ms.getAllCorrectMoves(color)) {
            try {
                Figure fig = board.getFigure(move.getTo());

                int curGrade = fig != null ? grades.get(fig.getType()) : 0;
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
        Move move = topMoves.get(new Random().nextInt(topMoves.size()));
        if (move.getMoveType() == MoveType.TURN_INTO) {
            move.setTurnInto(new Queen(color, move.getTo()));
        }
        return move;
    }
}