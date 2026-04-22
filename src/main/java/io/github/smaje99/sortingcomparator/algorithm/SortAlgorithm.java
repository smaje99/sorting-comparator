package io.github.smaje99.sortingcomparator.algorithm;

/**
 * Sorts values through a {@link SortContext} so every meaningful operation can be animated and measured.
 */
public interface SortAlgorithm {
    String name();

    void sort(InstrumentedArray array, SortContext context);
}
