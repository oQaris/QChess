package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History {
    private static final Logger log = LoggerFactory.getLogger(History.class);

    private final Map<Character, Character> notation = new HashMap<>();
    private List<String> recordsList;
    private Board board;
    private boolean whiteStep = true;
    private Move prevMove;

    public History(Board board) throws ChessException {
        this.board = board;
        recordsList = new ArrayList<>(50);
        log.debug("История инициализирована");

        notation.put('♔', 'K');
        notation.put('♕', 'Q');
        notation.put('♖', 'R');
        notation.put('♗', 'B');
        notation.put('♘', 'N');
        notation.put('♙', 'P');
        notation.put('♚', 'k');
        notation.put('♛', 'q');
        notation.put('♜', 'r');
        notation.put('♝', 'b');
        notation.put('♞', 'n');
        notation.put('♟', 'p');

        addRecord();
    }

    public String addRecord() throws ChessException {
        String record = convertBoardToStringForsytheEdwards();
        recordsList.add(record);
        log.debug("Запись {} добавлена в историю", record);
        whiteStep = !whiteStep;
        return record;
    }

    private String convertBoardToStringForsytheEdwards() throws ChessException {
        StringBuilder record = new StringBuilder(70);
        Figure currentFigure = null;

        for(int y = 0; y < Board.BOARD_SIZE; y++) {
            int emptySlots = 0;
            for(int x = 0; x < Board.BOARD_SIZE; x++) {

                currentFigure = board.getFigure(y, x);

                if(currentFigure == null) {
                    emptySlots++;
                } else {
                    if(emptySlots != 0) {
                        record.append(emptySlots);
                    }
                    record.append(notation.get(currentFigure.getCharIcon()));
                    emptySlots = 0;
                }
            }

            if(emptySlots != 0) {
                record.append(emptySlots);
            }
            record.append('/');
        }

        record.deleteCharAt(record.length() - 1);
        record.append(' ').append(whiteStep ? 'w' : 'b');

        String castlingPossibility = getCastlingPossibility();
        if(!"".equals(castlingPossibility)) {
            record.append(' ').append(castlingPossibility);
        }

        if(prevMove.getMoveType() == MoveType.LONG_MOVE) {
            record.append(' ').append(prevMove.getTo().toString().charAt(0));
            record.append(board.getFigure(prevMove.getTo()).isWhite()? '2' : '7');
        }

        return record.toString();
    }

    private String getCastlingPossibility() throws ChessException {
        StringBuilder result = new StringBuilder(4);
        Figure shortRook = null;
        Figure longRook = null;
        Figure whiteKing = board.getFigure(Cell.parse("e1"));
        if(whiteKing != null && !whiteKing.wasMoved()) {
            shortRook = board.getFigure(Cell.parse("h1"));
            longRook = board.getFigure(Cell.parse("a1"));
            if(shortRook != null && !shortRook.wasMoved()) {
                result.append('K');
            }
            if(longRook != null && !longRook.wasMoved()) {
                result.append('Q');
            }
        }

        Figure blackKing = board.getFigure(Cell.parse("e8"));
        if(blackKing != null && !blackKing.wasMoved()) {
            shortRook = board.getFigure(Cell.parse("h8"));
            longRook = board.getFigure(Cell.parse("a8"));
            if(shortRook != null && !shortRook.wasMoved()) {
                result.append('k');
            }
            if(longRook != null && !longRook.wasMoved()) {
                result.append('q');
            }
        }
        return result.toString();
    }

    public Move getPrevMove() {
        return prevMove;
    }

    public void setPrevMove(Move prevMove) {
        this.prevMove = prevMove;
    }
}
