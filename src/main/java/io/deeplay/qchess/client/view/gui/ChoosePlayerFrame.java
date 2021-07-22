package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ChoosePlayerFrame extends Frame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JPanel panel;
    private final ButtonGroup buttonGroup;
    private int enemyNumber;
    private final List<JRadioButton> rbs = new ArrayList<>();

    public ChoosePlayerFrame(MainFrame mf) {
        this.mf = mf;
        frame = new JFrame("Choose Enemy");
        frame.setSize(OUTER_FRAME_DIMENSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        enemyNumber = 0;

        panel = new JPanel();

        buttonGroup = new ButtonGroup();

        addRadioButton("Человек", true);
        addRadioButton("Слабый бот", false);
        addRadioButton("Нормальный бот", false);
        addRadioButton("Сильный бот", false);

        panel.add(addButtonConnect());
        frame.add(panel, BorderLayout.CENTER);

        this.frame.addWindowListener(new CloseFrameListener(this));

        frame.setVisible(true);
    }

    private JButton addButtonConnect() {
        JButton continueButton = new JButton("Continue");

        continueButton.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        for (int i = 0; i < rbs.size(); i++) {
                            if (rbs.get(i).isSelected()) {
                                enemyNumber = i;
                                break;
                            }
                        }
                        ClientController.chooseEnemy(enemyNumber);
                        mf.createChooseStyleFrame(enemyNumber);
                        mf.destroyChoosePlayerFrame();
                    }
                });

        return continueButton;
    }

    public void addRadioButton(String name, boolean pressed) {
        JRadioButton button = new JRadioButton(name, pressed);

        buttonGroup.add(button);
        panel.add(button);

        rbs.add(button);
    }
}
