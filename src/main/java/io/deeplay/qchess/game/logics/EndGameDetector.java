package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.ERROR_WHILE_CHECKING_FOR_DRAW;

public class EndGameDetector {
  private final GameSettings roomSettings;
  private int pieceMoveCount = 0;

  public EndGameDetector(GameSettings roomSettings) {
    this.roomSettings = roomSettings;
  }

  /** @return true, если это не ничья */
  public boolean isNotDraw(Figure removedFigure, Move move) throws ChessError {
    try {
      return !isDrawWithMoves(removedFigure, move)
          && !isDrawWithRepetitions()
          && !isNotEnoughMaterialForCheckmate();
    } catch (ChessException e) {
      throw new ChessError(ERROR_WHILE_CHECKING_FOR_DRAW, e);
    }
  }

  /**
   * Условия ничьи: 1) пешка не ходит 50 ходов 2) никто не рубит
   *
   * @return true, если ничья
   */
  private boolean isDrawWithMoves(Figure removedFigure, Move move) throws ChessException {
    if (removedFigure != null
        || roomSettings.board.getFigure(move.getTo()).getType() == TypeFigure.PAWN) {
      pieceMoveCount = 0;
    } else {
      ++pieceMoveCount;
    }
    return pieceMoveCount == 50;
  }

  /**
   * Условия ничьи: минимум 5 повторений позиций доски
   *
   * @return true, если ничья
   */
  private boolean isDrawWithRepetitions() {
    return roomSettings.history.checkRepetitions(5);
  }

  private boolean isNotEnoughMaterialForCheckmate() {
    List<List<TypeFigure>> material =
        Arrays.asList(
            Collections.singletonList(TypeFigure.KING),
            Arrays.asList(TypeFigure.KING, TypeFigure.KNIGHT),
            Arrays.asList(TypeFigure.KING, TypeFigure.BISHOP),
            Arrays.asList(TypeFigure.KING, TypeFigure.KNIGHT, TypeFigure.KNIGHT));

    return material.stream()
        .anyMatch(
            m ->
                (isAllFiguresSame(Color.BLACK, m)
                        && isAllFiguresSame(
                            Color.WHITE, Collections.singletonList(TypeFigure.KING)))
                    || (isAllFiguresSame(Color.WHITE, m)
                            && isAllFiguresSame(
                                Color.BLACK, Collections.singletonList(TypeFigure.KING)))
                        && isKingsWithSameBishop());
  }

  private boolean isAllFiguresSame(Color color, List<TypeFigure> typeFigures) {
    List<TypeFigure> typeFiguresCopy = new ArrayList<>(typeFigures);
    for (Figure figure : roomSettings.board.getFigures(color))
      if (!typeFiguresCopy.remove(figure.getType())) return false;
    return true;
  }

  private boolean isKingsWithSameBishop() {
    Figure whiteBishop = getBishop(Color.WHITE);
    Figure blackBishop = getBishop(Color.BLACK);

    if (whiteBishop == null || blackBishop == null) return false;

    Cell whiteBishopPosition = whiteBishop.getCurrentPosition();
    Cell blackBishopPosition = blackBishop.getCurrentPosition();

    return (whiteBishopPosition.getColumn() + whiteBishopPosition.getRow()) % 2
        == (blackBishopPosition.getColumn() + blackBishopPosition.getRow()) % 2;
  }

  private Figure getBishop(Color color) {
    for (Figure figure : roomSettings.board.getFigures(color))
      if (figure.getType() == TypeFigure.BISHOP) return figure;
    return null;
  }

  /** @return true, если установленному цвету поставили мат */
  public boolean isCheckmate(Color color) throws ChessError {
    return isStalemate(color) && isCheck(color);
  }

  /** @return true, если установленному цвету поставили пат (нет доступных ходов) */
  public boolean isStalemate(Color color) throws ChessError {
    return roomSettings.moveSystem.getAllCorrectMoves(color).isEmpty();
  }

  /** @return true если игроку с указанным цветом ставят шах */
  public boolean isCheck(Color color) throws ChessError {
    return Board.isAttackedCell(
        roomSettings, roomSettings.board.findKingCell(color), color.inverse());
  }
}
