package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import java.util.Set;

public class Knight extends Figure {

    public Knight(Color color, Cell position) {
        super(color, position);
    }

    @Override
    public Set<Move> getAllMoves(GameSettings settings) {
        return stepForEach(settings.board, Figure.knightMove);
    }

    @Override
    public FigureType getType() {
        return FigureType.KNIGHT;
    }
}