package io.deeplay.qchess.server;

import ch.qos.logback.classic.util.ContextInitializer;
import io.deeplay.qchess.server.view.IServerView;
import io.deeplay.qchess.server.view.ServerConsole;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "./logback-server.xml");

        IServerView view = new ServerConsole();
        view.startView();
        view.close();
    }
}
