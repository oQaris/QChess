package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.service.GameGUIAdapterService;
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
    private final JTextField ipField;
    private final JTextField portField;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);

    public ConnectFrame() {
        frame = new JFrame("Connect");
        frame.setSize(OUTER_FRAME_DIMENSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        panel = new JPanel();
        ipField = getInputIP();
        portField = getInputPort();
        panel.add(ipField);
        panel.add(portField);
        panel.add(addButtonConnect());
        frame.add(panel);

        frame.setVisible(true);
    }

    private JTextField getInputIP() {
        return new JTextField("255.255.255.255");
    }

    private JTextField getInputPort() {
        return new JTextField("8080");
    }

    private JButton addButtonConnect() {
        JButton connectButton = new JButton("Connect");

        connectButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                String ip = ipField.getText();
                int port = Integer.parseInt(portField.getText());
                System.out.println(ip + ":" + port);

               // if(ClientController.isConnected()) {
                    frame.dispose();
                    frame.setVisible(false);
                    GameGUIAdapterService.init();
                    Table table = new Table("onestyle", true);
                //}
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
