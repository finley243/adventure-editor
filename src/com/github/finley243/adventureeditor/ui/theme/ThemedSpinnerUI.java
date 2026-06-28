package com.github.finley243.adventureeditor.ui.theme;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

public class ThemedSpinnerUI extends BasicSpinnerUI {
    public static ComponentUI createUI(JComponent c) {
        return new ThemedSpinnerUI();
    }

    @Override
    protected Component createNextButton() {
        JButton button = (JButton) super.createNextButton();
        button.setUI(new ThemedButtonUI());
        return button;
    }

    @Override
    protected Component createPreviousButton() {
        JButton button = (JButton) super.createPreviousButton();
        button.setUI(new ThemedButtonUI());
        return button;
    }
}
