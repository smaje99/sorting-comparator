package io.github.smaje99.sortingcomparator.algorithm;

public final class ImprovedBubbleSort implements SortAlgorithm {
    @Override
    public String name() {
        return "Improved Bubble";
    }

    @Override
    public void sort(InstrumentedArray array, SortContext context) {
        for (int i = 1; i < array.length(); i++) {
            for (int j = 0; j < array.length() - i; j++) {
                if (array.compare(j, j + 1, context) > 0) {
                    array.swap(j, j + 1, context);
                }
            }
        }
    }
}
