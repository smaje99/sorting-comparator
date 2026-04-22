package io.github.smaje99.sortingcomparator.ui;

import io.github.smaje99.sortingcomparator.engine.SortingEngine;
import io.github.smaje99.sortingcomparator.model.AlgorithmType;
import io.github.smaje99.sortingcomparator.model.SortMetrics;
import io.github.smaje99.sortingcomparator.model.SortSnapshot;
import io.github.smaje99.sortingcomparator.model.SortStatus;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;

public final class AlgorithmPanel extends JPanel {
    private final AlgorithmType type;
    private final transient SortingEngine engine;
    private final transient Runnable snapshotListener;
    private final SortingCanvas canvas;
    private final JLabel statusLabel = new JLabel("Idle");
    private final JLabel comparisonsLabel = new JLabel("comparisons: 0");
    private final JLabel swapsLabel = new JLabel("swaps: 0");
    private final JLabel writesLabel = new JLabel("writes: 0");
    private final JLabel timeLabel = new JLabel("time: 0 ms");
    private final JButton runButton = new JButton("Run");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton resetButton = new JButton("Reset");
    private transient SortSnapshot lastSnapshot;

    public AlgorithmPanel(AlgorithmType type, int[] dataset, IntSupplier delaySupplier, Runnable snapshotListener) {
        super(new BorderLayout(10, 8));
        this.type = type;
        this.snapshotListener = Objects.requireNonNull(snapshotListener);
        SortSnapshot initialSnapshot = new SortSnapshot(
                dataset,
                io.github.smaje99.sortingcomparator.model.SortHighlight.none(),
                SortMetrics.zero(),
                SortStatus.IDLE
        );
        this.lastSnapshot = initialSnapshot;
        this.canvas = new SortingCanvas(initialSnapshot);
        this.engine = new SortingEngine(type, dataset, this::applySnapshot, SwingUtilities::invokeLater, delaySupplier);
        build();
    }

    public AlgorithmType type() {
        return type;
    }

    public SortStatus status() {
        return engine.status();
    }

    public SortSnapshot snapshot() {
        return lastSnapshot;
    }

    public void runSort() {
        engine.run();
    }

    public void pauseOrResume() {
        if (engine.status() == SortStatus.PAUSED) {
            engine.resume();
        } else {
            engine.pause();
        }
    }

    public void resetSort() {
        engine.reset();
    }

    public void setDataset(int[] dataset) {
        engine.setDataset(dataset);
    }

    public void close() {
        engine.close();
    }

    private void build() {
        UiTheme.makeCard(this);

        JLabel title = new JLabel(type.displayName());
        title.setFont(UiTheme.titleFont(title.getFont(), 17f));
        title.setForeground(new Color(255, 248, 236));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 12f));
        statusLabel.setForeground(new Color(255, 234, 204));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(headerColor());
        header.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        header.add(title, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);

        JPanel metrics = new JPanel(new GridLayout(2, 2, 6, 2));
        metrics.setOpaque(false);
        List.of(comparisonsLabel, swapsLabel, writesLabel, timeLabel).forEach(label -> {
            UiTheme.styleMutedLabel(label);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        });
        metrics.add(comparisonsLabel);
        metrics.add(swapsLabel);
        metrics.add(writesLabel);
        metrics.add(timeLabel);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        controls.setOpaque(false);
        UiTheme.styleButton(runButton);
        UiTheme.styleSecondaryButton(pauseButton);
        UiTheme.styleQuietButton(resetButton);
        pauseButton.setEnabled(false);
        runButton.addActionListener(event -> runSort());
        pauseButton.addActionListener(event -> pauseOrResume());
        resetButton.addActionListener(event -> resetSort());
        controls.add(runButton);
        controls.add(pauseButton);
        controls.add(resetButton);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        bottom.add(metrics, BorderLayout.CENTER);
        bottom.add(controls, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void applySnapshot(SortSnapshot snapshot) {
        lastSnapshot = snapshot;
        canvas.setSnapshot(snapshot);
        SortMetrics metrics = snapshot.metrics();
        comparisonsLabel.setText("comparisons: " + metrics.comparisons());
        swapsLabel.setText("swaps: " + metrics.swaps());
        writesLabel.setText("writes: " + metrics.writes());
        timeLabel.setText("time: " + metrics.elapsedMillis() + " ms");
        statusLabel.setText(label(snapshot.status()));
        runButton.setEnabled(snapshot.status() != SortStatus.RUNNING);
        pauseButton.setEnabled(snapshot.status() == SortStatus.RUNNING || snapshot.status() == SortStatus.PAUSED);
        pauseButton.setText(snapshot.status() == SortStatus.PAUSED ? "Resume" : "Pause");
        snapshotListener.run();
    }

    private String label(SortStatus status) {
        return switch (status) {
            case IDLE -> "Idle";
            case RUNNING -> "Running";
            case PAUSED -> "Paused";
            case COMPLETED -> "Completed";
            case CANCELLED -> "Cancelled";
            case FAILED -> "Failed";
        };
    }

    private Color headerColor() {
        return type.ordinal() % 2 == 0 ? UiTheme.HEADER : UiTheme.HEADER_ALT;
    }
}
