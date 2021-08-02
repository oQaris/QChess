package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Queen extends Figure {

    public Queen(Color color, Cell position) {
        super(color, position, FigureType.QUEEN);
    }

    @Override
    public Set<Move> getAllMoves(GameSettings settings) {
        return rayTrace(
                settings.board,
                Stream.concat(Figure.xMove.stream(), Figure.plusMove.stream())
                        .collect(Collectors.toList()));
    }

    @Override
    public boolean isAttackedCell(GameSettings settings, Cell cell) {
        return Rook.isAttackedCell(settings, position, cell)
                || Bishop.isAttackedCell(settings, position, cell);
    }
}
