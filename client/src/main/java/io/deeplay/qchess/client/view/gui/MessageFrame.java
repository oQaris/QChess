package io.deeplay.qchess.client.view.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MessageFrame {
    public MessageFrame(JFrame frame, String title, Object message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
