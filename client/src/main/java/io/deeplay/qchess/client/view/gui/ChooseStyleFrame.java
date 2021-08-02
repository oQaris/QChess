package io.deeplay.qchess.client.view.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ChooseStyleFrame extends Frame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JPanel panel;
    private final ButtonGroup buttonGroup;
    private final Map<JRadioButton, String> rbs = new HashMap<>();
    private final GridBagConstraints gbc;

    public ChooseStyleFrame(MainFrame mf) {
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

        addRadioButton("По-умолчанию", true, "onestyle");
        addRadioButton("Красивый", false, "twostyle");

        panel.add(addButtonConnect(), gbc);
        frame.add(panel, BorderLayout.CENTER);

        // this.frame.addWindowListener(new CloseFrameListener(this));

        frame.setVisible(true);
    }

    private JButton addButtonConnect() {
        JButton continueButton = new JButton("Продолжить");

        continueButton.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        String style = null;
                        for (Entry<JRadioButton, String> rb : rbs.entrySet()) {
                            if (rb.getKey().isSelected()) {
                                style = rb.getValue();
                                break;
                            }
                        }
                        mf.createConnectFrame(style);
                        mf.destroyChooseStyleFrame();
                    }
                });

        return continueButton;
    }

    public void addRadioButton(String name, boolean pressed, String text) {
        JRadioButton button = new JRadioButton(name, pressed);

        buttonGroup.add(button);
        panel.add(button, gbc);

        rbs.put(button, text);
    }
}
