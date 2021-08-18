package io.deeplay.qchess.client.view.gui;

import static javax.swing.SwingUtilities.isLeftMouseButton;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.view.model.ViewColor;
import io.deeplay.qchess.client.view.model.ViewFigure;
import io.deeplay.qchess.client.view.model.ViewFigureType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Table extends Frame {
    private static final int BOARD_SIZE = 8;
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension CELL_PANEL_DIMENSION = new Dimension(10, 10);
    private static final Color lightCellColor = Color.decode("#FFFFE0");
    private static final Color darkCellColor = Color.decode("#5F9EA0");
    private static final Color chooseCellColor = Color.decode("#FFD700");
    private static final Color chooseHoverCellColor = Color.decode("#FFFF00");
    private static final Color quietPossibleCellColor = Color.decode("#90EE90");
    private static final Color quietPossibleHoverCellColor = Color.decode("#ADFF2F");
    private static final Color attackCellColor = Color.decode("#B22222");
    private static final Color attackHoverCellColor = Color.decode("#8B0000");
    private static final Color hoverCellColor = Color.CYAN;

    /**
     * Этот путь указывает на файлы в .jar, и используется, если файлы по обычным путям не были
     * найдены или повреждены
     */
    private static final String JAR_figureImagesPath = "/art/figures";
    /**
     * Этот путь указывает на файлы в .jar, и используется, если файлы по обычным путям не были
     * найдены или повреждены
     */
    private static final String JAR_iconPath = "/art/other/icon.png";

    // TODO: получать путь из конфига
    // TODO: нужно сделать пути до картинок в конфиге
    /** Обычный путь на файлы, лежащие рядом с .jar */
    private static final String figureImagesPath = "./art/figures";
    /** Обычный путь на файлы, лежащие рядом с .jar */
    private static final String iconPath = "./art/other/icon.png";

    private static final String[] figures = {"Ферзь", "Ладья", "Конь", "Слон"};
    private final String figureStyle;
    private final Set<Integer> taggedCells = new HashSet<>();
    private BoardPanel boardPanel;
    private boolean myColor;
    private int clickedCell;
    private final Map<ViewFigure, ImageIcon> figureArtMap = new HashMap<>();

    public Table(final String figureStyle, final boolean myColor, final MainFrame mf) {
        this.figureStyle = figureStyle;
        loadArts();
        this.mf = mf;
        this.myColor = myColor;
        frame = new JFrame("QChess");
        frame.setLayout(new BorderLayout());
        frame.setSize(OUTER_FRAME_DIMENSION);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        final File file = new File(iconPath);
        try (final InputStream png =
                file.exists() && file.canRead()
                        ? new FileInputStream(file)
                        : getClass().getResourceAsStream(JAR_iconPath)) {
            final BufferedImage image = ImageIO.read(png);
            frame.setIconImage(image);
        } catch (final IOException | NullPointerException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        boardPanel = new BoardPanel();
        frame.add(boardPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        clickedCell = -1;

        frame.addWindowListener(new CloseFrameListener(this));

        if (mf.getMyPlayerType() != PlayerType.USER) {
            frame.addKeyListener(
                    new KeyAdapter() {
                        @Override
                        public void keyReleased(final KeyEvent e) {
                            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                                if (ClientController.isMyStep()) {
                                    ClientController.botMove();
                                }
                            }
                        }
                    });
        }
    }

    public void setMyColor(final boolean myColor) {
        this.myColor = myColor;
    }

    public void remakeBoardPanel() {
        frame.remove(boardPanel);
        boardPanel = new BoardPanel();
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.validate();
        frame.repaint();
    }

    @Override
    public void repaint() {
        boardPanel.drawBoard();
    }

    public void showMessage(final String message) {
        new MessageFrame(frame, "Игра окончена", message);
    }

    public void closeGame(final String message) {
        showMessage(message);
        mf.destroyTable();
        try {
            ClientController.disconnect("Игра окончена");
        } catch (final ClientException e) {
            System.err.println(e.getMessage());
        }
    }

    private void initFigureArtMap() {
        for(final ViewColor viewColor : ViewColor.values()) {
            for(final ViewFigureType viewFigureType : ViewFigureType.values()) {
                figureArtMap.put(new ViewFigure(viewColor.toString(), viewFigureType), null);
            }
        }
    }

    private void loadArts() {
        initFigureArtMap();
        for(final ViewFigure viewFigure : figureArtMap.keySet()) {
            final File file = new File(getFigureImagesPath(figureImagesPath, viewFigure));
            try (final InputStream png =
                file.exists() && file.canRead()
                    ? new FileInputStream(file)
                    : getClass()
                        .getResourceAsStream(
                            getFigureImagesPath(
                                JAR_figureImagesPath, viewFigure))) {
                final BufferedImage image = ImageIO.read(png);
                final ImageIcon icon =
                    new ImageIcon(image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                figureArtMap.put(viewFigure, icon);

            } catch (final IOException | NullPointerException | IllegalArgumentException e) { }
        }
    }

    private String getFigureImagesPath(
        final String figureImagesDirectoryPath, final ViewFigure figure) {
        return String.format(
            "%s/%s/%s_%s.png",
            figureImagesDirectoryPath,
            figureStyle,
            figure.getColor().toLowerCase(),
            figure.getType().toString().toLowerCase());
    }

    private class BoardPanel extends JPanel {
        final List<CellPanel> boardCells;

        BoardPanel() {
            super(new GridLayout(BOARD_SIZE, BOARD_SIZE));
            boardCells = new ArrayList<>();
            final int size = BOARD_SIZE * BOARD_SIZE;
            for (int i = 0; i < size; i++) {
                final CellPanel cellPanel = new CellPanel(this, inverseInt(i, size));
                cellPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                boardCells.add(cellPanel);
                add(cellPanel);
            }
            if (!myColor) {
                Collections.reverse(boardCells);
            }
            setPreferredSize(Table.BOARD_PANEL_DIMENSION);
            validate();
        }

        private int inverseInt(final int i, final int size) {
            final int inverse = myColor ? 0 : 1;
            return (1 - 2 * inverse) * (i - (size - 1) * inverse);
        }

        private void drawBoard() {
            for (final CellPanel cp : boardCells) {
                cp.drawCell();
            }
            validate();
            super.repaint();

            ViewCell cell = null;
            if (ClientController.isCheck(myColor)) {
                cell = ClientController.getKingCell(myColor);
                boardCells.get(cell.getRow() * BOARD_SIZE + cell.getColumn()).cellColor =
                        attackCellColor;
                boardCells.get(cell.getRow() * BOARD_SIZE + cell.getColumn()).assignCellColor();
            }
        }
    }

    private class CellPanel extends JPanel {
        private final int cellId;
        private final BoardPanel boardPanel;
        private Color cellColor;

        CellPanel(final BoardPanel boardPanel, final int cellId) {
            super(new GridBagLayout());
            this.cellId = cellId;
            this.boardPanel = boardPanel;
            setPreferredSize(Table.CELL_PANEL_DIMENSION);

            cellColor =
                    (cellId / BOARD_SIZE + cellId % BOARD_SIZE) % 2 == 0
                            ? Table.lightCellColor
                            : Table.darkCellColor;

            assignCellColor();
            assignCellFigureIcon();
            final CellPanel thisCellPanel = this;

            if (mf.getMyPlayerType() == PlayerType.USER) {
                addMouseListener(
                        new MouseAdapter() {
                            @Override
                            public void mousePressed(final MouseEvent e) {
                                // TODO: Refactor this method to reduce its Cognitive Complexity
                                if (isLeftMouseButton(e) && ClientController.isMyStep()) {
                                    boolean twoClick = false;
                                    if (ClientController.checkFigure(
                                            cellId / BOARD_SIZE, cellId % BOARD_SIZE, myColor)) {
                                        if (clickedCell != -1) {
                                            twoClick = clickedCell == cellId;
                                            clearColorOnBoard();
                                        }
                                        if (clickedCell != cellId && !twoClick) {
                                            setColorOnBoard();
                                        }
                                    } else if (taggedCells.contains(cellId)) {
                                        // move
                                        final int action =
                                                ClientController.tryMakeMove(
                                                        clickedCell / BOARD_SIZE,
                                                        clickedCell % BOARD_SIZE,
                                                        cellId / BOARD_SIZE,
                                                        cellId % BOARD_SIZE);
                                        if (action > 0) {
                                            Object turnFigure = null;
                                            if (action == 2) {
                                                turnFigure =
                                                        JOptionPane.showInputDialog(
                                                                frame,
                                                                "Выберите фигуру для замены :",
                                                                "Выбор фигуры",
                                                                JOptionPane.QUESTION_MESSAGE,
                                                                null,
                                                                figures,
                                                                figures[0]);
                                                // Диалоговое окно вывода сообщения
                                                JOptionPane.showMessageDialog(
                                                        frame,
                                                        turnFigure,
                                                        "Оповещение",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                            }
                                            try {
                                                ClientController.makeMove(
                                                        clickedCell / BOARD_SIZE,
                                                        clickedCell % BOARD_SIZE,
                                                        cellId / BOARD_SIZE,
                                                        cellId % BOARD_SIZE,
                                                        turnFigure);
                                            } catch (final ClientException clientException) {
                                                clientException.printStackTrace();
                                            }

                                            boardPanel.boardCells.get(clickedCell).drawCell();
                                            thisCellPanel.drawCell();
                                            if (action == 4) {
                                                boardPanel.drawBoard();
                                            }

                                            if (action == 3) {
                                                final int coeff = myColor ? BOARD_SIZE : 1;
                                                for (int i = BOARD_SIZE * (coeff - 1);
                                                        i < BOARD_SIZE * coeff;
                                                        i++) {
                                                    boardPanel.boardCells.get(i).drawCell();
                                                }
                                            }
                                            boardPanel.validate();
                                            boardPanel.repaint();
                                            clearColorOnBoard();
                                        } else if (action == 0) {
                                            clearColorOnBoard();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void mouseEntered(final MouseEvent e) {
                                if (ClientController.isMyStep()) {
                                    if (thisCellPanel.getBackground() == chooseCellColor) {
                                        thisCellPanel.setBackground(chooseHoverCellColor);
                                    } else if (thisCellPanel.getBackground()
                                            == quietPossibleCellColor) {
                                        thisCellPanel.setBackground(quietPossibleHoverCellColor);
                                    } else if (thisCellPanel.getBackground() == attackCellColor) {
                                        thisCellPanel.setBackground(attackHoverCellColor);
                                    } else if (ClientController.checkFigure(
                                            cellId / BOARD_SIZE, cellId % BOARD_SIZE, myColor)) {
                                        thisCellPanel.setBackground(hoverCellColor);
                                    }
                                }
                            }

                            @Override
                            public void mouseExited(final MouseEvent e) {
                                if (ClientController.isMyStep()) {
                                    if (thisCellPanel.getBackground() == chooseHoverCellColor) {
                                        thisCellPanel.setBackground(chooseCellColor);
                                    } else if (thisCellPanel.getBackground()
                                            == quietPossibleHoverCellColor) {
                                        thisCellPanel.setBackground(quietPossibleCellColor);
                                    } else if (thisCellPanel.getBackground()
                                            == attackHoverCellColor) {
                                        thisCellPanel.setBackground(attackCellColor);
                                    } else if (ClientController.checkFigure(
                                            cellId / BOARD_SIZE, cellId % BOARD_SIZE, myColor)) {
                                        assignCellColor();
                                    }
                                }
                            }
                        });
            }
            validate();
        }

        private void assignCellFigureIcon() {
            removeAll();
            final ViewFigure figure =
                    ClientController.getFigure(cellId / BOARD_SIZE, cellId % BOARD_SIZE);
            if (figure != null) {
                final JLabel label = new JLabel(figureArtMap.get(figure));
                removeAll();
                add(label);
            }
            super.repaint();
        }

        private void assignCellColor() {
            setBackground(cellColor);
        }

        private void drawCell() {
            cellColor =
                    (cellId / BOARD_SIZE + cellId % BOARD_SIZE) % 2 == 0
                            ? Table.lightCellColor
                            : Table.darkCellColor;
            assignCellColor();
            assignCellFigureIcon();
            validate();
            super.repaint();
        }

        private void clearColorOnBoard() {
            for (final Integer id : taggedCells) {
                boardPanel.boardCells.get(id).assignCellColor();
            }
            clickedCell = -1;
            taggedCells.clear();
        }

        private void setColorOnBoard() {
            clickedCell = cellId;
            setBackground(chooseHoverCellColor);
            final Set<ViewCell> cellList =
                    ClientController.getAllMoves(cellId / BOARD_SIZE, cellId % BOARD_SIZE);
            taggedCells.add(cellId);
            if (!cellList.isEmpty()) {
                for (final ViewCell cell : cellList) {
                    final int id = cell.getRow() * BOARD_SIZE + cell.getColumn();
                    taggedCells.add(id);
                    boardPanel
                            .boardCells
                            .get(id)
                            .setBackground(
                                    cell.isAttack() ? attackCellColor : quietPossibleCellColor);
                }
            }
        }
    }
}
