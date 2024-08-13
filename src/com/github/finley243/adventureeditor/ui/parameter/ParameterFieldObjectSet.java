package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObjectSet;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.EditorFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ParameterFieldObjectSet extends ParameterField implements DataSaveTarget {

    private final JList<Data> objectList;
    private final JButton buttonAdd;
    private final JButton buttonEdit;
    private final JButton buttonRemove;

    private final List<EditorFrame> editorFrames;
    private final List<EditorFrame> unsavedEditorFrames;
    private final String name;
    private final Template template;
    private final boolean requireUniqueValues;

    public ParameterFieldObjectSet(EditorFrame editorFrame, boolean optional, String name, ParameterField parentField, Template template, boolean requireUniqueValues, Main main) {
        super(editorFrame, optional, name, parentField);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.editorFrames = new ArrayList<>();
        this.unsavedEditorFrames = new ArrayList<>();
        this.name = name;
        this.template = template;
        this.requireUniqueValues = requireUniqueValues;
        getInnerPanel().setLayout(new GridBagLayout());
        JComponent label;
        if (optional) {
            label = getOptionalCheckbox();
        } else {
            label = new JLabel(name);
        }
        this.objectList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(objectList);
        this.buttonAdd = new JButton("New");
        this.buttonEdit = new JButton("Edit");
        this.buttonRemove = new JButton("Remove");
        scrollPane.setPreferredSize(new Dimension(150, 100));
        objectList.setModel(new DefaultListModel<>());
        objectList.setDragEnabled(false);
        objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // May not be the ideal listener to use
        objectList.addListSelectionListener(e -> onFieldUpdated());
        objectList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = objectList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Data selectedItem = objectList.getModel().getElementAt(index);
                        if (selectedItem != null) {
                            if (editorFrames.get(index) != null) {
                                editorFrames.get(index).toFront();
                                editorFrames.get(index).requestFocus();
                            } else {
                                EditorFrame objectFrame = new EditorFrame(main, editorFrame, template, selectedItem, ParameterFieldObjectSet.this, false);
                                editorFrames.set(index, objectFrame);
                            }
                        }
                    }
                }
            }
        });
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
        objectList.addListSelectionListener(e -> {
            boolean enableSelectionButtons = objectList.getSelectedIndex() != -1;
            buttonEdit.setEnabled(enableSelectionButtons);
            buttonRemove.setEnabled(enableSelectionButtons);
        });
        buttonAdd.addActionListener(e -> {
            EditorFrame objectFrame = new EditorFrame(main, editorFrame, template, null, this, false);
            unsavedEditorFrames.add(objectFrame);
        });
        buttonEdit.addActionListener(e -> {
            Data objectData = objectList.getSelectedValue();
            int objectIndex = objectList.getSelectedIndex();
            if (objectData != null) {
                if (editorFrames.get(objectIndex) != null) {
                    editorFrames.get(objectIndex).toFront();
                    editorFrames.get(objectIndex).requestFocus();
                } else {
                    EditorFrame objectFrame = new EditorFrame(main, editorFrame, template, objectData, this, false);
                    editorFrames.set(objectIndex, objectFrame);
                }
            }
        });
        buttonRemove.addActionListener(e -> {
            int selectedIndex = objectList.getSelectedIndex();
            if (selectedIndex != -1) {
                if (editorFrames.get(selectedIndex) != null) {
                    boolean didClose = editorFrames.get(selectedIndex).requestClose(false, false);
                    if (!didClose) {
                        return;
                    }
                }
                ((DefaultListModel<Data>) objectList.getModel()).removeElementAt(selectedIndex);
                editorFrames.remove(selectedIndex);
                if (objectList.getModel().getSize() > selectedIndex) {
                    objectList.setSelectedIndex(selectedIndex);
                } else if (objectList.getModel().getSize() == selectedIndex) {
                    objectList.setSelectedIndex(selectedIndex - 1);
                }
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

    public java.util.List<Data> getValue() {
        java.util.List<Data> test = new ArrayList<>();
        for (int i = 0; i < objectList.getModel().getSize(); i++) {
            test.add(objectList.getModel().getElementAt(i));
        }
        return test;
    }

    public void setValue(List<Data> value) {
        ((DefaultListModel<Data>) objectList.getModel()).clear();
        ((DefaultListModel<Data>) objectList.getModel()).addAll(value);
        editorFrames.clear();
        for (int i = 0; i < value.size(); i++) {
            editorFrames.add(null);
        }
    }

    @Override
    public boolean requestClose(boolean forceClose, boolean forceSave) {
        for (int i = 0; i < editorFrames.size(); i++) {
            if (editorFrames.get(i) != null) {
                boolean didClose = editorFrames.get(i).requestClose(forceClose, forceSave);
                if (!didClose) {
                    return false;
                }
            }
        }
        while (!unsavedEditorFrames.isEmpty()) {
            EditorFrame frame = unsavedEditorFrames.getFirst();
            boolean didClose = frame.requestClose(forceClose, forceSave);
            if (!didClose) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setEnabledState(boolean enabled) {
        if (!enabled) {
            objectList.setSelectedIndex(-1);
            objectList.setBackground(UIManager.getColor("Label.disabledBackground"));
        } else {
            objectList.setBackground(UIManager.getColor("List.background"));
        }
        objectList.setEnabled(enabled);
        if (enabled) {
            buttonAdd.setEnabled(true);
            boolean enableSelectionButtons = objectList.getSelectedIndex() != -1;
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
        int addIndex = objectList.getSelectedIndex() + 1;
        if (addIndex == 0) {
            addIndex = objectList.getModel().getSize();
        }
        if (initialData != null) {
            addIndex = ((DefaultListModel<Data>) objectList.getModel()).indexOf(initialData);
            ((DefaultListModel<Data>) objectList.getModel()).remove(addIndex);
            editorFrames.remove(addIndex);
        }
        ((DefaultListModel<Data>) objectList.getModel()).add(addIndex, data);
        editorFrames.add(addIndex, null);
        onFieldUpdated();
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
         int index = editorFrames.indexOf(frame);
         if (index != -1) {
             editorFrames.set(index, null);
         } else {
             unsavedEditorFrames.remove(frame);
         }
    }

    @Override
    public ErrorData isDataValidOrShowDialog(Data currentData, Data initialData) {
        if (requireUniqueValues && !isDataUnique(currentData, initialData)) {
            String value = currentData.toString();
            return new ErrorData(true, name + " already contains the value " + value + ".");
        }
        return new ErrorData(false, null);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return new DataObjectSet(new ArrayList<>());
        }
        List<Data> objectData = new ArrayList<>(getValue());
        return new DataObjectSet(objectData);
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataObjectSet dataObjectSet) {
            List<Data> objectData = dataObjectSet.getValue();
            if (objectData.isEmpty()) {
                setOptionalEnabled(false);
            }
            setValue(objectData);
        }
    }

    private boolean isDataUnique(Data newData, Data initialData) {
        for (int i = 0; i < objectList.getModel().getSize(); i++) {
            Data currentData = objectList.getModel().getElementAt(i);
            if (initialData != null && initialData.equals(currentData)) {
                continue;
            }
            if (currentData.isDuplicateValue(newData)) {
                return false;
            }
        }
        return true;
    }

}
