package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.service.GameService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ConnectFrame extends Frame {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(200, 200);
    private final JTextField ipField;
    private final JTextField portField;
    private final GridBagConstraints gbc;

    public ConnectFrame(MainFrame mf) {
        this.mf = mf;
        frame = new JFrame("Присоединиться");
        frame.setSize(OUTER_FRAME_DIMENSION);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JPanel ipPanel = new JPanel();
        JPanel portPanel = new JPanel();

        ipField = getInputIP();
        portField = getInputPort();

        ipPanel.add(new JLabel("IP: "));
        ipPanel.add(ipField);
        portPanel.add(new JLabel("Port: "));
        portPanel.add(portField);

        panel.add(ipPanel, gbc);
        panel.add(portPanel, gbc);
        panel.add(addButtonConnect(), gbc);

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
                    public void mouseClicked(MouseEvent e) {
                    }

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
                            // TODO: переписать на автомат
                            ClientController.waitForAcceptConnection();
                            boolean color = true;
                            GameService.initGame(color);

                            frame.dispose();

                            mf.createTable(color);
                            ClientController.sendFindGameRequest();
                            mf.destroyConnectFrame();

                        } catch (ClientException clientException) {
                            System.err.println(clientException.getMessage());
                        }
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
