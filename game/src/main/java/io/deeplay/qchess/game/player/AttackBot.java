package io.deeplay.qchess.game.player;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.BOT_ERROR;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttackBot extends RemotePlayer {
    private static final Logger logger = LoggerFactory.getLogger(AttackBot.class);
    private static final Map<FigureType, Integer> grades = preparedGrades();

    public AttackBot(GameSettings roomSettings, Color color) {
        super(roomSettings, color, "attack-bot-" + UUID.randomUUID());
    }

    private static Map<FigureType, Integer> preparedGrades() {
        Map<FigureType, Integer> res = new EnumMap<>(FigureType.class);
        res.put(FigureType.PAWN, 1);
        res.put(FigureType.KNIGHT, 3);
        res.put(FigureType.BISHOP, 3);
        res.put(FigureType.ROOK, 5);
        res.put(FigureType.QUEEN, 9);
        res.put(FigureType.KING, 100);
        return res;
    }

    @Override
    public Move getNextMove() throws ChessError {
        List<Move> topMoves = new ArrayList<>();
        int maxGrade = 0;
        for (Move move : ms.getAllCorrectMoves(color))
            try {
                Figure figure = board.getFigure(move.getTo());

                int curGrade = figure != null ? grades.get(figure.figureType) : 0;
                if (curGrade > maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                }
                if (curGrade >= maxGrade) topMoves.add(move);
            } catch (ChessException | NullPointerException e) {
                logger.error("Возникла ошибка в атакующем боте: {}", e.getMessage());
                throw new ChessError(BOT_ERROR, e);
            }
        Move move = topMoves.get(new Random().nextInt(topMoves.size()));
        turnIntoInQueen(move);
        return move;
    }

    @Override
    public PlayerType getPlayerType() {
        return PlayerType.ATTACK_BOT;
    }

    protected void turnIntoInQueen(Move move) {
        if (move.getMoveType() == MoveType.TURN_INTO
                || move.getMoveType() == MoveType.TURN_INTO_ATTACK)
            move.turnInto = FigureType.QUEEN;
    }
}
