package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalComboBoxUI;
import java.awt.*;

public class ThemedComboBoxUI extends MetalComboBoxUI {
    public static ComponentUI createUI(JComponent c) {
        return new ThemedComboBoxUI();
    }

    @Override
    protected ComboBoxEditor createEditor() {
        ComboBoxEditor editor = super.createEditor();
        if (editor.getEditorComponent() instanceof JTextField tf) {
            tf.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        }
        return editor;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        for (Component child : c.getComponents()) {
            if (child instanceof JComponent jc) {
                jc.setBorder(BorderFactory.createEmptyBorder());
            }
        }
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.current().border(), 1),
                BorderFactory.createEmptyBorder(1, 2, 1, 2)
        ));
    }

    @Override
    public Insets getInsets() {
        return new Insets(1, 2, 1, 2);
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        g.setColor(ThemeManager.current().inputBackground());
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    protected ListCellRenderer createRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                return this;
            }
        };
    }
}
