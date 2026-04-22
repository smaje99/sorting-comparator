package io.github.smaje99.sortingcomparator.engine;

import io.github.smaje99.sortingcomparator.model.AlgorithmType;
import io.github.smaje99.sortingcomparator.model.SortSnapshot;
import io.github.smaje99.sortingcomparator.model.SortStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortingEngineTest {
    @Test
    void canPauseResumeAndCompleteRun() {
        SnapshotRecorder snapshots = new SnapshotRecorder();
        try (SortingEngine engine = new SortingEngine(
                AlgorithmType.SIMPLE_BUBBLE,
                shuffledThirty(),
                snapshots,
                Runnable::run,
                () -> 1
        )) {
            engine.run();
            waitForStatus(snapshots, SortStatus.RUNNING);
            engine.pause();
            assertEquals(SortStatus.PAUSED, engine.status());
            engine.resume();
            waitForStatus(snapshots, SortStatus.COMPLETED);

            int[] sorted = snapshots.last().values();
            int[] expected = shuffledThirty();
            Arrays.sort(expected);
            assertArrayEquals(expected, sorted);
        }
    }

    @Test
    void canCancelAndReset() {
        SnapshotRecorder snapshots = new SnapshotRecorder();
        int[] original = shuffledHundred();
        try (SortingEngine engine = new SortingEngine(
                AlgorithmType.SIMPLE_BUBBLE,
                original,
                snapshots,
                Runnable::run,
                () -> 2
        )) {
            engine.run();
            waitForStatus(snapshots, SortStatus.RUNNING);
            engine.cancel();
            waitForStatus(snapshots, SortStatus.CANCELLED);
            engine.reset();

            assertEquals(SortStatus.IDLE, engine.status());
            assertArrayEquals(original, snapshots.last().values());
        }
    }

    @Test
    void canRunAgainAfterCompletion() {
        SnapshotRecorder snapshots = new SnapshotRecorder();
        try (SortingEngine engine = new SortingEngine(
                AlgorithmType.QUICK_SORT,
                new int[]{8, 7, 6, 5, 4, 3, 2, 1},
                snapshots,
                Runnable::run,
                () -> 0
        )) {
            engine.run();
            waitForStatus(snapshots, SortStatus.COMPLETED);
            assertDoesNotThrow(engine::run);
            waitForStatus(snapshots, SortStatus.COMPLETED);
            assertTrue(snapshots.size() > 2);
        }
    }

    private int[] shuffledHundred() {
        int[] values = new int[100];
        for (int i = 0; i < values.length; i++) {
            values[i] = values.length - i;
        }
        return values;
    }

    private int[] shuffledThirty() {
        int[] values = new int[30];
        for (int i = 0; i < values.length; i++) {
            values[i] = values.length - i;
        }
        return values;
    }

    private void waitForStatus(SnapshotRecorder snapshots, SortStatus status) {
        try {
            assertTrue(snapshots.awaitStatus(status, Duration.ofSeconds(8)), "Timed out waiting for " + status);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for " + status, e);
        }
    }

    private static final class SnapshotRecorder implements Consumer<SortSnapshot> {
        private final CopyOnWriteArrayList<SortSnapshot> snapshots = new CopyOnWriteArrayList<>();
        private final BlockingQueue<SortStatus> statuses = new LinkedBlockingQueue<>();

        @Override
        public void accept(SortSnapshot snapshot) {
            snapshots.add(snapshot);
            statuses.add(snapshot.status());
        }

        private SortSnapshot last() {
            return snapshots.getLast();
        }

        private int size() {
            return snapshots.size();
        }

        private boolean awaitStatus(SortStatus expected, Duration timeout) throws InterruptedException {
            long deadline = System.nanoTime() + timeout.toNanos();
            while (true) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    return false;
                }
                SortStatus observed = statuses.poll(remaining, TimeUnit.NANOSECONDS);
                if (observed == expected) {
                    return true;
                }
                if (observed == null) {
                    return false;
                }
            }
        }
    }
}
