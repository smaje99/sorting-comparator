package io.github.smaje99.sortingcomparator.engine;

import io.github.smaje99.sortingcomparator.model.AlgorithmType;
import io.github.smaje99.sortingcomparator.model.SortSnapshot;
import io.github.smaje99.sortingcomparator.model.SortStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortingEngineTest {
    @Test
    void canPauseResumeAndCompleteRun() {
        CopyOnWriteArrayList<SortSnapshot> snapshots = new CopyOnWriteArrayList<>();
        try (SortingEngine engine = new SortingEngine(
                AlgorithmType.SIMPLE_BUBBLE,
                shuffledThirty(),
                snapshots::add,
                Runnable::run,
                () -> 1
        )) {
            engine.run();
            waitForStatus(engine, SortStatus.RUNNING);
            engine.pause();
            assertEquals(SortStatus.PAUSED, engine.status());
            engine.resume();
            waitForStatus(engine, SortStatus.COMPLETED);

            int[] sorted = snapshots.getLast().values();
            int[] expected = shuffledThirty();
            Arrays.sort(expected);
            assertArrayEquals(expected, sorted);
        }
    }

    @Test
    void canCancelAndReset() {
        CopyOnWriteArrayList<SortSnapshot> snapshots = new CopyOnWriteArrayList<>();
        int[] original = shuffledHundred();
        try (SortingEngine engine = new SortingEngine(
                AlgorithmType.SIMPLE_BUBBLE,
                original,
                snapshots::add,
                Runnable::run,
                () -> 2
        )) {
            engine.run();
            waitForStatus(engine, SortStatus.RUNNING);
            engine.cancel();
            waitForStatus(engine, SortStatus.CANCELLED);
            engine.reset();

            assertEquals(SortStatus.IDLE, engine.status());
            assertArrayEquals(original, snapshots.getLast().values());
        }
    }

    @Test
    void canRunAgainAfterCompletion() {
        CopyOnWriteArrayList<SortSnapshot> snapshots = new CopyOnWriteArrayList<>();
        try (SortingEngine engine = new SortingEngine(
                AlgorithmType.QUICK_SORT,
                new int[]{8, 7, 6, 5, 4, 3, 2, 1},
                snapshots::add,
                Runnable::run,
                () -> 0
        )) {
            engine.run();
            waitForStatus(engine, SortStatus.COMPLETED);
            assertDoesNotThrow(engine::run);
            waitForStatus(engine, SortStatus.COMPLETED);
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

    private void waitForStatus(SortingEngine engine, SortStatus status) {
        org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(Duration.ofSeconds(8), () -> {
            while (engine.status() != status) {
                Thread.sleep(10);
            }
        });
    }
}
