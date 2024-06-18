package edu.rice.comp322;

import junit.framework.TestCase;

import java.util.Random;

import static edu.rice.hj.Module0.launchHabaneroApp;

/**
 * This is a test class for your homework and should not be modified.
 *
 * @author Vivek Sarkar (vsarkar@rice.edu)
 */
public class Homework3Checkpoint3CorrectnessTest extends TestCase {
    public void testSparseParScoring1() {
        final int xLength = 1;
        final int yLength = 1;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = SparseParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }

    public void testSparseParScoring2() {

        final int xLength = 10;
        final int yLength = 12;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = SparseParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }

    public void testSparseParScoring3() {

        final int xLength = 100;
        final int yLength = 120;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = SparseParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }

    public void testSparseParScoring4() {

        final int xLength = 120;
        final int yLength = 100;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = SparseParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }
}
