package io.deeplay.qchess.core.config;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigService {

    /**
     * Валидация пути к папке.
     *
     * @param property - строковое представление пути к папке.
     * @return Результат property.
     * @throws ConfigException выбросится если:<br>
     *     - значение было пустым или null<br>
     *     - путь не соответствует шаблону представления пути.
     */
    public static String validatePath(final String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(ConfigExceptionErrorCode.ABSENT_PATH);
        }
        if (Files.notExists(Paths.get(property))) {
            throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_PATH);
        }
        return property;
    }

    /**
     * Валидация значения, полученного по ключу tournamentNumberGame.
     *
     * @param property поле считанное с config файла по ключу tournamentNumberGame.
     * @return Результат целочисленное представление параметра property.
     * @throws ConfigException выбросится если:<br>
     *     - значение поля было пустым или tournamentNumberGame вообще не было в config файле<br>
     *     - tournamentNumberGame не int<br>
     *     - tournamentNumberGame является неположительным числом.
     */
    public static int validateNumberGame(final String property) throws ConfigException {
        if (property == null || property.isEmpty()) {
            throw new ConfigException(ConfigExceptionErrorCode.ABSENT_TOURNAMENT);
        }
        try {
            final int number = Integer.parseInt(property);
            if (number <= 0) {
                throw new ConfigException(ConfigExceptionErrorCode.NON_POSITIVE_TOURNAMENT_VALUE);
            }
            return number;
        } catch (final NumberFormatException e) {
            throw new ConfigException(ConfigExceptionErrorCode.INCORRECT_TOURNAMENT_VALUE);
        }
    }
}
