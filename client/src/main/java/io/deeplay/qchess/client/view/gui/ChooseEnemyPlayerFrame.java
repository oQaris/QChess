package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

public class ChooseEnemyPlayerFrame extends Frame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JPanel panel;
    private final ButtonGroup buttonGroup;
    private final GridBagConstraints gbc;
    private final Map<JRadioButton, PlayerType> rbs = new HashMap<>();

    public ChooseEnemyPlayerFrame(MainFrame mf) {
        this.mf = mf;
        frame = new JFrame("Choose Enemy");
        frame.setSize(OUTER_FRAME_DIMENSION);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        buttonGroup = new ButtonGroup();

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(new JLabel("Противником будет: "), gbc);

        addRadioButton("Человек", true, PlayerType.USER);
        addRadioButton("Слабый бот", false, PlayerType.EASYBOT);
        addRadioButton("Нормальный бот", false, PlayerType.MEDIUMBOT);
        addRadioButton("Сильный бот", false, PlayerType.HARDBOT);

        panel.add(addButtonConnect(), gbc);
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JButton addButtonConnect() {
        JButton continueButton = new JButton("Продолжить");

        continueButton.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        PlayerType playerType = null;
                        for (JRadioButton rb : rbs.keySet()) {
                            if (rb.isSelected()) {
                                playerType = rbs.get(rb);
                                break;
                            }
                        }

                        ClientController.chooseEnemy(playerType);
                        mf.createChooseStyleFrame(playerType);
                        mf.destroyChoosePlayerFrame();
                    }
                });

        return continueButton;
    }

    public void addRadioButton(String name, boolean pressed, PlayerType playerType) {
        JRadioButton button = new JRadioButton(name, pressed);

        buttonGroup.add(button);
        panel.add(button, gbc);

        rbs.put(button, playerType);
    }
}
