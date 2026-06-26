package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.ui.TitleBar;

import javax.swing.*;
import java.awt.*;

public class ThemedDialog extends JDialog {
    private final TitleBar titleBar;
    private final JPanel contentPanel;

    public ThemedDialog(Window owner, String title) {
        super(owner);
        setModal(false);
        setUndecorated(true);
        super.setLayout(new BorderLayout());
        titleBar = new TitleBar(this, title);
        super.add(titleBar, BorderLayout.NORTH);
        contentPanel = new JPanel(new BorderLayout());
        super.add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        if (titleBar != null) {
            titleBar.setTitle(title);
        }
    }

    @Override
    public Component add(Component comp) {
        if (contentPanel == null) return super.add(comp);
        return contentPanel.add(comp);
    }

    @Override
    public void add(Component comp, Object constraints) {
        if (contentPanel == null) { super.add(comp, constraints); return; }
        contentPanel.add(comp, constraints);
    }
}
