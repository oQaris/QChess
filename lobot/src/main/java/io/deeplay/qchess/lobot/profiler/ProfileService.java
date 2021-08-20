package io.deeplay.qchess.lobot.profiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProfileService {
    private static final File saveDirectory = new File("./lobot/src/main/resources/profiles");
    private static final File loadDirectory = new File("./lobot/src/main/resources/profiles");

    public static void saveProfile(final Profile profile) {
        final String namePath = saveDirectory + "/" + profile.getName();
        try(final BufferedWriter bw = new BufferedWriter(new FileWriter(namePath + "/profile.csv"))) {
            profile.save(bw);
        } catch (final IOException e) {
            //
        }
    }

    public static Profile loadProfile() {
        final Profile profile = null;
        return profile;
    }
}
