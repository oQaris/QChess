package io.deeplay.qchess.client;

import ch.qos.logback.classic.util.ContextInitializer;
import io.deeplay.qchess.client.service.config.ClientSettings;
import io.deeplay.qchess.client.service.config.ConfigException;
import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.gui.ClientGUI;
import java.io.IOException;

public class Main {
    public static void main(final String[] args) throws IOException, ConfigException {
        final ClientSettings cs = new ClientSettings();
        final String logback = cs.getLogBack() + ClientSettings.DEFAULT_LOGBACK_NAME;
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, logback);

        // TODO: применить другие параметры из конфига
        final IClientView view = new ClientGUI();
        view.startView();
        view.close();
    }
}
