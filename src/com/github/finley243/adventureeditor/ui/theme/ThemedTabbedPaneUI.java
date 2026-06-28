package com.github.finley243.adventureeditor.ui.theme;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class ThemedTabbedPaneUI extends BasicTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new ThemedTabbedPaneUI();
    }

    @Override
    protected boolean shouldRotateTabRuns(int tabPlacement) {
        return false;
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(2, 8, 2, 8);
        selectedTabPadInsets = new Insets(2, 0, 0, 0);
        contentBorderInsets = new Insets(1, 0, 0, 0);
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h, boolean isSelected) {
        // handled in paintTabBackground
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.clipRect(x, y, w, h);
        EditorTheme theme = ThemeManager.current();
        int arc = 6;
        g2.setColor(isSelected ? theme.background() : theme.backgroundSecondary());
        g2.fillRoundRect(x, y, w, h + arc, arc, arc);
        g2.setColor(theme.border());
        g2.drawRoundRect(x, y, w - 1, h + arc - 1, arc, arc);
        g2.dispose();
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex,
                                             int x, int y, int w, int h) {
        EditorTheme theme = ThemeManager.current();
        g.setColor(theme.border());
        g.drawLine(x, y, x + w - 1, y);
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex,
                                              int x, int y, int w, int h) {
        // no border
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex,
                                               int x, int y, int w, int h) {
        // no border
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex,
                                                int x, int y, int w, int h) {
        // no border
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                       int tabIndex, Rectangle iconRect, Rectangle textRect,
                                       boolean isSelected) {
        // no focus indicator
    }
}
