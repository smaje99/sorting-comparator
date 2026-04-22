package io.github.smaje99.sortingcomparator.algorithm;

import java.util.function.Consumer;

import io.github.smaje99.sortingcomparator.model.SortHighlight;
import io.github.smaje99.sortingcomparator.model.SortMetrics;
import io.github.smaje99.sortingcomparator.model.SortSnapshot;
import io.github.smaje99.sortingcomparator.model.SortStatus;

public final class SortContext {
    private final Consumer<SortSnapshot> listener;
    private final Runnable checkpoint;
    private final long delayMillis;
    private long comparisons;
    private long swaps;
    private long writes;
    private long startedAt;

    public SortContext(Consumer<SortSnapshot> listener, Runnable checkpoint, long delayMillis) {
        this.listener = listener;
        this.checkpoint = checkpoint;
        this.delayMillis = Math.max(0, delayMillis);
        this.startedAt = System.nanoTime();
    }

    public int compare(int[] values, int first, int second) {
        comparisons++;
        publish(values, SortHighlight.compared(first, second), SortStatus.RUNNING);
        return Integer.compare(values[first], values[second]);
    }

    public int compareValue(int[] values, int index, int value) {
        comparisons++;
        publish(values, SortHighlight.marked(index), SortStatus.RUNNING);
        return Integer.compare(values[index], value);
    }

    public void swap(int[] values, int first, int second) {
        if (first == second) {
            publish(values, SortHighlight.marked(first), SortStatus.RUNNING);
            return;
        }
        int temp = values[first];
        values[first] = values[second];
        values[second] = temp;
        swaps++;
        writes += 2;
        publish(values, SortHighlight.swapped(first, second), SortStatus.RUNNING);
    }

    public void write(int[] values, int index, int value) {
        values[index] = value;
        writes++;
        publish(values, SortHighlight.marked(index), SortStatus.RUNNING);
    }

    public void mark(int[] values, int... indices) {
        publish(values, SortHighlight.marked(indices), SortStatus.RUNNING);
    }

    public void pivot(int[] values, int pivotIndex, int... activeIndices) {
        publish(values, SortHighlight.pivot(pivotIndex, activeIndices), SortStatus.RUNNING);
    }

    public void publish(int[] values, SortHighlight highlight, SortStatus status) {
        checkpoint.run();
        listener.accept(new SortSnapshot(values, highlight, metrics(), status));
        if (delayMillis > 0 && status == SortStatus.RUNNING) {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
                throw new SortInterruptedException();
            }
        }
        checkpoint.run();
    }

    public SortMetrics metrics() {
        long elapsedMillis = Math.max(0, (System.nanoTime() - startedAt) / 1_000_000L);
        return new SortMetrics(comparisons, swaps, writes, elapsedMillis);
    }

    public void resetClock() {
        startedAt = System.nanoTime();
    }
}
