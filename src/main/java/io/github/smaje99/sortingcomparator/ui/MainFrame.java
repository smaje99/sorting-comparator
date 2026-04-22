package io.github.smaje99.sortingcomparator.ui;

import io.github.smaje99.sortingcomparator.model.AlgorithmType;
import io.github.smaje99.sortingcomparator.model.DatasetFactory;
import io.github.smaje99.sortingcomparator.model.SortMetrics;
import io.github.smaje99.sortingcomparator.model.SortSnapshot;
import io.github.smaje99.sortingcomparator.model.SortStatus;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public final class MainFrame extends JFrame {
    private static final URI AUTHOR_PROFILE = URI.create("https://github.com/smaje99");

    private final List<AlgorithmPanel> panels = new ArrayList<>();
    private final JButton statsButton = new JButton("Show stats");
    private final JSlider speedSlider = new JSlider(0, 600, 120);
    private final JLabel speedValueLabel = new JLabel("120 ms");
    private final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(20, DatasetFactory.MIN_SIZE, DatasetFactory.MAX_SIZE, 1));
    private final JComboBox<AlgorithmType> firstComparison = new JComboBox<>(AlgorithmType.values());
    private final JComboBox<AlgorithmType> secondComparison = new JComboBox<>(AlgorithmType.values());
    private List<AlgorithmPanel> statsTargets = List.of();
    private int[] dataset = DatasetFactory.randomUniqueValues(20);

    public MainFrame() {
        super("Sorting Comparator");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(content());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                panels.forEach(AlgorithmPanel::close);
            }
        });
        setSize(1280, 860);
        setLocationRelativeTo(null);
        setMinimumSize(new java.awt.Dimension(980, 680));
    }

    private JPanel content() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.setBackground(UiTheme.APP_BACKGROUND);
        root.add(header(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(dashboard());
        scrollPane.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
        JViewport viewport = scrollPane.getViewport();
        viewport.setBackground(UiTheme.APP_BACKGROUND);
        root.add(scrollPane, BorderLayout.CENTER);
        return root;
    }

    private JPanel header() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UiTheme.APP_BACKGROUND);
        header.add(titleRow());
        header.add(Box.createVerticalStrut(12));
        header.add(controlDeck());
        return header;
    }

    private JPanel titleRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel title = new JLabel("Sorting Comparator");
        title.setFont(UiTheme.titleFont(title.getFont(), 30f));
        title.setForeground(UiTheme.TEXT);
        JLabel subtitle = new JLabel("Instrumented sorting algorithms with synchronized animation controls");
        subtitle.setForeground(UiTheme.MUTED_TEXT);
        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title);
        text.add(subtitle);
        row.add(text, BorderLayout.WEST);
        row.add(authorLink(), BorderLayout.EAST);
        return row;
    }

    private JLabel authorLink() {
        JLabel signature = new JLabel("<html><u>Hecho por Sergio Majé</u></html>");
        signature.setFont(UiTheme.titleFont(signature.getFont(), 14f));
        signature.setForeground(UiTheme.MUTED_TEXT);
        signature.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signature.setToolTipText(AUTHOR_PROFILE.toString());
        signature.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                openAuthorProfile();
            }
        });
        return signature;
    }

    private void openAuthorProfile() {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            return;
        }
        try {
            Desktop.getDesktop().browse(AUTHOR_PROFILE);
        } catch (IOException | SecurityException | UnsupportedOperationException _) {
            // Ignore browse failures; the signature remains visible even when the OS blocks opening links.
        }
    }

    private JPanel controlDeck() {
        JPanel deck = new JPanel(new BorderLayout(12, 8));
        deck.setBackground(UiTheme.CONTROL_BACKGROUND);
        deck.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        deck.add(globalControls(), BorderLayout.NORTH);
        deck.add(comparisonControls(), BorderLayout.SOUTH);
        return deck;
    }

    private JPanel globalControls() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row.setOpaque(false);
        styleInputs();
        JButton runAll = new JButton("Run all");
        JButton pauseAll = new JButton("Pause/Resume all");
        JButton resetAll = new JButton("Reset all");
        JButton randomize = new JButton("Random dataset");
        JButton editDataset = new JButton("Edit dataset");
        UiTheme.styleButton(runAll);
        UiTheme.styleSecondaryButton(pauseAll);
        UiTheme.styleQuietButton(resetAll);
        UiTheme.styleSecondaryButton(statsButton);
        UiTheme.styleButton(randomize);
        UiTheme.styleSecondaryButton(editDataset);
        statsButton.setEnabled(false);

        UiTheme.styleSlider(speedSlider);
        speedSlider.setMajorTickSpacing(200);
        speedSlider.setPreferredSize(new Dimension(210, 42));
        speedSlider.setToolTipText("Delay per animation step in milliseconds");
        speedValueLabel.setPreferredSize(new Dimension(52, 24));
        UiTheme.styleLabel(speedValueLabel);
        speedSlider.addChangeListener(event -> speedValueLabel.setText(speedSlider.getValue() + " ms"));

        runAll.addActionListener(event -> runTracked(panels));
        pauseAll.addActionListener(event -> panels.forEach(panel -> {
            if (panel.status() == SortStatus.RUNNING || panel.status() == SortStatus.PAUSED) {
                panel.pauseOrResume();
            }
        }));
        resetAll.addActionListener(event -> {
            clearStatsRun();
            panels.forEach(AlgorithmPanel::resetSort);
        });
        randomize.addActionListener(event -> {
            clearStatsRun();
            dataset = DatasetFactory.randomUniqueValues((Integer) sizeSpinner.getValue());
            applyDatasetToPanels();
        });
        editDataset.addActionListener(event -> editDataset());
        statsButton.addActionListener(event -> showStatsDialog());

        row.add(runAll);
        row.add(pauseAll);
        row.add(resetAll);
        row.add(statsButton);
        row.add(separator());
        row.add(label("Size"));
        row.add(sizeSpinner);
        row.add(randomize);
        row.add(editDataset);
        row.add(separator());
        row.add(label("Delay"));
        row.add(speedValueLabel);
        row.add(speedSlider);
        return row;
    }

    private JPanel comparisonControls() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row.setOpaque(false);
        styleInputs();
        secondComparison.setSelectedIndex(1);
        JButton runComparison = new JButton("Run comparison");
        UiTheme.styleSecondaryButton(runComparison);
        runComparison.addActionListener(event -> runComparison());
        row.add(label("Compare"));
        row.add(firstComparison);
        row.add(label("with"));
        row.add(secondComparison);
        row.add(runComparison);
        return row;
    }

    private JPanel dashboard() {
        JPanel dashboard = new JPanel(new GridLayout(0, 3, 12, 12));
        dashboard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        dashboard.setBackground(UiTheme.APP_BACKGROUND);
        Arrays.stream(AlgorithmType.values()).forEach(type -> {
            AlgorithmPanel panel = new AlgorithmPanel(type, dataset, speedSlider::getValue, this::refreshStatsButton);
            panels.add(panel);
            dashboard.add(panel);
        });
        return dashboard;
    }

    private void editDataset() {
        DatasetDialog dialog = new DatasetDialog(this, dataset);
        dialog.setVisible(true);
        if (dialog.accepted()) {
            clearStatsRun();
            dataset = dialog.dataset();
            sizeSpinner.setValue(dataset.length);
            applyDatasetToPanels();
        }
    }

    private void applyDatasetToPanels() {
        panels.forEach(panel -> panel.setDataset(dataset));
    }

    private void runComparison() {
        AlgorithmType first = (AlgorithmType) firstComparison.getSelectedItem();
        AlgorithmType second = (AlgorithmType) secondComparison.getSelectedItem();
        if (first == null || second == null || first == second) {
            return;
        }
        panels.forEach(AlgorithmPanel::resetSort);
        List<AlgorithmPanel> selectedPanels = panels.stream()
                .filter(panel -> panel.type() == first || panel.type() == second)
                .toList();
        runTracked(selectedPanels);
    }

    private void runTracked(List<AlgorithmPanel> targets) {
        statsTargets = List.copyOf(targets);
        statsButton.setEnabled(false);
        statsTargets.forEach(AlgorithmPanel::runSort);
        refreshStatsButton();
    }

    private void clearStatsRun() {
        statsTargets = List.of();
        statsButton.setEnabled(false);
    }

    private void refreshStatsButton() {
        statsButton.setEnabled(!statsTargets.isEmpty()
                && statsTargets.stream().allMatch(panel -> panel.status() == SortStatus.COMPLETED));
    }

    private void showStatsDialog() {
        if (!statsButton.isEnabled()) {
            return;
        }

        String[] columns = {"Algorithm", "Status", "Comparisons", "Swaps", "Writes", "Time (ms)"};
        Object[][] rows = statsTargets.stream()
                .map(this::statsRow)
                .toArray(Object[][]::new);
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(720, Math.min(320, 64 + rows.length * 24)));
        JOptionPane.showMessageDialog(this, scrollPane, "Sorting stats", JOptionPane.INFORMATION_MESSAGE);
    }

    private Object[] statsRow(AlgorithmPanel panel) {
        SortSnapshot snapshot = panel.snapshot();
        SortMetrics metrics = snapshot.metrics();
        return new Object[]{
                panel.type().displayName(),
                label(snapshot.status()),
                metrics.comparisons(),
                metrics.swaps(),
                metrics.writes(),
                metrics.elapsedMillis()
        };
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

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        UiTheme.styleLabel(label);
        return label;
    }

    private JComponent separator() {
        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(1, 28));
        separator.setBackground(UiTheme.BORDER);
        return separator;
    }

    private void styleInputs() {
        UiTheme.styleComboBox(firstComparison);
        firstComparison.setPreferredSize(new Dimension(170, 36));
        UiTheme.styleComboBox(secondComparison);
        secondComparison.setPreferredSize(new Dimension(170, 36));
        UiTheme.styleSpinner(sizeSpinner);
        sizeSpinner.setPreferredSize(new Dimension(68, 36));
    }
}
