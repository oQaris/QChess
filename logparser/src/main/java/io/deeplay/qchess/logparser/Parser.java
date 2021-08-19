package io.deeplay.qchess.logparser;

import io.deeplay.qchess.lobot.profiler.Profile;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

public class Parser {
    private final ParseMode parseMode;
    private final Profile profile;

    public Parser(final ParseMode parseMode, final int parseModeParameter) throws ParseException {
        if(parseMode == null) {
            throw new ParseException(ParseErrorCode.NULL_PARSE_MODE);
        }
        if(parseModeParameter <= 0) {
            throw new ParseException(ParseErrorCode.NON_POSITIVE_PARSE_MODE_PARAMETER);
        }
        this.parseMode = parseMode;
        this.parseMode.setCount(parseModeParameter);
        profile = new Profile();
    }

    public void profilesUpdate(final File directory) throws ParseException {
        profilesUpdate(directory, -1);
    }

    public void profilesUpdate(final File directory, final int logsCount) throws ParseException {
        if(!directory.isDirectory()) {
            throw new ParseException(ParseErrorCode.WRONG_DIRECTORY);
        }

        final InputStream inputStream = openDirectory(directory);
        parseLogs(inputStream, logsCount);
        //saveProfile();
    }

    private InputStream openDirectory(final File directory) {
        final BufferedInputStream bis = null;
        return bis;
    }

    private void parseLogs(final InputStream inputStream, final int logsCount) {
    }
}
