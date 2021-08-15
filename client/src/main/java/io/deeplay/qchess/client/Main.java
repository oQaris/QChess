package io.deeplay.qchess.client;

import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.gui.ClientGUI;
import java.io.IOException;

public class Main {
    public static void main(final String[] args) throws IOException {
        final IClientView view = new ClientGUI();
        view.startView();
        view.close();
    }
}
