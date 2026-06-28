package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.ui.theme.ThemeManager;
import com.github.finley243.adventureeditor.ui.TitleBar;
import com.github.finley243.adventureeditor.ui.WindowResizeHandler;

import javax.swing.*;
import java.awt.*;

public class ThemedFrame extends JFrame {
    private final TitleBar titleBar;
    private final JPanel contentPanel;

    public ThemedFrame(String title) {
        super();
        setUndecorated(true);
        super.setLayout(new BorderLayout());
        titleBar = new TitleBar(this, title);
        super.add(titleBar, BorderLayout.NORTH);
        contentPanel = new JPanel(new BorderLayout());
        super.add(contentPanel, BorderLayout.CENTER);
        setBackground(ThemeManager.current().background());
        new WindowResizeHandler(this).install();
    }

    @Override
    public void setJMenuBar(JMenuBar menuBar) {
        if (contentPanel == null) {
            super.setJMenuBar(menuBar);
        } else {
            contentPanel.add(menuBar, BorderLayout.NORTH);
            contentPanel.revalidate();
        }
    }

    @Override
    public void add(Component comp, Object constraints) {
        if (contentPanel == null) {
            super.add(comp, constraints);
        } else {
            contentPanel.add(comp, constraints);
        }
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        if (titleBar != null) {
            titleBar.setTitle(title);
        }
    }
}