package io.deeplay.qchess.client.service.config;

import org.junit.Assert;
import org.junit.Test;

public class ClientSettingsTest {

    @Test
    public void testAbsentIp() {
        final String configPath = "/00_test_absent_ip.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_IP) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIpOctetNumber() {
        final String configPath = "/01_test_ip_octet_number.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.INCORRECT_IP_OCTETS_NUMBER) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIpOctetValue() {
        final String configPath = "/02_test_ip_octet_value.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.INCORRECT_IP_OCTET_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testRangeOutOctet() {
        final String configPath = "/03_test_ip_octet_range_out.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.RANGE_OUT_IP_OCTET) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentPort() {
        final String configPath = "/04_test_absent_port.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_PORT) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testNonPositivePort() {
        final String configPath = "/05_test_non_positive_port.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.NON_POSITIVE_PORT_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPort() {
        final String configPath = "/06_test_incorrect_port.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.INCORRECT_PORT_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentBoolean() {
        final String configPath = "/07_test_absent_boolean.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_BOOLEAN) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectBoolean() {
        final String configPath = "/08_test_incorrect_boolean.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.INCORRECT_BOOLEAN_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentPlayerType() {
        final String configPath = "/09_test_absent_player_type.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_PLAYER_TYPE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPlayerType() {
        final String configPath = "/10_test_incorrect_player_type.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.INCORRECT_PLAYER_TYPE_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentPath() {
        final String configPath = "/11_test_absent_path.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPath() {
        final String configPath = "/12_test_incorrect_path.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.INCORRECT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentColor() {
        final String configPath = "/13_test_absent_color.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_COLOR) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectColor() {
        final String configPath = "/14_test_incorrect_color.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.INCORRECT_COLOR) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyIp() {
        final String configPath = "/15_test_empty_ip.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_IP) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyPort() {
        final String configPath = "/16_test_empty_port.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_PORT) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyBoolean() {
        final String configPath = "/17_test_empty_boolean.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_BOOLEAN) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyPlayerType() {
        final String configPath = "/18_test_empty_player_type.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_PLAYER_TYPE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyPath() {
        final String configPath = "/19_test_empty_path.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyColor() {
        final String configPath = "/20_test_empty_color.conf";
        try {
            new ClientSettings(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionEnum.ABSENT_COLOR) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testCompeteConfigRead1() {
        final String configPath = "/50_test_complete.conf";
        try {
            new ClientSettings(configPath);
        } catch (ConfigException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompeteConfigRead2() {
        final String configPath = "/51_test_complete.conf";
        try {
            new ClientSettings(configPath);
        } catch (ConfigException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompeteConfigRead3() {
        final String configPath = "/52_test_complete.conf";
        try {
            new ClientSettings(configPath);
        } catch (ConfigException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompeteConfigRead4() {
        final String configPath = "/53_test_complete.conf";
        try {
            new ClientSettings(configPath);
        } catch (ConfigException e) {
            Assert.fail();
        }
    }
}
