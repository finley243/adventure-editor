package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObjectSet;
import com.github.finley243.adventureeditor.template.Template;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ParameterFieldObjectSet extends EditorElement implements DataSaveTarget {

    private final JList<Data> textList;
    private final JButton buttonAdd;
    private final JButton buttonEdit;
    private final JButton buttonRemove;

    public ParameterFieldObjectSet(EditorFrame editorFrame, String name, Template template, Main main) {
        super(editorFrame);
        setLayout(new GridBagLayout());
        JLabel label = new JLabel(name);
        this.textList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(textList);
        this.buttonAdd = new JButton("New");
        this.buttonEdit = new JButton("Edit");
        this.buttonRemove = new JButton("Remove");
        scrollPane.setPreferredSize(new Dimension(150, 100));
        textList.setModel(new DefaultListModel<>());
        textList.setDragEnabled(false);
        textList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // May not be the ideal listener to use
        textList.addListSelectionListener(e -> editorFrame.onEditorElementUpdated());
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
        textList.addListSelectionListener(e -> {
            if (textList.getSelectedIndex() == -1) {
                buttonEdit.setEnabled(false);
                buttonRemove.setEnabled(false);
            } else {
                buttonEdit.setEnabled(true);
                buttonRemove.setEnabled(true);
            }
        });
        buttonAdd.addActionListener(e -> {
            EditorFrame objectFrame = new EditorFrame(main, template, null, this);
            /*JFrame objectFrame = new JFrame();
            objectFrame.setLayout(new FlowLayout());
            objectFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            ParameterFieldObject objectParameter = new ParameterFieldObject(name, template, main, false);
            objectFrame.getContentPane().add(objectParameter);
            JButton buttonSave = new JButton("Save");
            JButton buttonCancel = new JButton("Cancel");
            buttonSave.addActionListener(eSave -> {
                int addIndex = textList.getSelectedIndex() + 1;
                if (addIndex == 0) {
                    addIndex = textList.getModel().getSize();
                }
                ((DefaultListModel<Data>) textList.getModel()).addElement(objectParameter.getData());
                objectFrame.dispose();
            });
            buttonCancel.addActionListener(eCancel -> objectFrame.dispose());
            objectFrame.getContentPane().add(buttonSave);
            objectFrame.getContentPane().add(buttonCancel);
            objectFrame.pack();
            objectFrame.setLocationRelativeTo(null);
            objectFrame.setVisible(true);*/
        });
        buttonEdit.addActionListener(e -> {
            Data objectData = textList.getSelectedValue();
            if (objectData != null) {
                EditorFrame objectFrame = new EditorFrame(main, template, objectData, this);
            }
        });
        buttonRemove.addActionListener(e -> {
            int selectedIndex = textList.getSelectedIndex();
            if (selectedIndex != -1) {
                ((DefaultListModel<Data>) textList.getModel()).removeElementAt(selectedIndex);
                if (textList.getModel().getSize() > selectedIndex) {
                    textList.setSelectedIndex(selectedIndex);
                } else if (textList.getModel().getSize() == selectedIndex) {
                    textList.setSelectedIndex(selectedIndex - 1);
                }
            }
        });
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(label, labelConstraints);
        add(scrollPane, valueConstraints);
        add(buttonAdd, addConstraints);
        add(buttonEdit, editConstraints);
        add(buttonRemove, removeConstraints);
    }

    public java.util.List<Data> getValue() {
        java.util.List<Data> test = new ArrayList<>();
        for (int i = 0; i < textList.getModel().getSize(); i++) {
            test.add(textList.getModel().getElementAt(i));
        }
        return test;
    }

    public void setValue(List<Data> value) {
        ((DefaultListModel<Data>) textList.getModel()).clear();
        ((DefaultListModel<Data>) textList.getModel()).addAll(value);
    }

    @Override
    public void saveData(Data data, Data initialData) {
        int addIndex = textList.getSelectedIndex() + 1;
        if (addIndex == 0) {
            addIndex = textList.getModel().getSize();
        }
        if (initialData != null) {
            addIndex = ((DefaultListModel<Data>) textList.getModel()).indexOf(initialData);
            ((DefaultListModel<Data>) textList.getModel()).removeElement(initialData);
        }
        ((DefaultListModel<Data>) textList.getModel()).add(addIndex, data);
        editorFrame.onEditorElementUpdated();
    }

    @Override
    public Data getData() {
        List<Data> objectData = new ArrayList<>(getValue());
        return new DataObjectSet(objectData);
    }

    @Override
    public void setData(Data data) {
        if (data instanceof DataObjectSet dataObjectSet) {
            List<Data> objectData = dataObjectSet.getValue();
            setValue(objectData);
        }
    }

}
