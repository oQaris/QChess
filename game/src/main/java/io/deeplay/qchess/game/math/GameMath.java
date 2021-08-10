package io.deeplay.qchess.game.math;

public class GameMath {

    public static final int[] hash64Coeff = new int[64];
    public static final int pow31in64;

    static {
        int degree = 1;
        for (int i = 63; i >= 0; --i) {
            hash64Coeff[i] = degree;
            degree *= 31;
        }
        pow31in64 = degree;
    }

    /** @return a в степени n */
    public static int pow(int a, int n) {
        int res = 1;
        while (n != 0) {
            if ((n & 1) != 0) res *= a;
            a *= a;
            n >>= 1;
        }
        return res;
    }

    /**
     * Возвращает хеши для массива с размером 64, равные {@link java.util.Arrays#hashCode(byte[]
     * array)}. Есть возможность пересчитать хеш, не пересчитывая весь массив при его изменении:
     *
     * <pre>{@code
     * hash64 = hashCode64(array);
     * }</pre>
     *
     * Пересчитывание хеша при изменении массива:
     *
     * <pre>{@code
     * hash64 += GameMath.hash64Coeff[i] * (newValue - array[i]);
     * array[i] = newValue;
     * }</pre>
     *
     * @param array array.length == 64
     * @return хеш array
     */
    public static int hashCode64(final int[] array) {
        return pow31in64
                + array[0] * hash64Coeff[0]
                + array[1] * hash64Coeff[1]
                + array[2] * hash64Coeff[2]
                + array[3] * hash64Coeff[3]
                + array[4] * hash64Coeff[4]
                + array[5] * hash64Coeff[5]
                + array[6] * hash64Coeff[6]
                + array[7] * hash64Coeff[7]
                + array[8] * hash64Coeff[8]
                + array[9] * hash64Coeff[9]
                + array[10] * hash64Coeff[10]
                + array[11] * hash64Coeff[11]
                + array[12] * hash64Coeff[12]
                + array[13] * hash64Coeff[13]
                + array[14] * hash64Coeff[14]
                + array[15] * hash64Coeff[15]
                + array[16] * hash64Coeff[16]
                + array[17] * hash64Coeff[17]
                + array[18] * hash64Coeff[18]
                + array[19] * hash64Coeff[19]
                + array[20] * hash64Coeff[20]
                + array[21] * hash64Coeff[21]
                + array[22] * hash64Coeff[22]
                + array[23] * hash64Coeff[23]
                + array[24] * hash64Coeff[24]
                + array[25] * hash64Coeff[25]
                + array[26] * hash64Coeff[26]
                + array[27] * hash64Coeff[27]
                + array[28] * hash64Coeff[28]
                + array[29] * hash64Coeff[29]
                + array[30] * hash64Coeff[30]
                + array[31] * hash64Coeff[31]
                + array[32] * hash64Coeff[32]
                + array[33] * hash64Coeff[33]
                + array[34] * hash64Coeff[34]
                + array[35] * hash64Coeff[35]
                + array[36] * hash64Coeff[36]
                + array[37] * hash64Coeff[37]
                + array[38] * hash64Coeff[38]
                + array[39] * hash64Coeff[39]
                + array[40] * hash64Coeff[40]
                + array[41] * hash64Coeff[41]
                + array[42] * hash64Coeff[42]
                + array[43] * hash64Coeff[43]
                + array[44] * hash64Coeff[44]
                + array[45] * hash64Coeff[45]
                + array[46] * hash64Coeff[46]
                + array[47] * hash64Coeff[47]
                + array[48] * hash64Coeff[48]
                + array[49] * hash64Coeff[49]
                + array[50] * hash64Coeff[50]
                + array[51] * hash64Coeff[51]
                + array[52] * hash64Coeff[52]
                + array[53] * hash64Coeff[53]
                + array[54] * hash64Coeff[54]
                + array[55] * hash64Coeff[55]
                + array[56] * hash64Coeff[56]
                + array[57] * hash64Coeff[57]
                + array[58] * hash64Coeff[58]
                + array[59] * hash64Coeff[59]
                + array[60] * hash64Coeff[60]
                + array[61] * hash64Coeff[61]
                + array[62] * hash64Coeff[62]
                + array[63] * hash64Coeff[63];
    }
}
