package io.deeplay.qchess.experimental;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_COORDINATES;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.model.figures.Pawn;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// не, плохая идея
public class Board {
  private static final Logger logger = LoggerFactory.getLogger(Board.class);

  public final int boardSize;
  private final Figure[][] cells;

  public Board(int size, BoardFilling fillingType) {
    boardSize = size;
    cells = new Figure[boardSize][boardSize];
    fill(fillingType);
  }

  public Board(BoardFilling fillingType) {
    this(8, fillingType);
  }

  private void fill(BoardFilling fillingType){
    logger.debug("Начато заполнение {} доски", fillingType);
      switch (fillingType) {
        case STANDARD -> fillBoardForFirstLine(
            new FigureType[] {
                FigureType.ROOK, FigureType.KNIGHT, FigureType.BISHOP,
                FigureType.QUEEN, FigureType.KING, FigureType.BISHOP,
                FigureType.KNIGHT, FigureType.ROOK
            });
        case CHESS960 -> fillBoardForFirstLine(
            //todo Добавить рандома
            new FigureType[] {
                FigureType.KNIGHT, FigureType.QUEEN, FigureType.ROOK,
                FigureType.KING, FigureType.BISHOP, FigureType.ROOK,
                FigureType.KNIGHT, FigureType.BISHOP
            });
      }
    logger.debug("Доска {} инициализирована", fillingType);
  }

  private void fillBoardForFirstLine(FigureType[] orderFirstLine){
    Cell startBlack = new Cell(0, 0);
    Cell startWhite = new Cell(0, boardSize - 1);
    Cell shift = new Cell(1, 0);

    for (FigureType figureType : orderFirstLine) {
      setFigure(Figure.build(figureType, Color.BLACK, startBlack));
      setFigure(new Pawn(Color.BLACK, startBlack.createAdd(new Cell(0, 1))));
      setFigure(Figure.build(figureType, Color.WHITE, startWhite));
      setFigure(new Pawn(Color.WHITE, startWhite.createAdd(new Cell(0, -1))));
      startBlack.shift(shift);
      startWhite.shift(shift);
    }
  }

  /** Устанавливает фигуру на доску */
  public void setFigure(Figure figure) {
    int x = figure.getCurrentPosition().getColumn();
    int y = figure.getCurrentPosition().getRow();
    cells[y][x] = figure;
    logger.trace("Фигура {} установлена на доску", figure);
  }

  /**
   * @param color цвет игрока
   * @return фигуры определенного цвета
   */
  public List<Figure> getFigures(Color color) {
    List<Figure> list = new ArrayList<>(16);
    for (Figure[] figures : cells)
      for (Figure figure : figures)
        if (figure != null && figure.getColor() == color) list.add(figure);
    return list;
  }

  /** @return все фигуры на доске */
  public List<Figure> getAllFigures() {
    List<Figure> list = new ArrayList<>(32);
    for (Figure[] figures : cells)
      for (Figure figure : figures) if (figure != null) list.add(figure);
    return list;
  }
}
