package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;

public class ThemedButtonUI extends MetalButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new ThemedButtonUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = c.getWidth();
        int height = c.getHeight();
        int arc = 6;

        EditorTheme theme = ThemeManager.current();
        Color bg;
        if (!button.isEnabled()) {
            bg = theme.disabledBackground();
        } else if (model.isPressed()) {
            bg = theme.buttonPressed();
        } else if (model.isRollover()) {
            bg = theme.buttonHover();
        } else {
            bg = theme.buttonBackground();
        }

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, width, height, arc, arc);

        Color borderColor = !button.isEnabled() ? theme.disabledBorder() : theme.buttonBorder();
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

        g2.dispose();
        super.paint(g, c);
    }

    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        // optional: draw a subtle focus ring
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeManager.current().accent());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(2, 2, b.getWidth() - 5, b.getHeight() - 5, 6, 6);
        g2.dispose();
    }

}
