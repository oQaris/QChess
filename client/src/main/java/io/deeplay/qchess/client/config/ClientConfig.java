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

    public ClientConfig() throws ConfigException {
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
            throw new ConfigException("Read config file error");
        } catch (ConfigException e) {
            throw e;
        }
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
                "Validate ip from config error (ip field is absent or ip is empty)");
        }
        String[] octets = property.split("\\.");
        if (octets.length != 4) {
            throw new ConfigException("Validate ip from config error (ip is not 4 octets)");
        }
        for (int i = 0; i < 4; i++) {
            try {
                int intOctet = Integer.parseInt(octets[i]);
                if (intOctet < 0 || intOctet > 255) {
                    throw new ConfigException(
                        String.format("Validate ip from config error (octet #%d out of bound)", i));
                }
            } catch (NumberFormatException e) {
                throw new ConfigException(
                    String.format("Validate ip from config error (octet #%d parse error)", i));
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
                "Validate port from config error (port field is absent or port is empty)");
        }
        try {
            int port = Integer.parseInt(property);
            if (port <= 0) {
                throw new ConfigException("Validate port from config error (non-positive value)");
            }
            return port;
        } catch (NumberFormatException e) {
            throw new ConfigException("Validate port from config error (parse error)");
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
                "Validate boolean from config error (boolean field is absent or boolean is empty)");
        }
        if (property.equals("true")) {
            return true;
        } else if (property.equals("false")) {
            return false;
        }
        throw new ConfigException("Validate boolean config parameter error (parse error)");
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
                "Validate player type config error (player type field is absent or player type is empty)");
        }
        try {
            return PlayerType.valueOf(property);
        } catch (IllegalArgumentException e) {
            throw new ConfigException("Validate player type from config error (parse error)");
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
                "Validate path from config error (path field is absent or path is empty)");
        }
        String pattern = "/([\\w]+/)*";
        if (Pattern.matches(pattern, property)) {
            throw new ConfigException(
                "Validate path from config error (the path is written incorrectly)");
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
                "Validate color from config error (color field is absent or color is empty)");
        }
        if (property.equals("WHITE")) {
            return Color.WHITE;
        } else if (property.equals("BLACK")) {
            return Color.BLACK;
        } else if (property.equals("RANDOM")) {
            return null;
        }
        throw new ConfigException("Validate color from config error");
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
