package io.deeplay.qchess.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArenaSettings {
    public static final String JAR_CONFIG_PATH = "/arena_configuration.conf";
    public static final String DEFAULT_CONFIG_PATH = "./arena_configuration.conf";
    public static final String DEFAULT_LOGBACK_NAME = "logback.xml";

    private static final Logger logger = LoggerFactory.getLogger(ArenaSettings.class);

    private String logback;
    private int numberGame;
    private String qbotName;
    private String lobotName;
    private String nukebotName;

    public ArenaSettings(final String configPath) throws ConfigException {
        final File file = new File(configPath);
        if (!file.exists() || !file.canRead()) {
            logger.warn("Локальный конфиг не был прочитан, попытка прочитать из .jar ...");
        }
        try (final InputStream config =
                file.exists() && file.canRead()
                        ? new FileInputStream(file)
                        : getClass().getResourceAsStream(JAR_CONFIG_PATH)) {
            readConfig(config);
        } catch (final IOException e) {
            logger.error("Ошибка чтения локального конфига: {}", e.getMessage());
            throw new ConfigException(ConfigExceptionErrorCode.READ_CONFIG_FILE);
        }
        logger.info("Конфиг успешно установлен");
    }

    public ArenaSettings() throws ConfigException {
        this(DEFAULT_CONFIG_PATH);
    }

    public void readConfig(final InputStream config) throws ConfigException {
        try {
            final Properties property = new Properties();

            property.load(config);
            logback = ConfigService.validatePath(property.getProperty("arena.logback"));
            numberGame = ConfigService.validateNumberGame(property.getProperty("arena.numberGame"));
            qbotName = property.getProperty("arena.qbot");
            lobotName = property.getProperty("arena.lobot");
            nukebotName = property.getProperty("arena.nukebot");
        } catch (final IOException e) {
            logger.error("Ошибка парсинга локального конфига: {}", e.getMessage());
            throw new ConfigException(ConfigExceptionErrorCode.READ_CONFIG_FILE);
        }
    }

    /** @return Строковое представление пути до папки, где хранится файл с логбэком */
    public String getLogback() {
        return logback;
    }

    /** @return Количество игр, которые будет играться в турнире при его проведении */
    public int getNumberGame() {
        return numberGame;
    }

    public String getQbotName() {
        return qbotName;
    }

    public String getLobotName() {
        return lobotName;
    }

    public String getNukebotName() {
        return nukebotName;
    }
}
