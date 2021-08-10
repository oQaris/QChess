package io.deeplay.qchess.game.math;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class GameMathTest {

    @Test
    public void testPow() {
        Assert.assertEquals(1, GameMath.pow(31, 0));
        Assert.assertEquals(31, GameMath.pow(31, 1));
        Assert.assertEquals(28629151, GameMath.pow(31, 5));
        Assert.assertEquals(-1807454463, GameMath.pow(31, 8));
        Assert.assertEquals(129082719, GameMath.pow(31, 11));
    }

    @Test
    public void testPowOverflow() {
        int simpleMult = 1;
        for (int t = 1; t < 100; ++t) {
            simpleMult *= 31;
            int fastMult = GameMath.pow(31, t);

            Assert.assertEquals(simpleMult, fastMult);
        }
    }

    @Test
    public void testHashCode64_STD() {
        int[] array = new int[64];
        for (int i = 0; i < 64; ++i) array[i] = i * 11 % 37;

        final int stdHash = Arrays.hashCode(array);

        int result = 1;
        for (int i = 0; i < 64; ++i) result = 31 * result + array[i];

        Assert.assertEquals(stdHash, result);
        Assert.assertEquals(stdHash, GameMath.hashCode64(array));
    }

    @Test
    public void testRecalcHash() {
        int[] array = new int[64];
        int[] toSet = new int[64];
        for (int i = 0; i < 64; ++i) {
            array[i] = i * 11 % 37;
            toSet[i] = i * 31 % 29;
        }

        int hash64 = GameMath.hashCode64(array);
        int hash64FastRecalc = GameMath.hashCode64(array);

        for (int i = 0; i < 64; ++i) {
            // to recalc hash64:
            // hash64 = hash64 + GameMath.pows64[i] * toSet[i] - GameMath.pows64[i] * array[i];
            // hash64 = hash64 + GameMath.pows64[i] * (toSet[i] - array[i]);

            // fast recalc:
            hash64FastRecalc += GameMath.hash64Coeff[i] * (toSet[i] - array[i]);

            // simple recalc:
            // pick
            hash64 -= GameMath.hash64Coeff[i] * array[i];
            // set
            array[i] = toSet[i];
            // recalc
            hash64 += GameMath.hash64Coeff[i] * toSet[i];

            // tests:
            final int finalHash64 = GameMath.hashCode64(array);
            Assert.assertEquals(finalHash64, hash64);
            Assert.assertEquals(finalHash64, hash64FastRecalc);
        }
    }

    @Ignore
    @Test
    public void speedTest() {
        int[] array = new int[64];
        for (int i = 0; i < 64; ++i) array[i] = i * 11 % 37;

        double maxTime = Double.MIN_VALUE;
        double minTime = Double.MAX_VALUE;

        int count = 100000;
        int i = count;
        double time = 0;
        while (--i >= 0) {
            long startTime = System.nanoTime();

            for (int j = 0; j < 1000; ++j) GameMath.hashCode64(array);
            // for (int j = 0; j < 1000; ++j) Arrays.hashCode(array);

            long endTime = System.nanoTime();
            double t = (double) (endTime - startTime);
            if (t > maxTime) maxTime = t;
            if (t < minTime) minTime = t;
            time += t / count;
        }
        System.out.println("average: " + time / 1000000. + " millis");
        System.out.println("max: " + maxTime / 1000000. + " millis");
        System.out.println("min: " + minTime / 1000000. + " millis");
    }
}
