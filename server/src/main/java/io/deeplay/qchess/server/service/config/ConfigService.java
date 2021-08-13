package io.deeplay.qchess.server.service.config;

import java.util.regex.Pattern;

public class ConfigService {

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
            throw new ConfigException(ConfigExceptionErrorCode.ABSENT_PORT);
        }
        try {
            final int port = Integer.parseInt(property);
            if (port <= 0) {
                throw new ConfigException(ConfigExceptionErrorCode.NON_POSITIVE_PORT_VALUE);
            }
            return port;
        } catch (NumberFormatException e) {
            throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_PORT_VALUE);
        }
    }

    /**
     * Валидация значения, полученного по ключу maxPlayers.
     *
     * @param property поле считанное с config файла по ключу maxPlayers.
     * @return Результат целочисленное представление параметра property.
     * @throws ConfigException выбросится если:<br> - значение поля было пустым или maxPlayers
     *                         вообще не было в config файле<br> - maxPlayers не int<br> -
     *                         maxPlayers является неположительным числом.
     */
    public static int validateMaxPlayers(final String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(ConfigExceptionErrorCode.ABSENT_MAX_PLAYERS);
        }
        try {
            final int port = Integer.parseInt(property);
            if (port <= 0) {
                throw new ConfigException(ConfigExceptionErrorCode.NON_POSITIVE_MAX_PLAYERS_VALUE);
            }
            return port;
        } catch (NumberFormatException e) {
            throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_MAX_PLAYERS);
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
            throw new ConfigException(ConfigExceptionErrorCode.ABSENT_PATH);
        }
        /*pattern - регулярка, по которой пройдут
         * только строки, которые:
         * - начинаются с подстроки "/", "./" или "../"
         * - оканчиваются симоволом '/' (в том числе строка "/")
         * - между двумя символами '/' не пусто
         * - между двумя символами '/' могут быть либо буквы, цифры, '_' и пробел,
         * либо подстроки "." или ".."
         */
        final String pattern = "(\\.){0,2}/((([\\w\\s]+)|(\\.){1,2})/)*";
        if (!Pattern.matches(pattern, property)) {
            throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_PATH);
        }
        return property;
    }

    /**
     * Валидация значения, полученного по ключу tournamentNumberGame.
     *
     * @param property поле считанное с config файла по ключу tournamentNumberGame.
     * @return Результат целочисленное представление параметра property.
     * @throws ConfigException выбросится если:<br> - значение поля было пустым или
     *                         tournamentNumberGame вообще не было в config файле<br> -
     *                         tournamentNumberGame не int<br> - tournamentNumberGame является
     *                         неположительным числом.
     */
    public static int validateTournamentNumberGame(String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(ConfigExceptionErrorCode.ABSENT_TOURNAMENT);
        }
        try {
            final int number = Integer.parseInt(property);
            if (number <= 0) {
                throw new ConfigException(ConfigExceptionErrorCode.NON_POSITIVE_TOURNAMENT_VALUE);
            }
            return number;
        } catch (NumberFormatException e) {
            throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_TOURNAMENT_VALUE);
        }
    }
}
