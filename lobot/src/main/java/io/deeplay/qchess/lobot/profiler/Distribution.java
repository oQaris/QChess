package io.deeplay.qchess.lobot.profiler;

import io.deeplay.qchess.game.model.Move;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Distribution {
    private final Map<Move, Integer> data;

    public Distribution() {
        data = new HashMap<>();
    }

    /**
     * Если такой ход уже есть в распределении то значение увеличится на 1, <br>
     * иначе создастся новая пара со значением 1.
     * @param move ключ по которому идет обновление распределения
     */
    public Distribution setOrAddMove(final Move move) {
        final int value = data.getOrDefault(move, 0);
        data.put(move, value + 1);
        return this;
    }

    /**
     * Проверка распределения на пустоту.
     * @return true если распределение пусто, false иначе
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * @param moves лист ходов, для которых нужно получить оценку
     * @return лист оценок для ходов где ходу moves.get(i) соответствует оценка под номером i
     */
    public List<Integer> getProbabilities(final List<Move> moves) {
        final List<Integer> result = new LinkedList<>();
        for(final Move move : moves) {
            result.add(data.getOrDefault(move, 0));
        }
        return result;
    }
}
