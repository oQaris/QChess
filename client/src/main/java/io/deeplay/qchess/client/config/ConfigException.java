package io.deeplay.qchess.client.config;

public class ConfigException extends Exception {
    private final String message;
    public ConfigException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
