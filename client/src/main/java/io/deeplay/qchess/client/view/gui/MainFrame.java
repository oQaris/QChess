package io.deeplay.qchess.client.view.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;

public class MainFrame {
    private ConnectFrame connectFrame;
    private ChoosePlayerFrame choosePlayerFrame;
    private ChooseStyleFrame chooseStyleFrame;
    private Table table;
    private EnemyType enemyType;
    private String style;

    public void createStartFrame() {
        JFrame frame = new JFrame("Начало");
        frame.setSize(new Dimension(200, 200));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        JButton startButton = new JButton("Начать");
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                createChoosePlayerFrame();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.add(startButton);
        frame.setVisible(true);
    }
    public void createChoosePlayerFrame() {
        choosePlayerFrame = new ChoosePlayerFrame(this);
    }

    public void createChooseStyleFrame(EnemyType enemyType) {
        this.enemyType = enemyType;
        chooseStyleFrame = new ChooseStyleFrame(this);
    }

    public void createConnectFrame(String style) {
        this.style = style;
        connectFrame = new ConnectFrame(this);
    }

    public void createTable(boolean color) {
        table = new Table(style, color, this);
    }

    public void destroyChoosePlayerFrame() {
        choosePlayerFrame.destroy();
        choosePlayerFrame = null;
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

    public EnemyType getEnemyNumber() {
        return enemyType;
    }

    public String getStyle() {
        return style;
    }
}
