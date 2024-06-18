package edu.rice.comp322;

/**
 * A constuctor of any DNA scoring.
 *
 * @author Derek Peirce
 */
@FunctionalInterface
public interface IScoringConstructor {

    /**
     * Creates a DNA scoring.
     *
     * @param xLength the length of the first sequence
     * @param yLength the length of the second sequence
     * @return a new scoring, capable of scoring the two sequences
     */
    public AbstractDnaScoring create(int xLength, int yLength);
}
