package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.Set;

public class Rook extends Figure {

  public Rook(Color color, Cell position) {
    super(color, position);
  }

  @Override
  public Set<Move> getAllMoves(GameSettings settings) {
    return rayTrace(settings.board, plusMove);
  }

  @Override
  public TypeFigure getType() {
    return TypeFigure.ROOK;
  }
}
