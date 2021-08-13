package io.deeplay.qchess.server.service.config;

import org.junit.Assert;
import org.junit.Test;

public class ConfigServiceTest {

    @Test
    public void testNonPositivePort() {
        final String incorrectPort = "-100";
        try {
            ConfigService.validatePort(incorrectPort);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.NON_POSITIVE_PORT_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testNonPositiveMaxPlayers() {
        final String incorrectMaxPlayers = "-1";
        try {
            ConfigService.validateMaxPlayers(incorrectMaxPlayers);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.NON_POSITIVE_MAX_PLAYERS_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testNonPositiveTournamentNumberGame() {
        final String incorrectTournamentNumberGame = "0";
        try {
            ConfigService.validateTournamentNumberGame(incorrectTournamentNumberGame);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.NON_POSITIVE_TOURNAMENT_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectPort() {
        final String incorrectPort = "8a88";
        try {
            ConfigService.validatePort(incorrectPort);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PORT_VALUE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectMaxPlayers() {
        final String incorrectMaxPlayers = "двадцать";
        try {
            ConfigService.validateMaxPlayers(incorrectMaxPlayers);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_MAX_PLAYERS) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testIncorrectTournamentNumberGame() {
        final String incorrectTournamentNumberGame = "100o0";
        try {
            ConfigService.validateTournamentNumberGame(incorrectTournamentNumberGame);
            Assert.fail();
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_TOURNAMENT_VALUE) {
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
        } catch (ConfigException e) {
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
        } catch (ConfigException e) {
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
        } catch (ConfigException e) {
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
        } catch (ConfigException e) {
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
        } catch (ConfigException e) {
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
        } catch (ConfigException e) {
            if (e.getExceptionType() != ConfigExceptionErrorCode.INCORRECT_PATH) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testCompeteConfigRead() {
        try {
            new ServerSettings();
        } catch (ConfigException e) {
            Assert.fail();
        }
    }
}
