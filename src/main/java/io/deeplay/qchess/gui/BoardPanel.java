package io.deeplay.qchess.gui;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

class BoardPanel extends JPanel {
    final List<CellPanel> boardCells;

    BoardPanel() throws ChessException {
        super(new GridLayout(8, 8));
        this.boardCells = new ArrayList<>();
        for (int i = 0; i < Board.BOARD_SIZE * Board.BOARD_SIZE; i++) {
            final CellPanel cellPanel = new CellPanel(this, i);
            this.boardCells.add(cellPanel);
            this.add(cellPanel);
        }
        this.setPreferredSize(Table.BOARD_PANEL_DIMENSION);
        this.validate();
    }
}
