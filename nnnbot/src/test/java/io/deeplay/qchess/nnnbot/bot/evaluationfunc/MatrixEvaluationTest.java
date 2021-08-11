package io.deeplay.qchess.nnnbot.bot.evaluationfunc;

import static io.deeplay.qchess.nnnbot.bot.evaluationfunc.ImprovedMatrixEvaluation.bishopEval;
import static io.deeplay.qchess.nnnbot.bot.evaluationfunc.ImprovedMatrixEvaluation.costInPawns;
import static io.deeplay.qchess.nnnbot.bot.evaluationfunc.ImprovedMatrixEvaluation.knightEval;
import static io.deeplay.qchess.nnnbot.bot.evaluationfunc.ImprovedMatrixEvaluation.middleGameKingEval;
import static io.deeplay.qchess.nnnbot.bot.evaluationfunc.ImprovedMatrixEvaluation.pawnEval;
import static io.deeplay.qchess.nnnbot.bot.evaluationfunc.ImprovedMatrixEvaluation.queenEval;
import static io.deeplay.qchess.nnnbot.bot.evaluationfunc.ImprovedMatrixEvaluation.rookEval;

import io.deeplay.qchess.game.model.figures.FigureType;
import org.junit.Ignore;
import org.junit.Test;

public class MatrixEvaluationTest {

    /** Проверка того, что стоимости фигур пропорциональны стоимости пешки с учетом позиции */
    @Ignore
    @Test
    public void testMatrixProportional() {
        // B > N > 3P
        // B + N = R + 1.5P
        // Q + P = 2R

        final int failSoft = 100;

        for (int row = 0; row < 8; ++row) {
            for (int column = 0; column < 8; ++column) {
                final int B = bishopEval[row][column] + costInPawns.get(FigureType.BISHOP);
                final int N = knightEval[row][column] + costInPawns.get(FigureType.KNIGHT);
                final int P = pawnEval[row][column] + costInPawns.get(FigureType.PAWN);
                final int R = rookEval[row][column] + costInPawns.get(FigureType.ROOK);
                final int Q = queenEval[row][column] + costInPawns.get(FigureType.QUEEN);

                boolean isPassed = true;

                if (B > N && N > 3 * P) {
                    if (B + N == R + (int) (1.5 * P)) {
                        if (Q + P == 2 * R) {
                            System.out.println("passed");
                        } else {
                            final int errorRate = Q + P - 2 * R;
                            if (errorRate < -failSoft || errorRate > failSoft) {
                                isPassed = false;
                                System.err.println("3 fail:");
                                System.err.println("\tQ + P = " + (Q + P));
                                System.err.println("\t2R = " + (2 * R));
                                System.err.println("\t~" + errorRate);
                            } else System.out.println("3 fail-soft");
                        }
                    } else {
                        final int errorRate = B + N - R - (int) (1.5 * P);
                        if (errorRate < -failSoft || errorRate > failSoft) {
                            isPassed = false;
                            System.err.println("2 fail:");
                            System.err.println("\tB + N = " + (B + N));
                            System.err.println("\tR + 1.5P = " + (R + (int) (1.5 * P)));
                            System.err.println("\t~" + errorRate);
                        } else System.out.println("2 fail-soft");
                    }
                } else {
                    System.err.println("1 fail:");
                    System.err.println("\tB > N > 3P ?= " + B + " > " + N + " > " + (3 * P));
                    isPassed = false;
                }

                if (!isPassed) {
                    System.err.println("row: " + row + ", column: " + column);
                    System.err.println("B: " + B);
                    System.err.println("N: " + N);
                    System.err.println("P: " + P);
                    System.err.println("R: " + R);
                    System.err.println("Q: " + Q);
                    // Assert.fail();
                }
            }
        }
    }

    @Test
    public void test() {
        for (int row = 0; row < 8; ++row) {
            for (int column = 0; column < 8; ++column) {
                // final int B = bishopEval[row][column] + MatrixEvaluation.bishopEval[row][column]
                // / 4;
                // final int N = knightEval[row][column] + MatrixEvaluation.knightEval[row][column]
                // / 4;
                // final int P = pawnEval[row][column] + MatrixEvaluation.pawnEval[row][column] / 4;
                // final int R = rookEval[row][column] + MatrixEvaluation.rookEval[row][column] / 4;
                final int Q =
                        middleGameKingEval[row][column]
                                + MatrixEvaluation.kingEval[row][column] / 4;
                System.out.print(Q + ", ");
            }
            System.out.println();
        }
    }
}
