package io.deeplay.qchess.core.config;

public class ConfigException extends Exception {

    private final ConfigExceptionErrorCode configExceptionErrorCode;

    public ConfigException(final ConfigExceptionErrorCode configExceptionErrorCode) {
        super(configExceptionErrorCode.getMessage());
        this.configExceptionErrorCode = configExceptionErrorCode;
    }

    public ConfigExceptionErrorCode getExceptionType() {
        return configExceptionErrorCode;
    }
}
