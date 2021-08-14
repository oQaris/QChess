package io.deeplay.qchess.server;

import ch.qos.logback.classic.util.ContextInitializer;
import io.deeplay.qchess.server.view.IServerView;
import io.deeplay.qchess.server.view.ServerConsole;
import java.io.IOException;

public class Main {
    public static void main(final String[] args) throws IOException {
        // TODO: получение пути до logback из конфига
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "./logback-server.xml");
        // TODO: если конфиг поврежден или путь до logback некорректный, продолжить программу

        final IServerView view = new ServerConsole();
        view.startView();
        view.close();
    }
}
