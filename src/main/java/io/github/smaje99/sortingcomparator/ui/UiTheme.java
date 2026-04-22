package io.github.smaje99.sortingcomparator.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

public final class UiTheme {
    static final Color APP_BACKGROUND = new Color(247, 241, 232);
    static final Color PANEL_BACKGROUND = new Color(255, 252, 247);
    static final Color CANVAS_BACKGROUND = new Color(255, 248, 238);
    static final Color CONTROL_BACKGROUND = new Color(255, 248, 239);
    static final Color TEXT = new Color(36, 25, 20);
    static final Color MUTED_TEXT = new Color(82, 65, 52);
    static final Color BORDER = new Color(164, 117, 78);
    static final Color HEADER = new Color(92, 46, 30);
    static final Color HEADER_ALT = new Color(113, 58, 34);
    static final Color BUTTON = new Color(122, 53, 29);
    static final Color BUTTON_SECONDARY = new Color(255, 243, 224);
    static final Color BUTTON_QUIET = new Color(242, 219, 195);
    static final Color BUTTON_DISABLED = new Color(226, 214, 199);
    static final Color BUTTON_TEXT = new Color(255, 249, 241);
    static final Color BUTTON_SECONDARY_TEXT = new Color(46, 29, 19);
    static final Color DISABLED_TEXT = new Color(83, 70, 58);
    static final Color INPUT_BACKGROUND = new Color(255, 250, 242);
    static final Color INPUT_BORDER = new Color(139, 96, 62);
    static final Color SLIDER_TRACK = new Color(210, 167, 123);
    static final Color SLIDER_FILL = new Color(122, 53, 29);
    static final Color SLIDER_THUMB = new Color(255, 248, 239);
    static final Color BAR = new Color(145, 78, 35);
    static final Color COMPARED = new Color(0, 93, 105);
    static final Color SWAPPED = new Color(157, 50, 34);
    static final Color MARKED = new Color(132, 86, 0);
    static final Color PIVOT = new Color(82, 62, 130);
    static final Color COMPLETE = new Color(54, 105, 53);

    private UiTheme() {
    }

    public static void installDefaults() {
        UIManager.put("Button.disabledText", DISABLED_TEXT);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("ComboBox.background", PANEL_BACKGROUND);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("TextField.background", PANEL_BACKGROUND);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("Spinner.background", PANEL_BACKGROUND);
        UIManager.put("Spinner.foreground", TEXT);
        UIManager.put("Slider.background", CONTROL_BACKGROUND);
        UIManager.put("Slider.foreground", TEXT);
    }

    static void styleButton(JButton button) {
        button.setUI(new BasicButtonUI());
        configureButton(button, BUTTON, BUTTON_TEXT, new Color(90, 38, 22));
    }

    static void styleSecondaryButton(JButton button) {
        button.setUI(new BasicButtonUI());
        configureButton(button, BUTTON_SECONDARY, BUTTON_SECONDARY_TEXT, BORDER);
    }

    static void styleQuietButton(JButton button) {
        button.setUI(new BasicButtonUI());
        configureButton(button, BUTTON_QUIET, TEXT, new Color(183, 139, 97));
    }

    static <T> void styleComboBox(JComboBox<T> comboBox) {
        comboBox.setUI(new AccessibleComboBoxUi());
        comboBox.setBackground(INPUT_BACKGROUND);
        comboBox.setForeground(TEXT);
        comboBox.setRenderer(accessibleComboRenderer());
        comboBox.setOpaque(true);
        comboBox.setBorder(BorderFactory.createLineBorder(INPUT_BORDER));
        comboBox.setFocusable(false);
    }

