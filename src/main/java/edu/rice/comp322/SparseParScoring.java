package edu.rice.comp322;

import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module0.forallPhased;
import static edu.rice.hj.Module0.next;

/**
 * A scorer that allocates memory during computation, so that it may compute the scores for two sequences without
 * requiring O(|X||Y|) memory.
 */
public class SparseParScoring extends AbstractDnaScoring {
    /**
     * The length of the first sequence.
     */
    private final int xLength;
    /**
     * The length of the second sequence.
     */
    private final int yLength;

    private int[] curScores; // current scores;
    private int[] prevScores; // previous scores;
    private int[] ppScores; // previous previous scores;
    private DiagStruct curStruct; // current struct;
    private DiagStruct prevStruct; // previous struct;
    private DiagStruct ppStruct; // previous previous struct;
    private int numChunk = 8;

    /**
     * <p>main.</p> Takes the names of two files, and in parallel calculates the sequence aligment scores of the two DNA
     * strands that they represent.
     *
     * @param args The names of two files.
     */
    public static void main(final String[] args) throws Exception {
        final ScoringRunner scoringRunner = new ScoringRunner(SparseParScoring::new);
        scoringRunner.start("SparseParScoring", args);
    }

    /**
     * Creates a new SparseParScoring.
     *
     * @param xLength length of the first sequence
     * @param yLength length of the second sequence
     */
    public SparseParScoring(final int xLength, final int yLength) {
        if (xLength <= 0 || yLength <= 0) {
            throw new IllegalArgumentException("Lengths (" + xLength + ", " + yLength + ") must be positive!");
        }

        this.xLength = xLength;
        this.yLength = yLength;
    }

    /**
     * Here you should implement a parallel version of the SW alignment algorithm that can support datasets where the
     * size of the S matrix exceeds the available memory.
     * {@inheritDoc}
     */
    public int scoreSequences(final String x, final String y) throws SuspendableException {

        // TODO: implement this in parallel!
        final int xLength = getXLength();
        final int yLength = getYLength();
        // We only need two previous struct in order to calculate the current struct.
        forallPhased(0, numChunk - 1, (idx) -> {
            // Each thread is responsible for one anti-diagonal.
            for (int s = 0; s < xLength + yLength - 1; s++) {
                if (idx == 0) {
                    // initializing
                    ppScores = prevScores;
                    ppStruct = prevStruct;
                    prevScores = curScores;
                    prevStruct = curStruct;
                    curStruct = computeDiagonal(xLength, yLength, s);
                    curScores = new int[curStruct.length];
                }
                next(); // wait for the first thread to finish working.

                int length = curStruct.length;
                // Compute the start of the loop.
                int lowerBound = length / numChunk * idx;
                // Compute the upper bound of the loop.
                int upperBound = (idx + 1 == numChunk) ? length : lowerBound + length / numChunk;
                for (int a = lowerBound; a < upperBound; a++) {
                    compute(curStruct.xArray[a], curStruct.yArray[a], x, y, a, s);
                }
                next(); // Wait for all thread to finish.
            }
        });
        // The result should be in the last anti-diagonal.
        return curScores[0];
    }

    /**
     * Doing the computation.
     * @param i row
     * @param j column
     * @param x x string
     * @param y y string
     * @param location position at the curStruct
     * @param t time
     */
    private void compute(int i, int j, String x, String y, int location, int t) {
        final boolean flag = t >= yLength;
        final char XChar = x.charAt(i - 1);
        final char YChar = y.charAt(j - 1);

        // Compute scores in the neighborhood.
        final int up = getUpDiag(i, j, location, flag);
        final int left = getLeftDiag(i, j, location, flag);
        final int tilt = getTiltDiag(i, j, location);

        final int diagScore = tilt + getScore(charMap(XChar), charMap(YChar));
        final int leftScore = left + getScore(0, charMap(YChar));
        final int topScore = up + getScore(charMap(XChar), 0);
        // Update curScores.
        curScores[location] = Math.max(diagScore, Math.max(leftScore, topScore));
    }

    /**
     * Find the left diagonal.
     * @param i row
     * @param j column
     * @param location position at the curStruct
     * @param flag the flag to indicate
     * @return integer represents the score
     */
    private int getLeftDiag(int i, int j, int location, boolean flag) {
        if (j == 1) {
            return M[1][0] * i;
        } else {
            if (flag) {
                // The case when it is a sub anti-diagonal.
                return prevScores[location + 1];
            } else {
                return prevScores[location];
            }
        }
    }

    /**
     * Find the upper diagonal.
     * @param i row
     * @param j column
     * @param location the position at the curStruct
     * @param flag the flag to indicate
     * @return integer represents the score
     */
    private int getUpDiag(int i, int j, int location, boolean flag) {
        if (i == 1) {
            // the case when it is on the top
            return M[0][1] * j;
        } else {
            if (flag) {
                return prevScores[location];
            } else {
                return prevScores[location - 1];
            }
        }
    }

    /**
     * Find the tilted diagonal.
     * @param i row
     * @param j column
     * @param location position at the curStruct
     * @return integer represents the score
     */
    private int getTiltDiag(int i, int j, int location) {
        if (i == 1 || j == 1) {
            int max = Math.max(i, j);
            return (max == i) ? M[1][0] * (max - 1) : M[0][1] * (max - 1);
        } else {
            final int prevX = curStruct.xArray[location] - 1;
            final int prevY = curStruct.yArray[location] - 1;
            // calculate the diagonal
            if (location > 0 && ppStruct.xArray[location - 1] == prevX && ppStruct.yArray[location - 1] == prevY) {
                return ppScores[location - 1];
            }
            if (location + 1 < ppStruct.length && ppStruct.xArray[location + 1] == prevX && ppStruct.yArray[location + 1] == prevY) {
                return ppScores[location + 1];
            }
            return ppScores[location];
        }
    }

    /**
     * Return the diagonal struct.
     * @param xLen the x length
     * @param yLen the y length
     * @param i row
     * @return the diagonal struct
     */
    protected static DiagStruct computeDiagonal(int xLen, int yLen, int i) {
        // making the bound
        int bound = Math.min(xLen, yLen);
        int len;
        if (i + 1 >= (xLen + yLen) / 2) {
            len = Math.min(bound, xLen + yLen - i - 1);
        } else {
            len = Math.min(bound, i + 1);
        }
        final DiagStruct result = new DiagStruct(len);
        // Initialize the start of the x and y array.
        result.xArray[0] = Math.max(1, i + 2 - yLen);
        result.yArray[0] = Math.min(i + 1, yLen);
        // Fill in the rest of the x and y array.
        for (int s = 1; s < result.length; s++) {
            result.xArray[s] = result.xArray[s - 1] + 1;
            result.yArray[s] = result.yArray[s - 1] - 1;
        }
        return result;
    }

    /**
     * Fetch the length of the first sequence being aligned.
     * @return length of the first sequence
     */
    protected int getXLength() {
        return xLength;
    }

    /**
     * Fetch the length of the second sequence being aligned.
     * @return length of the second sequence
     */
    protected int getYLength() {
        return yLength;
    }

    /**
     * The structure that stores the anti-diagonal.
     */
    private static class DiagStruct {
        private int[] xArray;
        private int[] yArray;
        private int length;

        private DiagStruct(int len) {
            xArray = new int[len];
            yArray = new int[len];
            length = len;
        }
    }

}

