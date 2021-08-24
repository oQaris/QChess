package io.deeplay.qchess.logparser;

import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.lobot.profiler.Profile;
import io.deeplay.qchess.lobot.profiler.ProfileException;
import io.deeplay.qchess.lobot.profiler.ProfileService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private final Pattern moveCellPattern = Pattern.compile("[a-h][1-8]-[a-h][1-8]");
    private final Pattern moveTypePattern = Pattern.compile("\\([A-Z_]+\\)");
    private final Pattern figureTypePattern = Pattern.compile("turn into [A-Z_]+");
    private final ParseMode parseMode;
    private final List<Profile> profiles = new LinkedList<>();
    private final List<String> turnTemplates = new LinkedList<>();
    private final List<String> figureTemplates = Arrays.asList(FigureType.nameOfTypeNumber);

    public Parser(final ParseMode parseMode, final int parseModeParameter) throws ParseException {
        if(parseMode == null) {
            throw new ParseException(ParseErrorCode.NULL_PARSE_MODE);
        }
        if(parseModeParameter <= 0) {
            throw new ParseException(ParseErrorCode.NON_POSITIVE_PARSE_MODE_PARAMETER);
        }
        this.parseMode = parseMode;
        this.parseMode.setCount(parseModeParameter);
        profiles.add(new Profile());

        for (final MoveType mt : MoveType.values()) {
            turnTemplates.add(mt.name());
        }
    }

    public void profileUpdate(final File directory) throws ParseException, ProfileException {
        profileUpdate(directory, Integer.MIN_VALUE);
    }

    public void profileUpdate(final File directory, final int logsCount)
        throws ParseException, ProfileException {
        if(!directory.isDirectory()) {
            throw new ParseException(ParseErrorCode.WRONG_DIRECTORY);
        }
        if(logsCount < 1 && logsCount != Integer.MIN_VALUE) {
            throw new ParseException(ParseErrorCode.WRONG_LOGS_COUNT);
        }
        final List<File> logFiles = getAllLogFiles(directory, logsCount);
        parseLogs(logFiles);
        for(final Profile profile : profiles) {
            ProfileService.saveProfile(profile);
        }
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

    private Profile getProfileForName(final String name) {
        for(final Profile profile : profiles) {
            if(name.equals(profile.getName())) {
                return profile;
            }
        }
        final Profile profile = new Profile(name);
        profiles.add(profile);
        return profile;
    }

    private void parseLogs(final List<File> logFiles) throws ParseException {
        for(final File file : logFiles) {
            try (final BufferedReader is = new BufferedReader(new FileReader(file))) {
                String prevFEN = "FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";
                final String whiteName = is.readLine().split(":")[1].trim();
                final String blackName = is.readLine().split(":")[1].trim();
                String name = whiteName;
                String line = is.readLine();
                String move = null;
                while (line != null) {
                    if(line.startsWith("WHITE")) {
                        name = whiteName;
                        move = line;
                    } else if (line.startsWith("BLACK")) {
                        name = blackName;
                        move = line;
                    } else if(move != null) {
                        if(line.startsWith("FEN") && !name.equals("Рандомный_Бот")) {
                            addToProfile(getProfileForName(name), move, prevFEN);
                            prevFEN = line;
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

    private void addToProfile(final Profile profile, final String moveLine, final String fenLine) throws ParseException {
        final Move move = moveParse(moveLine);
        final String fen = fenParse(fenLine);
        profiles.get(0).add(fen, move);
        profile.add(fen, move);
    }

    private Move moveParse(final String moveLine) throws ParseException {
        final String moveCellStr = moveComponentParse(moveLine, moveCellPattern);
        final String moveTypeStr = moveComponentParse(moveLine, moveTypePattern);
        final String moveTypeStrCut = moveTypeStr.substring(1, moveTypeStr.length() - 1);

        if(!turnTemplates.contains(moveTypeStrCut)) {
            throw new ParseException(ParseErrorCode.REGEX_ERROR_MOVE_TYPE);
        }
        final Move move = new Move(MoveType.valueOf(moveTypeStrCut), Cell.parse(moveCellStr.substring(0, 2)), Cell.parse(moveCellStr.substring(3)));
        if(move.getMoveType() == MoveType.TURN_INTO || move.getMoveType() == MoveType.TURN_INTO_ATTACK) {
            final String moveTurnIntoFigure = moveComponentParse(moveLine, figureTypePattern);
            final String moveTurnIntoFigureCut  = moveTurnIntoFigure.split(" ")[2];
            if(!figureTemplates.contains(moveTurnIntoFigureCut)) {
                throw new ParseException(ParseErrorCode.REGEX_ERROR_FIGURE_TYPE);
            }
            move.turnInto = FigureType.valueOf(moveTurnIntoFigureCut);
        }
        return move;
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
