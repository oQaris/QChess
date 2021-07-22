package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;;
import java.util.Map;
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
    private final Map<JRadioButton, EnemyNumber> rbs = new HashMap<>();
    private final GridBagConstraints gbc;
    private final Map<JRadioButton, EnemyType> rbs = new HashMap<>();

    public ChoosePlayerFrame(MainFrame mf) {
        this.mf = mf;
        frame = new JFrame("Choose Enemy");
        frame.setSize(OUTER_FRAME_DIMENSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        enemyNumber = 0;

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        buttonGroup = new ButtonGroup();

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;

        addRadioButton("Человек", true, EnemyNumber.USER);
        addRadioButton("Слабый бот", false, EnemyNumber.EASYBOT);
        addRadioButton("Нормальный бот", false, EnemyNumber.MEDIUMBOT);
        addRadioButton("Сильный бот", false, EnemyNumber.HARDBOT);

        panel.add(addButtonConnect(), gbc);
        frame.add(panel, BorderLayout.CENTER);

        //this.frame.addWindowListener(new CloseFrameListener(this));

        frame.setVisible(true);
    }

    private JButton addButtonConnect() {
        JButton continueButton = new JButton("Продолжить");

        continueButton.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        EnemyType enemyType = null;
                        for (JRadioButton rb : rbs.keySet()) {
                            if (rb.isSelected()) {
                                enemyType = rbs.get(rb);
                                break;
                            }
                        }

                        ClientController.chooseEnemy(enemyType);
                        mf.createChooseStyleFrame(enemyType);
                        mf.destroyChoosePlayerFrame();
                    }
                });

        return continueButton;
    }

    public void addRadioButton(String name, boolean pressed, EnemyNumber enemyNumber) {
        JRadioButton button = new JRadioButton(name, pressed);

        buttonGroup.add(button);
        panel.add(button);

        rbs.put(button, enemyNumber);
    }
}