    static void styleSpinner(JSpinner spinner) {
        spinner.setBackground(PANEL_BACKGROUND);
        spinner.setForeground(TEXT);
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            JTextField field = editor.getTextField();
            field.setUI(new BasicTextFieldUI());
            field.setBackground(PANEL_BACKGROUND);
            field.setForeground(TEXT);
            field.setCaretColor(HEADER);
            field.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        }
    }

    static void styleSlider(JSlider slider) {
        slider.setUI(new AccessibleSliderUi(slider));
        slider.setBackground(CONTROL_BACKGROUND);
        slider.setForeground(TEXT);
        slider.setOpaque(false);
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);
        slider.setBorder(BorderFactory.createEmptyBorder(6, 0, 2, 0));
    }

    static void styleLabel(JLabel label) {
        label.setForeground(TEXT);
    }

    static void styleMutedLabel(JLabel label) {
        label.setForeground(MUTED_TEXT);
    }

    static void makeCard(JComponent component) {
        component.setBackground(PANEL_BACKGROUND);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
    }

    static Font titleFont(Font base, float size) {
        return base.deriveFont(Font.BOLD, size);
    }

    private static void configureButton(JButton button, Color enabledBackground, Color enabledForeground, Color borderColor) {
        button.setBackground(button.isEnabled() ? enabledBackground : BUTTON_DISABLED);
        button.setForeground(button.isEnabled() ? enabledForeground : DISABLED_TEXT);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        button.setOpaque(true);
        button.addPropertyChangeListener("enabled", event -> {
            boolean enabled = Boolean.TRUE.equals(event.getNewValue());
            button.setBackground(enabled ? enabledBackground : BUTTON_DISABLED);
            button.setForeground(enabled ? enabledForeground : DISABLED_TEXT);
        });
    }

    private static <T> ListCellRenderer<? super T> accessibleComboRenderer() {
        return (JList<? extends T> list, T value, int index, boolean selected, boolean focus) -> {
            JLabel label = new JLabel(value == null ? "" : value.toString());
            label.setOpaque(true);
            label.setBackground(selected ? HEADER : INPUT_BACKGROUND);
            label.setForeground(selected ? BUTTON_TEXT : TEXT);
            label.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
            return label;
        };
    }

    private static final class AccessibleComboBoxUi extends BasicComboBoxUI {
        @Override
        public void paint(Graphics graphics, JComponent component) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(INPUT_BACKGROUND);
            g.fillRect(0, 0, component.getWidth(), component.getHeight());
            g.setColor(INPUT_BORDER);
            g.drawRect(0, 0, component.getWidth() - 1, component.getHeight() - 1);
            g.dispose();
            super.paint(graphics, component);
        }

        @Override
        public void paintCurrentValueBackground(Graphics graphics, Rectangle bounds, boolean hasFocus) {
            graphics.setColor(INPUT_BACKGROUND);
            graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(INPUT_BACKGROUND);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(INPUT_BORDER);
                    g.drawLine(0, 0, 0, getHeight());
                    int middleX = getWidth() / 2;
                    int middleY = getHeight() / 2 + 2;
                    Polygon arrow = new Polygon(
                            new int[]{middleX - 6, middleX + 6, middleX},
                            new int[]{middleY - 4, middleY - 4, middleY + 4},
                            3
                    );
                    g.setColor(TEXT);
                    g.fillPolygon(arrow);
                    g.dispose();
                }
            };
            button.setUI(new BasicButtonUI());
            button.setPreferredSize(new Dimension(34, 28));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(true);
            return button;
        }
    }

    private static final class AccessibleSliderUi extends BasicSliderUI {
        private AccessibleSliderUi(JSlider slider) {
            super(slider);
        }

        @Override
        public void paintTrack(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle track = trackRect;
            int y = track.y + track.height / 2 - 3;
            g.setColor(SLIDER_TRACK);
            g.fillRoundRect(track.x, y, track.width, 6, 8, 8);
            int fillWidth = Math.max(0, thumbRect.x + thumbRect.width / 2 - track.x);
            g.setColor(SLIDER_FILL);
            g.fillRoundRect(track.x, y, fillWidth, 6, 8, 8);
            g.dispose();
        }

        @Override
        public void paintThumb(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(SLIDER_THUMB);
            g.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            g.setColor(INPUT_BORDER);
            g.drawOval(thumbRect.x, thumbRect.y, thumbRect.width - 1, thumbRect.height - 1);
            g.dispose();
        }

        @Override
        protected Dimension getThumbSize() {
            return new Dimension(16, 16);
        }
    }
}
