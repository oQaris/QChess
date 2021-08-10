package io.deeplay.qchess.client.config;

public enum ConfigExceptionEnum {
    READ_CONFIG_FILE("Read config file error"),

    ABSENT_IP("Validate ip from config error (ip field is absent or ip is empty)"),
    ABSENT_PORT("Validate port from config error (port field is absent or port is empty)"),
    ABSENT_BOOLEAN( "Validate boolean from config error (boolean field is absent or boolean is empty)"),
    ABSENT_PLAYER_TYPE("Validate player type config error (player type field is absent or player type is empty)"),
    ABSENT_PATH("Validate path from config error (path field is absent or path is empty)"),
    ABSENT_COLOR("Validate color from config error (color field is absent or color is empty)"),

    INCORRECT_PORT_VALUE("Validate port from config error (parse error)"),
    INCORRECT_BOOLEAN_VALUE("Validate boolean config parameter error (parse error)"),
    INCORRECT_PLAYER_TYPE_VALUE("Validate player type from config error (parse error)"),
    INCORRECT_PATH("Validate path from config error (the path is written incorrectly)"),
    INCORRECT_COLOR("Validate color from config error"),

    INCORRECT_IP_OCTETS_NUMBER("Validate ip from config error (ip is not 4 octets)"),
    INCORRECT_IP_OCTET_VALUE("Validate ip from config error (octet parse error)"),
    RANGE_OUT_IP_OCTET("Validate ip from config error (octet out of bound)"),
    NON_POSITIVE_PORT_VALUE("Validate port from config error (non-positive value)");

    private final String message;
    ConfigExceptionEnum(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
