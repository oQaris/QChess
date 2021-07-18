package io.deeplay.qchess.client;

import io.deeplay.qchess.client.view.ClientConsole;
import io.deeplay.qchess.client.view.IClientView;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        IClientView view = new ClientConsole();
        view.startView();
        view.close();
    }
}
