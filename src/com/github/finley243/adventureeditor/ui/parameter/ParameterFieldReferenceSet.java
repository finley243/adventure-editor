package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataReferenceSet;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.EditorFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ParameterFieldReferenceSet extends ParameterField implements DataSaveTarget {

    private final Main main;
    private final JList<String> referenceList;
    private final JButton buttonAdd;
    private final JButton buttonEdit;
    private final JButton buttonRemove;

    private final String name;

    public ParameterFieldReferenceSet(EditorFrame editorFrame, boolean optional, String name, Template template, Main main) {
        super(editorFrame, optional, name);
        this.main = main;
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.name = name;
        getInnerPanel().setLayout(new GridBagLayout());
        JComponent label;
        if (optional) {
            label = getOptionalCheckbox();
        } else {
            label = new JLabel(name);
        }
        this.referenceList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(referenceList);
        this.buttonAdd = new JButton("New");
        this.buttonEdit = new JButton("Edit");
        this.buttonRemove = new JButton("Remove");
        scrollPane.setPreferredSize(new Dimension(150, 100));
        referenceList.setModel(new DefaultListModel<>());
        referenceList.setDragEnabled(false);
        referenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // May not be the ideal listener to use
        referenceList.addListSelectionListener(e -> editorFrame.onEditorElementUpdated());
        GridBagConstraints labelConstraints = new GridBagConstraints();
        GridBagConstraints valueConstraints = new GridBagConstraints();
        GridBagConstraints addConstraints = new GridBagConstraints();
        GridBagConstraints editConstraints = new GridBagConstraints();
        GridBagConstraints removeConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.gridwidth = 3;
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        labelConstraints.insets = new Insets(2, 2, 2, 2);
        valueConstraints.gridx = 0;
        valueConstraints.gridy = 1;
        valueConstraints.gridwidth = 3;
        valueConstraints.weightx = 1;
        valueConstraints.weighty = 1;
        valueConstraints.fill = GridBagConstraints.BOTH;
        addConstraints.gridx = 0;
        addConstraints.gridy = 2;
        addConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        editConstraints.gridx = 1;
        editConstraints.gridy = 2;
        editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        removeConstraints.gridx = 2;
        removeConstraints.gridy = 2;
        removeConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        buttonEdit.setEnabled(false);
        buttonRemove.setEnabled(false);
        referenceList.addListSelectionListener(e -> {
            boolean enableSelectionButtons = referenceList.getSelectedIndex() != -1;
            buttonEdit.setEnabled(enableSelectionButtons);
            buttonRemove.setEnabled(enableSelectionButtons);
        });
        referenceList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = referenceList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedItem = referenceList.getModel().getElementAt(index);
                        if (selectedItem != null) {
                            main.getDataManager().editObject(template.id(), selectedItem, ParameterFieldReferenceSet.this);
                        }
                    }
                }
            }
        });
        buttonAdd.addActionListener(e -> {
            main.getDataManager().newObject(template.id(), this);
        });
        buttonEdit.addActionListener(e -> {
            main.getDataManager().editObject(template.id(), referenceList.getSelectedValue(), this);
        });
        buttonRemove.addActionListener(e -> {
            int selectedIndex = referenceList.getSelectedIndex();
            boolean didDelete = main.getDataManager().deleteObject(template.id(), referenceList.getSelectedValue());
            if (didDelete) {
                ((DefaultListModel<String>) referenceList.getModel()).remove(selectedIndex);
            }
        });
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        getInnerPanel().add(label, labelConstraints);
        getInnerPanel().add(scrollPane, valueConstraints);
        getInnerPanel().add(buttonAdd, addConstraints);
        getInnerPanel().add(buttonEdit, editConstraints);
        getInnerPanel().add(buttonRemove, removeConstraints);
        if (optional) {
            setEnabledState(false);
        }
    }

    public List<String> getValue() {
        List<String> test = new ArrayList<>();
        for (int i = 0; i < referenceList.getModel().getSize(); i++) {
            test.add(referenceList.getModel().getElementAt(i));
        }
        return test;
    }

    public void setValue(List<String> value) {
        ((DefaultListModel<String>) referenceList.getModel()).clear();
        ((DefaultListModel<String>) referenceList.getModel()).addAll(value);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        if (!enabled) {
            referenceList.setSelectedIndex(-1);
        }
        referenceList.setEnabled(enabled);
        if (enabled) {
            buttonAdd.setEnabled(true);
            boolean enableSelectionButtons = referenceList.getSelectedIndex() != -1;
            buttonEdit.setEnabled(enableSelectionButtons);
            buttonRemove.setEnabled(enableSelectionButtons);
        } else {
            buttonAdd.setEnabled(false);
            buttonEdit.setEnabled(false);
            buttonRemove.setEnabled(false);
        }
    }

    @Override
    public void saveObjectData(Data data, Data initialData) {
        main.getMainFrame().saveObjectData(data, initialData);
        int addIndex = referenceList.getSelectedIndex() + 1;
        if (addIndex == 0) {
            addIndex = referenceList.getModel().getSize();
        }
        String objectID = ((DataObject) data).getID();
        if (initialData != null) {
            String initialID = ((DataObject) initialData).getID();
            addIndex = ((DefaultListModel<String>) referenceList.getModel()).indexOf(initialID);
            ((DefaultListModel<String>) referenceList.getModel()).remove(addIndex);
        }
        ((DefaultListModel<String>) referenceList.getModel()).add(addIndex, objectID);
        parentFrame.onEditorElementUpdated();
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        main.getMainFrame().onEditorFrameClose(frame);
    }

    @Override
    public ErrorData isDataValidOrShowDialog(Data currentData, Data initialData) {
        return main.getMainFrame().isDataValidOrShowDialog(currentData, initialData);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return new DataReferenceSet(new ArrayList<>());
        }
        List<String> objectData = new ArrayList<>(getValue());
        return new DataReferenceSet(objectData);
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataReferenceSet dataReferenceSet) {
            List<String> objectData = dataReferenceSet.getValue();
            if (objectData.isEmpty()) {
                setOptionalEnabled(false);
            }
            setValue(objectData);
        }
    }

}
