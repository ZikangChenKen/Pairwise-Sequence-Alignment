package edu.rice.comp322;

import junit.framework.TestCase;

import java.util.Random;

import static edu.rice.hj.Module0.launchHabaneroApp;

/**
 * This is a test class for your homework and should not be modified.
 *
 * @author Vivek Sarkar (vsarkar@rice.edu)
 */
public class Homework3Checkpoint2CorrectnessTest extends TestCase {
    public void testUsefulParScoring1() {
        final int xLength = 1;
        final int yLength = 1;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = UsefulParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }

    public void testUsefulParScoring2() {
        final int xLength = 10;
        final int yLength = 12;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = UsefulParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }

    public void testUsefulParScoring3() {
        final int xLength = 100;
        final int yLength = 120;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = UsefulParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }

    public void testUsefulParScoring4() {
        final int xLength = 120;
        final int yLength = 100;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = UsefulParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }
}
