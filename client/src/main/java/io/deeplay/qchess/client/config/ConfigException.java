package io.deeplay.qchess.client.config;

public class ConfigException extends Exception {

    private final ConfigExceptionEnum configExceptionEnum;

    public ConfigException(ConfigExceptionEnum configExceptionEnum) {
        this.configExceptionEnum = configExceptionEnum;
    }

    public ConfigExceptionEnum getExceptionType() {
        return configExceptionEnum;
    }
}
