package edu.rice.comp322;

import edu.rice.hj.api.HjDataDrivenFuture;
import edu.rice.hj.api.HjSuspendable;
import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module0.*;
import static edu.rice.hj.Module1.asyncAwait;
import static edu.rice.hj.Module1.forall;

/**
 * A scorer that works in parallel.
 */
public class IdealParScoring extends AbstractDnaScoring {
    /**
     * The length of the first sequence.
     */
    private final int xLength;
    /**
     * The length of the second sequence.
     */
    private final int yLength;

    /**
     * <p>main.</p> Takes the names of two files, and in parallel calculates the sequence alignment scores of the two DNA
     * strands that they represent.
     *
     * @param args The names of two files.
     */
    public static void main(final String[] args) throws Exception {
        final ScoringRunner scoringRunner = new ScoringRunner(IdealParScoring::new);
        scoringRunner.start("IdealParScoring", args);
    }

    /**
     * Creates a new IdealParScoring.
     *
     * @param xLength length of the first sequence
     * @param yLength length of the second sequence
     */
    public IdealParScoring(final int xLength, final int yLength) {
        if (xLength <= 0 || yLength <= 0) {
            throw new IllegalArgumentException("Lengths (" + xLength + ", " + yLength + ") must be positive!");
        }
        // TODO: implement this!
        this.xLength = xLength;
        this.yLength = yLength;
//        throw new UnsupportedOperationException("Parallel allocation not implemented yet!");
    }

    /**
     * This method should be filled in with a parallel implementation of the Smith-Waterman alignment algorithm that
     * maximizes ideal parallelism.
     * {@inheritDoc}
     */
    public int scoreSequences(final String x, final String y) throws SuspendableException {

        // TODO: implement this in parallel!
        final int xLength = getXLength();
        final int yLength = getYLength();
        final HjDataDrivenFuture[][] matrix = new HjDataDrivenFuture[xLength + 1][yLength + 1];

        forall(0, xLength, 0, yLength, (i, j) -> {
            matrix[i][j] = newDataDrivenFuture();
        });

        matrix[0][0].put(0);

        // Initialize the first row.
        forall(1, xLength, (ii) -> {
            matrix[ii][0].put(getScore(1, 0) * ii);
        });

        // Initialize the first column.
        forall(1, yLength, (jj) -> {
            matrix[0][jj].put(getScore(0, 1) * jj);
        });

        // Find the result inside the matrix.
        finish(() -> {
            for (int i = 1; i <= xLength; i++) {
                for (int j = 1; j <= yLength; j++) {
                    // the two characters to be compared
                    final char XChar = x.charAt(i - 1);
                    final char YChar = y.charAt(j - 1);
                    final int finalI = i;
                    final int finalJ = j;
                    asyncAwait(matrix[i - 1][j - 1], matrix[i][j - 1], matrix[i - 1][j], () -> {
                        // find the largest point to jump from, and use it
                        final int diagScore = (int)matrix[finalI - 1][finalJ - 1].safeGet() + getScore(charMap(XChar), charMap(YChar));
                        final int topColScore = (int)matrix[finalI - 1][finalJ].safeGet() + getScore(charMap(XChar), 0);
                        final int leftRowScore = (int)matrix[finalI][finalJ - 1].safeGet() + getScore(0, charMap(YChar));
                        doWork(1);
                        matrix[finalI][finalJ].put(Math.max(diagScore, Math.max(leftRowScore, topColScore)));
                    });
                }
            }
        });
        return (int)matrix[xLength][yLength].get();
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

}

