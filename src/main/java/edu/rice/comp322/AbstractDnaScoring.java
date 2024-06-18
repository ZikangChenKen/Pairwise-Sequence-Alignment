package edu.rice.comp322;

import edu.rice.hj.api.SuspendableException;

/**
 * Any method of scoring the sequential alignment of two DNA strings.
 *
 * @author Derek
 */
public abstract class AbstractDnaScoring {

    // scoring matrix
    /*
           _  A  C  G  T
        ------------------
        _ |-8 -2 -2 -2 -2
        A |-4  5  2  2  2
        C |-4  2  5  2  2
        G |-4  2  2  5  2
        T |-4  2  2  2  5
    */
    protected static final int[][] M = new int[][]{{-8, -2, -2, -2, -2},
        {-4, 5, 2, 2, 2},
        {-4, 2, 5, 2, 2},
        {-4, 2, 2, 5, 2},
        {-4, 2, 2, 2, 5}};

    /**
     * Helper method for getting the index of character in scoring matrix.
     *
     * @param inputChar The character to search for.
     * @return The index of the requested character in the matrix.
     */
    protected int charMap(final char inputChar) {
        switch (inputChar) {
            case '_':
                return 0;
            case 'A':
            case 'a':
                return 1;
            case 'C':
            case 'c':
                return 2;
            case 'G':
            case 'g':
                return 3;
            case 'T':
            case 't':
                return 4;
            default:
                throw new IllegalArgumentException("Invalid DNA character: " + inputChar);
        }
    }

    /**
     * Get score assigned for alignment of two letters.
     *
     * @param iVal the first index in the scoring matrix
     * @param jVal the second index in the scoring matrix
     * @return the corresponding value in the matrix
     */
    protected int getScore(final int iVal, final int jVal) {
        return M[iVal][jVal];
    }

    /**
     * Helper method for printing out a two dimensional int array, can be useful for debugging.
     *
     * @param matrix The desired int matrix to be printed.
     */
    public void printMatrix(final int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("--------------------------------");
    }

    /**
     * Compute the solution matrix and return the alignment score of two DNA sequences. This one is done sequentially.
     *
     * @param x The first sequence to be compared.
     * @param y The second sequence to be compared.
     * @return The alignment score between them
     */
    public abstract int scoreSequences(String x, String y) throws SuspendableException;
}
