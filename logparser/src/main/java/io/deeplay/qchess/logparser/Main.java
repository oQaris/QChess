package io.deeplay.qchess.logparser;

import java.io.File;

public class Main {

    public static void main(final String[] args) throws ParseException {
        final Parser parser = new Parser(ParseMode.START_MOVES, 4);
        parser.profileUpdate(new File("C:/Users/Admin/IdeaProjects/QChess/logparser/src/main/resources/logs"));
    }
}
