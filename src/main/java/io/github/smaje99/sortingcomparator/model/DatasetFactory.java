package io.github.smaje99.sortingcomparator.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DatasetFactory {
    public static final int MIN_SIZE = 5;
    public static final int MAX_SIZE = 100;
    private static final SecureRandom RANDOM = new SecureRandom();

    private DatasetFactory() {
    }

    public static int[] randomUniqueValues(int size) {
        validateSize(size);
        List<Integer> values = new ArrayList<>();
        for (int value = 10; value < 999; value++) {
            values.add(value);
        }
        Collections.shuffle(values, RANDOM);
        return values.stream().limit(size).mapToInt(Integer::intValue).toArray();
    }

    public static void validateDataset(int[] values) {
        validateSize(values.length);
        java.util.HashSet<Integer> seen = new java.util.HashSet<>();
        for (int value : values) {
            if (value <= 0) {
                throw new IllegalArgumentException("Values must be positive integers.");
            }
            if (!seen.add(value)) {
                throw new IllegalArgumentException("Values must be unique.");
            }
        }
    }

    private static void validateSize(int size) {
        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new IllegalArgumentException("Dataset size must be between 5 and 100.");
        }
    }
}
