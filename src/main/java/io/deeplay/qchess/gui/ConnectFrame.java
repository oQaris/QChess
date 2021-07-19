package io.deeplay.qchess.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectFrame {
    private final JFrame frame;
    private final JPanel panel;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);

    public ConnectFrame() {
        frame = new JFrame("Connect");
        frame.setSize(OUTER_FRAME_DIMENSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        panel = new JPanel();
        panel.add(addInputIP());
        panel.add(addInputPort());
        panel.add(addButtonConnect());
        frame.add(panel);

        frame.setVisible(true);
    }

    private JTextField addInputIP() {
        JTextField ipField = new JTextField("255.255.255.255");
        return ipField;
    }

    private JTextField addInputPort() {
        JTextField portField = new JTextField("8080");
        return portField;
    }

    private JButton addButtonConnect() {
        JButton connectButton = new JButton("Connect");

        connectButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        return connectButton;
    }
}
