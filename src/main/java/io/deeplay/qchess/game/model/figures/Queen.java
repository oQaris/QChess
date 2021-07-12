package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Queen extends Figure {

    public Queen(Color color, Cell position) {
        super(color, position);
    }

    @Override
    public Set<Move> getAllMoves(GameSettings settings) {
        return rayTrace(settings.board,
                Stream.concat(xMove.stream(), plusMove.stream())
                        .collect(Collectors.toList()));
    }

    @Override
    public TypeFigure getType() {
        return TypeFigure.QUEEN;
    }
}