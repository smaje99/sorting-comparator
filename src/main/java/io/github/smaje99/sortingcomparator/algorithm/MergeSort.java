package io.github.smaje99.sortingcomparator.algorithm;

public final class MergeSort implements SortAlgorithm {
    @Override
    public String name() {
        return "MergeSort";
    }

    @Override
    public void sort(InstrumentedArray array, SortContext context) {
        int[] buffer = new int[array.length()];
        mergeSort(array, context, buffer, 0, array.length() - 1);
    }

    private void mergeSort(InstrumentedArray array, SortContext context, int[] buffer, int left, int right) {
        if (left >= right) {
            return;
        }
        int middle = left + (right - left) / 2;
        mergeSort(array, context, buffer, left, middle);
        mergeSort(array, context, buffer, middle + 1, right);
        merge(array, context, buffer, left, middle, right);
    }

    private void merge(InstrumentedArray array, SortContext context, int[] buffer, int left, int middle, int right) {
        for (int i = left; i <= right; i++) {
            buffer[i] = array.get(i);
        }
        int i = left;
        int j = middle + 1;
        int k = left;
        while (i <= middle && j <= right) {
            array.mark(context, i, j, k);
            if (Integer.compare(buffer[i], buffer[j]) <= 0) {
                array.write(k++, buffer[i++], context);
            } else {
                array.write(k++, buffer[j++], context);
            }
        }
        while (i <= middle) {
            array.write(k++, buffer[i++], context);
        }
        while (j <= right) {
            array.write(k++, buffer[j++], context);
        }
    }
}
