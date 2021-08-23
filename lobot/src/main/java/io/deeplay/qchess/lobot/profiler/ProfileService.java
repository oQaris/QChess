package io.deeplay.qchess.lobot.profiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProfileService {
    private static final File saveDirectory = new File("./lobot/src/main/resources/profiles");
    private static final File loadDirectory = new File("./lobot/src/main/resources/profiles");

    public static void saveProfile(final Profile profile) throws ProfileException {
        final String namePath = saveDirectory + "/" + profile.getName();
        final File dir = new File(namePath);
        dir.mkdirs();
        try(final BufferedWriter bw = new BufferedWriter(new FileWriter(dir.getAbsoluteFile() + "/" + profile.getName() + ".profile"))) {
            profile.save(bw);
        } catch (final IOException e) {
            e.printStackTrace();
            throw new ProfileException(ProfileErrorCode.SAVE_ERROR);
        }
    }

    public static Profile loadProfile() {
        final Profile profile = null;
        return profile;
    }
}
