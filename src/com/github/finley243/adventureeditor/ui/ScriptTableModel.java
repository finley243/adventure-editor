package com.github.finley243.adventureeditor.ui;

import javax.swing.table.DefaultTableModel;

public class ScriptTableModel extends DefaultTableModel {

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
