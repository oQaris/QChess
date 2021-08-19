package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.lobot.evaluation.Evaluation;
import io.deeplay.qchess.lobot.evaluation.PestoEvaluation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Strategy {

    private final Evaluation evaluation;
    private final TraversalAlgorithm algorithm;
    private final int depth;
    private final boolean onMonteCarlo;

    public Strategy() {
        this(new PestoEvaluation(), TraversalAlgorithm.MINIMAX, 5, false);
    }

    public Strategy(
            final Evaluation evaluation,
            final TraversalAlgorithm algorithm,
            final int depth,
            final boolean onMonteCarlo) {
        this.evaluation = evaluation;
        this.algorithm = algorithm;
        this.depth = depth;
        this.onMonteCarlo = onMonteCarlo;
    }

    public static int getTerminalEvaluation(final Color color, final EndGameType endGameType) {
        if (color == Color.WHITE) {
            return switch (endGameType) {
                case CHECKMATE_TO_BLACK -> Integer.MIN_VALUE + 100;
                case CHECKMATE_TO_WHITE -> Integer.MAX_VALUE - 100;
                default -> 0;
            };
        } else {
            return switch (endGameType) {
                case CHECKMATE_TO_WHITE -> Integer.MIN_VALUE + 100;
                case CHECKMATE_TO_BLACK -> Integer.MAX_VALUE - 100;
                default -> 0;
            };
        }
    }

    public static List<Integer> getClusters(final Set<Integer> evaluationSet) {
        return null;
    }

    // -1 - noise, 0 - undefined
    public static void DBSCAN(final Set<ClusterPoint> DB, final int minPts, final double eps) {
        int C = 1; /* Счётчик кластеров */
        for (final ClusterPoint P : DB) {
            if (P.getMark() != 0) {
                continue; /* Точка была просмотрена во внутреннем цикле */
            }
            List<ClusterPoint> N = RangeQuery(DB, P, eps); /* Находим соседей */
            if (N.size() < minPts) {
                /* Проверка плотности */
                P.setMark(-1); /* Помечаем как шум */
                continue;
            }
            ++C; /* следующая метка кластера */
            P.setMark(C); /* Помечаем начальную точку */
            final Set<ClusterPoint> S =
                    N.stream()
                            .filter(cp -> !cp.equals(P))
                            .collect(Collectors.toSet()); /* Соседи для расширения */
            System.err.println(P + " <--> " + S);
            // TODO вылетает Exception из-за добавления в S
            for (final ClusterPoint Q : S) {
                /* Обрабатываем каждую зачаточную точку */
                if (Q.getMark() == -1) {
                    Q.setMark(C); /* Заменяем метку Шум на Край */
                }
                if (Q.getMark() != 0) {
                    continue; /* Была просмотрена */
                }
                Q.setMark(C); /* Помечаем соседа */
                N = RangeQuery(DB, Q, eps); /* Находим соседей */
                if (N.size() >= minPts) {
                    /* Проверяем плотность */
                    S.addAll(N); /* Добавляем соседей в набор зачаточных точек */
                }
            }
        }
    }

    private static List<ClusterPoint> RangeQuery(
            final Set<ClusterPoint> set, final ClusterPoint Q, final double eps) {
        final BiFunction<ClusterPoint, ClusterPoint, Integer> distFunc =
                (cp1, cp2) -> Math.abs(cp1.getValue() - cp2.getValue());
        final List<ClusterPoint> neighbors = new ArrayList<>();

        for (final ClusterPoint P : set) {
            if (distFunc.apply(Q, P) < eps) {
                neighbors.add(P);
            }
        }

        return neighbors;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public TraversalAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getDepth() {
        return depth;
    }

    public boolean getOnMonteCarlo() {
        return onMonteCarlo;
    }
}
