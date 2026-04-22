package io.github.smaje99.sortingcomparator.algorithm;

public final class OptimizedBubbleSort implements SortAlgorithm {
    @Override
    public String name() {
        return "Optimized Bubble";
    }

    @Override
    public void sort(InstrumentedArray array, SortContext context) {
        int length = array.length();
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 1; i < length; i++) {
                if (array.compare(i, i - 1, context) < 0) {
                    array.swap(i, i - 1, context);
                    swapped = true;
                }
            }
            length--;
        }
    }
}
