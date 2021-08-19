package io.deeplay.qchess.logparser;

import io.deeplay.qchess.lobot.profiler.Profile;
import io.deeplay.qchess.lobot.profiler.ProfileService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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

    public void profileUpdate(final File directory) throws ParseException {
        profileUpdate(directory, Integer.MIN_VALUE);
    }

    public void profileUpdate(final File directory, final int logsCount) throws ParseException {
        if(!directory.isDirectory()) {
            throw new ParseException(ParseErrorCode.WRONG_DIRECTORY);
        }
        if(logsCount < 1 && logsCount != Integer.MIN_VALUE) {
            throw new ParseException(ParseErrorCode.WRONG_LOGS_COUNT);
        }
        final List<File> logFiles = getAllLogFiles(directory, logsCount);
        parseLogs(logFiles);
        ProfileService.saveProfile(profile);
    }

    private List<File> getAllLogFiles(final File directory, int logsCount) {
        final List<File> result = new LinkedList<>();
        final File[] files = directory.listFiles();
        if(files != null && files.length > 0) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    final List<File> subDirectoryFileList = getAllLogFiles(file, logsCount);
                    result.addAll(subDirectoryFileList);
                    logsCount -= subDirectoryFileList.size();
                } else {
                    result.add(file);
                    logsCount--;
                }
                if(logsCount < 0 && logsCount != Integer.MIN_VALUE) {
                    break;
                }
            }
        }
        return result;
    }

    private void parseLogs(final List<File> logFiles) throws ParseException {
        for(final File file : logFiles) {
            try (final BufferedReader is = new BufferedReader(new FileReader(file))) {
                String line = is.readLine();
                String move = null;
                while (line != null) {
                    if(line.startsWith("WHITE") || line.startsWith("BLACK")) {
                        move = line;
                    } else if(move != null) {
                        if(line.startsWith("FEN")) {
                            addToProfile(move, line);
                        }
                        move = null;
                    }
                    line = is.readLine();
                }
            } catch (final IOException e) {
                throw new ParseException(ParseErrorCode.FILE_OPEN_ERROR);
            }
        }
    }

    private void addToProfile(final String move, final String line) {
    }
}
