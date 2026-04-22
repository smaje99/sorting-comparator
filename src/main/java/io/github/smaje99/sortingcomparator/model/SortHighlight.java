package io.github.smaje99.sortingcomparator.model;

import java.util.LinkedHashSet;
import java.util.Set;

public record SortHighlight(Set<Integer> compared, Set<Integer> swapped, Set<Integer> marked, int pivotIndex) {
    public static SortHighlight none() {
        return new SortHighlight(Set.of(), Set.of(), Set.of(), -1);
    }

    public static SortHighlight compared(int... indices) {
        return new SortHighlight(indexSet(indices), Set.of(), Set.of(), -1);
    }

    public static SortHighlight swapped(int first, int second) {
        return new SortHighlight(Set.of(), Set.of(first, second), Set.of(), -1);
    }

    public static SortHighlight marked(int... indices) {
        return new SortHighlight(Set.of(), Set.of(), indexSet(indices), -1);
    }

    public static SortHighlight pivot(int pivotIndex, int... activeIndices) {
        return new SortHighlight(indexSet(activeIndices), Set.of(), Set.of(), pivotIndex);
    }

    private static Set<Integer> indexSet(int[] indices) {
        LinkedHashSet<Integer> set = new LinkedHashSet<>();
        for (int index : indices) {
            if (index >= 0) {
                set.add(index);
            }
        }
        return Set.copyOf(set);
    }
}
