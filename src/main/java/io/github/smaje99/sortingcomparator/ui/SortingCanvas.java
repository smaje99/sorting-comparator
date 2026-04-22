package io.github.smaje99.sortingcomparator.ui;

import io.github.smaje99.sortingcomparator.model.SortHighlight;
import io.github.smaje99.sortingcomparator.model.SortSnapshot;
import io.github.smaje99.sortingcomparator.model.SortStatus;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

public final class SortingCanvas extends JComponent {
    private SortSnapshot snapshot;

    public SortingCanvas(SortSnapshot initialSnapshot) {
        this.snapshot = initialSnapshot;
        setPreferredSize(new Dimension(340, 210));
        setMinimumSize(new Dimension(260, 160));
        setOpaque(true);
        setBackground(UiTheme.CANVAS_BACKGROUND);
    }

    public void setSnapshot(SortSnapshot snapshot) {
        this.snapshot = snapshot;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(UiTheme.CANVAS_BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());

        int[] values = snapshot.values();
        if (values.length == 0) {
            g.dispose();
            return;
        }

        int max = Arrays.stream(values).max().orElse(1);
        int padding = 16;
        int labelSpace = 28;
        int usableWidth = Math.max(1, getWidth() - padding * 2);
        int usableHeight = Math.max(1, getHeight() - padding * 2 - labelSpace);
        double slot = (double) usableWidth / values.length;
        int barWidth = Math.max(2, (int) Math.floor(slot * 0.72));
        SortHighlight highlight = snapshot.highlight();
        int labelStep = Math.max(1, (int) Math.ceil(values.length / Math.max(1.0, usableWidth / 42.0)));
        drawBaseline(g, padding, usableHeight);

        for (int i = 0; i < values.length; i++) {
            int barHeight = Math.max(3, (int) Math.round((values[i] / (double) max) * usableHeight));
            int x = padding + (int) Math.round(i * slot + (slot - barWidth) / 2.0);
            int y = padding + usableHeight - barHeight;
            g.setColor(colorFor(i, highlight, snapshot.status()));
            g.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, 6, 6));
            if (highlight.pivotIndex() == i) {
                g.setColor(UiTheme.PIVOT.darker());
                g.setStroke(new BasicStroke(2f));
                g.draw(new RoundRectangle2D.Double(x, y, barWidth, barHeight, 6, 6));
            }
            if (i % labelStep == 0 || i == values.length - 1 || isHighlighted(i, highlight)) {
                drawValue(g, values[i], x, padding + usableHeight + 19, barWidth);
            }
        }
        g.dispose();
    }

    private Color colorFor(int index, SortHighlight highlight, SortStatus status) {
        if (status == SortStatus.COMPLETED) {
            return UiTheme.COMPLETE;
        }
        if (highlight.swapped().contains(index)) {
            return UiTheme.SWAPPED;
        }
        if (highlight.pivotIndex() == index) {
            return UiTheme.PIVOT;
        }
        if (highlight.marked().contains(index)) {
            return UiTheme.MARKED;
        }
        if (highlight.compared().contains(index)) {
            return UiTheme.COMPARED;
        }
        return UiTheme.BAR;
    }

    private void drawValue(Graphics2D g, int value, int x, int y, int width) {
        String text = Integer.toString(value);
        g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 10f));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int labelWidth = Math.max(textWidth + 8, Math.max(width + 4, 24));
        int labelX = x + (width - labelWidth) / 2;
        int labelY = y - metrics.getAscent();
        g.setColor(new Color(83, 61, 44));
        g.fillRoundRect(labelX, labelY - 2, labelWidth, metrics.getHeight() + 3, 8, 8);
        g.setColor(new Color(255, 246, 231));
        g.drawString(text, labelX + (labelWidth - textWidth) / 2, y);
    }

    private void drawBaseline(Graphics2D g, int padding, int usableHeight) {
        int y = padding + usableHeight + 3;
        g.setColor(new Color(214, 190, 160));
        g.drawLine(padding, y, getWidth() - padding, y);
    }

    private boolean isHighlighted(int index, SortHighlight highlight) {
        return highlight.pivotIndex() == index
                || highlight.compared().contains(index)
                || highlight.marked().contains(index)
                || highlight.swapped().contains(index);
    }
}
