package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class King extends Figure {
    private static final Logger logger = LoggerFactory.getLogger(King.class);

    public King(Color color, Cell position) {
        super(color, position);
    }

    @Override
    public Set<Move> getAllMoves(GameSettings settings) {
        Set<Move> res = getAttackedMoves(settings.board);
        // рокировка
        if (isCorrectCastling(settings, true))
            res.add(
                    new Move(
                            MoveType.SHORT_CASTLING, position, position.createAdd(new Cell(2, 0))));
        if (isCorrectCastling(settings, false))
            res.add(
                    new Move(
                            MoveType.LONG_CASTLING, position, position.createAdd(new Cell(-2, 0))));
        return res;
    }

    @Override
    public FigureType getType() {
        return FigureType.KING;
    }

    /** @return ходы без рокировки */
    public Set<Move> getAttackedMoves(Board board) {
        return stepForEach(
                board,
                Stream.concat(Figure.xMove.stream(), Figure.plusMove.stream())
                        .collect(Collectors.toList()));
    }

    /** @return true, если рокировка возможна */
    private boolean isCorrectCastling(GameSettings settings, boolean shortCastling) {
        logger.debug("Запущена проверка на возможность рокировки для {}", this);
        if (wasMoved
                || !settings.board.isEmptyCell(
                        position.createAdd(new Cell(shortCastling ? 1 : -1, 0)))
                || !settings.board.isEmptyCell(
                        position.createAdd(new Cell(shortCastling ? 2 : -2, 0)))
                || !shortCastling
                        && !settings.board.isEmptyCell(position.createAdd(new Cell(-3, 0)))
                || Board.isAttackedCell(settings, position, color.inverse())
                || Board.isAttackedCell(
                        settings,
                        position.createAdd(new Cell(shortCastling ? 1 : -1, 0)),
                        color.inverse())
                || Board.isAttackedCell(
                        settings,
                        position.createAdd(new Cell(shortCastling ? 2 : -2, 0)),
                        color.inverse())) return false;
        try {
            Figure rook =
                    settings.board.getFigure(
                            position.createAdd(new Cell(shortCastling ? 3 : -4, 0)));
            return rook != null && !rook.wasMoved() && rook.getType() == FigureType.ROOK;
        } catch (ChessException | NullPointerException e) {
            logger.warn("В проверке на рокировку возникло исключение: {}", e.getMessage());
            return false;
        }
    }
}
