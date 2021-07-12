package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

public class EndGameDetector {
    private final GameSettings roomSettings;
    private int pieceMoveCount = 0;

    public EndGameDetector(GameSettings roomSettings) {
        this.roomSettings = roomSettings;
    }

    /**
     * @return true, если это не ничья
     */
    public boolean isNotDraw(Figure removedFigure, Move move) throws ChessError {
        try {
            return !isDrawWithMoves(removedFigure, move)
                    && !isDrawWithRepetitions();
        } catch (ChessException e) {
            throw new ChessError("Ошибка при проверки на ничью", e);
        }
    }

    /**
     * Условия ничьи:
     * 1) пешка не ходит 50 ходов
     * 2) никто не рубит
     *
     * @return true, если ничья
     */
    private boolean isDrawWithMoves(Figure removedFigure, Move move) throws ChessException {
        if (removedFigure != null || roomSettings.board.getFigure(move.getTo()).getType() == TypeFigure.PAWN) {
            pieceMoveCount = 0;
        } else {
            ++pieceMoveCount;
        }
        return pieceMoveCount == 50;
    }

    /**
     * Условия ничьи:
     * минимум 5 повторений позиций доски
     *
     * @return true, если ничья
     */
    private boolean isDrawWithRepetitions() {
        return roomSettings.history.checkRepetitions(5);
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
        return Board.isAttackedCell(roomSettings, roomSettings.board.findKingCell(color), color.inverse());
    }
}
