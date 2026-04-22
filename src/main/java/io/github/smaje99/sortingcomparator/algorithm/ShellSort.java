package io.github.smaje99.sortingcomparator.algorithm;

public final class ShellSort implements SortAlgorithm {
    @Override
    public String name() {
        return "ShellSort";
    }

    @Override
    public void sort(InstrumentedArray array, SortContext context) {
        int gap = array.length() / 2;
        while (gap > 0) {
            for (int i = gap; i < array.length(); i++) {
                int j = i - gap;
                while (j >= 0) {
                    int k = j + gap;
                    if (array.compare(j, k, context) <= 0) {
                        break;
                    }
                    array.swap(j, k, context);
                    j -= gap;
                }
            }
            gap /= 2;
        }
    }
}
