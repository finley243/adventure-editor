package com.github.finley243.adventureeditor.ui.table;

import javax.swing.table.DefaultTableModel;

public class PhraseTableModel extends DefaultTableModel {

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
