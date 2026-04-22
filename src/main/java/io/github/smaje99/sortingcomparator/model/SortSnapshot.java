package io.github.smaje99.sortingcomparator.model;

import java.util.Arrays;

public record SortSnapshot(int[] values, SortHighlight highlight, SortMetrics metrics, SortStatus status) {
    public SortSnapshot {
        values = Arrays.copyOf(values, values.length);
        highlight = highlight == null ? SortHighlight.none() : highlight;
        metrics = metrics == null ? SortMetrics.zero() : metrics;
        status = status == null ? SortStatus.IDLE : status;
    }

    @Override
    public int[] values() {
        return Arrays.copyOf(values, values.length);
    }
}
