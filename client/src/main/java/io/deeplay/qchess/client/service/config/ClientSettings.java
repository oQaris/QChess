package io.deeplay.qchess.client.service.config;

import io.deeplay.qchess.client.view.gui.PlayerType;
import io.deeplay.qchess.game.model.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class ClientSettings {

    public static final String DEFAULT_CONFIG_PATH = "/client_configuration.conf";

    private final String ip;
    private final int port;
    private final boolean isGui;
    private final PlayerType playerType;
    /** Строковое представление пути до папки, где хранятся логи */
    private final String logPath;
    /** Строковое представление пути до папки, где хранится файл с логбэком */
    private final String logBack;

    private final Color color;
    private final boolean isTournament;

    public ClientSettings(final String configPath) throws ConfigException {
        final Properties property = new Properties();
        try (final FileInputStream fis =
                new FileInputStream(
                        Objects.requireNonNull(getClass().getResource(configPath)).getFile())) {
            property.load(fis);
            ip = ConfigService.validateIp(property.getProperty("client.ip"));
            port = ConfigService.validatePort(property.getProperty("client.port"));
            isGui = ConfigService.validateBoolean(property.getProperty("client.isGui"));
            playerType =
                    ConfigService.validatePlayerType(property.getProperty("client.playerType"));
            logPath = ConfigService.validatePath(property.getProperty("client.logPath"));
            logBack = ConfigService.validatePath(property.getProperty("client.logBack"));
            color = ConfigService.validateColor(property.getProperty("client.color"));
            isTournament =
                    ConfigService.validateBoolean(property.getProperty("client.isTournament"));

        } catch (final IOException | NullPointerException e) {
            throw new ConfigException(ConfigExceptionErrorCode.READ_CONFIG_FILE);
        } catch (final ConfigException e) {
            throw e;
        }
    }

    public ClientSettings() throws ConfigException {
        this(DEFAULT_CONFIG_PATH);
    }

    public static String getConfigPath() {
        return DEFAULT_CONFIG_PATH;
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

    /**
     * Возвращает экземпляр Color или null
     *
     * @return Экземпляр Color если в конфигах было одно из значений WHITE или BLACK. null - если в
     *     конфигах было RANDOM
     */
    public Color getColor() {
        return color;
    }

    public boolean isTournament() {
        return isTournament;
    }
}
