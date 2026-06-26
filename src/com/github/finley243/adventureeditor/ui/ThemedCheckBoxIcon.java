package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import java.awt.*;

public class ThemedCheckBoxIcon implements Icon {
    private static final int SIZE = 14;
    private static final int ARC = 4;

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        AbstractButton button = (AbstractButton) c;
        boolean selected = button.isSelected();
        boolean enabled = button.isEnabled();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        EditorTheme theme = ThemeManager.current();
        if (selected) {
            g2.setColor(enabled ? theme.accent() : theme.disabledBackground());
            g2.fillRoundRect(x, y, SIZE, SIZE, ARC, ARC);
            g2.setColor(enabled ? theme.border() : theme.disabledBorder());
            g2.drawRoundRect(x, y, SIZE - 1, SIZE - 1, ARC, ARC);
            // draw X
            g2.setColor(enabled ? theme.selectionForeground() : theme.disabledForeground());
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int pad = 3;
            g2.drawLine(x + pad, y + SIZE / 2, x + SIZE / 2 - 1, y + SIZE - pad - 1);
            g2.drawLine(x + SIZE / 2 - 1, y + SIZE - pad - 1, x + SIZE - pad - 1, y + pad);
        } else {
            g2.setColor(enabled ? theme.inputBackground() : theme.disabledBackground());
            g2.fillRoundRect(x, y, SIZE, SIZE, ARC, ARC);
            g2.setColor(enabled ? theme.border() : theme.disabledBorder());
            g2.drawRoundRect(x, y, SIZE - 1, SIZE - 1, ARC, ARC);
        }
        g2.dispose();
    }

    @Override public int getIconWidth() { return SIZE; }
    @Override public int getIconHeight() { return SIZE; }
}
