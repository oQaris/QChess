package io.deeplay.qchess.lobot.profiler;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile {
    private final Map<String, Distribution> repository;
    private final String name;

    public Profile() {
        this("general");
    }

    public Profile(final String name) {
        repository = new HashMap<>();
        this.name = name;
    }

    /**
     * Добавляет запись в профиль. Если такого ключа не было создаётся новая запись<br>
     * Иначе обновляется значение о ключу.
     *
     * @param fen нотация Форсайта-Эдвардса
     * @param move ход который добавится в значение
     */
    public void add(final String fen, final Move move) {
        final Distribution distribution = repository.getOrDefault(fen, defaultFEN());
        if (distribution.isEmpty()) {
            repository.put(fen, distribution);
        }
        distribution.setOrAddMove(move, 1);
    }

    /**
     * Добавляет запись в профиль. Если такого ключа не было создаётся новая запись<br>
     * Иначе обновляется значение о ключу.
     *
     * @param gs из неё мы получим состояние борды, которое используем как ключ
     * @param move ход который добавится в значение
     */
    public void add(final GameSettings gs, final Move move) throws ChessError {
        final String fen = gs.history.getBoardToStringForsythEdwards();
        add(fen, move);
    }

    public void add(final String fen, final Distribution distribution) {
        repository.put(fen, distribution);
    }

    /**
     * @param gs по которому будет вернётся распределение оценок ходов
     * @return распределение оценок ходов для этого GameSetting
     */
    public Distribution get(final GameSettings gs) throws ChessError {
        final String fen = gs.history.getBoardToStringForsythEdwards();
        return repository.getOrDefault(fen, defaultFEN());
    }

    /** @return распределение для GameSetting которого нет в профиле */
    private Distribution defaultFEN() {
        return new Distribution();
    }

    public String getName() {
        return name;
    }

    public void save(final BufferedWriter bw) throws IOException {
        final List<String> list = new ArrayList<>(repository.keySet());
        list.sort(
                (o1, o2) -> {
                    final int i1 = FENService.getFiguresCount(o1);
                    final int i2 = FENService.getFiguresCount(o2);
                    return -Integer.compare(i1, i2);
                });
        for (final String key : list) {
            bw.write(convertProfileRowToString(key));
            bw.write(System.lineSeparator());
        }
    }

    public void load(final BufferedReader br) throws IOException, ProfileException {
        String line = br.readLine();
        addRecord(line);
        while (line != null) {
            line = br.readLine();
            addRecord(line);
        }
    }

    private void addRecord(final String line) throws ProfileException {
        if (line == null) return;
        final String[] parameters = line.split(" \\| ");
        final String fen = parameters[1];
        final Distribution distribution = distributionParse(parameters[2]);
        add(fen, distribution);
    }

    private Distribution distributionParse(final String line) throws ProfileException {
        final Distribution distribution = new Distribution();
        final String[] distributionElements = line.split("; ");
        for (final String distributionElement : distributionElements) {
            final String[] moveElements = distributionElement.split(", ");
            final Move move = moveParse(moveElements[0]);
            distribution.setOrAddMove(move, Integer.parseInt(moveElements[1]));
        }
        return distribution;
    }

    private Move moveParse(final String moveLine) throws ProfileException {
        final String moveCellStr = moveComponentParse(moveLine, ProfileService.moveCellPattern);
        final String moveTypeStr = moveComponentParse(moveLine, ProfileService.moveTypePattern);
        final String moveTypeStrCut = moveTypeStr.substring(1, moveTypeStr.length() - 1);

        if (!ProfileService.turnTemplates.contains(moveTypeStrCut)) {
            throw new ProfileException(ProfileErrorCode.REGEX_ERROR);
        }
        final Move move =
                new Move(
                        MoveType.valueOf(moveTypeStrCut),
                        Cell.parse(moveCellStr.substring(0, 2)),
                        Cell.parse(moveCellStr.substring(3)));
        if (move.getMoveType() == MoveType.TURN_INTO
                || move.getMoveType() == MoveType.TURN_INTO_ATTACK) {
            final String moveTurnIntoFigure =
                    moveComponentParse(moveLine, ProfileService.figureTypePattern);
            final String moveTurnIntoFigureCut = moveTurnIntoFigure.split(" ")[2];
            if (!ProfileService.figureTemplates.contains(moveTurnIntoFigureCut)) {
                throw new ProfileException(ProfileErrorCode.REGEX_ERROR);
            }
            move.turnInto = FigureType.valueOf(moveTurnIntoFigureCut);
        }
        return move;
    }

    private String moveComponentParse(final String moveLine, final Pattern pattern)
            throws ProfileException {
        final Matcher matcher = pattern.matcher(moveLine);
        if (!matcher.find()) {
            throw new ProfileException(ProfileErrorCode.REGEX_ERROR);
        }
        return moveLine.substring(matcher.start(), matcher.end());
    }

    private String convertProfileRowToString(final String fen) {
        final StringBuilder sb = new StringBuilder();
        final Distribution distribution = repository.get(fen);
        sb.append(FENService.getFiguresCount(fen)).append(" | ");
        sb.append(fen).append(" | ");
        sb.append(distribution);
        return sb.toString();
    }
}
