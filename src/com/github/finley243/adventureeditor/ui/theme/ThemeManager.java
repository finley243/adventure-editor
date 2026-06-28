package com.github.finley243.adventureeditor.ui.theme;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    private static EditorTheme currentTheme = new LightTheme();

    public static EditorTheme current() {
        return currentTheme;
    }

    public static void setTheme(EditorTheme theme) {
        currentTheme = theme;
        applyTheme();
    }

    private static void applyTheme() {
        EditorTheme t = currentTheme;
        UIManager.put("Panel.background", t.background());
        UIManager.put("OptionPane.background", t.background());
        UIManager.put("Label.foreground", t.foreground());
        UIManager.put("Button.background", t.buttonBackground());
        UIManager.put("Button.foreground", t.foreground());
        UIManager.put("TextField.background", t.inputBackground());
        UIManager.put("TextField.foreground", t.foreground());
        UIManager.put("TextField.caretForeground", t.foreground());
        UIManager.put("TextArea.background", t.inputBackground());
        UIManager.put("TextArea.foreground", t.foreground());
        UIManager.put("ComboBox.background", t.inputBackground());
        UIManager.put("ComboBox.foreground", t.foreground());
        UIManager.put("ComboBox.selectionBackground", t.selectionBackground());
        UIManager.put("ComboBox.selectionForeground", t.selectionForeground());
        UIManager.put("CheckBox.foreground", t.foreground());
        UIManager.put("Tree.background", t.background());
        UIManager.put("Tree.foreground", t.foreground());
        UIManager.put("Tree.selectionBackground", t.selectionBackground());
        UIManager.put("Tree.selectionForeground", t.selectionForeground());
        UIManager.put("ScrollPane.background", t.background());
        UIManager.put("TabbedPane.background", t.background());
        UIManager.put("TabbedPane.foreground", t.foreground());
        UIManager.put("TabbedPane.selected", t.backgroundSecondary());
        UIManager.put("MenuBar.background", t.background());
        UIManager.put("Menu.background", t.background());
        UIManager.put("Menu.foreground", t.foreground());
        UIManager.put("MenuItem.background", t.background());
        UIManager.put("MenuItem.foreground", t.foreground());
        UIManager.put("MenuItem.selectionBackground", t.selectionBackground());
        UIManager.put("MenuItem.selectionForeground", t.selectionForeground());
        UIManager.put("CheckBoxMenuItem.selectionBackground", t.selectionBackground());
        UIManager.put("CheckBoxMenuItem.selectionForeground", t.selectionForeground());
        UIManager.put("RadioButtonMenuItem.selectionBackground", t.selectionBackground());
        UIManager.put("RadioButtonMenuItem.selectionForeground", t.selectionForeground());
        UIManager.put("Separator.foreground", t.border());
        UIManager.put("Separator.background", t.background());
        UIManager.put("Menu.selectionBackground", t.selectionBackground());
        UIManager.put("Menu.selectionForeground", t.selectionForeground());
        UIManager.put("PopupMenu.background", t.background());
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(t.border(), 1));
        UIManager.put("SplitPane.background", t.background());
        UIManager.put("CheckBox.background", t.background());
        UIManager.put("CheckBox.foreground", t.foreground());
        UIManager.put("List.background", t.inputBackground());
        UIManager.put("List.foreground", t.foreground());
        UIManager.put("List.selectionBackground", t.selectionBackground());
        UIManager.put("List.selectionForeground", t.selectionForeground());
        UIManager.put("Tree.textBackground", t.background());
        UIManager.put("Tree.textForeground", t.foreground());
        UIManager.put("Tree.selectionBorderColor", t.selectionBackground());
        UIManager.put("Tree.lineColor", t.border());
        UIManager.put("OptionPane.messageForeground", t.foreground());
        UIManager.put("Viewport.background", t.background());

        UIManager.put("Table.background", t.background());
        UIManager.put("Table.foreground", t.foreground());
        UIManager.put("Table.selectionBackground", t.selectionBackground());
        UIManager.put("Table.selectionForeground", t.selectionForeground());
        UIManager.put("Table.gridColor", t.border());
        UIManager.put("Table.focusCellBackground", t.selectionBackground());
        UIManager.put("Table.focusCellForeground", t.selectionForeground());
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
        UIManager.put("TableHeader.background", t.backgroundSecondary());
        UIManager.put("TableHeader.foreground", t.foreground());
        UIManager.put("TableHeader.cellBorder", BorderFactory.createMatteBorder(0, 0, 1, 1, t.border()));

        UIManager.put("Table.ascendingSortIcon", new ThemedSortIcon(true));
        UIManager.put("Table.descendingSortIcon", new ThemedSortIcon(false));

        UIManager.put("MenuBar.border", BorderFactory.createMatteBorder(0, 0, 1, 0, t.border()));
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.border(), 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        UIManager.put("Spinner.background", t.inputBackground());
        UIManager.put("Spinner.foreground", t.foreground());
        UIManager.put("Spinner.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.border(), 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        UIManager.put("FormattedTextField.background", t.inputBackground());
        UIManager.put("FormattedTextField.foreground", t.foreground());
        UIManager.put("FormattedTextField.border", BorderFactory.createEmptyBorder(0, 0, 0, 0));
        UIManager.put("ComboBox.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.border(), 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        UIManager.put("List.border", BorderFactory.createLineBorder(t.border(), 1));
        //UIManager.put("List.border", BorderFactory.createEmptyBorder());
        UIManager.put("List.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(t.border(), 1));
        UIManager.put("MenuItem.border", BorderFactory.createEmptyBorder(2, 4, 2, 4));
        UIManager.put("Menu.border", BorderFactory.createEmptyBorder(2, 4, 2, 4));
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(t.border(), 1));

        UIManager.put("CheckBox.icon", new ThemedCheckBoxIcon());
        UIManager.put("CheckBox.focus", new Color(0, 0, 0, 0));

        UIManager.put("Label.disabledForeground", t.disabledForeground());
        UIManager.put("TextField.disabledBackground", t.disabledBackground());
        UIManager.put("TextField.disabledForeground", t.disabledForeground());
        UIManager.put("FormattedTextField.disabledBackground", t.disabledBackground());
        UIManager.put("FormattedTextField.disabledForeground", t.disabledForeground());
        UIManager.put("ComboBox.disabledBackground", t.disabledBackground());
        UIManager.put("ComboBox.disabledForeground", t.disabledForeground());
        UIManager.put("Spinner.disabledBackground", t.disabledBackground());
        UIManager.put("Spinner.disabledForeground", t.disabledForeground());
        UIManager.put("CheckBox.disabledText", t.disabledForeground());
        UIManager.put("Button.disabledText", t.disabledForeground());
        UIManager.put("Button.disabled", t.disabledBackground());
        UIManager.put("TextArea.disabledBackground", t.disabledBackground());
        UIManager.put("TextArea.disabledForeground", t.disabledForeground());
        UIManager.put("List.disabledBackground", t.disabledBackground());
        UIManager.put("List.disabledForeground", t.disabledForeground());
        UIManager.put("TextField.inactiveBackground", t.disabledBackground());
        UIManager.put("TextField.inactiveForeground", t.disabledForeground());
        UIManager.put("FormattedTextField.inactiveBackground", t.disabledBackground());
        UIManager.put("FormattedTextField.inactiveForeground", t.disabledForeground());
        UIManager.put("List.disabledCellBackground", t.disabledBackground());
        UIManager.put("List.disabledCellForeground", t.disabledForeground());

        UIManager.put("ScrollBarUI", ThemedScrollBarUI.class.getName());
        UIManager.put("ComboBoxUI", ThemedComboBoxUI.class.getName());
        UIManager.put("ButtonUI", ThemedButtonUI.class.getName());
        UIManager.put("TabbedPaneUI", ThemedTabbedPaneUI.class.getName());
        UIManager.put("SpinnerUI", ThemedSpinnerUI.class.getName());
        UIManager.put("TreeUI", ThemedTreeUI.class.getName());

        // repaint all open windows
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

}
