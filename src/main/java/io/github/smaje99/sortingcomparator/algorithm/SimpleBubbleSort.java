package io.github.smaje99.sortingcomparator.algorithm;

public final class SimpleBubbleSort implements SortAlgorithm {
    @Override
    public String name() {
        return "Simple Bubble";
    }

    @Override
    public void sort(InstrumentedArray array, SortContext context) {
        for (int i = 0; i < array.length(); i++) {
            for (int j = 0; j < array.length(); j++) {
                if (array.compare(i, j, context) < 0) {
                    array.swap(i, j, context);
                }
            }
        }
    }
}
