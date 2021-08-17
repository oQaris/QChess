package io.deeplay.qchess.lobot;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;

public class StrategyTest {

    @Test
    public void testDBSCAN() {
        final List<Integer> list = Arrays.asList(1, 2, 3, 114, 115, 116, 117, 288, 289, 500);
        final Set<ClusterPoint> set = list.stream().map(i -> new ClusterPoint(i, 0))
            .collect(Collectors.toSet());
        Strategy.DBSCAN(set, 2, 40);
        for (final ClusterPoint cp : set) {
            System.out.println(cp);
        }
    }
}