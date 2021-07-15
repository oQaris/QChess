package io.deeplay.qchess.gui;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Table {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    static final Dimension CELL_PANEL_DIMENSION = new Dimension(10, 10);
    static final Color lightCellColor = Color.decode("#FFFFE0");
    static final Color darkCellColor = Color.decode("#5F9EA0");
    static final String defaultFigureImagesPath = "art/figures/onestyle";
    static Board board;

    public Table(final Board board) throws ChessException {
        Table.board = board;
        this.gameFrame = new JFrame("SHAKHMATY");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setResizable(false);

        this.boardPanel = new BoardPanel();
        this.gameFrame.add(boardPanel, BorderLayout.CENTER);

        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);

        this.gameFrame.setVisible(true);
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
}
