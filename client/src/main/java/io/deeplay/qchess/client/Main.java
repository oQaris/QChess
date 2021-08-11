package io.deeplay.qchess.client;

import ch.qos.logback.classic.util.ContextInitializer;
import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.gui.ClientGUI;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // TODO: получение пути до logback из конфига
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "./logback-client.xml");
        // TODO: если конфиг поврежден или путь до logback некорректный, продолжить программу

        IClientView view = new ClientGUI();
        view.startView();
        view.close();
    }
}
