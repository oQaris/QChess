package io.deeplay.qchess.client.view.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MessageFrame {
    public MessageFrame(final JFrame frame, final String title, final Object message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
