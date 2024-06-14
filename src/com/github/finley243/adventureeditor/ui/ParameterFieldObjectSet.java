package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObjectSet;
import com.github.finley243.adventureeditor.template.Template;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParameterFieldObjectSet extends EditorElement {

    private final JList<Data> textList;
    private final JButton buttonAdd;
    private final JButton buttonEdit;
    private final JButton buttonRemove;

    public ParameterFieldObjectSet(String name, Template template, Map<String, Template> templates, Map<String, Map<String, Data>> data) {
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
            JFrame objectFrame = new JFrame();
            objectFrame.setLayout(new FlowLayout());
            objectFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            ParameterFieldObject objectParameter = new ParameterFieldObject(name, template, templates, data, false);
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
            objectFrame.setVisible(true);
        });
        buttonEdit.addActionListener(e -> {
            Data objectData = textList.getSelectedValue();
            if (objectData != null) {
                JFrame objectFrame = new JFrame();
                ParameterFieldObject objectParameter = new ParameterFieldObject(name, template, templates, data, false);
                objectParameter.setData(objectData);
                objectFrame.setLayout(new FlowLayout());
                objectFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                objectFrame.getContentPane().add(objectParameter);
                JButton buttonSave = new JButton("Save");
                JButton buttonCancel = new JButton("Cancel");
                buttonSave.addActionListener(eSave -> objectFrame.dispose());
                buttonCancel.addActionListener(eCancel -> objectFrame.dispose());
                objectFrame.getContentPane().add(buttonSave);
                objectFrame.getContentPane().add(buttonCancel);
                objectFrame.pack();
                objectFrame.setLocationRelativeTo(null);
                objectFrame.setVisible(true);
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
