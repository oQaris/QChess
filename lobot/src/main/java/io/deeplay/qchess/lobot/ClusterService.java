package io.deeplay.qchess.lobot;

import io.deeplay.qchess.MoveWeight;
import io.deeplay.qchess.game.model.Move;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ClusterService {
    private static final BiFunction<ClusterPoint, ClusterPoint, Integer> distFunc = (cp1, cp2) -> Math
        .abs(cp1.getValue() - cp2.getValue());

    public static List<Move> getClusteredMovesSequential(final Set<MovePoint> evaluationSet, final int clusterCount, final int eps) {
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

    public static List<MoveWeight> getClusteredMovesDBSCANWithWeight(final Set<MovePoint> evaluationSet, final int minPts, final int eps) {
        DBSCAN(evaluationSet, minPts, eps);
        return formClustersDBSCANWithWeight(new ArrayList<>(evaluationSet));
    }

    private static List<MoveWeight> formClustersDBSCANWithWeight(final List<MovePoint> points) {
        Collections.shuffle(points);
        int i = -2;
        for(final MovePoint point : points) {
            if(point.getMark() == -1) {
                point.setMark(i);
                i--;
            }
        }
        // todo походу долго
        final Map<MovePoint, Integer> moveWeightMap = new HashMap<>();
        for(final MovePoint movePoint : points) {
            if(!moveWeightMap.containsKey(movePoint)) {
                moveWeightMap.put(movePoint, 1);
            } else {
                moveWeightMap.put(movePoint, moveWeightMap.get(movePoint) + 1);
            }
        }

        final int fullSize = points.size();
        final List<MoveWeight> result = new LinkedList<>();
        for(final MovePoint movePoint : moveWeightMap.keySet()) {
            result.add(new MoveWeight(movePoint.getMove(), (moveWeightMap.get(movePoint) * 1.0) / fullSize));
        }
        return result;
    }

    public static List<Move> getClusteredMovesDBSCAN(final Set<MovePoint> evaluationSet, final int minPts, final int eps) {
        DBSCAN(evaluationSet, minPts, eps);
        return formClustersDBSCAN(new ArrayList<>(evaluationSet)).stream().map(MovePoint::getMove).collect(Collectors.toList());
    }

    private static <T extends ClusterPoint> Set<T> formClustersDBSCAN(final List<T> points) {
        Collections.shuffle(points);
        int i = -2;
        for(final T point : points) {
            if(point.getMark() == -1) {
                point.setMark(i);
                i--;
            }
        }
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
    public static <T extends ClusterPoint> void DBSCAN(final Set<T> DB, final int minPts, final int eps) {
        int C = 1;                                                  /* Счётчик кластеров */
        for (final ClusterPoint P : DB) {
            if (P.getMark() != 0) {
                continue;                          /* Точка была просмотрена во внутреннем цикле */
            }
            final List<T> N = RangeQuery(DB, P, eps);          /* Находим соседей */
            if (N.size() < minPts) {                                /* Проверка плотности */
                P.setMark(-1);                                      /* Помечаем как шум */
                continue;
            }
            ++C;                                                    /* следующая метка кластера */
            P.setMark(C);                                           /* Помечаем начальную точку */
            N.remove(P);
                                                                        /* Соседи для расширения */
            final Set<ClusterPoint> S = new HashSet<>(N);
            for (final ClusterPoint Q : S) {                        /* Обрабатываем каждую зачаточную точку */
                if (Q.getMark() == -1) {
                    Q.setMark(C);                                   /* Заменяем метку Шум на Край */
                }
                if (Q.getMark() != 0) {
                    continue;                      /* Была просмотрена */
                }
                Q.setMark(C);                                       /* Помечаем соседа */
                final List<T> N1 = RangeQuery(DB, Q, eps);                         /* Находим соседей */
                if (N1.size() >= minPts) {                           /* Проверяем плотность */
                    N.addAll(
                        N1);                                    /* Добавляем соседей в набор зачаточных точек */
                }
                N.remove(Q);
            }
        }
    }

    private static <T extends ClusterPoint> List<T> RangeQuery(final Set<T> set, final ClusterPoint Q,
        final int eps) {
        final BiFunction<ClusterPoint, ClusterPoint, Integer> distFunc = (cp1, cp2) -> Math
            .abs(cp1.getValue() - cp2.getValue());
        final List<T> neighbors = new ArrayList<>();

        for (final T P : set) {
            if (distFunc.apply(Q, P) < eps) {
                neighbors.add(P);
            }
        }

        return neighbors;
    }
}
