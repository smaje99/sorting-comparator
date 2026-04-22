package io.github.smaje99.sortingcomparator.algorithm;

import io.github.smaje99.sortingcomparator.model.AlgorithmType;
import io.github.smaje99.sortingcomparator.model.DatasetFactory;
import io.github.smaje99.sortingcomparator.model.SortSnapshot;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortingAlgorithmTest {
    @ParameterizedTest
    @EnumSource(AlgorithmType.class)
    void sortsRandomDataset(AlgorithmType type) {
        assertSorts(type, DatasetFactory.randomUniqueValues(30));
    }

    @ParameterizedTest
    @EnumSource(AlgorithmType.class)
    void sortsAlreadySortedDataset(AlgorithmType type) {
        assertSorts(type, new int[]{10, 15, 23, 42, 54, 61, 77, 88});
    }

    @ParameterizedTest
    @EnumSource(AlgorithmType.class)
    void sortsReverseDataset(AlgorithmType type) {
        assertSorts(type, new int[]{88, 77, 61, 54, 42, 23, 15, 10});
    }

    @ParameterizedTest
    @EnumSource(AlgorithmType.class)
    void sortsMinimumDataset(AlgorithmType type) {
        assertSorts(type, new int[]{50, 20, 70, 10, 30});
    }

    @ParameterizedTest
    @EnumSource(AlgorithmType.class)
    void sortsLargeDataset(AlgorithmType type) {
        assertSorts(type, DatasetFactory.randomUniqueValues(100));
    }

    private void assertSorts(AlgorithmType type, int[] input) {
        int[] expected = Arrays.copyOf(input, input.length);
        Arrays.sort(expected);
        AtomicReference<SortSnapshot> lastSnapshot = new AtomicReference<>();
        SortContext context = new SortContext(lastSnapshot::set, () -> {}, 0);
        InstrumentedArray array = new InstrumentedArray(input);

        type.createAlgorithm().sort(array, context);

        assertArrayEquals(expected, array.snapshot(), type.displayName());
        assertNotNull(lastSnapshot.get(), "algorithm should emit at least one snapshot");
        assertTrue(context.metrics().comparisons() >= 0);
        assertTrue(context.metrics().swaps() >= 0);
        assertTrue(context.metrics().writes() >= 0);
    }
}
