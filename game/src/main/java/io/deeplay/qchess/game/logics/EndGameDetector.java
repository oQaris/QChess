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
    public static final int END_PEACE_MOVE_COUNT = 75;
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

    public EndGameDetector(GameSettings gs) {
        this.gs = gs;
    }

    public EndGameType getGameResult() {
        return gameResult;
    }

    public void resetEndGameStatus() {
        gameResult = EndGameType.NOTHING;
    }

    public void revertEndGameStatus() {
        gameResult = prevGameResult;
    }

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

    /** @return результат игры для цвета color */
    public EndGameType updateEndGameStatus(Color color) {
        return updateEndGameStatus(!isStalemate(color), color);
    }

    /**
     * @param allMoves все доступные ходы цвета color
     * @return результат игры для цвета color, у которого все доступные ходы в allMoves
     */
    public EndGameType updateEndGameStatus(List<Move> allMoves, Color color) {
        return updateEndGameStatus(!allMoves.isEmpty(), color);
    }

    public EndGameType updateEndGameStatus(boolean isHasAnyCorrectMove, Color color) {
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

    /** @return true, если это не ничья */
    public boolean isDraw() {
        return isDrawWithRepetitions()
                || isDrawWithNotEnoughMaterialForCheckmate()
                || isDrawWithPeaceMoves();
    }

    /**
     * Условия ничьи: 1) пешка не ходит 50 ходов 2) никто не рубит
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
     * Недостаточно фигур, чтобы поставить мат
     *
     * @return true, если ничья
     */
    public boolean isDrawWithNotEnoughMaterialForCheckmate() {
        List<Figure> whiteFigures = gs.board.getFigures(Color.WHITE);
        List<Figure> blackFigures = gs.board.getFigures(Color.BLACK);

        if (isKingsWithSameBishop(whiteFigures, blackFigures)) return true;

        List<FigureType> oneKing = Collections.singletonList(FigureType.KING);
        boolean isOneKingWhite = isAllFiguresSame(whiteFigures, oneKing);
        boolean isOneKingBlack = isAllFiguresSame(blackFigures, oneKing);

        for (List<FigureType> figureTypes : MATERIAL) {
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
    private boolean isAllFiguresSame(List<Figure> figures, List<FigureType> figureTypes) {
        List<FigureType> figuresCopyType = new ArrayList<>(figureTypes);
        if (figures.size() != figureTypes.size()) return false;
        for (Figure figure : figures) if (!figuresCopyType.remove(figure.figureType)) return false;
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
        List<FigureType> kingWithBishop = Arrays.asList(FigureType.KING, FigureType.BISHOP);
        if (!isAllFiguresSame(whiteFigures, kingWithBishop)
                || !isAllFiguresSame(blackFigures, kingWithBishop)) return false;

        Figure whiteBishop = getBishop(whiteFigures);
        Figure blackBishop = getBishop(blackFigures);

        if (whiteBishop == null || blackBishop == null) return false;

        Cell whiteBishopPosition = whiteBishop.getCurrentPosition();
        Cell blackBishopPosition = blackBishop.getCurrentPosition();

        return (whiteBishopPosition.column + whiteBishopPosition.row) % 2
                == (blackBishopPosition.column + blackBishopPosition.row) % 2;
    }

    /**
     * Ищет в списке фигуру типа Слона
     *
     * @param figures Список фигур для поиска
     * @return найденного слона, или null - иначе
     */
    private Figure getBishop(List<Figure> figures) {
        for (Figure figure : figures) if (figure.figureType == FigureType.BISHOP) return figure;
        return null;
    }

    /** @return true, если установленному цвету поставили мат */
    public boolean isCheckmate(Color color) {
        return isStalemate(color) && isCheck(color);
    }

    /** @return true, если установленному цвету поставили мат */
    public boolean isCheckmate(List<Move> allMoves, Color color) {
        return isStalemate(allMoves) && isCheck(color);
    }

    /** @return true, если установленному цвету поставили пат (нет доступных ходов) */
    public boolean isStalemate(Color color) {
        return !gs.moveSystem.isHasAnyCorrectMoveSilence(color);
    }

    /** @return true, если установленному цвету поставили пат (нет доступных ходов) */
    public boolean isStalemate(List<Move> allMoves) {
        return allMoves.isEmpty();
    }

    /** @return true если игроку с указанным цветом ставят шах */
    public boolean isCheck(Color color) {
        Cell kingCell = gs.board.findKingCell(color);
        if (kingCell == null) return false;
        return Board.isAttackedCell(gs, kingCell, color.inverse());
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
