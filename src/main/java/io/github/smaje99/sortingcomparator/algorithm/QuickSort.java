package io.github.smaje99.sortingcomparator.algorithm;

public final class QuickSort implements SortAlgorithm {
    @Override
    public String name() {
        return "QuickSort";
    }

    @Override
    public void sort(InstrumentedArray array, SortContext context) {
        quickSort(array, context, 0, array.length() - 1);
    }

    private void quickSort(InstrumentedArray array, SortContext context, int first, int last) {
        int i = first;
        int j = last;
        int pivotIndex = first + (last - first) / 2;
        int pivot = array.get(pivotIndex);
        array.pivot(context, pivotIndex, i, j);
        while (i <= j) {
            while (array.compareValue(i, pivot, context) < 0) {
                i++;
            }
            while (array.compareValue(j, pivot, context) > 0) {
                j--;
            }
            array.pivot(context, pivotIndex, i, j);
            if (i <= j) {
                array.swap(i, j, context);
                i++;
                j--;
            }
        }
        if (first < j) {
            quickSort(array, context, first, j);
        }
        if (i < last) {
            quickSort(array, context, i, last);
        }
    }
}
