package io.github.smaje99.sortingcomparator;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import io.github.smaje99.sortingcomparator.ui.MainFrame;
import io.github.smaje99.sortingcomparator.ui.UiTheme;

public final class SortingComparatorApp {
    private SortingComparatorApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException _) {
                // The default Swing look and feel is good enough when the system one is unavailable.
            }
            UiTheme.installDefaults();
            new MainFrame().setVisible(true);
        });
    }
}
