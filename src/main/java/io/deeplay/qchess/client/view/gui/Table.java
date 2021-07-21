package io.deeplay.qchess.client.view.gui;

import static javax.swing.SwingUtilities.isLeftMouseButton;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.view.model.ViewFigure;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Table {
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
    private static final String defaultFigureImagesPath = "/art/figures";
    private static final String[] figures = {"Ферзь", "Ладья", "Конь", "Слон"};
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final String figureStyle;
    private final boolean myColor;
    private final Set<Integer> taggedCells = new HashSet<>();
    private int clickedCell;

    public Table(String figureStyle, boolean myColor) {
        this.myColor = myColor;
        this.figureStyle = figureStyle;
        this.gameFrame = new JFrame("SHAKHMATY");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setResizable(false);

        try (InputStream png = getClass().getResourceAsStream("/art/other/icon.png")) {
            assert png != null;
            final BufferedImage image = ImageIO.read(png);
            this.gameFrame.setIconImage(image);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        this.boardPanel = new BoardPanel();
        this.gameFrame.add(boardPanel, BorderLayout.CENTER);

        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);

        this.gameFrame.setVisible(true);

        clickedCell = -1;

        this.gameFrame.addWindowListener(
                new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {}

                    @Override
                    public void windowClosing(WindowEvent event) {
                        Object[] options = {"Да", "Нет!"};
                        int n =
                                JOptionPane.showOptionDialog(
                                        event.getWindow(),
                                        "Закрыть окно?",
                                        "Подтверждение",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        options,
                                        options[0]);
                        if (n == 0) {
                            event.getWindow().setVisible(false);
                            ClientController.disconnect();
                            System.exit(0);
                        }
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {}

                    @Override
                    public void windowIconified(WindowEvent e) {}

                    @Override
                    public void windowDeiconified(WindowEvent e) {}

                    @Override
                    public void windowActivated(WindowEvent e) {}

                    @Override
                    public void windowDeactivated(WindowEvent e) {}
                });
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.addActionListener(e -> System.out.println("loading..."));
        fileMenu.add(loadMenuItem);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private int inverseInt(int i, int size) {
        int inverse = myColor ? 0 : 1;
        return (1 - 2 * inverse) * (i - (size - 1) * inverse);
    }

    public void repaint() {
        boardPanel.drawBoard();
    }

    public void endGame() {
        EndGame endGame = ClientController.getEndGame();
        if (endGame.isEnd()) {
            Object[] options = {"Да", "Нет!"};
            int n =
                    JOptionPane.showOptionDialog(
                            gameFrame,
                            endGame.getStatus() + "\nЗакрыть окно?",
                            "Подтверждение",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);
            if (n == 0) {
                gameFrame.setVisible(false);
                ClientController.disconnect();
                System.exit(0);
            }
        }
    }

    private class BoardPanel extends JPanel {
        final List<CellPanel> boardCells;

        BoardPanel() {
            super(new GridLayout(BOARD_SIZE, BOARD_SIZE));
            this.boardCells = new ArrayList<>();
            int size = BOARD_SIZE * BOARD_SIZE;
            for (int i = 0; i < size; i++) {
                final CellPanel cellPanel = new CellPanel(this, inverseInt(i, size));
                this.boardCells.add(cellPanel);
                this.add(cellPanel);
            }
            if (!myColor) {
                Collections.reverse(boardCells);
            }
            this.setPreferredSize(Table.BOARD_PANEL_DIMENSION);
            this.validate();
        }

        private void drawBoard() {
            validate();
            repaint();
            for (CellPanel cp : boardCells) {
                cp.drawCell();
            }
        }
    }

    private class CellPanel extends JPanel {
        private final int cellId;
        private final BoardPanel boardPanel;

        CellPanel(final BoardPanel boardPanel, final int cellId) {
            super(new GridBagLayout());
            this.cellId = cellId;
            this.boardPanel = boardPanel;
            this.setPreferredSize(Table.CELL_PANEL_DIMENSION);
            this.assignCellColor();
            this.assignCellFigureIcon();
            CellPanel thisCellPanel = this;

            this.addMouseListener(
                    new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {}

                        @Override
                        public void mousePressed(MouseEvent e) {
                            if (isLeftMouseButton(e) && ClientController.isWhiteStep()) {
                                // if (isLeftMouseButton(e)) {
                                boolean twoClick = false;
                                if (ClientController.checkFigure(
                                        cellId / BOARD_SIZE, cellId % BOARD_SIZE, myColor)) {
                                    if (clickedCell != -1) {
                                        twoClick = clickedCell == cellId;
                                        clearColorOnBoard();
                                    }
                                    if (clickedCell != cellId & !twoClick) {
                                        setColorOnBoard();
                                    }
                                } else if (taggedCells.contains(cellId)) {
                                    // move
                                    int action =
                                            ClientController.tryMakeMove(
                                                    clickedCell / BOARD_SIZE,
                                                    clickedCell % BOARD_SIZE,
                                                    cellId / BOARD_SIZE,
                                                    cellId % BOARD_SIZE);
                                    if (action > 0) {
                                        System.out.println("Watafak1");

                                        Object turnFigure = null;
                                        if (action == 2) {
                                            turnFigure =
                                                    JOptionPane.showInputDialog(
                                                            gameFrame,
                                                            "Выберите фигуру для замены :",
                                                            "Выбор фигуры",
                                                            JOptionPane.QUESTION_MESSAGE,
                                                            null,
                                                            figures,
                                                            figures[0]);
                                            // Диалоговое окно вывода сообщения
                                            JOptionPane.showMessageDialog(gameFrame, turnFigure);
                                        }
                                        ClientController.makeMove(
                                                clickedCell / BOARD_SIZE,
                                                clickedCell % BOARD_SIZE,
                                                cellId / BOARD_SIZE,
                                                cellId % BOARD_SIZE,
                                                turnFigure);

                                        boardPanel.boardCells.get(clickedCell).drawCell();
                                        thisCellPanel.drawCell();
                                        clearColorOnBoard();
                                        ClientController.repaint = true;
                                    } else if (action == 0) {
                                        clearColorOnBoard();
                                    }
                                    System.out.println("Watafak");
                                }
                            }
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {}

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            if (ClientController.isWhiteStep()) {
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
                        public void mouseExited(MouseEvent e) {
                            if (ClientController.isWhiteStep()) {
                                if (thisCellPanel.getBackground() == chooseHoverCellColor) {
                                    thisCellPanel.setBackground(chooseCellColor);
                                } else if (thisCellPanel.getBackground()
                                        == quietPossibleHoverCellColor) {
                                    thisCellPanel.setBackground(quietPossibleCellColor);
                                } else if (thisCellPanel.getBackground() == attackHoverCellColor) {
                                    thisCellPanel.setBackground(attackCellColor);
                                } else if (ClientController.checkFigure(
                                        cellId / BOARD_SIZE, cellId % BOARD_SIZE, myColor)) {
                                    assignCellColor();
                                }
                            }
                        }
                    });

            this.validate();
        }

        private void assignCellFigureIcon() {
            this.removeAll();
            ViewFigure figure =
                    ClientController.getFigure(cellId / BOARD_SIZE, cellId % BOARD_SIZE);
            if (figure != null) {
                try (InputStream png =
                        getClass()
                                .getResourceAsStream(
                                        String.format(
                                                "%s/%s/%s_%s.png",
                                                defaultFigureImagesPath,
                                                figureStyle,
                                                figure.getColor().toLowerCase(),
                                                figure.getType().toString().toLowerCase()))) {
                    assert png != null;
                    final BufferedImage image = ImageIO.read(png);

                    ImageIcon icon =
                            new ImageIcon(image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                    JLabel label = new JLabel(icon);
                    this.add(label);

                } catch (IOException | NullPointerException e) {
                    // logger + what do i can do?
                    e.printStackTrace();
                }
            }
            this.repaint();
        }

        private void assignCellColor() {
            this.setBackground(
                    (cellId / BOARD_SIZE + cellId % BOARD_SIZE) % 2 == 0
                            ? Table.lightCellColor
                            : Table.darkCellColor);
        }

        private void drawCell() {
            assignCellColor();
            assignCellFigureIcon();
            validate();
            repaint();
        }

        private void clearColorOnBoard() {
            for (Integer id : taggedCells) {
                boardPanel.boardCells.get(id).assignCellColor();
            }
            clickedCell = -1;
            taggedCells.clear();
        }

        private void setColorOnBoard() {
            clickedCell = cellId;
            this.setBackground(chooseHoverCellColor);
            Set<ViewCell> cellList =
                    ClientController.getAllMoves(cellId / BOARD_SIZE, cellId % BOARD_SIZE);
            taggedCells.add(cellId);
            if (!cellList.isEmpty()) {
                for (ViewCell cell : cellList) {
                    int id = cell.getRow() * BOARD_SIZE + cell.getColumn();
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
