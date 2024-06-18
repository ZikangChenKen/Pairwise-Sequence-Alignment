package edu.rice.comp322;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static edu.rice.hj.Module0.launchHabaneroApp;

/**
 * Contains all of the code necessary to test a DNA scorer through multiple trials.
 */
public class ScoringRunner {

    /**
     * The constructor of a DNA scoring.
     */
    private final IScoringConstructor constructor;

    /**
     * Creates a runner.
     *
     * @param constructor provides the DNA scoring to run
     */
    public ScoringRunner(final IScoringConstructor constructor) {
        this.constructor = constructor;
    }

    /**
     * Starts the runner.
     *
     * @param mainName the name of the class that called this method
     * @param args     the arguments that class was given, should be the names of two files
     * @throws java.io.IOException on a file name or system problem
     */
    public void start(final String mainName, final String[] args) throws IOException {

        // first read the two files as DNA sequences

        final String filename1;
        final String filename2;

        if (args.length == 2) {
            filename1 = args[0];
            filename2 = args[1];
            System.out.println("File Name-1: " + filename1);
            System.out.println("File Name-2: " + filename2);
        } else {
            System.out.println("Usage: ./" + mainName + " fileName1 fileName2");
            return;
        }

        final String X = initString(filename1);
        final String Y = initString(filename2);
        System.out.println("Size of input string 1 is " + X.length());
        System.out.println("Size of input string 2 is " + Y.length());

        // then test scorers created by the given constructor against the two sequences

        final int xLength = X.length();
        final int yLength = Y.length();
        final int numIter = 5;
        final long[] times = new long[numIter];
        final int[] scores = new int[numIter];
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;


        for (int iter = 0; iter < numIter; iter++) {

            final int iterLoc = iter;
            final long[] execTime = {0};
            launchHabaneroApp(() -> {
                final AbstractDnaScoring scoring = constructor.create(xLength, yLength);
                final long start = System.currentTimeMillis();

                final int score = scoring.scoreSequences(X, Y);
                scores[iterLoc] = score;

                execTime[0] = System.currentTimeMillis() - start;
                System.out.println("  The score = " + score + " in iteration " + (iterLoc + 1));
                System.out.println("  The execution time = " + execTime[0] + " milliseconds in iteration " + (iterLoc + 1));
            });
            totalTime += execTime[0];
            times[iter] = execTime[0];
            if (execTime[0] < minTime) {
                minTime = execTime[0];
            }

        }
        for (int iter = 1; iter < numIter; iter++) {
            if (scores[iter] != scores[0]) {
                System.err.println("Different scores from different runs! " + scores[iter] + " and " + scores[0]);
            }
        }
        System.out.println("Avg time of computation is " + totalTime / numIter);
        System.out.println("Min time of computation is " + minTime + " milliseconds ");
    }

    /**
     * Read the sequence from a given text file.
     *
     * @param fileName The file name to read text from.
     * @return A string containing the sequence from the file.
     * @throws java.io.IOException if any.
     */
    private String initString(final String fileName) throws IOException {
        final BufferedReader r = new BufferedReader(new FileReader(fileName));
        final StringBuilder text = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            text.append(line);
        }
        return text.toString();
    }
}
