package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EndGameDetector {
    private final GameSettings roomSettings;
    private final List<List<TypeFigure>> material =
            Arrays.asList(
                    Collections.singletonList(TypeFigure.KING),
                    Arrays.asList(TypeFigure.KING, TypeFigure.KNIGHT),
                    Arrays.asList(TypeFigure.KING, TypeFigure.BISHOP),
                    Arrays.asList(TypeFigure.KING, TypeFigure.KNIGHT, TypeFigure.KNIGHT));

    public EndGameDetector(GameSettings roomSettings) {
        this.roomSettings = roomSettings;
    }

    /** @return true, если это не ничья */
    public boolean isDraw() {
        return isDrawWithMoves()
                || isDrawWithRepetitions()
                || isDrawWithNotEnoughMaterialForCheckmate();
    }

    /**
     * Условия ничьи: 1) пешка не ходит 50 ходов 2) никто не рубит
     *
     * @return true, если ничья
     */
    public boolean isDrawWithMoves() {
        return roomSettings.history.getPieceMoveCount() >= 50;
    }

    /**
     * Условия ничьи: минимум 5 повторений позиций доски
     *
     * @return true, если ничья
     */
    public boolean isDrawWithRepetitions() {
        return roomSettings.history.checkRepetitions(5);
    }

    /**
     * Недостаточно фигур, чтобы поставить мат
     *
     * @return true, если ничья
     */
    public boolean isDrawWithNotEnoughMaterialForCheckmate() {
        if (isKingsWithSameBishop()) return true;
        for (List<TypeFigure> typeFigures : material) {
            if (isAllFiguresSame(Color.BLACK, typeFigures) && isOneKing(Color.WHITE)) return true;
            if (isAllFiguresSame(Color.WHITE, typeFigures) && isOneKing(Color.BLACK)) return true;
        }
        return false;
    }

    private boolean isAllFiguresSame(Color color, List<TypeFigure> typeFigures) {
        List<TypeFigure> typeFiguresCopy = new ArrayList<>(typeFigures);
        for (Figure figure : roomSettings.board.getFigures(color))
            if (!typeFiguresCopy.remove(figure.getType())) return false;
        return true;
    }

    private boolean isOneKing(Color color) {
        return isAllFiguresSame(color, Collections.singletonList(TypeFigure.KING));
    }

    private boolean isKingsWithSameBishop() {
        Figure whiteBishop = getBishop(Color.WHITE);
        Figure blackBishop = getBishop(Color.BLACK);

        if (whiteBishop == null || blackBishop == null) return false;

        Cell whiteBishopPosition = whiteBishop.getCurrentPosition();
        Cell blackBishopPosition = blackBishop.getCurrentPosition();

        return (whiteBishopPosition.getColumn() + whiteBishopPosition.getRow()) % 2
                == (blackBishopPosition.getColumn() + blackBishopPosition.getRow()) % 2;
    }

    private Figure getBishop(Color color) {
        for (Figure figure : roomSettings.board.getFigures(color))
            if (figure.getType() == TypeFigure.BISHOP) return figure;
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
