package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.validation.*;
import com.github.finley243.adventureeditor.ui.table.PhraseTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Consumer;

public class ErrorListFrame extends ThemedDialog {

    private static final String ERROR_LIST_TITLE = "Project Errors";

    private final PhraseTableModel tableModel;
    private final JTable errorTable;
    private final Consumer<ValidationIssue> onOpenIssue;
    private final Runnable onClose;

    private List<ValidationIssue> currentIssues;

    public ErrorListFrame(Window parentWindow, Consumer<ValidationIssue> onOpenIssue, Runnable onClose) {
        super(parentWindow, ERROR_LIST_TITLE);
        this.onOpenIssue = onOpenIssue;
        this.onClose = onClose;
        this.setTitle(ERROR_LIST_TITLE);
        this.setModalityType(ModalityType.MODELESS);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        this.tableModel = new PhraseTableModel();
        tableModel.addColumn("Severity");
        tableModel.addColumn("Type");
        tableModel.addColumn("Key");
        tableModel.addColumn("Message");

        this.errorTable = new JTable(tableModel);
        errorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumnModel columnModel = errorTable.getColumnModel();
        columnModel.getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && value == ValidationSeverity.WARNING) {
                    c.setForeground(Color.ORANGE.darker());
                } else if (!isSelected && value == ValidationSeverity.ERROR) {
                    c.setForeground(Color.RED);
                } else if (!isSelected) {
                    c.setForeground(table.getForeground());
                }
                return c;
            }
        });

        TableRowSorter<PhraseTableModel> sorter = new TableRowSorter<>(tableModel);
        errorTable.setRowSorter(sorter);
        sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));

        errorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    openSelectedIssue();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(errorTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        this.getContentPane().add(mainPanel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);

        Action closeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeEditor();
            }
        };
        Action openIssueAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSelectedIssue();
            }
        };

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put("closeList", closeAction);
        actionMap.put("openIssue", openIssueAction);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeList");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openIssue");

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void loadIssues(List<ValidationIssue> issues) {
        this.currentIssues = issues;
        tableModel.setRowCount(0);
        for (ValidationIssue issue : issues) {
            tableModel.addRow(new Object[]{issue.severity(), typeLabel(issue), keyLabel(issue), issue.message()});
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

    private void openSelectedIssue() {
        int viewRow = errorTable.getSelectedRow();
        if (viewRow == -1) return;
        int modelRow = errorTable.convertRowIndexToModel(viewRow);
        onOpenIssue.accept(currentIssues.get(modelRow));
    }

    private String typeLabel(ValidationIssue issue) {
        return switch (issue) {
            case ObjectValidationIssue oi -> "Object (" + oi.categoryID() + ")";
            case PhraseValidationIssue pi -> "Phrase";
            case ConfigValidationIssue ci -> "Config";
            case ScriptValidationIssue sci -> "Script";
        };
    }

    private String keyLabel(ValidationIssue issue) {
        return switch (issue) {
            case ObjectValidationIssue oi -> oi.key();
            case PhraseValidationIssue pi -> pi.key();
            case ConfigValidationIssue ci -> "";
            case ScriptValidationIssue sci -> sci.scriptName();
        };
    }

    private void selectRow(int viewIndex) {
        if (errorTable.getRowCount() == 0) return;
        if (viewIndex >= errorTable.getRowCount()) {
            errorTable.setRowSelectionInterval(errorTable.getRowCount() - 1, errorTable.getRowCount() - 1);
        } else if (viewIndex <= 0) {
            errorTable.setRowSelectionInterval(0, 0);
        } else {
            errorTable.setRowSelectionInterval(viewIndex, viewIndex);
        }
    }

    private void closeEditor() {
        onClose.run();
        this.dispose();
    }

    private void adjustColumnWidths() {
        TableColumnModel columnModel = errorTable.getColumnModel();
        for (int column = 0; column < errorTable.getColumnCount(); column++) {
            int width = 50;
            TableCellRenderer headerRenderer = errorTable.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(errorTable, errorTable.getColumnName(column), false, false, 0, column);
            width = Math.max(width, headerComp.getPreferredSize().width);
            for (int row = 0; row < errorTable.getRowCount(); row++) {
                TableCellRenderer cellRenderer = errorTable.getCellRenderer(row, column);
                Component cellComp = cellRenderer.getTableCellRendererComponent(errorTable, errorTable.getValueAt(row, column), false, false, row, column);
                width = Math.max(width, cellComp.getPreferredSize().width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

}