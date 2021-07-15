package io.deeplay.qchess.game.logics;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.ERROR_WHEN_MOVING_FIGURE;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.KING_NOT_FOUND;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.History;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Хранит различные данные об игре для контроля специфичных ситуаций */
public class MoveSystem {
    private static final Logger logger = LoggerFactory.getLogger(MoveSystem.class);
    private final GameSettings roomSettings;
    private final Board board;
    private final EndGameDetector endGameDetector;
    private final History history;

    public MoveSystem(GameSettings roomSettings) {
        this.roomSettings = roomSettings;
        board = roomSettings.board;
        endGameDetector = roomSettings.endGameDetector;
        history = roomSettings.history;
    }

    /**
     * Делает ход без проверок
     *
     * @return удаленная фигура или null, если ни одну фигуру не взяли
     */
    public Figure move(Move move) throws ChessError {
        try {
            logger.debug("Начато выполнение хода: {}", move);
            Figure removedFigure =
                    switch (move.getMoveType()) {
                            // взятие на проходе
                        case EN_PASSANT -> {
                            board.moveFigure(move);
                            yield board.removeFigure(history.getLastMove().getTo());
                        }
                            // превращение пешки
                        case TURN_INTO -> {
                            Figure turnIntoFigure = move.getTurnInto();
                            turnIntoFigure.setCurrentPosition(move.getTo());
                            Figure removed = board.moveFigure(move);
                            board.setFigure(turnIntoFigure);
                            yield removed;
                        }
                            // рокировка
                        case SHORT_CASTLING -> {
                            Cell from = move.getFrom().createAdd(new Cell(3, 0));
                            Cell to = move.getFrom().createAdd(new Cell(1, 0));
                            board.getFigure(from).setWasMoved(true);
                            board.moveFigure(new Move(MoveType.QUIET_MOVE, from, to));
                            yield board.moveFigure(move);
                        }
                        case LONG_CASTLING -> {
                            Cell from = move.getFrom().createAdd(new Cell(-4, 0));
                            Cell to = move.getFrom().createAdd(new Cell(-1, 0));
                            board.getFigure(from).setWasMoved(true);
                            board.moveFigure(new Move(MoveType.QUIET_MOVE, from, to));
                            yield board.moveFigure(move);
                        }
                        default -> board.moveFigure(move);
                    };

            history.checkAndAddPeaceMoveCount(move);
            history.addRecord(move);

            logger.debug("Ход <{}> выполнен успешно, удаленная фигура: {}", move, removedFigure);
            return removedFigure;
        } catch (ChessException | NullPointerException e) {
            logger.error("Ошибка при выполнении хода: {}", move);
            throw new ChessError(ERROR_WHEN_MOVING_FIGURE, e);
        }
    }

    /**
     * @param color цвет фигур
     * @return все возможные ходы
     */
    public List<Move> getAllCorrectMoves(Color color) throws ChessError {
        List<Move> res = new ArrayList<>(64);
        for (Figure f : board.getFigures(color))
            for (Move m : f.getAllMoves(roomSettings)) if (isCorrectMove(m)) res.add(m);
        return res;
    }

    /** @return true если ход корректный */
    public boolean isCorrectMove(Move move) throws ChessError {
        try {
            return move != null
                    && checkCorrectnessIfSpecificMove(move)
                    && inAvailableMoves(move)
                    && isCorrectVirtualMove(move);
        } catch (ChessException | NullPointerException e) {
            logger.warn(
                    "Проверяемый (некорректный) ход <{}> кинул исключение: {}",
                    move,
                    e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет специфичные ситуации
     *
     * @param move ход для этой фигуры
     * @return true если move специфичный и корректный либо move не специфичный, иначе false
     */
    private boolean checkCorrectnessIfSpecificMove(Move move) throws ChessException {
        // превращение пешки
        logger.debug("Начата проверка хода {} на превращение", move);
        if (move.getMoveType() == MoveType.TURN_INTO)
            return move.getTurnInto() != null
                    && move.getTurnInto().getColor() == board.getFigure(move.getFrom()).getColor()
                    && move.getTurnInto().getCurrentPosition().equals(move.getTo())
                    && (move.getTurnInto().getType() == TypeFigure.BISHOP
                            || move.getTurnInto().getType() == TypeFigure.KNIGHT
                            || move.getTurnInto().getType() == TypeFigure.QUEEN
                            || move.getTurnInto().getType() == TypeFigure.ROOK);
        return true;
    }

    /** @return true если ход лежит в доступных */
    private boolean inAvailableMoves(Move move) throws ChessException {
        logger.debug("Начата проверка хода {} на содержание его в доступных ходах", move);
        Figure figure = board.getFigure(move.getFrom());
        Set<Move> allMoves = figure.getAllMoves(roomSettings);
        return allMoves.contains(move);
    }

    /** @param move корректный ход */
    private boolean isCorrectVirtualMove(Move move) throws ChessError, ChessException {
        logger.debug("Начата проверка виртуального хода {}", move);
        Figure figureToMove = board.getFigure(move.getFrom());
        boolean hasBeenMoved = figureToMove.wasMoved();
        // виртуальный ход
        Figure virtualKilled = board.moveFigure(move);
        if (virtualKilled != null && virtualKilled.getType() == TypeFigure.KING) {
            logger.error("Срубили короля при проверке виртуального хода {}", move);
            throw new ChessError(KING_NOT_FOUND);
        }
        boolean isCheck = endGameDetector.isCheck(figureToMove.getColor());
        // отмена виртуального хода
        board.moveFigure(new Move(move.getMoveType(), move.getTo(), move.getFrom()));
        figureToMove.setWasMoved(hasBeenMoved);
        if (virtualKilled != null) board.setFigure(virtualKilled);
        return !isCheck;
    }
}
