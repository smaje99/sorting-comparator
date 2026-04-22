package io.github.smaje99.sortingcomparator;

import io.github.smaje99.sortingcomparator.ui.MainFrame;
import io.github.smaje99.sortingcomparator.ui.UiTheme;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class SortingComparatorApp {
    private SortingComparatorApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignored) {
                // The default Swing look and feel is good enough when the system one is unavailable.
            }
            UiTheme.installDefaults();
            new MainFrame().setVisible(true);
        });
    }
}
