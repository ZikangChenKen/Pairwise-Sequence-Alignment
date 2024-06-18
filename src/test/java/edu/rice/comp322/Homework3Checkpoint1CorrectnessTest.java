package edu.rice.comp322;

import junit.framework.TestCase;

import java.util.Random;

import edu.rice.hj.runtime.config.HjSystemProperty;
import edu.rice.hj.api.HjMetrics;
import static edu.rice.hj.Module1.abstractMetrics;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.async;

/**
 * This is a test class for your homework and should not be modified.
 *
 * @author Vivek Sarkar (vsarkar@rice.edu)
 */
public class Homework3Checkpoint1CorrectnessTest extends TestCase {

    public void testIdealParScoring1() {
        final int xLength = 1;
        final int yLength = 1;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = IdealParScoring::new;

        kernel(xLength, yLength, seqConstructor, parConstructor, true);
    }

    public void testIdealParScoring2() {

        final int xLength = 10;
        final int yLength = 12;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = IdealParScoring::new;

        kernel(xLength, yLength, seqConstructor, parConstructor, true);
    }

    public void testIdealParScoring3() {

        final int xLength = 100;
        final int yLength = 120;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = IdealParScoring::new;

        kernel(xLength, yLength, seqConstructor, parConstructor, true);
    }

    public void testIdealParScoring4() {

        final int xLength = 120;
        final int yLength = 100;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = IdealParScoring::new;

        kernel(xLength, yLength, seqConstructor, parConstructor, true);
    }

    public static void kernel(final int xLength, final int yLength,
            final IScoringConstructor seqConstructor,
            final IScoringConstructor parConstructor,
            final boolean checkAbstractMetrics) {
        final String xInput = RandomStringUtils.randomString(xLength);
        final String yInput = RandomStringUtils.randomString(yLength);

        final boolean[] valid = {false};
        final int[] seqScore = {0};
        final int[] parScore = {0};
        final long[] parCPL = {0};
        final long[] parWork = {0};

        if (checkAbstractMetrics) {
            HjSystemProperty.abstractMetrics.setProperty(true);
            HjSystemProperty.eventLogging.setProperty(true);
        }

        launchHabaneroApp(() -> {
            seqScore[0] = seqConstructor.create(xLength, yLength).scoreSequences(xInput, yInput);
        });

        if (checkAbstractMetrics) {
            HjSystemProperty.abstractMetrics.setProperty(true);
            HjSystemProperty.eventLogging.setProperty(true);
        }

        launchHabaneroApp(() -> {
            async(() -> {
                parScore[0] = parConstructor.create(xLength, yLength).scoreSequences(xInput, yInput);
            });
        }, () -> {
            if (checkAbstractMetrics) {
                HjMetrics metrics = abstractMetrics();
                parCPL[0] = metrics.criticalPathLength();
                parWork[0] = metrics.totalWork();
            }
        });

        if (checkAbstractMetrics) {
            System.out.println("Homework3Test.kernel: seqScore = " + seqScore[0] + ", parScore = " + parScore[0] +
                    ", parWork = " + parWork[0] + ", parCPL = " + parCPL[0]);
        } else {
            System.out.println("Homework3Test.kernel: seqScore = " + seqScore[0] + ", parScore = " + parScore[0]);
        }

        assertTrue("Scores do not match! Sequential = " + seqScore[0] + ", Parallel = " + parScore[0],
                seqScore[0] == parScore[0]);
        if (checkAbstractMetrics) {
            assertTrue("Work does not match! Sequential = " + (xLength * yLength) + ", Parallel = " + parWork[0],
                    (xLength * yLength) == parWork[0]);
            assertTrue("CPL for ideal parallel version should be " + (xLength + yLength - 1) + " for xLength = " +
                    xLength + ", yLength = " + yLength + ", but got " + parCPL[0] + " from parallel implementation",
                    (xLength + yLength - 1) == parCPL[0]);
        }

        System.out.println("Test successful for size " + xLength + " and " + yLength);
    }
}
