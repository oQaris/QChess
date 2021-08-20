package io.deeplay.qchess.logparser;

import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.lobot.profiler.Profile;
import io.deeplay.qchess.lobot.profiler.ProfileService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private final Pattern moveCellPattern = Pattern.compile("[a-h][1-8]-[a-h][1-8]");
    private final Pattern moveTypePattern = Pattern.compile("\\([A-Z_]+\\)");
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

    private void addToProfile(final String moveLine, final String fenLine) throws ParseException {
        final Move move = moveParse(moveLine);
        final String fen = fenParse(fenLine);
        //profile.add(fen, move);
        System.out.printf("{%s} -> {%s}\n", fen, move);
    }

    private Move moveParse(final String moveLine) throws ParseException {
        final String moveCellStr = moveComponentParse(moveLine, moveCellPattern);
        final String moveTypeStr = moveComponentParse(moveLine, moveTypePattern);
        
        return new Move(MoveType.valueOf(moveTypeStr.substring(1, moveTypeStr.length() - 1)), Cell.parse(moveCellStr.substring(0, 2)), Cell.parse(moveCellStr.substring(3)));
    }

    private String moveComponentParse(final String moveLine, final Pattern pattern) throws ParseException {
        final Matcher matcher = pattern.matcher(moveLine);
        if(!matcher.find()) {
            throw new ParseException(ParseErrorCode.REGEX_ERROR);
        }
        return moveLine.substring(matcher.start(), matcher.end());
    }

    private String fenParse(final String fenStr) {
        return fenStr.substring(5);
    }

}
