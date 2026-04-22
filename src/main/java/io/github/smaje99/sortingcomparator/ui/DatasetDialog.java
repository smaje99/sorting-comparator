package io.github.smaje99.sortingcomparator.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import io.github.smaje99.sortingcomparator.model.DatasetFactory;

public final class DatasetDialog extends JDialog {
    private final JTextArea valuesArea = new JTextArea(8, 52);
    private int[] dataset;
    private boolean accepted;

    public DatasetDialog(Window owner, int[] currentDataset) {
        super(owner, "Edit dataset", ModalityType.APPLICATION_MODAL);
        this.dataset = Arrays.copyOf(currentDataset, currentDataset.length);
        build();
    }

    public boolean accepted() {
        return accepted;
    }

    public int[] dataset() {
        return Arrays.copyOf(dataset, dataset.length);
    }

    private void build() {
        valuesArea.setLineWrap(true);
        valuesArea.setWrapStyleWord(true);
        valuesArea.setText(toText(dataset));
        valuesArea.setBackground(UiTheme.CANVAS_BACKGROUND);
        valuesArea.setForeground(UiTheme.TEXT);
        valuesArea.setCaretColor(UiTheme.HEADER);

        JLabel hint = new JLabel("Enter 5 to 100 unique positive integers separated by commas, spaces, or new lines.");
        UiTheme.styleLabel(hint);
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBackground(UiTheme.APP_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.add(hint, BorderLayout.NORTH);
        content.add(new JScrollPane(valuesArea), BorderLayout.CENTER);
        content.add(buttons(), BorderLayout.SOUTH);
        setContentPane(content);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel buttons() {
        JButton apply = new JButton("Apply");
        JButton cancel = new JButton("Cancel");
        UiTheme.styleButton(apply);
        UiTheme.styleSecondaryButton(cancel);
        apply.addActionListener(event -> applyDataset());
        cancel.addActionListener(event -> dispose());
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(UiTheme.APP_BACKGROUND);
        panel.add(cancel);
        panel.add(apply);
        return panel;
    }

    private void applyDataset() {
        try {
            int[] parsed = Arrays.stream(valuesArea.getText().trim().split("[,\\s]+"))
                    .filter(token -> !token.isBlank())
                    .mapToInt(Integer::parseInt)
                    .toArray();
            DatasetFactory.validateDataset(parsed);
            dataset = parsed;
            accepted = true;
            dispose();
        } catch (RuntimeException _) {
            JOptionPane.showMessageDialog(
                    this,
                    "Dataset must contain 5 to 100 unique positive integers.",
                    "Invalid dataset",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String toText(int[] values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(i % 12 == 0 ? System.lineSeparator() : ", ");
            }
            builder.append(values[i]);
        }
        return builder.toString();
    }
}
