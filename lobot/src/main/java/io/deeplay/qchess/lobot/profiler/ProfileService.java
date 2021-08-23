package io.deeplay.qchess.lobot.profiler;

import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class ProfileService {
    public static final Pattern moveCellPattern = Pattern.compile("[a-h][1-8]-[a-h][1-8]");
    public static final Pattern moveTypePattern = Pattern.compile("\\([A-Z_]+\\)");
    public static final Pattern figureTypePattern = Pattern.compile("turn into [A-Z_]+");
    public static final List<String> turnTemplates = new LinkedList<>();
    public static final List<String> figureTemplates = Arrays.asList(FigureType.nameOfTypeNumber);

    private static final File saveDirectory = new File("./lobot/src/main/resources/profiles");
    private static final File loadDirectory = new File("./lobot/src/main/resources/profiles");

    static {
        for (final MoveType mt : MoveType.values()) {
            turnTemplates.add(mt.name());
        }
    }

    public static void saveProfile(final Profile profile) throws ProfileException {
        final String namePath = saveDirectory + "/" + profile.getName();
        final File dir = new File(namePath);
        dir.mkdirs();
        try(final BufferedWriter bw = new BufferedWriter(new FileWriter(dir.getAbsoluteFile() + "/" + profile.getName() + ".profile"))) {
            profile.save(bw);
        } catch (final IOException e) {
            throw new ProfileException(ProfileErrorCode.SAVE_ERROR);
        }
    }

    public static Profile loadProfile(final String name) throws ProfileException {
        final Profile profile = new Profile(name);
        final String namePath = loadDirectory + "/" + name;
        try(final BufferedReader br = new BufferedReader(new FileReader(namePath + "/" + name + ".profile"))) {
            profile.load(br);
        } catch (final IOException e) {
            throw new ProfileException(ProfileErrorCode.LOAD_ERROR);
        }
        return profile;
    }

    public static Profile loadProfile() throws ProfileException {
        return loadProfile("general");
    }
}
