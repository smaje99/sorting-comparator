package io.github.smaje99.sortingcomparator.algorithm;

public final class SelectionSort implements SortAlgorithm {
    @Override
    public String name() {
        return "Selection";
    }

    @Override
    public void sort(InstrumentedArray array, SortContext context) {
        for (int i = 0; i < array.length() - 1; i++) {
            int min = i;
            for (int j = i + 1; j < array.length(); j++) {
                array.mark(context, i, j, min);
                if (array.compare(j, min, context) < 0) {
                    min = j;
                }
            }
            array.swap(min, i, context);
        }
    }
}
