package io.github.smaje99.sortingcomparator.algorithm;

import java.util.ArrayList;
import java.util.List;

public final class RadixSort implements SortAlgorithm {
    @Override
    public String name() {
        return "RadixSort";
    }

    @Override
    public void sort(InstrumentedArray array, SortContext context) {
        int max = 0;
        for (int i = 0; i < array.length(); i++) {
            max = Math.max(max, array.get(i));
            array.mark(context, i);
        }

        for (int divisor = 1; max / divisor > 0; divisor *= 10) {
            List<List<Integer>> buckets = buckets();
            for (int i = 0; i < array.length(); i++) {
                int value = array.get(i);
                int digit = (value / divisor) % 10;
                buckets.get(digit).add(value);
                array.mark(context, i);
            }

            int index = 0;
            for (List<Integer> bucket : buckets) {
                for (int value : bucket) {
                    array.write(index++, value, context);
                }
            }
        }
    }

    private List<List<Integer>> buckets() {
        List<List<Integer>> buckets = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            buckets.add(new ArrayList<>());
        }
        return buckets;
    }
}
