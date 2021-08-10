package io.deeplay.qchess.client.config;

import io.deeplay.qchess.client.view.gui.PlayerType;
import io.deeplay.qchess.game.model.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ClientConfig {

    private final static String configPath = "client/src/main/resources/client_configuration.conf";

    private final String ip;
    private final int port;
    private final boolean isGui;
    private final PlayerType playerType;
    private final String logPath;
    private final String logBack;
    private final Color color;
    private final boolean isTournament;

    public ClientConfig(final String configPath) throws ConfigException {
        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(configPath);
            property.load(fis);
            ip = ConfigService.validateIp(property.getProperty("client.ip"));
            port = ConfigService.validatePort(property.getProperty("client.port"));
            isGui = ConfigService.validateBoolean(property.getProperty("client.isGui"));
            playerType = ConfigService.validatePlayerType(property.getProperty("client.playerType"));
            logPath = ConfigService.validatePath(property.getProperty("client.logPath"));
            logBack = ConfigService.validatePath(property.getProperty("client.logBack"));
            color = ConfigService.validateColor(property.getProperty("client.color"));
            isTournament = ConfigService.validateBoolean(property.getProperty("isTournament"));

        } catch (IOException e) {
            throw new ConfigException(ConfigExceptionEnum.READ_CONFIG_FILE);
        } catch (ConfigException e) {
            throw e;
        }
    }

    public ClientConfig() throws ConfigException {
        this(configPath);
    }

    public static String getConfigPath() {
        return configPath;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isGui() {
        return isGui;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public String getLogPath() {
        return logPath;
    }

    public String getLogBack() {
        return logBack;
    }

    public Color getColor() {
        return color;
    }

    public boolean isTournament() {
        return isTournament;
    }
}
