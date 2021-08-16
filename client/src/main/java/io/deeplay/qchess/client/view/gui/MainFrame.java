package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.view.model.ViewColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

public class MainFrame {
    private ChooseMyColorFrame chooseMyColorFrame;
    private ChooseMyPlayerFrame chooseMyPlayerFrame;
    private ChooseEnemyPlayerFrame chooseEnemyPlayerFrame;
    private ChooseStyleFrame chooseStyleFrame;
    private ConnectFrame connectFrame;
    private Table table;
    private PlayerType myPlayerType;
    private PlayerType enemyPlayerType;
    private String style;
    private ViewColor myColor;

    public MainFrame() {
        UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
        UIManager.put("RadioButton.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
    }


    public void createStartFrame() {
        final JFrame frame = new JFrame("Начало");
        frame.setSize(new Dimension(200, 200));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        final JButton startButton = new JButton("Начать");
        startButton.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(final MouseEvent e) {
                        super.mousePressed(e);
                        createChooseMyColorFrame();
                        frame.setVisible(false);
                        frame.dispose();
                    }
                });
        //startButton.setFocusPainted(false);
        frame.add(startButton);
        frame.setVisible(true);
    }

    public void createChooseMyColorFrame() {
        chooseMyColorFrame = new ChooseMyColorFrame(this);
    }

    public void createChooseMyPlayerFrame(final ViewColor myColor) {
        this.myColor = myColor;
        chooseMyPlayerFrame = new ChooseMyPlayerFrame(this);
    }

    public void createChooseEnemyPlayerFrame(final PlayerType playerType) {
        myPlayerType = playerType;
        chooseEnemyPlayerFrame = new ChooseEnemyPlayerFrame(this);
    }

    public void createChooseStyleFrame(final PlayerType playerType) {
        enemyPlayerType = playerType;
        chooseStyleFrame = new ChooseStyleFrame(this);
    }

    public void createConnectFrame(final String style) {
        this.style = style;
        connectFrame = new ConnectFrame(this);
    }

    public void createTable(final boolean color) {
        table = new Table(style, color, this);
    }

    public void destroyChooseMyColorFrame() {
        chooseMyColorFrame.destroy();
        chooseMyColorFrame = null;
    }

    public void destroyChooseMyPlayerFrame() {
        chooseMyPlayerFrame.destroy();
        chooseMyPlayerFrame = null;
    }

    public void destroyChoosePlayerFrame() {
        chooseEnemyPlayerFrame.destroy();
        chooseEnemyPlayerFrame = null;
    }

    public void destroyChooseStyleFrame() {
        chooseStyleFrame.destroy();
        chooseStyleFrame = null;
    }

    public void destroyConnectFrame() {
        connectFrame.destroy();
        connectFrame = null;
    }

    public void destroyTable() {
        table.destroy();
        table = null;
    }

    public Table getTable() {
        return table;
    }

    public PlayerType getMyPlayerType() {
        return myPlayerType;
    }

    public PlayerType getEnemyPlayerType() {
        return enemyPlayerType;
    }

    public String getStyle() {
        return style;
    }

    public ViewColor getColor() {
        return myColor;
    }
}
