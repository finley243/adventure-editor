package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataStringSet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ParameterFieldStringSet extends EditorElement {

    private final JList<String> textList;
    private final JButton buttonAdd;
    private final JButton buttonRemove;

    public ParameterFieldStringSet(EditorFrame editorFrame, boolean optional, String name) {
        super(editorFrame, optional, name);
        getInnerPanel().setLayout(new GridBagLayout());
        JLabel label = new JLabel(name);
        this.textList = new JList<>();
        // May not be the ideal listener type
        textList.addListSelectionListener(e -> editorFrame.onEditorElementUpdated());
        JScrollPane scrollPane = new JScrollPane(textList);
        this.buttonAdd = new JButton("+");
        this.buttonRemove = new JButton("-");
        //textList.setPreferredSize(new Dimension(150, 100));
        scrollPane.setPreferredSize(new Dimension(150, 100));
        textList.setModel(new DefaultListModel<>());
        textList.setDragEnabled(false);
        textList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        GridBagConstraints labelConstraints = new GridBagConstraints();
        GridBagConstraints valueConstraints = new GridBagConstraints();
        GridBagConstraints addConstraints = new GridBagConstraints();
        GridBagConstraints removeConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        labelConstraints.insets = new Insets(2, 2, 2, 2);
        valueConstraints.gridx = 0;
        valueConstraints.gridy = 1;
        valueConstraints.gridwidth = 2;
        addConstraints.gridx = 0;
        addConstraints.gridy = 2;
        addConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        removeConstraints.gridx = 1;
        removeConstraints.gridy = 2;
        removeConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        buttonAdd.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("New value:");
            int addIndex = textList.getSelectedIndex() + 1;
            if (addIndex == 0) {
                addIndex = textList.getModel().getSize();
            }
            ((DefaultListModel<String>) textList.getModel()).add(addIndex, input);
            editorFrame.onEditorElementUpdated();
        });
        buttonRemove.addActionListener(e -> {
            int selectedIndex = textList.getSelectedIndex();
            if (selectedIndex != -1) {
                ((DefaultListModel<String>) textList.getModel()).removeElementAt(selectedIndex);
                if (textList.getModel().getSize() > selectedIndex) {
                    textList.setSelectedIndex(selectedIndex);
                } else if (textList.getModel().getSize() == selectedIndex) {
                    textList.setSelectedIndex(selectedIndex - 1);
                }
                editorFrame.onEditorElementUpdated();
            }
        });
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        getInnerPanel().add(label, labelConstraints);
        getInnerPanel().add(scrollPane, valueConstraints);
        getInnerPanel().add(buttonAdd, addConstraints);
        getInnerPanel().add(buttonRemove, removeConstraints);
    }

    public List<String> getValue() {
        List<String> test = new ArrayList<>();
        for (int i = 0; i < textList.getModel().getSize(); i++) {
            test.add(textList.getModel().getElementAt(i));
        }
        return test;
    }

    public void setValue(List<String> value) {
        ((DefaultListModel<String>) textList.getModel()).clear();
        ((DefaultListModel<String>) textList.getModel()).addAll(value);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        if (!enabled) {
            textList.setSelectedIndex(-1);
        }
        textList.setEnabled(enabled);
        buttonAdd.setEnabled(enabled);
        buttonRemove.setEnabled(enabled);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        return new DataStringSet(getValue());
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataStringSet dataStringSet) {
            setValue(dataStringSet.getValue());
        }
    }

}
