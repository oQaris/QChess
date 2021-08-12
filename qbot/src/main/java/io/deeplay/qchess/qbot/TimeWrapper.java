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

    public TimeWrapper(final RemotePlayer player) {
        super(
                player.getRoomSettings(),
                player.getColor(),
                player.getSessionToken(),
                player.getName());
        this.player = player;
    }

    @Override
    public Move getNextMove() throws ChessError {
        final long startTime = System.currentTimeMillis();
        final Move result = this.player.getNextMove();
        this.times.add(System.currentTimeMillis() - startTime);
        return result;
    }

    /** @return Среднее арифметическое времени хода */
    public double getMean() {
        return (double) this.times.stream().mapToLong(Long::longValue).sum() / this.times.size();
    }

    /** @return Медиану времени хода */
    public long getMedian() {
        return this.times.stream().sorted().skip(this.times.size() / 2).findFirst().orElse(0L);
    }

    /** @return Моду времени хода */
    public long getMode() {
        final Optional<Entry<Long, Long>> maxInFrequency =
                this.times.stream()
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet()
                        .stream()
                        .max(Entry.comparingByValue());
        return maxInFrequency.isPresent() ? maxInFrequency.get().getKey() : 0L;
    }

    /** @return Максимальное временя хода */
    public long getMax() {
        return this.times.stream().max(Long::compare).orElse(0L);
    }

    /** @return Минимальное временя хода */
    public long getMin() {
        return this.times.stream().min(Long::compare).orElse(0L);
    }

    /** Выводит на консоль схематичный график времени обдумывания каждого хода */
    public void printGraph() {
        final long min = this.getMin();
        for (final Long time : this.times) {
            final long t = time / min;
            System.out.println("*".repeat((int) t));
        }
    }
}
