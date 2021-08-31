package io.deeplay.qchess.lobot.profiler;

public class FENService {
    public static int getFiguresCount(final String fen) {
        final String arrangement = fen.split(" ")[0];
        int count = 0;
        for(final char c : arrangement.toCharArray()) {
            if(Character.isLetter(c)) {
                count++;
            }
        }
        return count;
    }
}
