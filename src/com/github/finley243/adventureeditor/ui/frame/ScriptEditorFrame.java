package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.ui.table.ScriptTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class ScriptEditorFrame extends JDialog {

    private static final String SCRIPT_EDITOR_TITLE = "Scripts";

    private final Main main;
    private final ScriptTableModel tableModel;
    private final JTable scriptTable;

    public ScriptEditorFrame(Main main) {
        super(main.getBrowserFrame());
        this.main = main;
        this.setTitle(SCRIPT_EDITOR_TITLE);
        this.setModalityType(ModalityType.MODELESS);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        this.tableModel = new ScriptTableModel();
        tableModel.addColumn("Name");

        this.scriptTable = new JTable(tableModel);
        scriptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<ScriptTableModel> sorter = new TableRowSorter<>(tableModel);
        scriptTable.setRowSorter(sorter);
        sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));

        reloadScripts();

        scriptTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    int viewRow = scriptTable.rowAtPoint(e.getPoint());
                    int row = scriptTable.convertRowIndexToModel(viewRow);
                    String scriptName = (String) tableModel.getValueAt(row, 0);
                    main.getScriptEditorManager().editScript(scriptName);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int viewRow = scriptTable.rowAtPoint(e.getPoint());
                    int row = scriptTable.convertRowIndexToModel(viewRow);
                    if (row != -1) {
                        scriptTable.setRowSelectionInterval(viewRow, viewRow);
                        openContextMenu(scriptTable, e.getPoint(), (String) tableModel.getValueAt(row, 0), viewRow);
                    }
                }
            }
        });
        adjustColumnWidths();

        JScrollPane scrollPane = new JScrollPane(scriptTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(mainPanel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);

        Action closeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeEditor();
            }
        };
        Action newPhraseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.getScriptEditorManager().newScript();
            }
        };
        Action editPhraseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = scriptTable.getSelectedRow();
                if (selectedRow != -1) {
                    String scriptName = (String) tableModel.getValueAt(scriptTable.convertRowIndexToModel(selectedRow), 0);
                    main.getScriptEditorManager().editScript(scriptName);
                }
            }
        };
        /*Action duplicatePhraseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = scriptTable.getSelectedRow();
                if (selectedRow != -1) {
                    String phraseKey = (String) tableModel.getValueAt(scriptTable.convertRowIndexToModel(selectedRow), 0);
                    main.getPhraseEditorManager().duplicatePhrase(phraseKey);
                    selectPhrase(phraseKey);
                }
            }
        };*/
        Action deletePhraseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = scriptTable.getSelectedRow();
                if (selectedRow != -1) {
                    String scriptName = (String) tableModel.getValueAt(scriptTable.convertRowIndexToModel(selectedRow), 0);
                    main.getScriptEditorManager().deleteScript(scriptName);
                    selectRow(selectedRow);
                }
            }
        };

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put("closeEditor", closeAction);
        actionMap.put("newScript", newPhraseAction);
        actionMap.put("editScript", editPhraseAction);
        //actionMap.put("duplicateScript", duplicatePhraseAction);
        actionMap.put("deleteScript", deletePhraseAction);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeEditor");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newScript");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editScript");
        //inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "duplicateScript");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteScript");

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void reloadScripts() {
        String selectedScriptName = null;
        int selectedRow = scriptTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedScriptName = (String) tableModel.getValueAt(scriptTable.convertRowIndexToModel(selectedRow), 0);
        }
        tableModel.setRowCount(0);
        Map<String, String> scripts = main.getScriptEditorManager().getScripts();
        for (String scriptName : scripts.keySet()) {
            tableModel.addRow(new Object[]{scriptName});
        }
        selectScript(selectedScriptName);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            closeEditor();
        } else {
            super.processWindowEvent(e);
        }
    }

    public void selectScript(String scriptName) {
        if (scriptName == null) return;
        for (int i = 0; i < scriptTable.getRowCount(); i++) {
            int modelIndex = scriptTable.convertRowIndexToModel(i);
            String rowKey = (String) tableModel.getValueAt(modelIndex, 0);
            if (rowKey.equals(scriptName)) {
                scriptTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private int indexOfScript(String scriptName) {
        for (int i = 0; i < scriptTable.getRowCount(); i++) {
            String rowKey = (String) tableModel.getValueAt(i, 0);
            if (rowKey.equals(scriptName)) {
                return i;
            }
        }
        return -1;
    }

    private void selectRow(int viewIndex) {
        if (scriptTable.getRowCount() == 0) return;
        if (viewIndex >= scriptTable.getRowCount()) {
            scriptTable.setRowSelectionInterval(scriptTable.getRowCount() - 1, scriptTable.getRowCount() - 1);
        } else if (viewIndex <= 0) {
            scriptTable.setRowSelectionInterval(0, 0);
        }else {
            scriptTable.setRowSelectionInterval(viewIndex, viewIndex);
        }
    }

    private void closeEditor() {
        boolean didClose = main.getScriptEditorManager().onCloseScriptEditor();
        if (didClose) {
            this.dispose();
        }
    }

    private void openContextMenu(Component component, Point point, String selectedScriptName, int viewRowIndex) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuOpen = new JMenuItem("Open");
        menuOpen.addActionListener(e -> main.getScriptEditorManager().editScript(selectedScriptName));
        menu.add(menuOpen);
        JMenuItem menuNew = new JMenuItem("New");
        menuNew.addActionListener(e -> main.getScriptEditorManager().newScript());
        menu.add(menuNew);
        /*JMenuItem menuDuplicate = new JMenuItem("Duplicate");
        menuDuplicate.addActionListener(e -> main.getPhraseEditorManager().duplicatePhrase(selectedScriptName));
        menu.add(menuDuplicate);*/
        JMenuItem menuDelete = new JMenuItem("Delete");
        menuDelete.addActionListener(e -> {
            main.getScriptEditorManager().deleteScript(selectedScriptName);
            selectRow(viewRowIndex);
        });
        menu.add(menuDelete);
        menu.show(component, point.x, point.y);
    }

    private void adjustColumnWidths() {
        TableColumnModel columnModel = scriptTable.getColumnModel();
        for (int column = 0; column < scriptTable.getColumnCount(); column++) {
            int width = 50; // Min width
            TableCellRenderer headerRenderer = scriptTable.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(scriptTable, scriptTable.getColumnName(column), false, false, 0, column);
            width = Math.max(width, headerComp.getPreferredSize().width);
            for (int row = 0; row < scriptTable.getRowCount(); row++) {
                TableCellRenderer cellRenderer = scriptTable.getCellRenderer(row, column);
                Component cellComp = cellRenderer.getTableCellRendererComponent(scriptTable, scriptTable.getValueAt(row, column), false, false, row, column);
                width = Math.max(width, cellComp.getPreferredSize().width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

}
