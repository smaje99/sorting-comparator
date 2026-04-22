package io.github.smaje99.sortingcomparator.ui;

import io.github.smaje99.sortingcomparator.model.SortHighlight;
import io.github.smaje99.sortingcomparator.model.SortMetrics;
import io.github.smaje99.sortingcomparator.model.SortSnapshot;
import io.github.smaje99.sortingcomparator.model.SortStatus;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SortingCanvasTest {
    @Test
    void paintsDynamicDatasetSizes() {
        int[] values = new int[100];
        for (int i = 0; i < values.length; i++) {
            values[i] = i + 1;
        }
        SortingCanvas canvas = new SortingCanvas(new SortSnapshot(
                values,
                SortHighlight.marked(0, 50, 99),
                SortMetrics.zero(),
                SortStatus.RUNNING
        ));
        canvas.setSize(800, 320);
        BufferedImage image = new BufferedImage(800, 320, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        assertDoesNotThrow(() -> canvas.paint(graphics));
        graphics.dispose();
    }
}
