package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class ChooseMyPlayerFrame extends Frame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JPanel panel;
    private final ButtonGroup buttonGroup;
    private final GridBagConstraints gbc;
    private final Map<JRadioButton, PlayerType> rbs = new HashMap<>();

    public ChooseMyPlayerFrame(MainFrame mf) {
        this.mf = mf;
        frame = new JFrame("Choose my plater");
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

        panel.add(new JLabel("Я буду играть: "), gbc);

        addRadioButton("Человеком", true, PlayerType.USER);
        addRadioButton("Слабым ботом", false, PlayerType.EASYBOT);
        addRadioButton("Нормальным ботом", false, PlayerType.MEDIUMBOT);
        addRadioButton("Сильный ботом", false, PlayerType.HARDBOT);

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
                        PlayerType myType = null;
                        for (JRadioButton rb : rbs.keySet()) {
                            if (rb.isSelected()) {
                                myType = rbs.get(rb);
                                break;
                            }
                        }

                        ClientController.chooseMyType(myType);
                        mf.createChoosePlayerFrame(myType);
                        mf.destroyChooseMyPlayerFrame();
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
