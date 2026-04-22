package io.github.smaje99.sortingcomparator.algorithm;

public final class InsertionSort implements SortAlgorithm {
    @Override
    public String name() {
        return "Insertion";
    }

    @Override
    public void sort(InstrumentedArray array, SortContext context) {
        for (int i = 1; i < array.length(); i++) {
            int key = array.get(i);
            int j = i - 1;
            array.mark(context, i, j);
            while (j >= 0 && array.compareValue(j, key, context) > 0) {
                array.write(j + 1, array.get(j), context);
                j--;
            }
            array.write(j + 1, key, context);
        }
    }
}
