package io.github.smaje99.sortingcomparator.engine;

import io.github.smaje99.sortingcomparator.algorithm.InstrumentedArray;
import io.github.smaje99.sortingcomparator.algorithm.SortAlgorithm;
import io.github.smaje99.sortingcomparator.algorithm.SortContext;
import io.github.smaje99.sortingcomparator.algorithm.SortInterruptedException;
import io.github.smaje99.sortingcomparator.model.AlgorithmType;
import io.github.smaje99.sortingcomparator.model.DatasetFactory;
import io.github.smaje99.sortingcomparator.model.SortHighlight;
import io.github.smaje99.sortingcomparator.model.SortMetrics;
import io.github.smaje99.sortingcomparator.model.SortSnapshot;
import io.github.smaje99.sortingcomparator.model.SortStatus;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

/**
 * Owns one reusable sorting execution lane with pause, resume, cancel, and reset semantics.
 */
public final class SortingEngine implements AutoCloseable {
    private final AlgorithmType type;
    private final Consumer<SortSnapshot> listener;
    private final Consumer<Runnable> dispatcher;
    private final IntSupplier delaySupplier;
    private final ExecutorService executor;
    private final Object pauseMonitor = new Object();
    private volatile boolean pauseRequested;
    private volatile boolean cancelRequested;
    private volatile SortStatus status = SortStatus.IDLE;
    private volatile Future<?> runningTask;
    private volatile int[] originalValues;
    private volatile SortSnapshot lastSnapshot;
    private volatile long generation;

    public SortingEngine(
            AlgorithmType type,
            int[] initialValues,
            Consumer<SortSnapshot> listener,
            Consumer<Runnable> dispatcher,
            IntSupplier delaySupplier
    ) {
        this.type = Objects.requireNonNull(type);
        DatasetFactory.validateDataset(initialValues);
        this.originalValues = Arrays.copyOf(initialValues, initialValues.length);
        this.listener = Objects.requireNonNull(listener);
        this.dispatcher = Objects.requireNonNull(dispatcher);
        this.delaySupplier = Objects.requireNonNull(delaySupplier);
        this.executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "sorting-" + type.name().toLowerCase());
            thread.setDaemon(true);
            return thread;
        });
        publish(new SortSnapshot(originalValues, SortHighlight.none(), SortMetrics.zero(), SortStatus.IDLE));
    }

    public AlgorithmType type() {
        return type;
    }

    public SortStatus status() {
        return status;
    }

    public synchronized boolean run() {
        if (status == SortStatus.RUNNING) {
            return false;
        }
        if (status == SortStatus.PAUSED) {
            resume();
            return true;
        }
        cancelRequested = false;
        pauseRequested = false;
        status = SortStatus.RUNNING;
        long runGeneration = ++generation;
        int[] runValues = Arrays.copyOf(originalValues, originalValues.length);
        SortAlgorithm algorithm = type.createAlgorithm();
        runningTask = executor.submit(() -> execute(algorithm, runValues, runGeneration));
        return true;
    }

    public void pause() {
        if (status != SortStatus.RUNNING) {
            return;
        }
        pauseRequested = true;
        status = SortStatus.PAUSED;
        publishStatus(SortStatus.PAUSED);
    }

    public void resume() {
        if (status != SortStatus.PAUSED) {
            return;
        }
        synchronized (pauseMonitor) {
            pauseRequested = false;
            status = SortStatus.RUNNING;
            publishStatus(SortStatus.RUNNING);
            pauseMonitor.notifyAll();
        }
    }

    public void cancel() {
        cancelRequested = true;
        pauseRequested = false;
        generation++;
        Future<?> task = runningTask;
        if (task != null) {
            task.cancel(true);
        }
        synchronized (pauseMonitor) {
            pauseMonitor.notifyAll();
        }
        status = SortStatus.CANCELLED;
        publishStatus(SortStatus.CANCELLED);
    }

    public void reset() {
        if (status == SortStatus.RUNNING || status == SortStatus.PAUSED) {
            cancel();
        }
        generation++;
        status = SortStatus.IDLE;
        publish(new SortSnapshot(originalValues, SortHighlight.none(), SortMetrics.zero(), SortStatus.IDLE));
    }

    public void setDataset(int[] values) {
        DatasetFactory.validateDataset(values);
        if (status == SortStatus.RUNNING || status == SortStatus.PAUSED) {
            cancel();
        }
        generation++;
        originalValues = Arrays.copyOf(values, values.length);
        status = SortStatus.IDLE;
        publish(new SortSnapshot(originalValues, SortHighlight.none(), SortMetrics.zero(), SortStatus.IDLE));
    }

    private void execute(SortAlgorithm algorithm, int[] runValues, long runGeneration) {
        InstrumentedArray array = new InstrumentedArray(runValues);
        SortContext context = new SortContext(
                snapshot -> publishFromRun(runGeneration, snapshot),
                () -> checkpoint(runGeneration),
                delaySupplier.getAsInt()
        );
        context.resetClock();
        publishFromRun(runGeneration, new SortSnapshot(array.snapshot(), SortHighlight.none(), context.metrics(), SortStatus.RUNNING));
        try {
            algorithm.sort(array, context);
            if (runGeneration != generation) {
                return;
            }
            if (cancelRequested) {
                status = SortStatus.CANCELLED;
                publishFromRun(runGeneration, new SortSnapshot(array.snapshot(), SortHighlight.none(), context.metrics(), SortStatus.CANCELLED));
                return;
            }
            status = SortStatus.COMPLETED;
            publishFromRun(runGeneration, new SortSnapshot(array.snapshot(), SortHighlight.none(), context.metrics(), SortStatus.COMPLETED));
        } catch (SortInterruptedException e) {
            if (runGeneration == generation) {
                status = SortStatus.CANCELLED;
                publishFromRun(runGeneration, new SortSnapshot(array.snapshot(), SortHighlight.none(), context.metrics(), SortStatus.CANCELLED));
            }
        } catch (RuntimeException e) {
            if (runGeneration == generation) {
                status = SortStatus.FAILED;
                publishFromRun(runGeneration, new SortSnapshot(array.snapshot(), SortHighlight.none(), context.metrics(), SortStatus.FAILED));
            }
            throw e;
        }
    }

    private void checkpoint(long runGeneration) {
        if (cancelRequested || runGeneration != generation || Thread.currentThread().isInterrupted()) {
            throw new SortInterruptedException();
        }
        synchronized (pauseMonitor) {
            while (pauseRequested && !cancelRequested) {
                try {
                    pauseMonitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SortInterruptedException();
                }
            }
        }
        if (cancelRequested || runGeneration != generation || Thread.currentThread().isInterrupted()) {
            throw new SortInterruptedException();
        }
    }

    private void publishStatus(SortStatus newStatus) {
        SortSnapshot snapshot = lastSnapshot;
        if (snapshot == null) {
            snapshot = new SortSnapshot(originalValues, SortHighlight.none(), SortMetrics.zero(), newStatus);
        } else {
            snapshot = new SortSnapshot(snapshot.values(), snapshot.highlight(), snapshot.metrics(), newStatus);
        }
        publish(snapshot);
    }

    private void publish(SortSnapshot snapshot) {
        lastSnapshot = snapshot;
        dispatcher.accept(() -> listener.accept(snapshot));
    }

    private void publishFromRun(long runGeneration, SortSnapshot snapshot) {
        if (runGeneration == generation) {
            publish(snapshot);
        }
    }

    @Override
    public void close() {
        cancelRequested = true;
        executor.shutdownNow();
    }
}
