package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class ThemedTreeUI extends BasicTreeUI {

    public static ComponentUI createUI(JComponent c) {
        return new ThemedTreeUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        // Override icons set by Metal
        setExpandedIcon(createExpandIcon(true));
        setCollapsedIcon(createExpandIcon(false));
        tree.putClientProperty("JTree.lineStyle", "None");
    }

    @Override
    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
        g.setColor(ThemeManager.current().border());
        drawDashedVerticalLine(g, x, top, bottom);
    }

    @Override
    protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
        g.setColor(ThemeManager.current().border());
        drawDashedHorizontalLine(g, y, left, right);
    }

    private Icon createExpandIcon(boolean expanded) {
        return new Icon() {
            private static final int SIZE = 9;

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                EditorTheme theme = ThemeManager.current();
                g2.setColor(theme.background());
                g2.fillRect(x, y, SIZE, SIZE);
                g2.setColor(theme.border());
                g2.drawRect(x, y, SIZE - 1, SIZE - 1);
                g2.setColor(theme.foreground());
                // horizontal bar
                g2.drawLine(x + 2, y + SIZE / 2, x + SIZE - 3, y + SIZE / 2);
                if (!expanded) {
                    // vertical bar for collapsed
                    g2.drawLine(x + SIZE / 2, y + 2, x + SIZE / 2, y + SIZE - 3);
                }
                g2.dispose();
            }

            @Override public int getIconWidth() { return SIZE; }
            @Override public int getIconHeight() { return SIZE; }
        };
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JTree t = (JTree) c;
        TreeCellRenderer renderer = t.getCellRenderer();
        if (renderer instanceof DefaultTreeCellRenderer r) {
            r.setLeafIcon(null);
            r.setOpenIcon(null);
            r.setClosedIcon(null);
        }
    }
}
