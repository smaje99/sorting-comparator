package io.github.smaje99.sortingcomparator.ui;

import io.github.smaje99.sortingcomparator.model.AlgorithmType;
import io.github.smaje99.sortingcomparator.model.DatasetFactory;
import io.github.smaje99.sortingcomparator.model.SortStatus;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.SpinnerNumberModel;

public final class MainFrame extends JFrame {
    private final List<AlgorithmPanel> panels = new ArrayList<>();
    private final JSlider speedSlider = new JSlider(0, 600, 120);
    private final JLabel speedValueLabel = new JLabel("120 ms");
    private final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(20, DatasetFactory.MIN_SIZE, DatasetFactory.MAX_SIZE, 1));
    private final JComboBox<AlgorithmType> firstComparison = new JComboBox<>(AlgorithmType.values());
    private final JComboBox<AlgorithmType> secondComparison = new JComboBox<>(AlgorithmType.values());
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
        return row;
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
        UiTheme.styleButton(randomize);
        UiTheme.styleSecondaryButton(editDataset);

        UiTheme.styleSlider(speedSlider);
        speedSlider.setMajorTickSpacing(200);
        speedSlider.setPreferredSize(new Dimension(210, 42));
        speedSlider.setToolTipText("Delay per animation step in milliseconds");
        speedValueLabel.setPreferredSize(new Dimension(52, 24));
        UiTheme.styleLabel(speedValueLabel);
        speedSlider.addChangeListener(event -> speedValueLabel.setText(speedSlider.getValue() + " ms"));

        runAll.addActionListener(event -> panels.forEach(AlgorithmPanel::runSort));
        pauseAll.addActionListener(event -> panels.forEach(panel -> {
            if (panel.status() == SortStatus.RUNNING || panel.status() == SortStatus.PAUSED) {
                panel.pauseOrResume();
            }
        }));
        resetAll.addActionListener(event -> panels.forEach(AlgorithmPanel::resetSort));
        randomize.addActionListener(event -> {
            dataset = DatasetFactory.randomUniqueValues((Integer) sizeSpinner.getValue());
            applyDatasetToPanels();
        });
        editDataset.addActionListener(event -> editDataset());

        row.add(runAll);
        row.add(pauseAll);
        row.add(resetAll);
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
            AlgorithmPanel panel = new AlgorithmPanel(type, dataset, speedSlider::getValue);
            panels.add(panel);
            dashboard.add(panel);
        });
        return dashboard;
    }

    private void editDataset() {
        DatasetDialog dialog = new DatasetDialog(this, dataset);
        dialog.setVisible(true);
        if (dialog.accepted()) {
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
        panels.stream()
                .filter(panel -> panel.type() == first || panel.type() == second)
                .forEach(AlgorithmPanel::runSort);
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
