package com.github.finley243.adventureeditor.ui.theme;

import javax.swing.*;
import java.awt.*;

public class ThemedSortIcon implements Icon {
    private static final int WIDTH = 8;
    private static final int HEIGHT = 6;
    private final boolean ascending;

    public ThemedSortIcon(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeManager.current().foreground());
        int[] xPoints, yPoints;
        if (ascending) {
            xPoints = new int[]{x, x + WIDTH / 2, x + WIDTH};
            yPoints = new int[]{y + HEIGHT, y, y + HEIGHT};
        } else {
            xPoints = new int[]{x, x + WIDTH / 2, x + WIDTH};
            yPoints = new int[]{y, y + HEIGHT, y};
        }
        g2.fillPolygon(xPoints, yPoints, 3);
        g2.dispose();
    }

    @Override public int getIconWidth() { return WIDTH; }
    @Override public int getIconHeight() { return HEIGHT; }
}
