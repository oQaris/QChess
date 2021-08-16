package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AttackBot extends RemotePlayer {
    private static final Map<FigureType, Integer> grades = new EnumMap<>(FigureType.class);

    static {
        grades.put(FigureType.PAWN, 1);
        grades.put(FigureType.KNIGHT, 3);
        grades.put(FigureType.BISHOP, 3);
        grades.put(FigureType.ROOK, 5);
        grades.put(FigureType.QUEEN, 9);
        grades.put(FigureType.KING, 100);
    }

    public AttackBot(final GameSettings roomSettings, final Color color) {
        super(roomSettings, color, "attack-bot-" + UUID.randomUUID(), "Атакующий_Бот");
    }

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> topMoves = new ArrayList<>();
        int maxGrade = 0;
        for (final Move move : ms.getAllPreparedMoves(color)) {
            final Figure attackedFigure = board.getFigureUgly(move.getTo());
            final int currentGrade =
                    attackedFigure == null ? 0 : grades.get(attackedFigure.figureType);
            if (currentGrade > maxGrade) {
                maxGrade = currentGrade;
                if (!topMoves.isEmpty()) topMoves.clear();
            }
            if (currentGrade == maxGrade) topMoves.add(move);
        }
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    @Override
    public PlayerType getPlayerType() {
        return PlayerType.ATTACK_BOT;
    }
}
