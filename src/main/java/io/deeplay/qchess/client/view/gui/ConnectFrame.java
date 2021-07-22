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

public class ConnectFrame extends Frame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JPanel panel;
    private final JTextField ipField;
    private final JTextField portField;

    public ConnectFrame(MainFrame mf) {
        this.mf = mf;
        frame = new JFrame("Connect");
        frame.setSize(OUTER_FRAME_DIMENSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

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
                            new MessageFrame(
                                    frame, "Предупреждение", "Не удалось подключиться к серверу");
                            return;
                        }
                        while (!ClientController.isConnected()) Thread.onSpinWait();
                        try {
                            ClientController.waitForAcceptConnection();
                            boolean color = ClientController.waitForGameSettings();
                            frame.dispose();
                            GameGUIAdapterService.init();
                            if (!color) GameGUIAdapterService.changeIsWhiteStep();

                            mf.createChoosePlayerFrame(color);
                            mf.destroyConnectFrame();
                        } catch (ClientException clientException) {
                            System.err.println(clientException.getMessage());
                        }
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
}
