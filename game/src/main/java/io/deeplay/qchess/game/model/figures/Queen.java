package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import java.util.List;

public class Queen extends Figure {

    public Queen(final Color color, final Cell position) {
        super(color, position, FigureType.QUEEN);
    }

    @Override
    public List<Move> getAllMoves(final GameSettings settings) {
        return rayTrace(settings.board, xPlusMove);
    }

    @Override
    public boolean isAttackedCell(final Board board, final Cell cell) {
        return Rook.isAttackedCell(board, position, cell)
                || Bishop.isAttackedCell(board, position, cell);
    }
}
