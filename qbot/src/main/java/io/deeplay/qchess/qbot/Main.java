package io.deeplay.qchess.qbot;

import static io.deeplay.qchess.qbot.profile.ParserKt.fill;
import static io.deeplay.qchess.qbot.profile.ParserKt.getProfilesMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.deeplay.qchess.qbot.profile.Profile;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public class Main {

    private static final Path toReplace = Paths.get("./data/profiles.gson");
    private static final Path newContents = toReplace.resolveSibling("profiles_temp.json");

    public static void main(final String[] args) throws IOException {
        // Запуск парсинга профилей
        fill();
        try (final BufferedWriter writer =
                Files.newBufferedWriter(
                        newContents,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)) {
            new Gson()
                    .toJson(
                            getProfilesMap(),
                            new TypeToken<HashMap<String, Profile>>() {}.getType(),
                            writer);
        }
        Files.move(
                newContents,
                toReplace,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
    }
}
