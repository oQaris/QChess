package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.exceptions.ClientException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

public class CloseFrameListener extends WindowAdapter {
    private final Frame frame;

    public CloseFrameListener(final Frame frame) {
        this.frame = frame;
    }

    @Override
    public void windowClosing(final WindowEvent event) {
        super.windowClosing(event);
        final Object[] options = {"Да", "Нет"};
        final int n =
                JOptionPane.showOptionDialog(
                        event.getWindow(),
                        "Закрыть окно?",
                        "Подтверждение",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
        if (n == 0) {
            frame.destroy();
            try {
                ClientController.disconnect("Клиент отключен");
            } catch (final ClientException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
