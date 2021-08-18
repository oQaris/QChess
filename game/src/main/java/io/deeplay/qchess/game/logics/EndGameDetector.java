package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EndGameDetector {
    /** Максимальное число ходов до ничьи без взятия и хода пешки */
    public static final int END_PEACE_MOVE_COUNT = 75;
    /** Максимальное число повторений доски до ничьи */
    public static final int END_REPETITIONS_COUNT = 5;

    private static final List<List<FigureType>> MATERIAL =
            Arrays.asList(
                    // todo Добавить Король против короля с 2 одноцветными слонами
                    Collections.singletonList(FigureType.KING),
                    Arrays.asList(FigureType.KING, FigureType.KNIGHT),
                    Arrays.asList(FigureType.KING, FigureType.BISHOP),
                    Arrays.asList(FigureType.KING, FigureType.KNIGHT, FigureType.KNIGHT));

    private final GameSettings gs;
    private EndGameType prevGameResult = EndGameType.NOTHING;
    private EndGameType gameResult = EndGameType.NOTHING;

    public EndGameDetector(final GameSettings gs) {
        this.gs = gs;
    }

    /** @return результат окончания игры */
    public EndGameType getGameResult() {
        return gameResult;
    }

    /** Сбрасывает результат окончания игры */
    public void resetEndGameStatus() {
        gameResult = EndGameType.NOTHING;
    }

    /** Устанавливает предыдущий результат окончания игры */
    public void revertEndGameStatus() {
        gameResult = prevGameResult;
    }

    /** @return обновляет и возвращает результат окончания игры */
    public EndGameType updateEndGameStatus() {
        prevGameResult = gameResult;
        gameResult = EndGameType.NOTHING;
        if (isStalemate(Color.WHITE)) {
            gameResult =
                    isCheck(Color.WHITE)
                            ? EndGameType.CHECKMATE_TO_WHITE
                            : EndGameType.STALEMATE_TO_WHITE;
        } else if (isStalemate(Color.BLACK)) {
            gameResult =
                    isCheck(Color.BLACK)
                            ? EndGameType.CHECKMATE_TO_BLACK
                            : EndGameType.STALEMATE_TO_BLACK;
        } else {
            if (isDrawWithRepetitions()) gameResult = EndGameType.DRAW_WITH_REPETITIONS;
            else if (isDrawWithNotEnoughMaterialForCheckmate())
                gameResult = EndGameType.DRAW_WITH_NOT_ENOUGH_MATERIAL;
            else if (isDrawWithPeaceMoves()) gameResult = EndGameType.DRAW_WITH_PEACE_MOVE_COUNT;
        }
        return gameResult;
    }

    /** @return обновляет и возвращает результат игры для цвета color */
    public EndGameType updateEndGameStatus(final Color color) {
        return updateEndGameStatus(!isStalemate(color), color);
    }

    /**
     * @param allMoves все доступные ходы цвета color
     * @return обновляет и возвращает результат игры для цвета color, у которого все доступные ходы
     *     в allMoves
     */
    public EndGameType updateEndGameStatus(final List<Move> allMoves, final Color color) {
        return updateEndGameStatus(!allMoves.isEmpty(), color);
    }

    /**
     * @param isHasAnyCorrectMove true, если игрок с цветом color имеет хотя бы 1 легальный ход
     * @return обновляет и возвращает результат окончания игры для цвета color
     */
    public EndGameType updateEndGameStatus(final boolean isHasAnyCorrectMove, final Color color) {
        gameResult = EndGameType.NOTHING;
        if (!isHasAnyCorrectMove) {
            gameResult =
                    isCheck(color)
                            ? color == Color.WHITE
                                    ? EndGameType.CHECKMATE_TO_WHITE
                                    : EndGameType.CHECKMATE_TO_BLACK
                            : color == Color.WHITE
                                    ? EndGameType.STALEMATE_TO_WHITE
                                    : EndGameType.STALEMATE_TO_BLACK;
        } else {
            if (isDrawWithRepetitions()) gameResult = EndGameType.DRAW_WITH_REPETITIONS;
            else if (isDrawWithNotEnoughMaterialForCheckmate())
                gameResult = EndGameType.DRAW_WITH_NOT_ENOUGH_MATERIAL;
            else if (isDrawWithPeaceMoves()) gameResult = EndGameType.DRAW_WITH_PEACE_MOVE_COUNT;
        }
        return gameResult;
    }

    /** @return true, если это ничья */
    public boolean isDraw() {
        return isDrawWithRepetitions()
                || isDrawWithNotEnoughMaterialForCheckmate()
                || isDrawWithPeaceMoves();
    }

    /**
     * Условия ничьи: 1) пешка не ходит 75 ходов 2) никто не рубит
     *
     * @return true, если ничья
     */
    public boolean isDrawWithPeaceMoves() {
        return gs.history.getPeaceMoveCount() >= END_PEACE_MOVE_COUNT;
    }

    /**
     * Условия ничьи: минимум 5 повторений позиций доски
     *
     * @return true, если ничья
     */
    public boolean isDrawWithRepetitions() {
        return gs.history.checkRepetitions(END_REPETITIONS_COUNT);
    }

    /**
     * Условия ничьи: недостаточно фигур, чтобы поставить мат
     *
     * @return true, если ничья
     */
    public boolean isDrawWithNotEnoughMaterialForCheckmate() {
        final List<Figure> whiteFigures = gs.board.getFigures(Color.WHITE);
        final List<Figure> blackFigures = gs.board.getFigures(Color.BLACK);

        if (isKingsWithSameBishop(whiteFigures, blackFigures)) return true;

        final List<FigureType> oneKing = Collections.singletonList(FigureType.KING);
        final boolean isOneKingWhite = isAllFiguresSame(whiteFigures, oneKing);
        final boolean isOneKingBlack = isAllFiguresSame(blackFigures, oneKing);

        for (final List<FigureType> figureTypes : MATERIAL) {
            if (isOneKingWhite && isAllFiguresSame(blackFigures, figureTypes)) return true;
            if (isOneKingBlack && isAllFiguresSame(whiteFigures, figureTypes)) return true;
        }
        return false;
    }

    /**
     * Проверяет, что все фигуры в figures соответствуют своим типам в figureTypes
     *
     * @param figures Список фигур
     * @param figureTypes Список требуемых типов
     * @return true - если списки равны и фигуры из первого списка соответствуют типам из второго
     *     (без учёта порядка)
     */
    private boolean isAllFiguresSame(
            final List<Figure> figures, final List<FigureType> figureTypes) {
        final List<FigureType> figuresCopyType = new ArrayList<>(figureTypes);
        if (figures.size() != figureTypes.size()) return false;
        for (final Figure figure : figures)
            if (!figuresCopyType.remove(figure.figureType)) return false;
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
    private boolean isKingsWithSameBishop(
            final List<Figure> whiteFigures, final List<Figure> blackFigures) {
        final List<FigureType> kingWithBishop = Arrays.asList(FigureType.KING, FigureType.BISHOP);
        if (!isAllFiguresSame(whiteFigures, kingWithBishop)
                || !isAllFiguresSame(blackFigures, kingWithBishop)) return false;

        final Figure whiteBishop = getBishop(whiteFigures);
        final Figure blackBishop = getBishop(blackFigures);

        if (whiteBishop == null || blackBishop == null) return false;

        final Cell whiteBishopPosition = whiteBishop.getCurrentPosition();
        final Cell blackBishopPosition = blackBishop.getCurrentPosition();

        return (whiteBishopPosition.column + whiteBishopPosition.row) % 2
                == (blackBishopPosition.column + blackBishopPosition.row) % 2;
    }

    /**
     * Ищет в списке фигуру типа Слона
     *
     * @param figures Список фигур для поиска
     * @return найденного слона, или null - иначе
     */
    private Figure getBishop(final List<Figure> figures) {
        for (final Figure figure : figures)
            if (figure.figureType == FigureType.BISHOP) return figure;
        return null;
    }

    /** @return true, если установленному цвету поставили мат */
    public boolean isCheckmate(final Color color) {
        return isStalemate(color) && isCheck(color);
    }

    /** @return true, если установленному цвету поставили мат */
    public boolean isCheckmate(final List<Move> allMoves, final Color color) {
        return isStalemate(allMoves) && isCheck(color);
    }

    /** @return true, если установленному цвету поставили пат (нет доступных ходов) */
    public boolean isStalemate(final Color color) {
        return !gs.moveSystem.isHasAnyCorrectMoveSilence(color);
    }

    /** @return true, если установленному цвету поставили пат (нет доступных ходов) */
    public boolean isStalemate(final List<Move> allMoves) {
        return allMoves.isEmpty();
    }

    /** @return true если игроку с указанным цветом ставят шах */
    public boolean isCheck(final Color color) {
        final Cell kingCell = gs.board.findKingCell(color);
        if (kingCell == null) return false;
        return Board.isAttackedCell(gs.board, kingCell, color.inverse());
    }

    public enum EndGameType {
        NOTHING,
        DRAW_WITH_PEACE_MOVE_COUNT,
        DRAW_WITH_REPETITIONS,
        DRAW_WITH_NOT_ENOUGH_MATERIAL,
        CHECKMATE_TO_BLACK,
        CHECKMATE_TO_WHITE,
        STALEMATE_TO_BLACK,
        STALEMATE_TO_WHITE
    }
}
