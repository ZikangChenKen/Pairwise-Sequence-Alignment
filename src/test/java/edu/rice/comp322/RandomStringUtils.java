package edu.rice.comp322;

import java.util.Random;

public class RandomStringUtils {
    public static String randomString(final int length, final int seed) {
        final StringBuilder sb = new StringBuilder(length);

        final Random random = new Random(seed);
        for (int i = 0; i < length; i++) {
            final int anInt = random.nextInt(4);
            switch (anInt) {
                case 0:
                    sb.append('A');
                    break;
                case 1:
                    sb.append('C');
                    break;
                case 2:
                    sb.append('G');
                    break;
                case 3:
                    sb.append('T');
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + anInt);
            }
        }

        return sb.toString();
    }

    public static String randomString(final int length) {
        return randomString(length, length);
    }
}
