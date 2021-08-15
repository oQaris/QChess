package io.deeplay.qchess.client.view.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

public class ChooseStyleFrame extends Frame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JPanel panel;
    private final ButtonGroup buttonGroup;
    private final Map<JRadioButton, String> rbs = new HashMap<>();
    private final GridBagConstraints gbc;

    public ChooseStyleFrame(final MainFrame mf) {
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
        final JButton continueButton = new JButton("Продолжить");

        continueButton.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(final MouseEvent e) {
                        super.mousePressed(e);
                        String style = null;
                        for (final Entry<JRadioButton, String> rb : rbs.entrySet()) {
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

    public void addRadioButton(final String name, final boolean pressed, final String text) {
        final JRadioButton button = new JRadioButton(name, pressed);

        buttonGroup.add(button);
        panel.add(button, gbc);

        rbs.put(button, text);
    }
}
