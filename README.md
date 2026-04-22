# Sorting Comparator

Sorting Comparator is a Java Swing desktop application for animating and comparing internal sorting algorithms. It uses an instrumented sorting engine so each algorithm can publish comparisons, swaps, writes, highlights, and lifecycle state without depending on Swing.

## Features

- Animated dashboard for 9 sorting algorithms.
- Global controls to run, pause/resume, reset, randomize, and edit datasets.
- Per-algorithm controls with independent run, pause/resume, and reset.
- Comparison mode for running two selected algorithms side by side from the same dataset.
- Responsive renderer for 5 to 100 unique positive integers.
- Maven build with Java 25 and an executable JAR.
- Unit tests for all algorithms, orchestration behavior, and dynamic canvas rendering.

## Algorithms

- Simple Bubble
- Improved Bubble
- Optimized Bubble
- QuickSort
- ShellSort
- RadixSort
- Selection
- Insertion
- MergeSort

## Requirements

- JDK 25
- Maven 3.8+

The legacy `Constrains.jar` dependency is vendored in `vendor/maven-repository` and declared as a normal Maven dependency for compatibility with the original project artifacts.

## Build And Run

```bash
mvn test
mvn package
java -jar target/sorting-comparator-2.0.0.jar
```

During development, you can also run the main class from your IDE:

```text
io.github.smaje99.sortingcomparator.SortingComparatorApp
```

## Project Structure

```text
src/main/java/io/github/smaje99/sortingcomparator
├── SortingComparatorApp.java
├── algorithm     # Instrumented sorting algorithms and sorting context
├── engine        # Reusable run/pause/resume/cancel/reset orchestration
├── model         # Algorithm types, snapshots, metrics, dataset helpers
└── ui            # Swing frame, algorithm panels, dataset editor, canvas

src/main/resources
└── image         # Existing visual assets

vendor/maven-repository
└── org/constrains/constrains/1.0.0

src/test/java    # JUnit 5 tests
```

## Architecture Notes

Algorithms implement `SortAlgorithm` and operate on `InstrumentedArray`. They do not update Swing directly. Every comparison, swap, write, marker, or pivot event goes through `SortContext`, which produces immutable `SortSnapshot` objects.

`SortingEngine` owns one execution lane per algorithm panel. It creates a fresh task for each run, so completed algorithms can be run again safely. It also handles pause, resume, cancel, reset, delay timing, and Swing-safe snapshot dispatch.

`SortingCanvas` consumes snapshots and paints the current values with deterministic highlights:

- Blue for comparisons.
- Red for swaps.
- Yellow for marked/current positions.
- Purple for pivots.
- Green for completed runs.

## Dataset Rules

Datasets must contain:

- 5 to 100 values.
- Positive integers only.
- Unique values only.

The editor accepts comma-separated, space-separated, or line-separated values.

## Development

Run the full verification suite before publishing changes:

```bash
mvn test
mvn package
```

The highest-risk areas are algorithm instrumentation and engine lifecycle behavior. When adding or changing algorithms, keep tests focused on sorted output, emitted snapshots, metrics, and cancellation responsiveness.
