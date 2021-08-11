package io.deeplay.qchess.client.service.config;

import io.deeplay.qchess.client.view.gui.PlayerType;
import io.deeplay.qchess.game.model.Color;
import java.util.regex.Pattern;

public class ConfigService {

    /**
     * Валидация значения, полученного по ключу ip.
     *
     * @param property поле считанное с config файла по ключу ip.
     * @return Результат property.
     * @throws ConfigException выбросится если:<br> - значение поля было пустым или ip вообще не
     *                         было в config файле<br> - ip состоит не из 4-х октетов<br> - один из
     *                         октетов не int<br> - один из октетов выходит за диапазон [0..255].
     */
    public static String validateIp(final String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionErrorCode.ABSENT_IP);
        }
        String[] octets = property.split("\\.");
        if (octets.length != 4) {
            throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_IP_OCTETS_NUMBER);
        }
        for (int i = 0; i < 4; i++) {
            try {
                final int intOctet = Integer.parseInt(octets[i]);
                if (intOctet < 0 || intOctet > 255) {
                    throw new ConfigException(
                        ConfigExceptionErrorCode.RANGE_OUT_IP_OCTET);
                }
            } catch (NumberFormatException e) {
                throw new ConfigException(
                    ConfigExceptionErrorCode.INCORRECT_IP_OCTET_VALUE);
            }
        }
        return property;
    }

    /**
     * Валидация значения, полученного по ключу port.
     *
     * @param property поле считанное с config файла по ключу port.
     * @return Результат целочисленное представление параметра property.
     * @throws ConfigException выбросится если:<br> - значение поля было пустым или port вообще не
     *                         было в config файле<br> - port не int<br> - port является
     *                         неположительным числом.
     */
    public static int validatePort(final String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionErrorCode.ABSENT_PORT);
        }
        try {
            int port = Integer.parseInt(property);
            if (port <= 0) {
                throw new ConfigException(ConfigExceptionErrorCode.NON_POSITIVE_PORT_VALUE);
            }
            return port;
        } catch (NumberFormatException e) {
            throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_PORT_VALUE);
        }
    }

    /**
     * Валидация boolean значения.
     *
     * @param property - boolean значение обёрнутое строкой.
     * @return Результат boolean представление параметра property.
     * @throws ConfigException выбросится если:<br> - значение было пустым или null<br> - property
     *                         не явлеяется строкой "true" или "false".
     */
    public static boolean validateBoolean(final String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionErrorCode.ABSENT_BOOLEAN);
        }
        if (property.equals("true")) {
            return true;
        } else if (property.equals("false")) {
            return false;
        }
        throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_BOOLEAN_VALUE);
    }

    /**
     * Валидация значения, полученного по ключу playerType.
     *
     * @param property поле считанное с config файла по ключу playerType.
     * @return Результат одно из значений enum {@link io.deeplay.qchess.client.view.gui.PlayerType}.
     * @throws ConfigException выбросится если: - значение поля было пустым или playerType вообще не
     *                         было в config файле<br> - неудалось распарсить значение в элемент
     *                         enum {@link io.deeplay.qchess.client.view.gui.PlayerType}.
     */
    public static PlayerType validatePlayerType(final String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionErrorCode.ABSENT_PLAYER_TYPE);
        }
        try {
            return PlayerType.valueOf(property);
        } catch (IllegalArgumentException e) {
            throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_PLAYER_TYPE_VALUE);
        }
    }

    /**
     * Валидация пути к папке.
     *
     * @param property - строковое представление пути к папке.
     * @return Результат property.
     * @throws ConfigException выбросится если:<br> - значение было пустым или null<br> - путь не
     *                         соответствует шаблону представления пути.
     */
    public static String validatePath(final String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionErrorCode.ABSENT_PATH);
        }
        /*pattern - регулярка, по которой пройдут
         * только строки, которые:
         * - начинаются и оканчиваются симоволом '/' (в том числе строка "/")
         * - между двумя символами '/' не пусто
         * - между двумя символами '/' могут быть буквы, цифры, '_' и пробел
         */
        final String pattern = "/([\\w\\s]+/)*";
        if (!Pattern.matches(pattern, property)) {
            throw new ConfigException(
                ConfigExceptionErrorCode.INCORRECT_PATH);
        }
        return property;
    }

    /**
     * Валидация значения, полученного по ключу color.
     *
     * @param property поле считанное с config файла по ключу color.
     * @return Результат одно из значений enum {@link io.deeplay.qchess.game.model.Color} или null
     * (нужно выбрать рандомный цвет).
     * @throws ConfigException выбросится если:<br> - значение поля было пустым или color вообще не
     *                         было в config файле<br> - property не парсится ни в один из цветов, а
     *                         также не является строкой "RANDOM".
     */
    public static Color validateColor(final String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(
                ConfigExceptionErrorCode.ABSENT_COLOR);
        }
        return switch (property) {
            case "WHITE" -> Color.WHITE;
            case "BLACK" -> Color.BLACK;
            case "RANDOM" -> null;
            default -> throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_COLOR);
        };
    }
}
