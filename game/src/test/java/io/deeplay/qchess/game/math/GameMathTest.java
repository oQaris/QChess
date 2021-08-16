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
            final int fastMult = GameMath.pow(31, t);

            Assert.assertEquals(simpleMult, fastMult);
        }
    }

    @Test
    public void testHashCode64_STD() {
        final int[] array = new int[64];
        for (int i = 0; i < 64; ++i) array[i] = i * 11 % 37;

        final int stdHash = Arrays.hashCode(array);

        int result = 1;
        for (int i = 0; i < 64; ++i) result = 31 * result + array[i];

        Assert.assertEquals(stdHash, result);
        Assert.assertEquals(stdHash, GameMath.hashCode64(array));
    }

    @Test
    public void testRecalcHash() {
        final int[] array = new int[64];
        final int[] toSet = new int[64];
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

    @Test
    public void testRecalcZobristHash() {
        final int[][] array = new int[8][8];
        final int[][] toSet = new int[8][8];
        GameMath.srand(1);
        for (int y = 0; y < 8; ++y)
            for (int x = 0; x < 8; ++x) {
                array[y][x] = Math.abs((y + x) * GameMath.rand()) % 13;
                toSet[y][x] = Math.abs((y + x) * GameMath.rand()) % 13;
            }

        int hash64 = GameMath.zobristHash64(array);

        for (int y = 0; y < 8; ++y)
            for (int x = 0; x < 8; ++x) {
                // recalc:
                hash64 ^=
                        GameMath.zobristHash64[array[y][x]][y][x]
                                ^ GameMath.zobristHash64[toSet[y][x]][y][x];

                // set
                array[y][x] = toSet[y][x];

                // tests:
                final int finalHash64 = GameMath.zobristHash64(array);
                Assert.assertEquals(finalHash64, hash64);
            }
    }

    @Ignore
    @Test
    public void speedTest() {
        final int[] array = new int[64];
        for (int i = 0; i < 64; ++i) array[i] = i * 11 % 37;

        final int[][] array2 = new int[8][8];
        final int[][] toSet = new int[8][8];
        GameMath.srand(1);
        for (int y = 0; y < 8; ++y)
            for (int x = 0; x < 8; ++x) {
                array2[y][x] = Math.abs((y + x) * GameMath.rand()) % 13;
                toSet[y][x] = Math.abs((y + x) * GameMath.rand()) % 13;
            }

        final int zobristHash64 = GameMath.zobristHash64(array2);
        int hash64 = GameMath.hashCode64(array);

        double maxTime = Double.MIN_VALUE;
        double minTime = Double.MAX_VALUE;

        final int count = 100000;
        int i = count;
        double time = 0;
        while (--i >= 0) {
            final long startTime = System.nanoTime();

            // for (int j = 0; j < 1000; ++j) GameMath.hashCode64(array);
            for (int j = 0; j < 1000; ++j) {
                for (int y = 0; y < 8; ++y)
                    for (int x = 0; x < 8; ++x) {
                        // recalc:
                        /*zobristHash64 ^=
                                GameMath.zobristHash64[array2[y][x]][y][x]
                                        ^ GameMath.zobristHash64[toSet[y][x]][y][x];*/

                        final int g = 8 * y + x;
                        hash64 += GameMath.hash64Coeff[g] * (toSet[y][x] - array[g]);

                        // set
                        // array2[y][x] = toSet[y][x];
                        array[g] = toSet[y][x];
                    }
            }

            final long endTime = System.nanoTime();
            final double t = (double) (endTime - startTime);
            if (t > maxTime) maxTime = t;
            if (t < minTime) minTime = t;
            time += t / count;
        }
        System.out.println("average: " + time / 1000000. + " millis");
        System.out.println("max: " + maxTime / 1000000. + " millis");
        System.out.println("min: " + minTime / 1000000. + " millis");
    }

    @Test
    public void testRandom() {
        GameMath.srand(1);
        // [6 фигур] x [2 цвета] x [64 квадрата]
        final int[] hashes = new int[768];
        for (int i = 0; i < 768; ++i) {
            hashes[i] = GameMath.rand();
        }

        for (int i = 0; i < 767; ++i) {
            for (int j = i + 1; j < 768; ++j) {
                if (hashes[i] == hashes[j]) {
                    Assert.fail();
                }
            }
        }
    }
}
