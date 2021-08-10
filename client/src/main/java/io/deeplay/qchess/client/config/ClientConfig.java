package io.deeplay.qchess.client.config;

import io.deeplay.qchess.client.view.gui.PlayerType;
import io.deeplay.qchess.game.model.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

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
            ip = validateIp(property.getProperty("client.ip"));
            port = validatePort(property.getProperty("client.port"));
            isGui = validateBoolean(property.getProperty("client.isGui"));
            playerType = validatePlayerType(property.getProperty("client.playerType"));
            logPath = validatePath(property.getProperty("client.logPath"));
            logBack = validatePath(property.getProperty("client.logBack"));
            color = validateColor(property.getProperty("client.color"));
            isTournament = validateBoolean(property.getProperty("isTournament"));

        } catch (IOException e) {
            throw new ConfigException(ConfigExceptionEnum.READ_CONFIG_FILE);
        } catch (ConfigException e) {
            throw e;
        }
    }

    public ClientConfig() throws ConfigException {
        this(configPath);
    }

    /**
     * Валидация значения полученного по ключу client.ip.
     *
     * @param property поле считанное с config файла по ключу client.ip.
     * @return Результат property.
     * @throws ConfigException выбросится если
     *  - значение поля было пустым или ip вообще не было в config файле;
     *  - ip состоит не из 4-х октетов;
     *  - один из октетов не int;
     *  - один из октетов выходит за диапазон [0..255].
     */
    private String validateIp(String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionEnum.ABSENT_IP);
        }
        String[] octets = property.split("\\.");
        if (octets.length != 4) {
            throw new ConfigException(ConfigExceptionEnum.INCORRECT_IP_OCTETS_NUMBER);
        }
        for (int i = 0; i < 4; i++) {
            try {
                int intOctet = Integer.parseInt(octets[i]);
                if (intOctet < 0 || intOctet > 255) {
                    throw new ConfigException(
                       ConfigExceptionEnum.RANGE_OUT_IP_OCTET);
                }
            } catch (NumberFormatException e) {
                throw new ConfigException(
                    ConfigExceptionEnum.INCORRECT_IP_OCTET_VALUE);
            }
        }
        return property;
    }

    /**
     * Валидация значения полученного по ключу client.port.
     *
     * @param property поле считанное с config файла по ключу client.port.
     * @return Результат целочисленное представление параметра property.
     * @throws ConfigException выбросится если
     *  - значение поля было пустым или port вообще не было в config файле;
     *  - port не int;
     *  - port является неположительным числом.
     */
    private int validatePort(String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionEnum.ABSENT_PORT);
        }
        try {
            int port = Integer.parseInt(property);
            if (port <= 0) {
                throw new ConfigException(ConfigExceptionEnum.NON_POSITIVE_PORT_VALUE);
            }
            return port;
        } catch (NumberFormatException e) {
            throw new ConfigException(ConfigExceptionEnum.INCORRECT_PORT_VALUE);
        }
    }

    /**
     * Валидация boolean значения.
     *
     * @param property - boolean значение обёрнутое строкой.
     * @return Результат boolean представление параметра property.
     * @throws ConfigException выбросится если
     *  - значение было пустым или null;
     *  - property не явлеяется строкой "true" или "false".
     */
    private boolean validateBoolean(String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionEnum.ABSENT_BOOLEAN);
        }
        if (property.equals("true")) {
            return true;
        } else if (property.equals("false")) {
            return false;
        }
        throw new ConfigException(ConfigExceptionEnum.INCORRECT_BOOLEAN_VALUE);
    }

    /**
     * Валидация значения полученного по ключу client.playerType.
     *
     * @param property поле считанное с config файла по ключу client.playerType.
     * @return Результат одно из значений enum client.view.gui.PlayerType.
     * @throws ConfigException выбросится если
     *  - значение поля было пустым или playerType вообще не было в config файле;
     *  - неудалось распарсить значение в элемент enum client.view.gui.PlayerType.
     */
    private PlayerType validatePlayerType(String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionEnum.ABSENT_PLAYER_TYPE);
        }
        try {
            return PlayerType.valueOf(property);
        } catch (IllegalArgumentException e) {
            throw new ConfigException(ConfigExceptionEnum.INCORRECT_PLAYER_TYPE_VALUE);
        }
    }

    /**
     * Валидация пути к папке
     *
     * @param property - строковое представление пути к папке.
     * @return Результат property.
     * @throws ConfigException выбросится если
     *  - значение было пустым или null;
     *  - путь не соответствует шаблону представления пути;
     */
    private String validatePath(String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
               ConfigExceptionEnum.ABSENT_PATH);
        }
        String pattern = "/([\\w]+/)*";
        if (Pattern.matches(pattern, property)) {
            throw new ConfigException(
                ConfigExceptionEnum.INCORRECT_PATH);
        }
        return property;
    }

    /**
     * Валидация значения полученного по ключу client.color.
     *
     * @param property поле считанное с config файла по ключу client.color.
     * @return Результат одно из значений enum game.model.Color или null (нужно выбрать рандомный цвет).
     * @throws ConfigException выбросится если
     *  - значение поля было пустым или color вообще не было в config файле;
     *  - property не парсится ни в один из цветов, а также не является строкой "RANDOM".
     */
    private Color validateColor(String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionEnum.ABSENT_COLOR);
        }
        if (property.equals("WHITE")) {
            return Color.WHITE;
        } else if (property.equals("BLACK")) {
            return Color.BLACK;
        } else if (property.equals("RANDOM")) {
            return null;
        }
        throw new ConfigException(ConfigExceptionEnum.INCORRECT_COLOR);
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
