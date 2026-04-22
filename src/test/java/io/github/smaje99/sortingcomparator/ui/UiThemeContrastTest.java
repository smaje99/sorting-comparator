package io.github.smaje99.sortingcomparator.ui;

import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UiThemeContrastTest {
    private static final double AA_NORMAL_TEXT = 4.5;

    @Test
    void coreTextPairsMeetAaContrast() {
        assertAa(UiTheme.TEXT, UiTheme.APP_BACKGROUND);
        assertAa(UiTheme.MUTED_TEXT, UiTheme.APP_BACKGROUND);
        assertAa(UiTheme.TEXT, UiTheme.PANEL_BACKGROUND);
        assertAa(UiTheme.TEXT, UiTheme.CONTROL_BACKGROUND);
        assertAa(UiTheme.BUTTON_TEXT, UiTheme.BUTTON);
        assertAa(UiTheme.BUTTON_SECONDARY_TEXT, UiTheme.BUTTON_SECONDARY);
        assertAa(UiTheme.TEXT, UiTheme.BUTTON_QUIET);
        assertAa(UiTheme.DISABLED_TEXT, UiTheme.BUTTON_DISABLED);
        assertAa(UiTheme.TEXT, UiTheme.INPUT_BACKGROUND);
        assertAa(UiTheme.TEXT, UiTheme.SLIDER_THUMB);
        assertAa(UiTheme.SLIDER_FILL, UiTheme.CONTROL_BACKGROUND);
    }

    private void assertAa(Color foreground, Color background) {
        double contrast = contrast(foreground, background);
        assertTrue(
                contrast >= AA_NORMAL_TEXT,
                "Expected AA contrast >= " + AA_NORMAL_TEXT + " but got " + contrast
                        + " for fg=" + foreground + " bg=" + background
        );
    }

    private double contrast(Color first, Color second) {
        double l1 = luminance(first);
        double l2 = luminance(second);
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        return (lighter + 0.05) / (darker + 0.05);
    }

    private double luminance(Color color) {
        return 0.2126 * channel(color.getRed())
                + 0.7152 * channel(color.getGreen())
                + 0.0722 * channel(color.getBlue());
    }

    private double channel(int value) {
        double normalized = value / 255.0;
        if (normalized <= 0.03928) {
            return normalized / 12.92;
        }
        return Math.pow((normalized + 0.055) / 1.055, 2.4);
    }
}
