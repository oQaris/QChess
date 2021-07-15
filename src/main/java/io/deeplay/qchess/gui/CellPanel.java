package io.deeplay.qchess.gui;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

class CellPanel extends JPanel {
    private final int cellId;

    CellPanel(final BoardPanel boardPanel, final int cellId) throws ChessException {
        super(new GridBagLayout());
        this.cellId = cellId;
        this.setPreferredSize(Table.CELL_PANEL_DIMENSION);
        this.assignCellColor();
        this.assignCellFigureIcon(Table.board);
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

                //System.out.println(this.getPreferredSize());
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
