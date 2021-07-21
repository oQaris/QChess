package io.deeplay.qchess.client.view.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ChooseStyleFrame extends Frame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JPanel panel;
    private final ButtonGroup buttonGroup;
    private int enemyNumber;
    private final Map<JRadioButton, String> rbs = new HashMap<>();

    public ChooseStyleFrame(MainFrame mf) {
        this.mf = mf;
        frame = new JFrame("Choose Enemy");
        frame.setSize(OUTER_FRAME_DIMENSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        enemyNumber = 0;

        panel = new JPanel();

        buttonGroup = new ButtonGroup();

        addRadioButton("По-умолчанию", true, "onestyle");
        addRadioButton("Красивый", false, "twostyle");

        panel.add(addButtonConnect());
        frame.add(panel, BorderLayout.CENTER);

        this.frame.addWindowListener(new CloseFrameListener(this));

        frame.setVisible(true);
    }

    private JButton addButtonConnect() {
        JButton continueButton = new JButton("Continue");

        continueButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                String style = null;
                for (JRadioButton rb : rbs.keySet()) {
                    if(rb.isSelected()) {
                        style = rbs.get(rb);
                        break;
                    }
                }
                mf.createTable(style);
                mf.destroyChooseStyleFrame();
            }
        });

        return continueButton;
    }

    public void addRadioButton(String name, boolean pressed, String text)
    {
        JRadioButton button = new JRadioButton(name, pressed);

        buttonGroup.add(button);
        panel.add(button);

        rbs.put(button, text);
    }
}
