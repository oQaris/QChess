package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.model.Move;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ClusterService {
    private static final BiFunction<ClusterPoint, ClusterPoint, Integer> distFunc = (cp1, cp2) -> Math
        .abs(cp1.getValue() - cp2.getValue());

    public static List<Move> getClusteredMoves(final Set<MovePoint> evaluationSet, final int clusterCount, final int eps) {
        final List<MovePoint> list = new ArrayList<>(evaluationSet);
        sequentialClustering(list, clusterCount, eps);
        return formClusters(list).stream().map(MovePoint::getMove).collect(Collectors.toList());
    }

    private static <T extends ClusterPoint> Set<T> formClusters(final List<T> points) {
        Collections.shuffle(points);
        final Set<T> set = new TreeSet<>(Comparator.comparingInt(ClusterPoint::getMark));
        set.addAll(points);
        return set;
    }

    /**
     * @param points лист ClusterPoint
     * @param clusterCount количество кластеров
     * @param eps ограничение расстояния между точками в кластере
     */
    public static <T extends ClusterPoint> void sequentialClustering(final List<T> points, final int clusterCount, final int eps) {
        if(points.size() < (clusterCount + 4)) {
            for(int i = 0; i < points.size(); i++) {
                points.get(i).setMark(i);
            }
        }
        points.sort(Comparator.comparingInt(ClusterPoint::getValue));
        Collections.reverse(points);
        int label = 1;
        int i = 1;
        final int size = points.size();
        boolean isClusterForming;
        ClusterPoint cp = points.get(0);
        cp.setMark(label);
        while(i < size) {
            final ClusterPoint nextCp = points.get(i);
            isClusterForming = distFunc.apply(cp, nextCp) < eps;
            if(isClusterForming) {
                nextCp.setMark(label);
            } else if (label >= clusterCount) {
                break;
            } else {
                label++;
                cp = nextCp;
                cp.setMark(label);
            }
            i++;
        }
    }

    // -1 - noise, 0 - undefined
    @Deprecated
    public static void DBSCAN(final Set<ClusterPoint> DB, final int minPts, final double eps) {
        int C = 1;                                                  /* Счётчик кластеров */
        for (final ClusterPoint P : DB) {
            if (P.getMark() != 0) {
                continue;                          /* Точка была просмотрена во внутреннем цикле */
            }
            List<ClusterPoint> N = RangeQuery(DB, P, eps);          /* Находим соседей */
            if (N.size() < minPts) {                                /* Проверка плотности */
                P.setMark(-1);                                      /* Помечаем как шум */
                continue;
            }
            ++C;                                                    /* следующая метка кластера */
            P.setMark(C);                                           /* Помечаем начальную точку */
            final Set<ClusterPoint> S = N
                .stream()
                .filter(cp -> !cp.equals(P))
                .collect(Collectors.toSet());                       /* Соседи для расширения */
            System.err.println(P + " <--> " + S);
            // TODO вылетает Exception из-за добавления в S
            for (final ClusterPoint Q : S) {                        /* Обрабатываем каждую зачаточную точку */
                if (Q.getMark() == -1) {
                    Q.setMark(C);                                   /* Заменяем метку Шум на Край */
                }
                if (Q.getMark() != 0) {
                    continue;                      /* Была просмотрена */
                }
                Q.setMark(C);                                       /* Помечаем соседа */
                N = RangeQuery(DB, Q, eps);                         /* Находим соседей */
                if (N.size() >= minPts) {                           /* Проверяем плотность */
                    S.addAll(
                        N);                                    /* Добавляем соседей в набор зачаточных точек */
                }
            }
        }
    }

    @Deprecated
    private static List<ClusterPoint> RangeQuery(final Set<ClusterPoint> set, final ClusterPoint Q,
        final double eps) {
        final BiFunction<ClusterPoint, ClusterPoint, Integer> distFunc = (cp1, cp2) -> Math
            .abs(cp1.getValue() - cp2.getValue());
        final List<ClusterPoint> neighbors = new ArrayList<>();

        for (final ClusterPoint P : set) {
            if (distFunc.apply(Q, P) < eps) {
                neighbors.add(P);
            }
        }

        return neighbors;
    }
}
