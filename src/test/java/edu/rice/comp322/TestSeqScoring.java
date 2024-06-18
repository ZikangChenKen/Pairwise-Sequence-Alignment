package edu.rice.comp322;

import edu.rice.hj.api.SuspendableException;

/**
 * Sequential Smith-Waterman algorithm adapted for COMP 322 homework.
 */
public class TestSeqScoring extends AbstractPreallocatedSorting {

    /**
     * Creates a new TestSeqScoring.
     *
     * @param xLength length of the first sequence
     * @param yLength length of the second sequence
     */
    public TestSeqScoring(final int xLength, final int yLength) {
        super(xLength, yLength);
    }

    /**
     * A reference sequential implementation of the Smith-Waterman alignment algorithm.
     * {@inheritDoc}
     */
    public int scoreSequences(final String x, final String y) throws SuspendableException {

        final int xLength = getXLength();
        final int yLength = getYLength();
        final int[][] S = getMatrix();

        for (int i = 1; i <= xLength; i++) {
            for (int j = 1; j <= yLength; j++) {
                // the two characters to be compared
                final char XChar = x.charAt(i - 1);
                final char YChar = y.charAt(j - 1);

                // find the largest point to jump from, and use it
                final int diagScore = S[i - 1][j - 1] + getScore(charMap(XChar), charMap(YChar));
                final int topColScore = S[i - 1][j] + getScore(charMap(XChar), 0);
                final int leftRowScore = S[i][j - 1] + getScore(0, charMap(YChar));
                S[i][j] = Math.max(diagScore, Math.max(leftRowScore, topColScore));
            }
        }

        // final value in the matrix is the score
        return S[xLength][yLength];
    }

}

