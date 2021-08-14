package io.deeplay.qchess.server.service.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSettings {

    public static final String JAR_CONFIG_PATH = "/server_configuration.conf";
    public static final String DEFAULT_CONFIG_PATH = "./server_configuration.conf";
    public static final String DEFAULT_LOGBACK_NAME = "logback-server.xml";

    private static final transient Logger logger = LoggerFactory.getLogger(ServerSettings.class);

    private int port;
    /** Максимальное кол-во игроков, которое может быть одновременно на сервере */
    private int maxPlayers;
    /** Строковое представление пути до папки, где хранятся логи */
    private String logPath;
    /** Строковое представление пути до папки, где хранится файл с логбэком */
    private String logBack;
    /** Количество игр, которые будет играться в турнире при его проведении */
    private int tournamentNumberGame;

    public ServerSettings(final String configPath) throws ConfigException {
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

    public ServerSettings() throws ConfigException {
        this(DEFAULT_CONFIG_PATH);
    }

    private void readConfig(final InputStream config) throws ConfigException {
        try {
            final Properties property = new Properties();

            property.load(config);
            port = ConfigService.validatePort(property.getProperty("server.port"));
            maxPlayers =
                    ConfigService.validateMaxPlayers(property.getProperty("server.maxPlayers"));
            logPath = ConfigService.validatePath(property.getProperty("server.logPath"));
            logBack = ConfigService.validatePath(property.getProperty("server.logBack"));
            tournamentNumberGame =
                    ConfigService.validateTournamentNumberGame(
                            property.getProperty("server.tournamentNumberGame"));
        } catch (final IOException e) {
            logger.error("Ошибка парсинга локального конфига: {}", e.getMessage());
            throw new ConfigException(ConfigExceptionErrorCode.READ_CONFIG_FILE);
        }
    }

    public int getPort() {
        return port;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    /** @return Строковое представление пути до папки, где хранятся логи */
    public String getLogPath() {
        return logPath;
    }

    /** @return Строковое представление пути до папки, где хранится файл с логбэком */
    public String getLogBack() {
        return logBack;
    }

    public int getTournamentNumberGame() {
        return tournamentNumberGame;
    }
}
