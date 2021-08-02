package io.deeplay.qchess.game.logics;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.ERROR_WHEN_MOVING_FIGURE;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.KING_WAS_KILLED_IN_VIRTUAL_MOVE;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.History;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.model.figures.Pawn;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Хранит различные данные об игре для контроля специфичных ситуаций */
public class MoveSystem {
    private static final Logger logger = LoggerFactory.getLogger(MoveSystem.class);

    private final GameSettings gs;
    private final Board board;
    private final History history;
    private final EndGameDetector egd;
    private Move prevMoveIfRecordNotUse;

    public MoveSystem(GameSettings gs) {
        this.gs = gs;
        this.board = gs.board;
        this.history = gs.history;
        this.egd = gs.endGameDetector;
    }

    public Figure move(Move move) throws ChessError {
        return move(move, true);
    }

    /**
     * Делает ход без проверок
     *
     * @return удаленная фигура или null, если ни одну фигуру не взяли
     */
    public Figure move(Move move, boolean useHistoryRecord) throws ChessError {
        try {
            logger.debug("Начато выполнение хода: {}", move);

            Figure moveFigure = board.getFigureUgly(move.getFrom());

            Figure removedFigure =
                    switch (move.getMoveType()) {
                            // взятие на проходе
                        case EN_PASSANT -> {
                            board.moveFigureUgly(move);
                            yield board.removeFigureUgly(history.getLastMove().getTo());
                        }
                            // превращение пешки
                        case TURN_INTO, TURN_INTO_ATTACK -> {
                            FigureType turnIntoType = move.getTurnInto();
                            Figure turnIntoFigure =
                                    Figure.build(turnIntoType, moveFigure.getColor(), move.getTo());
                            Figure removed = board.moveFigureUgly(move);
                            board.setFigureUgly(turnIntoFigure);
                            yield removed;
                        }
                            // рокировка
                        case SHORT_CASTLING -> {
                            Cell from = move.getFrom().createAdd(new Cell(3, 0));
                            Cell to = move.getFrom().createAdd(new Cell(1, 0));
                            board.moveFigureUgly(new Move(MoveType.QUIET_MOVE, from, to));
                            board.getFigureUgly(to).setWasMoved(true);
                            yield board.moveFigureUgly(move);
                        }
                        case LONG_CASTLING -> {
                            Cell from = move.getFrom().createAdd(new Cell(-4, 0));
                            Cell to = move.getFrom().createAdd(new Cell(-1, 0));
                            board.moveFigureUgly(new Move(MoveType.QUIET_MOVE, from, to));
                            board.getFigureUgly(to).setWasMoved(true);
                            yield board.moveFigureUgly(move);
                        }
                        default -> board.moveFigureUgly(move);
                    };

            history.setHasMovedBeforeLastMove(moveFigure.wasMoved());
            moveFigure.setWasMoved(true);
            history.setRemovedFigure(removedFigure);
            if (useHistoryRecord) {
                history.checkAndAddPeaceMoveCount(move);
                history.addRecord(move);
            } else prevMoveIfRecordNotUse = move;

            return removedFigure;
        } catch (NullPointerException e) {
            logger.warn("Ошибка при выполнении хода: {}", move);
            throw new ChessError(ERROR_WHEN_MOVING_FIGURE, e);
        }
    }

    public void undoMove() throws ChessError {
        undoMove(true);
    }

