package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.game.model.Color;
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

public class ChooseMyColorFrame extends Frame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JPanel panel;
    private final ButtonGroup buttonGroup;
    private final GridBagConstraints gbc;
    private final Map<JRadioButton, Color> rbs = new HashMap<>();

    public ChooseMyColorFrame(MainFrame mf) {
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

        panel.add(new JLabel("Я буду играть за: "), gbc);

        addRadioButton("Белый", true, Color.WHITE);
        addRadioButton("Чёрных", false, Color.BLACK);
        addRadioButton("Любым", false, null);

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
                    Color myColor = null;
                    for (JRadioButton rb : rbs.keySet()) {
                        if (rb.isSelected()) {
                            myColor = rbs.get(rb);
                            break;
                        }
                    }
                    ClientController.chooseMyColor(myColor);
                    mf.createChooseMyPlayerFrame(myColor);
                    mf.destroyChooseMyColorFrame();
                }
            });

        return continueButton;
    }

    public void addRadioButton(String name, boolean pressed, Color myColor) {
        JRadioButton button = new JRadioButton(name, pressed);

        buttonGroup.add(button);
        panel.add(button, gbc);

        rbs.put(button, myColor);
    }
}
