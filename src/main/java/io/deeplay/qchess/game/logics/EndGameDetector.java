package io.deeplay.qchess.game.logics;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.ERROR_WHILE_CHECKING_FOR_DRAW;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INTERNAL_ERROR;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EndGameDetector {
    private final GameSettings roomSettings;
    private final List<List<TypeFigure>> material =
            Arrays.asList(
                    Collections.singletonList(TypeFigure.KING),
                    Arrays.asList(TypeFigure.KING, TypeFigure.KNIGHT),
                    Arrays.asList(TypeFigure.KING, TypeFigure.BISHOP),
                    Arrays.asList(TypeFigure.KING, TypeFigure.KNIGHT, TypeFigure.KNIGHT));
    private int pieceMoveCount = 0;

    public EndGameDetector(GameSettings roomSettings) {
        this.roomSettings = roomSettings;
    }

    /** @return true, если это не ничья */
    public boolean isDraw(Figure removedFigure, Move move) throws ChessError {
        try {
            return isDrawWithMoves(removedFigure, move)
                    || isDrawWithRepetitions()
                    || isDrawWithNotEnoughMaterialForCheckmate();
        } catch (ChessException e) {
            throw new ChessError(ERROR_WHILE_CHECKING_FOR_DRAW, e);
        }
    }

    /**
     * Условия ничьи: 1) пешка не ходит 50 ходов 2) никто не рубит
     *
     * @return true, если ничья
     */
    public boolean isDrawWithMoves(Figure removedFigure, Move move) throws ChessException {
        if (removedFigure != null
                || roomSettings.board.getFigure(move.getTo()).getType() == TypeFigure.PAWN)
            pieceMoveCount = 0;
        else ++pieceMoveCount;
        return pieceMoveCount >= 50;
    }

    /**
     * Условия ничьи: минимум 5 повторений позиций доски
     *
     * @return true, если ничья
     */
    public boolean isDrawWithRepetitions() {
        return roomSettings.history.checkRepetitions(5);
    }

    public boolean isDrawWithNotEnoughMaterialForCheckmate() {
        List<Figure> whiteFigures = roomSettings.board.getFigures(Color.WHITE);
        List<Figure> blackFigures = roomSettings.board.getFigures(Color.BLACK);

        if (isKingsWithSameBishop(whiteFigures, blackFigures)) return true;

        List<TypeFigure> oneKing = Collections.singletonList(TypeFigure.KING);
        boolean isOneKingWhite = isAllFiguresSame(whiteFigures, oneKing);
        boolean isOneKingBlack = isAllFiguresSame(blackFigures, oneKing);

        for (List<TypeFigure> typeFigures : material) {
            if (isOneKingWhite && isAllFiguresSame(blackFigures, typeFigures)) return true;
            if (isOneKingBlack && isAllFiguresSame(whiteFigures, typeFigures)) return true;
        }
        return false;
    }

    /**
     * Проверяет, что все фигуры в figures соответствуют своим типам в typeFigures
     *
     * @param figures Список фигур
     * @param typeFigures Список требуемых типов
     * @return true - если списки равны и фигуры из первого списка соответствуют типам из второго
     *     (без учёта порядка)
     */
    private boolean isAllFiguresSame(List<Figure> figures, List<TypeFigure> typeFigures) {
        List<TypeFigure> typeFiguresCopy = new ArrayList<>(typeFigures);
        if (figures.size() != typeFigures.size()) return false;
        for (Figure figure : figures) if (!typeFiguresCopy.remove(figure.getType())) return false;
        return true;
    }

    /**
     * Проверяет, чтоб в переданных списках содержалось только по 2 фигуры - Король и Слон, причём
     * слоны должны быть одного цвета
     *
     * @param whiteFigures Список белых фигур
     * @param blackFigures Список чёрных фигур
     * @return true - если списки удовлетворяют условию
     */
    private boolean isKingsWithSameBishop(List<Figure> whiteFigures, List<Figure> blackFigures) {
        List<TypeFigure> kingWithBishop = Arrays.asList(TypeFigure.KING, TypeFigure.BISHOP);
        if (!isAllFiguresSame(whiteFigures, kingWithBishop)
            || !isAllFiguresSame(blackFigures, kingWithBishop)) return false;

        Figure whiteBishop = getBishop(whiteFigures);
        Figure blackBishop = getBishop(blackFigures);

        Objects.requireNonNull(whiteBishop, INTERNAL_ERROR.getMessage());
        Objects.requireNonNull(blackBishop, INTERNAL_ERROR.getMessage());

        Cell whiteBishopPosition = whiteBishop.getCurrentPosition();
        Cell blackBishopPosition = blackBishop.getCurrentPosition();

        return (whiteBishopPosition.getColumn() + whiteBishopPosition.getRow()) % 2
            == (blackBishopPosition.getColumn() + blackBishopPosition.getRow()) % 2;
    }

    /**
     * Ищет в списке фигуру типа Слона
     *
     * @param figures Список фигур для поиска
     * @return найденного слона, или null - иначе
     */
    private Figure getBishop(List<Figure> figures) {
        for (Figure figure : figures) if (figure.getType() == TypeFigure.BISHOP) return figure;
        return null;
    }

    /** @return true, если установленному цвету поставили мат */
    public boolean isCheckmate(Color color) throws ChessError {
        return isStalemate(color) && isCheck(color);
    }

    /** @return true, если установленному цвету поставили пат (нет доступных ходов) */
    public boolean isStalemate(Color color) throws ChessError {
        return roomSettings.moveSystem.getAllCorrectMoves(color).isEmpty();
    }

    /** @return true если игроку с указанным цветом ставят шах */
    boolean isCheck(Color color) throws ChessError {
        return Board.isAttackedCell(
                roomSettings, roomSettings.board.findKingCell(color), color.inverse());
    }
}
