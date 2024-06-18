package edu.rice.comp322;

/**
 * A scoring object that preallocates its matrix.
 *
 * @author Derek Peirce
 */
public abstract class AbstractPreallocatedSorting extends AbstractDnaScoring {

    /**
     * The length of the first sequence.
     */
    private final int xLength;
    /**
     * The length of the second sequence.
     */
    private final int yLength;

    /**
     * The Smith-Waterman matrix.
     */
    private final int[][] s;

    /**
     * Creates a sorting object, allocating all of the memory it will need for the computations.
     *
     * @param xLength length of the first sequence
     * @param yLength length of the second sequence
     */
    public AbstractPreallocatedSorting(final int xLength, final int yLength) {
        this.xLength = xLength;
        this.yLength = yLength;
        //pre allocate the matrix for alignment, dimension+1 for initializations
        s = new int[xLength + 1][yLength + 1];

        //init row
        for (int ii = 1; ii < xLength + 1; ii++) {
            s[ii][0] = getScore(1, 0) * ii;
        }

        //init column
        for (int jj = 1; jj < yLength + 1; jj++) {
            s[0][jj] = getScore(0, 1) * jj;
        }
        //init diagonal
        s[0][0] = 0;
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
     * The full Smith-Waterman matrix.
     * @return the Smith-Waterman matrix
     */
    protected int[][] getMatrix() {
        return s;
    }
}
