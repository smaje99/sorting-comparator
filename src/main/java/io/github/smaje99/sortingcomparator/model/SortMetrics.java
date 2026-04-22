package io.github.smaje99.sortingcomparator.model;

public record SortMetrics(long comparisons, long swaps, long writes, long elapsedMillis) {
    public static SortMetrics zero() {
        return new SortMetrics(0, 0, 0, 0);
    }
}
