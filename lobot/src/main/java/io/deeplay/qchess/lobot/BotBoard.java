package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import java.util.Arrays;

public class BotBoard {
    private final char[][] board;

    public BotBoard(Board board) {
        this.board = new char[Board.STD_BOARD_SIZE][Board.STD_BOARD_SIZE];
        for (int i = 0; i < Board.STD_BOARD_SIZE; i++) {
            Arrays.fill(this.board[i], 'e');
            for(int k = 0; k < Board.STD_BOARD_SIZE; k++) {
                Figure figure = null;
                try {
                    figure = board.getFigure(new Cell(k, i));
                } catch (ChessException e) {
                    e.printStackTrace();
                }
                if (figure != null) {
                    char charFigure = FigureService.convertFigureToChar(figure);
                    if (figure.getColor() == Color.WHITE) {
                        charFigure = Character.toUpperCase(charFigure);
                    }
                    this.board[i][k] = charFigure;
                }
            }
        }
    }

    private BotBoard(BotBoard bb) {
        board = new char[Board.STD_BOARD_SIZE][Board.STD_BOARD_SIZE];
        for (int i = 0; i < Board.STD_BOARD_SIZE; i++) {
            board[i] = Arrays.copyOf(bb.board[i], Board.STD_BOARD_SIZE);
        }
    }

    public BotBoard copy() {
        return new BotBoard(this);
    }

    public void makeMove(Move move) {
        int x1 = move.getFrom().getColumn();
        int y1 = move.getFrom().getRow();
        int x2 = move.getTo().getColumn();
        int y2 = move.getTo().getRow();

        board[y2][x2] = board[y1][x1];
        board[y1][x1] = 'e';

        boolean color = Character.isUpperCase(board[y2][x2]);

        if(move.getMoveType() == MoveType.EN_PASSANT) {
            board[y1][x2] = 'e';
        } else if (move.getMoveType() == MoveType.SHORT_CASTLING) {
            board[y2][x2 - 1] = board[y2][Board.STD_BOARD_SIZE - 1];
            board[y2][Board.STD_BOARD_SIZE - 1] = 'e';
        } else if (move.getMoveType() == MoveType.LONG_CASTLING) {
            board[y2][x2 + 1] = board[y2][0];
            board[y2][0] = 'e';
        } else if(move.getMoveType() == MoveType.TURN_INTO || move.getMoveType() == MoveType.TURN_INTO_ATTACK) {
            board[y2][x2] = color? 'Q' : 'q';
        }
    }

    public int getUtility(boolean color) {
        int result = 0;
        for(char[] row : board) {
            for(char cell : row) {
                if(Character.isUpperCase(cell) != color && cell != 'e') {
                    result += FigureService.convertCharFigureToInt(cell);
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BotBoard botBoard = (BotBoard) o;
        return Arrays.deepEquals(board, botBoard.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
