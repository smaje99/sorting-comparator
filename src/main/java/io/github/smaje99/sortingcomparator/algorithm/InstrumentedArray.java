package io.github.smaje99.sortingcomparator.algorithm;

import java.util.Arrays;

public final class InstrumentedArray {
    private final int[] values;

    public InstrumentedArray(int[] values) {
        this.values = Arrays.copyOf(values, values.length);
    }

    public int length() {
        return values.length;
    }

    public int get(int index) {
        return values[index];
    }

    public int compare(int first, int second, SortContext context) {
        return context.compare(values, first, second);
    }

    public int compareValue(int index, int value, SortContext context) {
        return context.compareValue(values, index, value);
    }

    public void swap(int first, int second, SortContext context) {
        context.swap(values, first, second);
    }

    public void write(int index, int value, SortContext context) {
        context.write(values, index, value);
    }

    public void mark(SortContext context, int... indices) {
        context.mark(values, indices);
    }

    public void pivot(SortContext context, int pivotIndex, int... activeIndices) {
        context.pivot(values, pivotIndex, activeIndices);
    }

    public int[] snapshot() {
        return Arrays.copyOf(values, values.length);
    }
}
