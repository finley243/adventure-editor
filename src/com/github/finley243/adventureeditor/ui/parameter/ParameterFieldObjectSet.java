package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.PresenterActions;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataObjectSet;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.ErrorData;
import com.github.finley243.adventureeditor.ui.UIConstants;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class ParameterFieldObjectSet extends ParameterField {

    private final JList<ObjectSetEntry> objectList;
    private final JButton buttonAdd;
    private final JButton buttonEdit;
    private final JButton buttonRemove;

    private final List<EditorFrame> editorFrames;
    private final String name;
    private final Template template;
    private final boolean requireUniqueValues;

    public ParameterFieldObjectSet(EditorFrame editorFrame, boolean optional, String name, ParameterField parentField, Template template, boolean requireUniqueValues, ParameterFactory parameterFactory, PresenterActions presenter) {
        super(editorFrame, optional, name, parentField);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.editorFrames = new ArrayList<>();
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
        scrollPane.setPreferredSize(UIConstants.PREFERRED_SIZE_LIST);
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
                        ObjectSetEntry selectedEntry = objectList.getModel().getElementAt(index);
                        if (selectedEntry != null) {
                            if (editorFrames.get(index) != null) {
                                editorFrames.get(index).toFront();
                                editorFrames.get(index).requestFocus();
                            } else {
                                UUID entryID = selectedEntry.id();
                                EditorFrame objectFrame = new EditorFrame(editorFrame, template, selectedEntry.data(), null, false, parameterFactory, presenter, data -> {
                                    ParameterFieldObjectSet.this.updateEntryDataSilently(data, entryID);
                                }, data -> {
                                    ParameterFieldObjectSet.this.saveObjectData(data, entryID);
                                }, data -> ParameterFieldObjectSet.this.validateObject(data, entryID), ParameterFieldObjectSet.this::onEditorFrameClose);
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
            UUID entryID = UUID.randomUUID();
            Data initialData = new DataObject(template, new HashMap<>());
            int addIndex = objectList.getSelectedIndex() + 1;
            if (addIndex == 0) {
                addIndex = objectList.getModel().getSize();
            }
            ((DefaultListModel<ObjectSetEntry>) objectList.getModel()).add(addIndex, new ObjectSetEntry(entryID, initialData));
            editorFrames.add(addIndex, null);

            EditorFrame objectFrame = new EditorFrame(editorFrame, template, initialData, null, false, parameterFactory, presenter, data -> {
                this.updateEntryDataSilently(data, entryID);
            }, data -> {
                this.saveObjectData(data, entryID);
            }, data -> this.validateObject(data, entryID), this::onEditorFrameClose);
            editorFrames.set(addIndex, objectFrame);
            onFieldUpdated();
        });
        buttonEdit.addActionListener(e -> {
            ObjectSetEntry selectedEntry = objectList.getSelectedValue();
            int objectIndex = objectList.getSelectedIndex();
            if (selectedEntry != null) {
                if (editorFrames.get(objectIndex) != null) {
                    editorFrames.get(objectIndex).toFront();
                    editorFrames.get(objectIndex).requestFocus();
                } else {
                    UUID entryID = selectedEntry.id();
                    EditorFrame objectFrame = new EditorFrame(editorFrame, template, selectedEntry.data(), null, false, parameterFactory, presenter, data -> {
                        this.updateEntryDataSilently(data, entryID);
                    }, data -> {
                        this.saveObjectData(data, entryID);
                    }, data -> this.validateObject(data, entryID), this::onEditorFrameClose);
                    editorFrames.set(objectIndex, objectFrame);
                }
            }
        });
        buttonRemove.addActionListener(e -> {
            int selectedIndex = objectList.getSelectedIndex();
            if (selectedIndex != -1) {
                if (editorFrames.get(selectedIndex) != null) {
                    editorFrames.get(selectedIndex).requestClose();
                }
                ((DefaultListModel<ObjectSetEntry>) objectList.getModel()).removeElementAt(selectedIndex);
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

    public List<Data> getValue() {
        List<Data> result = new ArrayList<>();
        for (int i = 0; i < objectList.getModel().getSize(); i++) {
            result.add(objectList.getModel().getElementAt(i).data());
        }
        return result;
    }

    public void setValue(List<Data> value) {
        DefaultListModel<ObjectSetEntry> model = (DefaultListModel<ObjectSetEntry>) objectList.getModel();
        model.clear();
        for (Data d : value) {
            model.addElement(new ObjectSetEntry(UUID.randomUUID(), d));
        }
        editorFrames.clear();
        for (int i = 0; i < value.size(); i++) {
            editorFrames.add(null);
        }
    }

    @Override
    public void requestClose() {
        for (EditorFrame editorFrame : new ArrayList<>(editorFrames)) {
            if (editorFrame != null) {
                editorFrame.requestClose();
            }
        }
    }

    @Override
    public void setEnabledState(boolean enabled) {
        if (!enabled) {
            objectList.setSelectedIndex(-1);
            //objectList.setBackground(UIManager.getColor("Label.disabledBackground"));
        } else {
            //objectList.setBackground(UIManager.getColor("List.background"));
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

    public void saveObjectData(Data data, UUID entryID) {
        int index = findIndexByID(entryID);
        ((DefaultListModel<ObjectSetEntry>) objectList.getModel()).set(index, new ObjectSetEntry(entryID, data));
        onFieldUpdated();
    }

    private void updateEntryDataSilently(Data data, UUID entryID) {
        int index = findIndexByID(entryID);
        ((DefaultListModel<ObjectSetEntry>) objectList.getModel()).set(index, new ObjectSetEntry(entryID, data));
    }

    private int findIndexByID(UUID id) {
        DefaultListModel<ObjectSetEntry> model = (DefaultListModel<ObjectSetEntry>) objectList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).id().equals(id)) {
                return i;
            }
        }
        throw new IllegalStateException("No object set entry found with ID " + id);
    }

    private void onEditorFrameClose(EditorFrame frame) {
         int index = editorFrames.indexOf(frame);
         if (index != -1) {
             editorFrames.set(index, null);
         }
    }

    private ErrorData validateObject(Data currentData, UUID entryID) {
        if (requireUniqueValues && !isDataUnique(currentData, entryID)) {
            return new ErrorData(true, name + " already contains the value " + currentData + ".");
        }
        return new ErrorData(false, null);
    }

    private boolean isDataUnique(Data newData, UUID excludeID) {
        DefaultListModel<ObjectSetEntry> model = (DefaultListModel<ObjectSetEntry>) objectList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ObjectSetEntry entry = model.getElementAt(i);
            if (entry.id().equals(excludeID)) {
                continue;
            }
            if (entry.data().isDuplicateValue(newData)) {
                return false;
            }
        }
        return true;
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
    protected void setDataInternal(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataObjectSet dataObjectSet) {
            List<Data> objectData = dataObjectSet.getValue();
            if (objectData.isEmpty()) {
                setOptionalEnabled(false);
            }
            setValue(objectData);
        }
    }

    private record ObjectSetEntry(UUID id, Data data) {
        @Override
        public String toString() {
            return data.toString();
        }
    }

}
