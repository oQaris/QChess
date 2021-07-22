package io.deeplay.qchess.server;

import io.deeplay.qchess.server.view.IServerView;
import io.deeplay.qchess.server.view.ServerConsole;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        IServerView view = new ServerConsole();
        view.startView();
        view.close();
    }
}
