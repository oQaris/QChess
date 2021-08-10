package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import java.util.List;

public class Knight extends Figure {

    public Knight(final Color color, final Cell position) {
        super(color, position, FigureType.KNIGHT);
    }

    @Override
    public List<Move> getAllMoves(final GameSettings settings) {
        return stepForEach(settings.board, Figure.knightMove, false);
    }

    @Override
    public boolean isAttackedCell(final GameSettings settings, final Cell cell) {
        for (final Cell c : Figure.knightMove) if (position.createAdd(c).equals(cell)) return true;
        return false;
    }
}