    /** Отменяет последний ход без проверок */
    public void undoMove(boolean useHistoryRecord) throws ChessError {
        Move move = useHistoryRecord ? history.getLastMove() : prevMoveIfRecordNotUse;
        boolean hasMoved = history.isHasMovedBeforeLastMove();
        Figure removedFigure = history.getRemovedFigure();
        try {
            logger.debug("Начата отмена хода: {}", move);

            Move revertMove = new Move(MoveType.QUIET_MOVE, move.getTo(), move.getFrom());
            if (useHistoryRecord) history.undo();
            else history.restore();

            board.moveFigureUgly(revertMove);
            Figure figureThatMoved = board.getFigureUgly(move.getFrom());
            figureThatMoved.setWasMoved(hasMoved);

            switch (move.getMoveType()) {
                    // взятие на проходе
                case EN_PASSANT -> {
                    Pawn pawn =
                            new Pawn(
                                    figureThatMoved.getColor().inverse(),
                                    history.getLastMove().getTo());
                    pawn.setWasMoved(true);
                    board.setFigureUgly(pawn);
                }
                    // превращение пешки
                case TURN_INTO, TURN_INTO_ATTACK -> {
                    Pawn pawn = new Pawn(figureThatMoved.getColor(), move.getFrom());
                    pawn.setWasMoved(true);
                    board.setFigureUgly(pawn);
                    if (removedFigure != null) board.setFigureUgly(removedFigure);
                }
                    // рокировка
                case SHORT_CASTLING -> {
                    Cell to = move.getFrom().createAdd(new Cell(3, 0));
                    Cell from = move.getFrom().createAdd(new Cell(1, 0));
                    board.moveFigureUgly(new Move(MoveType.QUIET_MOVE, from, to));
                    board.getFigureUgly(to).setWasMoved(false);
                }
                case LONG_CASTLING -> {
                    Cell to = move.getFrom().createAdd(new Cell(-4, 0));
                    Cell from = move.getFrom().createAdd(new Cell(-1, 0));
                    board.moveFigureUgly(new Move(MoveType.QUIET_MOVE, from, to));
                    board.getFigureUgly(to).setWasMoved(false);
                }
                default -> {
                    if (removedFigure != null) board.setFigureUgly(removedFigure);
                }
            }

        } catch (NullPointerException e) {
            logger.error("Ошибка при отмене хода: {}", move);
            throw new ChessError(ERROR_WHEN_MOVING_FIGURE, e);
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
        if (move.getMoveType() == MoveType.TURN_INTO
                || move.getMoveType() == MoveType.TURN_INTO_ATTACK)
            return move.getTurnInto() != null
                    && (move.getTurnInto() == FigureType.BISHOP
                            || move.getTurnInto() == FigureType.KNIGHT
                            || move.getTurnInto() == FigureType.QUEEN
                            || move.getTurnInto() == FigureType.ROOK);
        return true;
    }

    /** @return true если ход корректный */
    private boolean isCorrectMoveWithoutCheckAvailableMoves(Move move) throws ChessError {
        try {
            return move != null
                    && checkCorrectnessIfSpecificMove(move)
                    && isCorrectVirtualMove(move);
        } catch (ChessException | NullPointerException e) {
            logger.warn(
                    "Проверяемый (некорректный) ход <{}> кинул исключение: {}",
                    move,
                    e.getMessage());
            return false;
        }
    }

    /** @param move корректный ход */
    private boolean isCorrectVirtualMove(Move move) throws ChessError, ChessException {
        logger.trace("Начата проверка виртуального хода {}", move);
        Color figureToMove = board.getFigureUgly(move.getFrom()).getColor();
        Figure virtualKilled = move(move, false);

        if (virtualKilled != null && virtualKilled.figureType == FigureType.KING) {
            logger.error("Срубили короля при проверке виртуального хода {}", move);
            throw new ChessError(KING_WAS_KILLED_IN_VIRTUAL_MOVE);
        }
        boolean isCheck = egd.isCheck(figureToMove);

        undoMove(false);
        return !isCheck;
    }

    /**
     * Опасно! Выполняет ходы без проверки
     *
     * @param move Виртуальный ход.
     * @param func Функция, выполняемая после виртуального хода.
     * @return Результат функции func.
     * @throws ChessException Если выбрасывается в функции func.
     * @throws ChessError Если выбрасывается в функции func.
     */
    public <T> T virtualMove(Move move, ChessMoveFunc<T> func) throws ChessException, ChessError {
        logger.trace("Виртуальный ход {}", move);
        Color figureToMove = board.getFigureUgly(move.getFrom()).getColor();
        Figure virtualKilled = move(move);
        T res = func.apply(figureToMove, virtualKilled);
        undoMove();
        return res;
    }

    /**
     * @param color цвет фигур
     * @return все возможные ходы
     */
    public List<Move> getAllCorrectMoves(Color color) throws ChessError {
        List<Move> res = new ArrayList<>(70);
        for (Figure f : board.getFigures(color))
            for (Move m : f.getAllMoves(gs))
                if (isCorrectMoveWithoutCheckAvailableMoves(m)) res.add(m);
        return res;
    }

    public List<Move> getAllCorrectMovesSilence(Color color) {
        List<Move> res = new ArrayList<>(70);
        try {
            for (Figure f : board.getFigures(color))
                for (Move m : f.getAllMoves(gs))
                    if (isCorrectMoveWithoutCheckAvailableMovesSilence(m)) res.add(m);
        } catch (ChessError ignore) {
        }
        return res;
    }

    /** @return true если ход корректный */
    private boolean isCorrectMoveWithoutCheckAvailableMovesSilence(Move move) throws ChessError {
        try {
            return checkCorrectnessIfSpecificMove(move) && isCorrectVirtualMoveSilence(move);
        } catch (ChessException | NullPointerException e) {
            return false;
        }
    }

    /** @param move корректный ход */
    private boolean isCorrectVirtualMoveSilence(Move move) throws ChessError {
        Color figureToMove = board.getFigureUgly(move.getFrom()).getColor();
        move(move, false);
        boolean isCheck = egd.isCheck(figureToMove);
        undoMove(false);
        return !isCheck;
    }

    /**
     * @param cell клетка
     * @return все возможные ходы из клетки
     */
    public List<Move> getAllCorrectMoves(Cell cell) throws ChessError {
        List<Move> res = new ArrayList<>(27);
        try {
            for (Move m : board.getFigureUgly(cell).getAllMoves(gs))
                if (isCorrectVirtualMoveForCell(m)) res.add(m);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ignored) {
            // Вместо проверки клетки на доске
        }
        return res;
    }

    private boolean isCorrectVirtualMoveForCell(Move move) throws ChessError {
        try {
            return move != null && isCorrectVirtualMove(move);
        } catch (ChessException | NullPointerException e) {
            logger.warn(
                    "Проверяемый (некорректный) ход <{}> кинул исключение: {}",
                    move,
                    e.getMessage());
            return false;
        }
    }

    /** @return true если ход корректный */
    public boolean isCorrectMove(Move move) throws ChessError {
        try {
            return move != null
                    && checkCorrectnessIfSpecificMove(move)
                    && inAvailableMoves(move)
                    && isCorrectVirtualMove(move);
        } catch (ChessException | NullPointerException | ArrayIndexOutOfBoundsException e) {
            logger.warn(
                    "Проверяемый (некорректный) ход <{}> кинул исключение: {}",
                    move,
                    e.getMessage());
            return false;
        }
    }

    /** @return true если ход лежит в доступных */
    private boolean inAvailableMoves(Move move)
            throws ChessException, ArrayIndexOutOfBoundsException {
        Figure figure = board.getFigureUgly(move.getFrom());
        Set<Move> allMoves = figure.getAllMoves(gs);
        return allMoves.contains(move);
    }

    @FunctionalInterface
    public interface ChessMoveFunc<T> {
        T apply(Color figureToMove, Figure virtualKilled) throws ChessException, ChessError;
    }
}
