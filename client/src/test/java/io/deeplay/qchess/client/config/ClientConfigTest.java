package io.deeplay.qchess.client.config;

import org.junit.Assert;
import org.junit.Test;

public class ClientConfigTest {

    @Test
    public void testAbsentIp(){
        String configPath = "/00_test_absent_ip.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_IP) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIpOctetNumber() {
        String configPath = "/01_test_ip_octet_number.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.INCORRECT_IP_OCTETS_NUMBER) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIpOctetValue() {
        String configPath = "/02_test_ip_octet_value.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.INCORRECT_IP_OCTET_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testRangeOutOctet() {
        String configPath = "/03_test_ip_octet_range_out.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.RANGE_OUT_IP_OCTET) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentPort() {
        String configPath = "/04_test_absent_port.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_PORT) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testNonPositivePort() {
        String configPath = "/05_test_non_positive_port.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.NON_POSITIVE_PORT_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPort() {
        String configPath = "/06_test_incorrect_port.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.INCORRECT_PORT_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentBoolean() {
        String configPath = "/07_test_absent_boolean.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_BOOLEAN) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectBoolean() {
        String configPath = "/08_test_incorrect_boolean.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.INCORRECT_BOOLEAN_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentPlayerType() {
        String configPath = "/09_test_absent_player_type.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_PLAYER_TYPE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPlayerType() {
        String configPath = "/10_test_incorrect_player_type.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.INCORRECT_PLAYER_TYPE_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentPath() {
        String configPath = "/11_test_absent_path.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPath() {
        String configPath = "/12_test_incorrect_path.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.INCORRECT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentColor() {
        String configPath = "/13_test_absent_color.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_COLOR) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectColor() {
        String configPath = "/14_test_incorrect_color.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.INCORRECT_COLOR) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyIp() {
        String configPath = "/15_test_empty_ip.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_IP) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyPort() {
        String configPath = "/16_test_empty_port.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_PORT) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyBoolean() {
        String configPath = "/17_test_empty_boolean.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_BOOLEAN) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyPlayerType() {
        String configPath = "/18_test_empty_player_type.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_PLAYER_TYPE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyPath() {
        String configPath = "/19_test_empty_path.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyColor() {
        String configPath = "/20_test_empty_color.conf";
        try {
            new ClientConfig(configPath);
            Assert.fail();
        } catch (ConfigException e) {
            if(e.getExceptionType() != ConfigExceptionEnum.ABSENT_COLOR) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testCompeteConfigRead1() {
        String configPath = "/50_test_complete.conf";
        try {
            new ClientConfig(configPath);
        } catch (ConfigException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompeteConfigRead2() {
        String configPath = "/51_test_complete.conf";
        try {
            new ClientConfig(configPath);
        } catch (ConfigException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompeteConfigRead3() {
        String configPath = "/52_test_complete.conf";
        try {
            new ClientConfig(configPath);
        } catch (ConfigException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompeteConfigRead4() {
        String configPath = "/53_test_complete.conf";
        try {
            new ClientConfig(configPath);
        } catch (ConfigException e) {
            Assert.fail();
        }
    }
}