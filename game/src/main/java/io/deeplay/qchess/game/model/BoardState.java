package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.model.figures.Figure;

/** Описывает состояние доски */
public class BoardState {
    /** TODO: переделать на полную доску */
    public final int boardHash;
    /** Учитывать в equals и hashCode только LONG_MOVE */
    public final Move lastMove;

    public final boolean isWhiteCastlingPossibility = true;
    public final boolean isBlackCastlingPossibility = true;

    /** Не нужно учитывать в equals и hashCode */
    public final boolean hasMovedBeforeLastMove;
    /** Не нужно учитывать в equals и hashCode */
    public final Figure removedFigure;
    /** Не нужно учитывать в equals и hashCode */
    public final int peaceMoveCount;

    public BoardState(
            int boardHash,
            Move lastMove,
            int peaceMoveCount,
            boolean hasMovedBeforeLastMove,
            Figure removedFigure) {
        this.boardHash = boardHash;
        this.lastMove = lastMove;
        this.peaceMoveCount = peaceMoveCount;
        this.hasMovedBeforeLastMove = hasMovedBeforeLastMove;
        this.removedFigure = removedFigure;
    }

    /** Используется только для нахождения повторений доски */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || BoardState.class != o.getClass()) return false;
        BoardState that = (BoardState) o;
        try {
            return boardHash == that.boardHash
                    && isWhiteCastlingPossibility == that.isWhiteCastlingPossibility
                    && isBlackCastlingPossibility == that.isBlackCastlingPossibility
                    && (lastMove.getMoveType() == MoveType.LONG_MOVE
                                    && that.lastMove.getMoveType() == MoveType.LONG_MOVE
                            || lastMove.getMoveType() != MoveType.LONG_MOVE
                                    && that.lastMove.getMoveType() != MoveType.LONG_MOVE);
        } catch (NullPointerException e) {
            return false;
        }
    }

    /** Используется только для нахождения повторений доски */
    @Override
    public int hashCode() {
        final int h1 = lastMove == null ? 0 : lastMove.fullHashCode();
        int result = 31 * h1 + boardHash;
        result = 31 * result + (isWhiteCastlingPossibility ? 1 : 0);
        result = 31 * result + (isBlackCastlingPossibility ? 1 : 0);
        return result;
    }
}
