package io.deeplay.qchess.client.service.config;

import org.junit.Assert;
import org.junit.Test;

public class ConfigServiceTest {
    @Test
    public void testIpOctetNumber() {
        final String incorrectIp = "127.0.0.1.1";
        try {
            ConfigService.validateIp(incorrectIp);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_IP_OCTETS_NUMBER) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIpOctetValue() {
        final String incorrectIp = "127.0.a.1";
        try {
            ConfigService.validateIp(incorrectIp);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_IP_OCTET_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testRangeOutOctet() {
        final String incorrectIp = "127.300.0.1";
        try {
            ConfigService.validateIp(incorrectIp);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.RANGE_OUT_IP_OCTET) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testNonPositivePort() {
        final String incorrectPort = "-100";
        try {
            ConfigService.validatePort(incorrectPort);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.NON_POSITIVE_PORT_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPort() {
        final String incorrectPort = "8a80";
        try {
            ConfigService.validatePort(incorrectPort);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PORT_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectBoolean() {
        final String incorrectBoolean = "fulse";
        try {
            ConfigService.validateBoolean(incorrectBoolean);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_BOOLEAN_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPlayerType() {
        final String incorrectPlayerType = "LOOSER";
        try {
            ConfigService.validatePlayerType(incorrectPlayerType);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PLAYER_TYPE_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPath1() {
        final String incorrectPath = "/o/2//gf/";
        try {
            ConfigService.validatePath(incorrectPath);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPath2() {
        final String incorrectPath = ".../";
        try {
            ConfigService.validatePath(incorrectPath);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPath3() {
        final String incorrectPath = "./././.../";
        try {
            ConfigService.validatePath(incorrectPath);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPath4() {
        final String incorrectPath = "a/";
        try {
            ConfigService.validatePath(incorrectPath);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPath5() {
        final String incorrectPath = "/a.a/";
        try {
            ConfigService.validatePath(incorrectPath);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPath6() {
        final String incorrectPath = "/a";
        try {
            ConfigService.validatePath(incorrectPath);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectColor() {
        final String incorrectColor = "RED";
        try {
            ConfigService.validateColor(incorrectColor);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_COLOR) {
                Assert.fail();
            }
        }
    }
}
