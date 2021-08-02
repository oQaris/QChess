package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.model.figures.Figure;
import java.util.Arrays;

/** Описывает состояние доски */
public class BoardState {
    public final byte[] boardSnapshot;
    public final int boardSnapshotHash;
    /** Учитывать в equals и hashCode только LONG_MOVE */
    public final Move lastMove;

    public final boolean isWhiteMove;

    /** 0 - нет возможности рокироваться, 1 - левая рокировка возможна, 2 - правая, 3 - обе */
    public final int isWhiteCastlingPossibility;
    /** 0 - нет возможности рокироваться, 1 - левая рокировка возможна, 2 - правая, 3 - обе */
    public final int isBlackCastlingPossibility;

    /** Не нужно учитывать в equals и hashCode */
    public final boolean hasMovedBeforeLastMove;
    /** Не нужно учитывать в equals и hashCode */
    public final Figure removedFigure;
    /** Не нужно учитывать в equals и hashCode */
    public final int peaceMoveCount;

    public BoardState(
            byte[] boardSnapshot,
            int boardSnapshotHash,
            Move lastMove,
            int peaceMoveCount,
            boolean hasMovedBeforeLastMove,
            Figure removedFigure,
            boolean isWhiteMove,
            int isWhiteCastlingPossibility,
            int isBlackCastlingPossibility) {
        this.boardSnapshot = boardSnapshot;
        this.boardSnapshotHash = boardSnapshotHash;
        this.lastMove = lastMove;
        this.peaceMoveCount = peaceMoveCount;
        this.hasMovedBeforeLastMove = hasMovedBeforeLastMove;
        this.removedFigure = removedFigure;
        this.isWhiteMove = isWhiteMove;
        this.isWhiteCastlingPossibility = isWhiteCastlingPossibility;
        this.isBlackCastlingPossibility = isBlackCastlingPossibility;
    }

    /** Используется только для нахождения повторений доски */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || BoardState.class != o.getClass()) return false;
        BoardState that = (BoardState) o;
        try {
            return isWhiteMove == that.isWhiteMove
                    && boardSnapshotHash == that.boardSnapshotHash
                    && isWhiteCastlingPossibility == that.isWhiteCastlingPossibility
                    && isBlackCastlingPossibility == that.isBlackCastlingPossibility
                    && (lastMove.getMoveType() == MoveType.LONG_MOVE
                                    && that.lastMove.getMoveType() == MoveType.LONG_MOVE
                            || lastMove.getMoveType() != MoveType.LONG_MOVE
                                    && that.lastMove.getMoveType() != MoveType.LONG_MOVE)
                    && Arrays.equals(boardSnapshot, that.boardSnapshot);
        } catch (NullPointerException e) {
            return false;
        }
    }

    /** Используется только для нахождения повторений доски */
    @Override
    public int hashCode() {
        final int h1 = lastMove == null ? 0 : lastMove.getMoveType() == MoveType.LONG_MOVE ? 1 : 2;
        int result = 31 * h1 + isWhiteCastlingPossibility;
        result = 31 * result + isBlackCastlingPossibility;
        result = 31 * result + (isWhiteMove ? 1 : 0);
        return 31 * result + boardSnapshotHash;
    }
}
