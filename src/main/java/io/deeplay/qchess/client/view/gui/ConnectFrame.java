package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.service.GameGUIAdapterService;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectFrame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JFrame frame;
    private final JPanel panel;
    private final JTextField ipField;
    private final JTextField portField;
    private Table table;

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
        return new JTextField("127.0.0.1");
    }

    private JTextField getInputPort() {
        return new JTextField("8080");
    }

    private JButton addButtonConnect() {
        JButton connectButton = new JButton("Connect");

        connectButton.addMouseListener(
                new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {}

                    @Override
                    public void mousePressed(MouseEvent e) {
                        String ip = ipField.getText();
                        int port = Integer.parseInt(portField.getText());
                        System.out.println(ip + ":" + port);

                        try {
                            ClientController.connect(ip, port);
                        } catch (ClientException clientException) {
                            clientException.printStackTrace();
                            return;
                        }
                        while (!ClientController.isConnected()) Thread.onSpinWait();
                        boolean color = ClientController.waitForColor();

                        frame.dispose();
                        frame.setVisible(false);
                        GameGUIAdapterService.init();
                        if (!color) GameGUIAdapterService.changeIsWhiteStep();
                        table = new Table("onestyle", color);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {}

                    @Override
                    public void mouseEntered(MouseEvent e) {}

                    @Override
                    public void mouseExited(MouseEvent e) {}
                });

        return connectButton;
    }

    public Table getTable() {
        return table;
    }
}
