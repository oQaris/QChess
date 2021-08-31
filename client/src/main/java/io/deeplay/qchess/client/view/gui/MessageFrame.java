package io.deeplay.qchess.client.view.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MessageFrame {
    public MessageFrame(final JFrame frame, final String title, final Object message) {
        final Object[] options = {"Океюшки"};
        JOptionPane.showOptionDialog(
                frame,
                message,
                title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                null);
    }
}
