package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class History {
    private static final Logger log = LoggerFactory.getLogger(History.class);

    private List<String> recordsList;
    private Board board;

    public History(Board board) {
        this.board = board;
        recordsList = new ArrayList<>(50);
        log.debug("История инициализирована");
    }

    public String addRecord() throws ChessException {
        String record = convertBoardToStringForsytheEdwards(board);
        recordsList.add(record);
        log.debug("Запись {} добавлена в историю", record);
        return record;
    }

    private String convertBoardToStringForsytheEdwards(Board board) throws ChessException {
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
                    emptySlots = 0;
                }
            }
            if(emptySlots != 0) {
                record.append(emptySlots);
            }
            record.append('/');
        }
        return record.toString();
    }
}
