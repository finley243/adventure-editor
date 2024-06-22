package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataReferenceSet;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.EditorFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ParameterFieldReferenceSet extends ParameterField implements DataSaveTarget {

    private final JList<String> referenceList;
    private final JButton buttonAdd;
    private final JButton buttonEdit;
    private final JButton buttonRemove;

    //private final List<EditorFrame> editorFrames;
    //private final List<EditorFrame> unsavedEditorFrames;
    private final String name;

    public ParameterFieldReferenceSet(EditorFrame editorFrame, boolean optional, String name, Template template, Main main) {
        super(editorFrame, optional, name);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        //this.editorFrames = new ArrayList<>();
        //this.unsavedEditorFrames = new ArrayList<>();
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
        buttonAdd.addActionListener(e -> {
            main.getDataManager().newObject(template.id());
            /*EditorFrame objectFrame = new EditorFrame(main, editorFrame, template, null, this);
            unsavedEditorFrames.add(objectFrame);*/
        });
        buttonEdit.addActionListener(e -> {
            main.getDataManager().editObject(template.id(), referenceList.getSelectedValue());
            /*Data objectData = referenceList.getSelectedValue();
            int objectIndex = referenceList.getSelectedIndex();
            if (objectData != null) {
                if (editorFrames.get(objectIndex) != null) {
                    editorFrames.get(objectIndex).toFront();
                    editorFrames.get(objectIndex).requestFocus();
                } else {
                    EditorFrame objectFrame = new EditorFrame(main, editorFrame, template, objectData, this);
                    editorFrames.set(objectIndex, objectFrame);
                }
            }*/
        });
        buttonRemove.addActionListener(e -> {
            main.getDataManager().deleteObject(template.id(), referenceList.getSelectedValue());
            /*int selectedIndex = referenceList.getSelectedIndex();
            if (selectedIndex != -1) {
                if (editorFrames.get(selectedIndex) != null) {
                    boolean didClose = editorFrames.get(selectedIndex).requestClose(false, false);
                    if (!didClose) {
                        return;
                    }
                }
                ((DefaultListModel<Data>) referenceList.getModel()).removeElementAt(selectedIndex);
                editorFrames.remove(selectedIndex);
                if (referenceList.getModel().getSize() > selectedIndex) {
                    referenceList.setSelectedIndex(selectedIndex);
                } else if (referenceList.getModel().getSize() == selectedIndex) {
                    referenceList.setSelectedIndex(selectedIndex - 1);
                }
            }*/
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
    public boolean requestClose(boolean forceClose, boolean forceSave) {
        /*for (int i = 0; i < editorFrames.size(); i++) {
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
        }*/
        return true;
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
        /*int addIndex = referenceList.getSelectedIndex() + 1;
        if (addIndex == 0) {
            addIndex = referenceList.getModel().getSize();
        }
        if (initialData != null) {
            addIndex = ((DefaultListModel<String>) referenceList.getModel()).indexOf(initialData);
            ((DefaultListModel<String>) referenceList.getModel()).remove(addIndex);
            editorFrames.remove(addIndex);
        }
        ((DefaultListModel<String>) referenceList.getModel()).add(addIndex, data);
        editorFrames.add(addIndex, null);
        parentFrame.onEditorElementUpdated();*/
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
         /*int index = editorFrames.indexOf(frame);
         if (index != -1) {
             editorFrames.set(index, null);
         } else {
             unsavedEditorFrames.remove(frame);
         }*/
    }

    @Override
    public ErrorData isDataValidOrShowDialog(Data currentData, Data initialData) {
        /*if (!isDataUnique(currentData, initialData)) {
            String value = currentData.toString();
            return new ErrorData(true, name + " already contains the value " + value + ".");
        }*/
        return new ErrorData(false, null);
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

    /*private boolean isDataUnique(Data newData, Data initialData) {
        for (int i = 0; i < referenceList.getModel().getSize(); i++) {
            String currentData = referenceList.getModel().getElementAt(i);
            if (initialData == null && currentData.equals(currentData)) {
                return false;
            }
        }
        return true;
    }*/

}
