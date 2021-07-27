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

            history.setHasMovedBeforeLastMove(board.getFigure(move.getFrom()).wasMoved());

            Figure removedFigure =
                    switch (move.getMoveType()) {
                            // взятие на проходе
                        case EN_PASSANT -> {
                            board.moveFigure(move);
                            yield board.removeFigure(history.getLastMove().getTo());
                        }
                            // превращение пешки
                        case TURN_INTO, TURN_INTO_ATTACK -> {
                            FigureType turnIntoType = move.getTurnInto();
                            if (turnIntoType == null) turnIntoType = FigureType.QUEEN;
                            Figure turnIntoFigure =
                                    Figure.build(
                                            turnIntoType,
                                            board.getFigure(move.getFrom()).getColor(),
                                            move.getTo());
                            Figure removed = board.moveFigure(move);
                            board.setFigure(turnIntoFigure);
                            yield removed;
                        }
                            // рокировка
                        case SHORT_CASTLING -> {
                            Cell from = move.getFrom().createAdd(new Cell(3, 0));
                            Cell to = move.getFrom().createAdd(new Cell(1, 0));
                            board.moveFigure(new Move(MoveType.QUIET_MOVE, from, to));
                            yield board.moveFigure(move);
                        }
                        case LONG_CASTLING -> {
                            Cell from = move.getFrom().createAdd(new Cell(-4, 0));
                            Cell to = move.getFrom().createAdd(new Cell(-1, 0));
                            board.moveFigure(new Move(MoveType.QUIET_MOVE, from, to));
                            yield board.moveFigure(move);
                        }
                        default -> board.moveFigure(move);
                    };

            history.setRemovedFigure(removedFigure);
            history.checkAndAddPeaceMoveCount(move);
            history.addRecord(move);

            logger.debug("Ход <{}> выполнен успешно, удаленная фигура: {}", move, removedFigure);
            return removedFigure;
        } catch (ChessException | NullPointerException e) {
            logger.error("Ошибка при выполнении хода: {}", move);
            throw new ChessError(ERROR_WHEN_MOVING_FIGURE, e);
        }
    }

    /** Отменяет последний ход без проверок */
    public void undoMove() throws ChessError {
        Move move = history.getLastMove();
        boolean hasMoved = history.isHasMovedBeforeLastMove();
        Figure removedFigure = history.getRemovedFigure();
        try {
            logger.debug("Начата отмена хода: {}", move);

            Move revertMove = new Move(MoveType.QUIET_MOVE, move.getTo(), move.getFrom());
            history.undo();

            board.moveFigure(revertMove);
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
                    board.setFigure(pawn);
                }
                    // превращение пешки
                case TURN_INTO, TURN_INTO_ATTACK -> {
                    Pawn pawn = new Pawn(figureThatMoved.getColor(), move.getFrom());
                    pawn.setWasMoved(hasMoved);
                    board.setFigure(pawn);
                    if (removedFigure != null) board.setFigure(removedFigure);
                }
                    // рокировка
                case SHORT_CASTLING -> {
                    Cell to = move.getFrom().createAdd(new Cell(3, 0));
                    Cell from = move.getFrom().createAdd(new Cell(1, 0));
                    board.moveFigure(new Move(MoveType.QUIET_MOVE, from, to));
                    board.getFigureUgly(to).setWasMoved(false);
                }
                case LONG_CASTLING -> {
                    Cell to = move.getFrom().createAdd(new Cell(-4, 0));
                    Cell from = move.getFrom().createAdd(new Cell(-1, 0));
                    board.moveFigure(new Move(MoveType.QUIET_MOVE, from, to));
                    board.getFigureUgly(to).setWasMoved(false);
                }
                default -> {
                    if (removedFigure != null) board.setFigure(removedFigure);
                }
            }

            logger.debug("Ход <{}> успешно отменен", move);
        } catch (ChessException | NullPointerException e) {
            logger.error("Ошибка при отмене хода: {}", move);
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
            for (Move m : f.getAllMoves(roomSettings))
                if (isCorrectMoveWithoutCheckAvailableMoves(m, true)) res.add(m);
        return res;
    }

    public List<Move> getAllCorrectMovesForStalemate(Color color) {
        List<Move> res = new ArrayList<>(64);
        try {
            for (Figure f : board.getFigures(color))
                for (Move m : f.getAllMoves(roomSettings)) {
                    if (isCorrectMoveWithoutCheckAvailableMoves(m, false)) res.add(m);
                }
        } catch (ChessError ignore) {
        }
        return res;
    }

    /**
     * @param cell клетка
     * @return все возможные ходы из клетки
     */
    public List<Move> getAllCorrectMoves(Cell cell) throws ChessError {
        List<Move> res = new ArrayList<>(27);
        if (!board.isCorrectCell(cell.getColumn(), cell.getRow())) return res;
        Figure figure = board.getFigureUgly(cell);
        if (figure == null) return res;
        for (Move m : figure.getAllMoves(roomSettings))
            if (isCorrectVirtualMoveForCell(m)) res.add(m);
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
    private boolean isCorrectMoveWithoutCheckAvailableMoves(Move move, boolean checkKing)
            throws ChessError {
        try {
            return move != null && checkCorrectnessIfSpecificMove(move) && checkKing
                    ? isCorrectVirtualMove(move)
                    : virtualMove(
                            move,
                            (figureToMove, virtualKilled) ->
                                    !endGameDetector.isCheck(figureToMove));
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
        logger.trace("Начата проверка хода {} на превращение", move);
        if (move.getMoveType() == MoveType.TURN_INTO
                || move.getMoveType() == MoveType.TURN_INTO_ATTACK)
            return move.getTurnInto() != null
                    && (move.getTurnInto() == FigureType.BISHOP
                            || move.getTurnInto() == FigureType.KNIGHT
                            || move.getTurnInto() == FigureType.QUEEN
                            || move.getTurnInto() == FigureType.ROOK);
        return true;
    }

    /** @return true если ход лежит в доступных */
    private boolean inAvailableMoves(Move move) throws ChessException {
        logger.trace("Начата проверка хода {} на содержание его в доступных ходах", move);
        Figure figure = board.getFigure(move.getFrom());
        Set<Move> allMoves = figure.getAllMoves(roomSettings);
        return allMoves.contains(move);
    }

    /** @param move корректный ход */
    private boolean isCorrectVirtualMove(Move move) throws ChessError, ChessException {
        logger.trace("Начата проверка виртуального хода {}", move);
        return virtualMove(
                move,
                (figureToMove, virtualKilled) -> {
                    if (virtualKilled != null && virtualKilled.getType() == FigureType.KING) {
                        logger.error("Срубили короля при проверке виртуального хода {}", move);
                        throw new ChessError(KING_WAS_KILLED_IN_VIRTUAL_MOVE);
                    }
                    return !endGameDetector.isCheck(figureToMove);
                });
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
        // TODO: переделать с измененной историей
        logger.trace("Виртуальный ход {}", move);
        Color figureToMove = board.getFigureUgly(move.getFrom()).getColor();
        Figure virtualKilled = move(move);
        T res = func.apply(figureToMove, virtualKilled);
        undoMove();
        return res;
    }

    @FunctionalInterface
    public interface ChessMoveFunc<T> {
        T apply(Color figureToMove, Figure virtualKilled) throws ChessException, ChessError;
    }
}
