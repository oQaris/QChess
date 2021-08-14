package io.deeplay.qchess.client.service.config;

import io.deeplay.qchess.client.view.gui.PlayerType;
import io.deeplay.qchess.game.model.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSettings {

    public static final String JAR_CONFIG_PATH = "/client_configuration.conf";
    public static final String DEFAULT_CONFIG_PATH = "./client_configuration.conf";
    public static final String DEFAULT_LOGBACK_NAME = "logback-client.xml";

    private static final transient Logger logger = LoggerFactory.getLogger(ClientSettings.class);

    private String ip;
    private int port;
    private boolean isGui;
    private PlayerType playerType;
    /** Строковое представление пути до папки, где хранятся логи */
    private String logPath;
    /** Строковое представление пути до папки, где хранится файл с логбэком */
    private String logBack;

    private Color color;
    private boolean isTournament;

    public ClientSettings(final String configPath) throws ConfigException {
        final File file = new File(configPath);
        if (!file.exists() || !file.canRead()) {
            logger.warn("Локальный конфиг не был прочитан, попытка прочитать из .jar ...");
            readConfig(getClass().getResourceAsStream(JAR_CONFIG_PATH));
        } else
            try (final FileInputStream config = new FileInputStream(file)) {
                readConfig(config);
            } catch (final IOException e) {
                logger.error("Ошибка чтения локального конфига: {}", e.getMessage());
                throw new ConfigException(ConfigExceptionErrorCode.READ_CONFIG_FILE);
            }
        logger.info("Конфиг успешно установлен");
    }

    public ClientSettings() throws ConfigException {
        this(DEFAULT_CONFIG_PATH);
    }

    private void readConfig(final InputStream config) throws ConfigException {
        try {
            final Properties property = new Properties();

            property.load(config);
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
        } catch (final IOException e) {
            logger.error("Ошибка парсинга локального конфига: {}", e.getMessage());
            throw new ConfigException(ConfigExceptionErrorCode.READ_CONFIG_FILE);
        }
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

    /** @return Строковое представление пути до папки, где хранятся логи */
    public String getLogPath() {
        return logPath;
    }

    /** @return Строковое представление пути до папки, где хранится файл с логбэком */
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
