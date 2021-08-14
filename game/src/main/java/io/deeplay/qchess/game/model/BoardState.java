package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.model.figures.Figure;
import java.util.Arrays;

/** Описывает состояние доски */
public class BoardState {
    public final int[] boardSnapshot;
    public final int boardSnapshotHash;

    public final boolean isPawnEnPassantPossible;
    public final boolean isWhiteMove;

    /** 0 - нет возможности рокироваться, 1 - левая рокировка возможна, 2 - правая, 3 - обе */
    public final int isWhiteCastlingPossibility;
    /** 0 - нет возможности рокироваться, 1 - левая рокировка возможна, 2 - правая, 3 - обе */
    public final int isBlackCastlingPossibility;

    /** Не нужно учитывать в equals и hashCode */
    public final Move lastMove;
    /** Не нужно учитывать в equals и hashCode */
    public final boolean hasMovedBeforeLastMove;
    /** Не нужно учитывать в equals и hashCode */
    public final Figure removedFigure;
    /** Не нужно учитывать в equals и hashCode */
    public final int peaceMoveCount;

    public BoardState(
            final int[] boardSnapshot,
            final int boardSnapshotHash,
            final Move lastMove,
            final int peaceMoveCount,
            final boolean hasMovedBeforeLastMove,
            final Figure removedFigure,
            final boolean isWhiteMove,
            final int isWhiteCastlingPossibility,
            final int isBlackCastlingPossibility) {
        this.boardSnapshot = boardSnapshot;
        this.boardSnapshotHash = boardSnapshotHash;
        this.lastMove = lastMove;
        this.peaceMoveCount = peaceMoveCount;
        this.hasMovedBeforeLastMove = hasMovedBeforeLastMove;
        this.removedFigure = removedFigure;
        this.isWhiteMove = isWhiteMove;
        this.isWhiteCastlingPossibility = isWhiteCastlingPossibility;
        this.isBlackCastlingPossibility = isBlackCastlingPossibility;
        isPawnEnPassantPossible =
                lastMove != null && lastMove.getMoveType() == MoveType.LONG_MOVE;
    }

    /** Используется только для нахождения повторений доски */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || BoardState.class != o.getClass()) return false;
        final BoardState that = (BoardState) o;
        try {
            return isWhiteMove == that.isWhiteMove
                    && boardSnapshotHash == that.boardSnapshotHash
                    && isWhiteCastlingPossibility == that.isWhiteCastlingPossibility
                    && isBlackCastlingPossibility == that.isBlackCastlingPossibility
                    && isPawnEnPassantPossible == that.isPawnEnPassantPossible
                    && Arrays.equals(boardSnapshot, that.boardSnapshot);
        } catch (final NullPointerException e) {
            return false;
        }
    }

    /** Используется только для нахождения повторений доски */
    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + (isWhiteMove ? 1 : 0);
        result = 31 * result + boardSnapshotHash;
        result = 31 * result + isWhiteCastlingPossibility;
        result = 31 * result + isBlackCastlingPossibility;
        result = 31 * result + (isPawnEnPassantPossible ? 1 : 0);
        return result;
    }
}
