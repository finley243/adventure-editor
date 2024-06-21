package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class PhraseEditorFrame extends JFrame {

    private static final String PHRASE_EDITOR_TITLE = "Phrases";

    private final Main main;
    private final PhraseTableModel tableModel;
    private final JTable phraseTable;

    public PhraseEditorFrame(Main main) {
        super(PHRASE_EDITOR_TITLE);
        this.main = main;
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        this.tableModel = new PhraseTableModel();
        tableModel.addColumn("Key");
        tableModel.addColumn("Phrase");

        this.phraseTable = new JTable(tableModel);
        phraseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<PhraseTableModel> sorter = new TableRowSorter<>(tableModel);
        phraseTable.setRowSorter(sorter);
        sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));

        reloadPhrases();

        phraseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    int viewRow = phraseTable.rowAtPoint(e.getPoint());
                    int row = phraseTable.convertRowIndexToModel(viewRow);
                    String phraseKey = (String) tableModel.getValueAt(row, 0);
                    main.getPhraseEditorManager().editPhrase(phraseKey);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int viewRow = phraseTable.rowAtPoint(e.getPoint());
                    int row = phraseTable.convertRowIndexToModel(viewRow);
                    if (row != -1) {
                        phraseTable.setRowSelectionInterval(viewRow, viewRow);
                        openContextMenu(phraseTable, e.getPoint(), (String) tableModel.getValueAt(row, 0), viewRow);
                    }
                }
            }
        });
        adjustColumnWidths();

        JScrollPane scrollPane = new JScrollPane(phraseTable);
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
                main.getPhraseEditorManager().newPhrase();
            }
        };
        Action editPhraseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = phraseTable.getSelectedRow();
                if (selectedRow != -1) {
                    String phraseKey = (String) tableModel.getValueAt(phraseTable.convertRowIndexToModel(selectedRow), 0);
                    main.getPhraseEditorManager().editPhrase(phraseKey);
                }
            }
        };
        Action duplicatePhraseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = phraseTable.getSelectedRow();
                if (selectedRow != -1) {
                    String phraseKey = (String) tableModel.getValueAt(phraseTable.convertRowIndexToModel(selectedRow), 0);
                    main.getPhraseEditorManager().duplicatePhrase(phraseKey);
                    selectPhrase(phraseKey);
                }
            }
        };
        Action deletePhraseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = phraseTable.getSelectedRow();
                if (selectedRow != -1) {
                    String phraseKey = (String) tableModel.getValueAt(phraseTable.convertRowIndexToModel(selectedRow), 0);
                    main.getPhraseEditorManager().deletePhrase(phraseKey);
                    selectRow(selectedRow);
                }
            }
        };

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put("closeEditor", closeAction);
        actionMap.put("newPhrase", newPhraseAction);
        actionMap.put("editPhrase", editPhraseAction);
        actionMap.put("duplicatePhrase", duplicatePhraseAction);
        actionMap.put("deletePhrase", deletePhraseAction);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeEditor");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newPhrase");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editPhrase");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "duplicatePhrase");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deletePhrase");

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void reloadPhrases() {
        String selectedPhraseKey = null;
        int selectedRow = phraseTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedPhraseKey = (String) tableModel.getValueAt(phraseTable.convertRowIndexToModel(selectedRow), 0);
        }
        tableModel.setRowCount(0);
        Map<String, String> phrases = main.getPhraseEditorManager().getPhrases();
        for (String phraseKey : phrases.keySet()) {
            tableModel.addRow(new Object[]{phraseKey, phrases.get(phraseKey)});
        }
        selectPhrase(selectedPhraseKey);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            closeEditor();
        } else {
            super.processWindowEvent(e);
        }
    }

    public void selectPhrase(String phraseKey) {
        if (phraseKey == null) return;
        for (int i = 0; i < phraseTable.getRowCount(); i++) {
            int modelIndex = phraseTable.convertRowIndexToModel(i);
            String rowKey = (String) tableModel.getValueAt(modelIndex, 0);
            if (rowKey.equals(phraseKey)) {
                phraseTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private int indexOfPhrase(String phraseKey) {
        for (int i = 0; i < phraseTable.getRowCount(); i++) {
            String rowKey = (String) tableModel.getValueAt(i, 0);
            if (rowKey.equals(phraseKey)) {
                return i;
            }
        }
        return -1;
    }

    private void selectRow(int viewIndex) {
        if (phraseTable.getRowCount() == 0) return;
        if (viewIndex >= phraseTable.getRowCount()) {
            phraseTable.setRowSelectionInterval(phraseTable.getRowCount() - 1, phraseTable.getRowCount() - 1);
        } else if (viewIndex <= 0) {
            phraseTable.setRowSelectionInterval(0, 0);
        }else {
            phraseTable.setRowSelectionInterval(viewIndex, viewIndex);
        }
    }

    private void closeEditor() {
        boolean didClose = main.getPhraseEditorManager().onClosePhraseEditor();
        if (didClose) {
            this.dispose();
        }
    }

    private void openContextMenu(Component component, Point point, String selectedPhraseKey, int viewRowIndex) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuOpen = new JMenuItem("Open");
        menuOpen.addActionListener(e -> main.getPhraseEditorManager().editPhrase(selectedPhraseKey));
        menu.add(menuOpen);
        JMenuItem menuNew = new JMenuItem("New");
        menuNew.addActionListener(e -> {
            main.getPhraseEditorManager().newPhrase();
        });
        menu.add(menuNew);
        JMenuItem menuDuplicate = new JMenuItem("Duplicate");
        menuDuplicate.addActionListener(e -> main.getPhraseEditorManager().duplicatePhrase(selectedPhraseKey));
        menu.add(menuDuplicate);
        JMenuItem menuDelete = new JMenuItem("Delete");
        menuDelete.addActionListener(e -> {
            main.getPhraseEditorManager().deletePhrase(selectedPhraseKey);
            selectRow(viewRowIndex);
        });
        menu.add(menuDelete);
        menu.show(component, point.x, point.y);
    }

    private void adjustColumnWidths() {
        TableColumnModel columnModel = phraseTable.getColumnModel();
        for (int column = 0; column < phraseTable.getColumnCount(); column++) {
            int width = 50; // Min width
            TableCellRenderer headerRenderer = phraseTable.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(phraseTable, phraseTable.getColumnName(column), false, false, 0, column);
            width = Math.max(width, headerComp.getPreferredSize().width);
            for (int row = 0; row < phraseTable.getRowCount(); row++) {
                TableCellRenderer cellRenderer = phraseTable.getCellRenderer(row, column);
                Component cellComp = cellRenderer.getTableCellRendererComponent(phraseTable, phraseTable.getValueAt(row, column), false, false, row, column);
                width = Math.max(width, cellComp.getPreferredSize().width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

}
