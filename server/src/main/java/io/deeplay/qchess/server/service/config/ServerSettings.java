package io.deeplay.qchess.server.service.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class ServerSettings {

    public static final String DEFAULT_CONFIG_PATH = "/server_configuration.conf";

    private final int port;
    /**
     * Максимальное кол-во игроков, которое может быть одновременно на сервере
     */
    private final int maxPlayers;
    /**
     * Строковое представление пути до папки, где хранятся логи
     */
    private final String logPath;
    /**
     * Строковое представление пути до папки, где хранится файл с логбэком
     */
    private final String logBack;
    /**
     * Количество игр, которые будет играться в турнире при его проведении
     */
    private final int tournamentNumberGame;

    public ServerSettings(final String configPath) throws ConfigException {
        final Properties property = new Properties();
        try (final FileInputStream fis = new FileInputStream(
            Objects.requireNonNull(getClass().getResource(configPath)).getFile())) {
            property.load(fis);
            port = ConfigService.validatePort(property.getProperty("server.port"));
            maxPlayers = ConfigService
                .validateMaxPlayers(property.getProperty("server.maxPlayers"));
            logPath = ConfigService.validatePath(property.getProperty("server.logPath"));
            logBack = ConfigService.validatePath(property.getProperty("server.logBack"));
            tournamentNumberGame = ConfigService
                .validateTournamentNumberGame(property.getProperty("server.tournamentNumberGame"));

        } catch (final IOException | NullPointerException e) {
            throw new ConfigException(ConfigExceptionErrorCode.READ_CONFIG_FILE);
        } catch (final ConfigException e) {
            throw e;
        }
    }

    public ServerSettings() throws ConfigException {
        this(DEFAULT_CONFIG_PATH);
    }

    public int getPort() {
        return port;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getLogPath() {
        return logPath;
    }

    public String getLogBack() {
        return logBack;
    }

    public int getTournamentNumberGame() {
        return tournamentNumberGame;
    }
}
