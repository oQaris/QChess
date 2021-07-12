package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessErrorCode;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.*;

public class EndGameDetector {
    private final GameSettings roomSettings;
    private int pieceMoveCount = 0;

    public EndGameDetector(GameSettings roomSettings) {
        this.roomSettings = roomSettings;
    }

    /**
     * @return true, если это не ничья
     */
    public boolean isDraw(Figure removedFigure, Move move) throws ChessError {
        try {
            // условия ничьи:
            // пешка не ходит 50 ходов
            // никто не рубит
            if (removedFigure != null || roomSettings.board.getFigure(move.getTo()).getClass() == Pawn.class) {
                pieceMoveCount = 0;
            } else {
                pieceMoveCount++;
            }
            return pieceMoveCount == 50 && isNotEnoughMaterialForCheckmate();
        } catch (ChessException e) {
            throw new ChessError("Ошибка при проверки на ничью", e);
        }
    }

    private boolean isNotEnoughMaterialForCheckmate() {
        List<List<TypeFigure>> material = Arrays.asList(
                Collections.singletonList(TypeFigure.KING),
                Arrays.asList(TypeFigure.KING, TypeFigure.KNIGHT),
                Arrays.asList(TypeFigure.KING, TypeFigure.BISHOP),
                Arrays.asList(TypeFigure.KING, TypeFigure.KNIGHT, TypeFigure.KNIGHT));

        return material.stream().anyMatch(m ->
                (isAllFiguresSame(Color.BLACK, m)
                        && isAllFiguresSame(Color.WHITE, Collections.singletonList(TypeFigure.KING)))
                        || (isAllFiguresSame(Color.WHITE, m)
                        && isAllFiguresSame(Color.BLACK, Collections.singletonList(TypeFigure.KING)))
                        && isKingsWithSameBishop());
    }

    private boolean isAllFiguresSame(Color color, List<TypeFigure> typeFigures) {
        List<TypeFigure> typeFiguresCopy = new ArrayList<>(typeFigures);
        for (Figure figure : roomSettings.board.getFigures(color))
            if (!typeFiguresCopy.remove(figure.getType()))
                return false;
        return true;
    }

    private boolean isKingsWithSameBishop() {
        String msg = ChessErrorCode.INCORRECT_COORDINATES.getMessage();
        Cell whiteBishopPosition = Objects.requireNonNull(getBishop(Color.WHITE), msg).getCurrentPosition();
        Cell blackBishopPosition = Objects.requireNonNull(getBishop(Color.BLACK), msg).getCurrentPosition();
        return (whiteBishopPosition.getColumn() + whiteBishopPosition.getRow()) % 2
                == (blackBishopPosition.getColumn() + blackBishopPosition.getRow()) % 2;
    }

    private Figure getBishop(Color color) {
        for (Figure figure : roomSettings.board.getFigures(color))
            if (figure.getType() == TypeFigure.BISHOP)
                return figure;
        return null;
    }

    /**
     * @return true, если установленному цвету поставили мат
     */
    public boolean isCheckmate(Color color) throws ChessError {
        return isStalemate(color) && isCheck(color);
    }

    /**
     * @return true, если установленному цвету поставили пат (нет доступных ходов)
     */
    public boolean isStalemate(Color color) throws ChessError {
        return roomSettings.moveSystem.getAllCorrectMoves(color).isEmpty();
    }

    /**
     * @return true если игроку с указанным цветом ставят шах
     */
    public boolean isCheck(Color color) throws ChessError {
        return roomSettings.board.isAttackedCell(roomSettings.board.findKingCell(color), color.inverse());
    }
}