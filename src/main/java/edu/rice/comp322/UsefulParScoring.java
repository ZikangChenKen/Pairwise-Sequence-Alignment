package edu.rice.comp322;

import edu.rice.hj.api.HjDataDrivenFuture;
import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module0.*;
import static edu.rice.hj.Module1.asyncAwait;
import static edu.rice.hj.Module1.forall;

/**
 * A scorer that works in parallel.
 */
public class UsefulParScoring extends AbstractDnaScoring {

    /**
     * The length of the first sequence.
     */
    private final int xLength;
    /**
     * The length of the second sequence.
     */
    private final int yLength;
    /**
     * The number of column chunks.
     */
    private final int xNumber;
    /**
     * The number of row chunks.
     */
    private final int yNumber;
    /**
     * The chunkSize.
     */
    private final int chunkSize;
    /**
     * The indicator matrix.
     */
    private HjDataDrivenFuture<Boolean>[][] indicator;
    /**
     * The score matrix.
     */
    private int[][] s;

    /**
     * <p>main.</p> Takes the names of two files, and in parallel calculates the sequence aligment scores of the two DNA
     * strands that they represent.
     *
     * @param args The names of two files.
     */
    public static void main(final String[] args) throws Exception {
        final ScoringRunner scoringRunner = new ScoringRunner(UsefulParScoring::new);
        scoringRunner.start("UsefulParScoring", args);
    }

    /**
     * Creates a new UsefulParScoring.
     *
     * @param xLength length of the first sequence
     * @param yLength length of the second sequence
     */
    public UsefulParScoring(final int xLength, final int yLength) {
        if (xLength <= 0 || yLength <= 0) {
            throw new IllegalArgumentException("Lengths (" + xLength + ", " + yLength + ") must be positive!");
        }

        // TODO: implement this!
        this.xLength = xLength;
        this.yLength = yLength;
        this.chunkSize = 200;
        this.xNumber = (int)Math.ceil((double)xLength / (double)chunkSize);
        this.yNumber = (int)Math.ceil((double)yLength / (double)chunkSize);

        // Initializing the matrices.
        this.indicator = new HjDataDrivenFuture[xNumber + 1][yNumber + 1];
        this.s = new int[xLength + 1][yLength + 1];

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

        // Initialize the indicator matrix.
        for (int i = 0; i <= xNumber; i++) {
            for (int j = 0; j <= yNumber; j++) {
                indicator[i][j] = newDataDrivenFuture();
                if (i == 0 || j == 0) {
                    indicator[i][j].put(true);
                }
            }
        }

    }

    /**
     * Here you should provide an efficient parallel implementation of the Smith-Waterman algorithm that demonstrates
     * real execution time speedup.
     * {@inheritDoc}
     */
    public int scoreSequences(final String x, final String y) throws SuspendableException {

        // TODO: implement this in parallel!
        final int xLength = getXLength();
        final int yLength = getYLength();
        final int xNumber = getXNumber();
        final int yNumber = getYNumber();
        final int chunkSize = getChunkSize();
        final HjDataDrivenFuture<Boolean>[][] indicator = getMatrix();
        final int[][] s = getScoreMatrix();

        // Computing chunks in parallel.
        finish(() -> {
            for (int i = 1; i <= xNumber; i++) {
                for (int j = 1; j <= yNumber; j++) {
                    int initialRow = (i - 1) * chunkSize + 1;
                    int initialColumn = (j - 1) * chunkSize + 1;
                    int finalI = i;
                    int finalJ = j;
                    asyncAwait(indicator[i - 1][j - 1], indicator[i - 1][j], indicator[i][j - 1], () -> {
                        // Computing each chunk sequentially.
                        for (int ii = 0; ii < chunkSize; ii++) {
                            for (int jj = 0; jj < chunkSize; jj++) {
                                // Check if it is out of bound.
                                if (ii + initialRow > xLength || jj + initialColumn > yLength) {
                                    continue;
                                } else {
                                    final char XChar = x.charAt(initialRow + ii - 1);
                                    final char YChar = y.charAt(initialColumn + jj - 1);
                                    final int diagScore = s[initialRow + ii - 1][initialColumn + jj - 1] + getScore(charMap(XChar), charMap(YChar));
                                    final int topColScore = s[initialRow + ii - 1][initialColumn + jj] + getScore(charMap(XChar), 0);
                                    final int leftRowScore = s[initialRow + ii][initialColumn + jj - 1] + getScore(0, charMap(YChar));
                                    doWork(1);
                                    s[initialRow + ii][initialColumn + jj] = Math.max(diagScore, Math.max(leftRowScore, topColScore));
                                }
                            }
                        }
                        indicator[finalI][finalJ].put(true);
                    });
                }
            }
        });
        return s[xLength][yLength];
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
     * Fetch the length of the first sequence being aligned.
     * @return length of the first sequence
     */
    protected int getXNumber() {
        return xNumber;
    }

    /**
     * Fetch the length of the second sequence being aligned.
     * @return length of the second sequence
     */
    protected int getYNumber() {
        return yNumber;
    }

    /**
     * Fetch the chunkSize.
     * @return the chunkSize
     */
    protected int getChunkSize() {
        return chunkSize;
    }

    /**
     * Fetch the 2D array indicator matrix.
     * @return the 2D array indicator matrix
     */
    protected HjDataDrivenFuture<Boolean>[][] getMatrix() {
        return indicator;
    }

    /**
     * Fetch the 2D array score matrix.
     * @return the 2D array score matrix
     */
    protected int[][] getScoreMatrix() {
        return s;
    }
}

