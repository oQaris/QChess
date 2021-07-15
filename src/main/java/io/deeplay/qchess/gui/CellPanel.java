package io.deeplay.qchess.gui;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessErrorCode;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

class CellPanel extends JPanel {
    private final int cellId;
    private final BoardPanel boardPanel;

    CellPanel(final BoardPanel boardPanel, final int cellId) throws ChessException {
        super(new GridBagLayout());
        this.cellId = cellId;
        this.boardPanel = boardPanel;
        this.setPreferredSize(Table.CELL_PANEL_DIMENSION);
        this.assignCellColor();
        this.assignCellFigureIcon(Table.board);
        CellPanel cp = this;

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isLeftMouseButton(e)) {
                    try {
                        Figure figure = Table.board.getFigure(new Cell(cellId % 8,cellId / 8));
                        if(figure != null) {
                            cp.setBackground(Color.ORANGE);
                            Set<Move> moveList = figure.getAllMoves(new GameSettings(BoardFilling.STANDARD));
                            for(Move move : moveList) {
                                Cell cell = move.getTo();
                                int id = cell.getRow() * 8 + cell.getColumn();
                                boardPanel.boardCells.get(id).setBackground(Color.GREEN);
                            }
                        }
                    } catch (ChessException | ChessError chessException) {
                        // what
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        this.validate();
    }

    private void assignCellFigureIcon(final Board board) throws ChessException {
        this.removeAll();

        Figure figure = board.getFigure(new Cell(cellId % 8,cellId / 8));
        if(figure != null) {
            try {
                String colorString = figure.getColor().toString();
                String typeString = figure.getType().toString();
                final BufferedImage image = ImageIO.read(new File(
                    String.format("%s/%s_%s.png", Table.defaultFigureImagesPath, colorString.toLowerCase(), typeString.toLowerCase())));

                ImageIcon icon = new ImageIcon(image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                JLabel label = new JLabel(icon);
                this.add(label);

            } catch (IOException e) {
                //logger + what do i can do?
                e.printStackTrace();
            }

        }
    }

    private void assignCellColor() {
        this.setBackground((cellId / 8 + cellId % 8) % 2 == 0? Table.lightCellColor : Table.darkCellColor);
    }
}
