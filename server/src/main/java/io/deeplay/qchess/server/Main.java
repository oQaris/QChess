package io.deeplay.qchess.server;

import ch.qos.logback.classic.util.ContextInitializer;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.service.config.ConfigException;
import io.deeplay.qchess.server.service.config.ServerSettings;
import io.deeplay.qchess.server.view.IServerView;
import io.deeplay.qchess.server.view.ServerConsole;
import java.io.IOException;

public class Main {
    public static void main(final String[] args)
            throws IOException, ConfigException, ServerException {
        final ServerSettings ss = new ServerSettings();
        final String logback = ss.getLogBack() + ServerSettings.DEFAULT_LOGBACK_NAME;
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, logback);

        ServerController.setMaxClients(ss.getMaxPlayers());
        ServerController.setPort(ss.getPort());
        // TODO: количество игр в турнире

        final IServerView view = new ServerConsole();
        view.startView();
        view.close();
    }
}
