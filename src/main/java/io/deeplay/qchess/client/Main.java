package io.deeplay.qchess.client;

import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.gui.ClientGUI;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        IClientView view = new ClientGUI();
        view.startView();
        view.close();
    }
}
