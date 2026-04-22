package io.github.smaje99.sortingcomparator.model;

import io.github.smaje99.sortingcomparator.algorithm.ImprovedBubbleSort;
import io.github.smaje99.sortingcomparator.algorithm.InsertionSort;
import io.github.smaje99.sortingcomparator.algorithm.MergeSort;
import io.github.smaje99.sortingcomparator.algorithm.OptimizedBubbleSort;
import io.github.smaje99.sortingcomparator.algorithm.QuickSort;
import io.github.smaje99.sortingcomparator.algorithm.RadixSort;
import io.github.smaje99.sortingcomparator.algorithm.SelectionSort;
import io.github.smaje99.sortingcomparator.algorithm.ShellSort;
import io.github.smaje99.sortingcomparator.algorithm.SimpleBubbleSort;
import io.github.smaje99.sortingcomparator.algorithm.SortAlgorithm;

import java.util.function.Supplier;

public enum AlgorithmType {
    SIMPLE_BUBBLE("Simple Bubble", SimpleBubbleSort::new),
    IMPROVED_BUBBLE("Improved Bubble", ImprovedBubbleSort::new),
    OPTIMIZED_BUBBLE("Optimized Bubble", OptimizedBubbleSort::new),
    QUICK_SORT("QuickSort", QuickSort::new),
    SHELL_SORT("ShellSort", ShellSort::new),
    RADIX_SORT("RadixSort", RadixSort::new),
    SELECTION("Selection", SelectionSort::new),
    INSERTION("Insertion", InsertionSort::new),
    MERGE_SORT("MergeSort", MergeSort::new);

    private final String displayName;
    private final Supplier<SortAlgorithm> factory;

    AlgorithmType(String displayName, Supplier<SortAlgorithm> factory) {
        this.displayName = displayName;
        this.factory = factory;
    }

    public String displayName() {
        return displayName;
    }

    public SortAlgorithm createAlgorithm() {
        return factory.get();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
