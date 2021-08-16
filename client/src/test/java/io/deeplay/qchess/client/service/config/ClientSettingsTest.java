package io.deeplay.qchess.client.service.config;

import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClientSettingsTest {

    private ClientSettings cs;

    @Before
    public void setUp() throws ConfigException {
        cs = new ClientSettings();
    }

    @Test
    public void testAbsentIp() throws IOException {
        final String configPath = "/00_test_absent_ip.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_IP) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentPort() throws IOException {
        final String configPath = "/04_test_absent_port.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_PORT) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentBoolean() throws IOException {
        final String configPath = "/07_test_absent_boolean.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_BOOLEAN) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentPlayerType() throws IOException {
        final String configPath = "/09_test_absent_player_type.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_PLAYER_TYPE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentPath() throws IOException {
        final String configPath = "/11_test_absent_path.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testAbsentColor() throws IOException {
        final String configPath = "/13_test_absent_color.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_COLOR) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyIp() throws IOException {
        final String configPath = "/15_test_empty_ip.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_IP) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyPort() throws IOException {
        final String configPath = "/16_test_empty_port.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_PORT) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyBoolean() throws IOException {
        final String configPath = "/17_test_empty_boolean.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_BOOLEAN) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyPlayerType() throws IOException {
        final String configPath = "/18_test_empty_player_type.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_PLAYER_TYPE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyPath() throws IOException {
        final String configPath = "/19_test_empty_path.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testEmptyColor() throws IOException {
        final String configPath = "/20_test_empty_color.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
            Assert.fail();
        } catch (final ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.ABSENT_COLOR) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testCompeteConfigRead1() throws IOException {
        final String configPath = "/50_test_complete.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
        } catch (final ConfigException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompeteConfigRead2() throws IOException {
        final String configPath = "/51_test_complete.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
        } catch (final ConfigException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompeteConfigRead3() throws IOException {
        final String configPath = "/52_test_complete.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
        } catch (final ConfigException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompeteConfigRead4() throws IOException {
        final String configPath = "/53_test_complete.conf";
        try (final InputStream config = getClass().getResourceAsStream(configPath)) {
            cs.readConfig(config);
        } catch (final ConfigException e) {
            Assert.fail();
        }
    }
}
