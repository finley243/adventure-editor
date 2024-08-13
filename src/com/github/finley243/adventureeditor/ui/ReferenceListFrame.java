package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.Reference;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Set;

public class ReferenceListFrame extends JDialog {

    private static final String REFERENCE_LIST_TITLE = "References";

    private final Main main;
    private final PhraseTableModel tableModel;
    private final JTable referenceTable;

    public ReferenceListFrame(Main main) {
        super(main.getBrowserFrame());
        this.main = main;
        this.setTitle(REFERENCE_LIST_TITLE);
        this.setModalityType(ModalityType.MODELESS);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        this.tableModel = new PhraseTableModel();
        tableModel.addColumn("Type");
        tableModel.addColumn("ID");

        this.referenceTable = new JTable(tableModel);
        referenceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<PhraseTableModel> sorter = new TableRowSorter<>(tableModel);
        referenceTable.setRowSorter(sorter);
        sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));

        referenceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    int viewRow = referenceTable.rowAtPoint(e.getPoint());
                    int row = referenceTable.convertRowIndexToModel(viewRow);
                    String category = (String) tableModel.getValueAt(row, 0);
                    String object = (String) tableModel.getValueAt(row, 1);
                    openReference(category, object);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(referenceTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        this.getContentPane().add(mainPanel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);

        Action closeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeEditor();
            }
        };
        Action editPhraseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = referenceTable.getSelectedRow();
                if (selectedRow != -1) {
                    String category = (String) tableModel.getValueAt(referenceTable.convertRowIndexToModel(selectedRow), 0);
                    String object = (String) tableModel.getValueAt(referenceTable.convertRowIndexToModel(selectedRow), 0);
                    openReference(category, object);
                }
            }
        };

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put("closeList", closeAction);
        actionMap.put("openReference", editPhraseAction);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeList");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openReference");

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void loadReferences(Set<Reference> references) {
        tableModel.setRowCount(0);
        for (Reference reference : references) {
            tableModel.addRow(new Object[]{reference.categoryID(), reference.objectID()});
        }
        adjustColumnWidths();
        selectRow(0);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            closeEditor();
        } else {
            super.processWindowEvent(e);
        }
    }

    private void selectRow(int viewIndex) {
        if (referenceTable.getRowCount() == 0) return;
        if (viewIndex >= referenceTable.getRowCount()) {
            referenceTable.setRowSelectionInterval(referenceTable.getRowCount() - 1, referenceTable.getRowCount() - 1);
        } else if (viewIndex <= 0) {
            referenceTable.setRowSelectionInterval(0, 0);
        }else {
            referenceTable.setRowSelectionInterval(viewIndex, viewIndex);
        }
    }

    private void closeEditor() {
        boolean didClose = main.getReferenceListManager().onCloseReferenceList();
        if (didClose) {
            this.dispose();
        }
    }

    private void openReference(String categoryID, String objectID) {
        if (categoryID.isEmpty() && objectID.equals("config")) {
            main.getConfigMenuManager().openConfigMenu();
        } else {
            main.getDataManager().editObject(categoryID, objectID);
        }
    }

    private void adjustColumnWidths() {
        TableColumnModel columnModel = referenceTable.getColumnModel();
        for (int column = 0; column < referenceTable.getColumnCount(); column++) {
            int width = 50; // Min width
            TableCellRenderer headerRenderer = referenceTable.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(referenceTable, referenceTable.getColumnName(column), false, false, 0, column);
            width = Math.max(width, headerComp.getPreferredSize().width);
            for (int row = 0; row < referenceTable.getRowCount(); row++) {
                TableCellRenderer cellRenderer = referenceTable.getCellRenderer(row, column);
                Component cellComp = cellRenderer.getTableCellRendererComponent(referenceTable, referenceTable.getValueAt(row, column), false, false, row, column);
                width = Math.max(width, cellComp.getPreferredSize().width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

}
