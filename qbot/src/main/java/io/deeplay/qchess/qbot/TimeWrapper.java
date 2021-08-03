package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimeWrapper extends RemotePlayer {
    private final RemotePlayer player;
    private final List<Long> times = new ArrayList<>(300);

    public TimeWrapper(RemotePlayer player) {
        super(player.getRoomSettings(), player.getColor(), player.getSessionToken());
        this.player = player;
    }

    @Override
    public Move getNextMove() throws ChessError {
        final long startTime = System.currentTimeMillis();
        final Move result = player.getNextMove();
        times.add(System.currentTimeMillis() - startTime);
        return result;
    }

    public double getMean() {
        return (double) times.stream().mapToLong(Long::longValue).sum() / times.size();
    }

    public long getMedian() {
        return times.stream().sorted().skip(times.size() / 2).findFirst().orElse(0L);
    }

    public long getMode() {
        final Optional<Entry<Long, Long>> maxInFrequency =
                times.stream()
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet()
                        .stream()
                        .max(Entry.comparingByValue());
        return maxInFrequency.isPresent() ? maxInFrequency.get().getKey() : 0L;
    }

    public long getMax() {
        return times.stream().max(Long::compare).orElse(0L);
    }

    public long getMin() {
        return times.stream().min(Long::compare).orElse(0L);
    }

    public void printGraph() {
        final long min = getMin();
        for (Long time : times) {
            long t = time / min;
            System.out.println("*".repeat((int) t));
        }
    }
}
