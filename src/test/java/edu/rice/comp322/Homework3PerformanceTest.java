package edu.rice.comp322;

import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import java.util.Random;

import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.api.HjSuspendable;
import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module0.numWorkerThreads;

/**
 * This is a test class for your homework and should not be modified.
 *
 * @author Vivek Sarkar (vsarkar@rice.edu)
 */
public class Homework3PerformanceTest extends TestCase {
    public void testUsefulParScoring() throws IOException {
        final String x = RandomStringUtils.randomString(10_000, (int)System.currentTimeMillis());
        final String y = RandomStringUtils.randomString(10_000, (int)System.currentTimeMillis());

        final TestSeqScoring[] seqScoring = new TestSeqScoring[1];
        final int[] seqScore = new int[1];
        final UsefulParScoring[] parScoring = new UsefulParScoring[1];
        final int[] parScore = new int[1];

        final String testLabel = PerfTestUtils.getTestLabel();
        launchHabaneroApp(() -> {
            final PerfTestUtils.PerfTestResults timingInfo = PerfTestUtils.runPerfTest(testLabel,
                () -> {
                    parScoring[0] = new UsefulParScoring(x.length(), y.length());
                },
                () -> {
                    parScore[0] = parScoring[0].scoreSequences(x, y);
                }, () -> { },
                () -> {
                    seqScoring[0] = new TestSeqScoring(x.length(), y.length());
                },
                () -> {
                    seqScore[0] = seqScoring[0].scoreSequences(x, y);
                }, () -> { },
                () -> {
                    assertEquals("Score mismatch between sequential and parallel version when processing strings of " +
                        "lengths " + x.length() + " and " + y.length() + ", seqScore=" + seqScore[0] + ", parScore=" +
                        parScore[0], seqScore[0], parScore[0]);
                }, 10, 10, numWorkerThreads());
            System.out.println("testUsefulParScoring ran in " + timingInfo.parTime + " ms, " +
                ((double)timingInfo.seqTime / (double)timingInfo.parTime) + "x faster than the sequential (" +
                timingInfo.seqTime + " ms)");
        });
    }

    public void testUsefulParScoring2() throws IOException {
        final String x = RandomStringUtils.randomString(10_000, (int)System.currentTimeMillis());
        final String y = RandomStringUtils.randomString(10_000, (int)System.currentTimeMillis());

        final TestSeqScoring[] seqScoring = new TestSeqScoring[1];
        final int[] seqScore = new int[1];
        final UsefulParScoring[] parScoring = new UsefulParScoring[1];
        final int[] parScore = new int[1];

        final String testLabel = PerfTestUtils.getTestLabel();
        launchHabaneroApp(() -> {
            final PerfTestUtils.PerfTestResults timingInfo = PerfTestUtils.runPerfTest(testLabel,
                () -> {
                    parScoring[0] = new UsefulParScoring(x.length(), y.length());
                },
                () -> {
                    parScore[0] = parScoring[0].scoreSequences(x, y);
                }, () -> { },
                () -> {
                    seqScoring[0] = new TestSeqScoring(x.length(), y.length());
                },
                () -> {
                    seqScore[0] = seqScoring[0].scoreSequences(x, y);
                }, () -> { },
                () -> {
                    assertEquals("Score mismatch between sequential and parallel version when processing strings of " +
                        "lengths " + x.length() + " and " + y.length() + ", seqScore=" + seqScore[0] +
                        ", parScore=" + parScore[0], seqScore[0], parScore[0]);
                }, 10, 10, numWorkerThreads());
            System.out.println("testUsefulParScoring ran in " + timingInfo.parTime + " ms, " +
                ((double)timingInfo.seqTime / (double)timingInfo.parTime) + "x faster than the sequential (" +
                timingInfo.seqTime + " ms)");
        });
    }

    public void testSparseParScoring() throws IOException {
        final String x = RandomStringUtils.randomString(100_000, 666);
        final String y = RandomStringUtils.randomString(100_000, 777);

        final SparseParScoring[] parScoring = new SparseParScoring[1];
        final int[] parScore = new int[1];
        final int refScore = 314318;
        final int refTime = 412966;

        final String testLabel = PerfTestUtils.getTestLabel();
        launchHabaneroApp(() -> {
            final PerfTestUtils.PerfTestResults timingInfo = PerfTestUtils.runPerfTest(testLabel,
                () -> {
                    parScoring[0] = new SparseParScoring(x.length(), y.length());
                },  
                () -> {
                    parScore[0] = parScoring[0].scoreSequences(x, y); 
                }, () -> { },
                () -> {
                    assertEquals("Score mismatch in sparse implementation when processing strings of " +
                        "lengths " + x.length() + " and " + y.length() + ", expected " + refScore + " but got " +
                        parScore[0], refScore, parScore[0]);
                },
                5, refTime, numWorkerThreads());

            System.out.println(testLabel + " ran in " + timingInfo.parTime + " ms, " +
                ((double)timingInfo.seqTime / (double)timingInfo.parTime) + "x faster than the sequential (" +
                timingInfo.seqTime + " ms)");
        });
    }

    public void testSparseParScoring2() throws IOException {
        final String x = RandomStringUtils.randomString(100_000, 123);
        final String y = RandomStringUtils.randomString(100_000, 456);

        final SparseParScoring[] parScoring = new SparseParScoring[1];
        final int[] parScore = new int[1];
        final int refScore = 314363;
        final int refTime = 398719;

        final String testLabel = PerfTestUtils.getTestLabel();
        launchHabaneroApp(() -> {
            final PerfTestUtils.PerfTestResults timingInfo = PerfTestUtils.runPerfTest(testLabel,
                () -> {
                    parScoring[0] = new SparseParScoring(x.length(), y.length());
                },  
                () -> {
                    parScore[0] = parScoring[0].scoreSequences(x, y); 
                }, () -> { },
                () -> {
                    assertEquals("Score mismatch in sparse implementation when processing strings of " +
                        "lengths " + x.length() + " and " + y.length() + ", expected " + refScore + " but got " +
                        parScore[0], refScore, parScore[0]);
                },
                5 , refTime, numWorkerThreads());
            System.out.println(testLabel + " ran in " + timingInfo.parTime + " ms, " +
                ((double)timingInfo.seqTime / (double)timingInfo.parTime) + "x faster than the sequential (" +
                timingInfo.seqTime + " ms)");
        });
    }
}
