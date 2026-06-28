package com.github.finley243.adventureeditor.ui.theme;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ThemedTitledBorder extends TitledBorder {
    public ThemedTitledBorder(String title) {
        super(title);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        setBorder(BorderFactory.createLineBorder(ThemeManager.current().border(), 1));
        setTitleColor(ThemeManager.current().foreground());
        setTitleFont(UIManager.getFont("Label.font"));
        super.paintBorder(c, g, x, y, width, height);
    }
}
