package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataReference;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ParameterFieldReference extends EditorElement {

    private final JComboBox<String> dropdownMenu;

    public ParameterFieldReference(EditorFrame editorFrame, boolean optional, String name, String[] values) {
        super(editorFrame, optional, name);
        getInnerPanel().setLayout(new GridBagLayout());
        JComponent label;
        if (optional) {
            label = getOptionalCheckbox();
        } else {
            label = new JLabel(name);
        }
        Arrays.sort(values);
        this.dropdownMenu = new JComboBox<>(values);
        dropdownMenu.setPreferredSize(new Dimension(150, 20));
        dropdownMenu.setEditable(true);
        dropdownMenu.addActionListener(e -> editorFrame.onEditorElementUpdated());
        // TODO - Document listener not working, find another solution
        /*((JTextField) dropdownMenu.getEditor().getEditorComponent()).getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                editorFrame.onEditorElementUpdated();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                editorFrame.onEditorElementUpdated();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                editorFrame.onEditorElementUpdated();
            }
        });*/
        GridBagConstraints labelConstraints = new GridBagConstraints();
        GridBagConstraints valueConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        labelConstraints.insets = new Insets(2, 2, 2, 2);
        valueConstraints.gridx = 0;
        valueConstraints.gridy = 1;
        getInnerPanel().add(label, labelConstraints);
        getInnerPanel().add(dropdownMenu, valueConstraints);
    }

    public String getValue() {
        return (String) dropdownMenu.getSelectedItem();
    }

    public void setValue(String value) {
        dropdownMenu.setSelectedItem(value);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        dropdownMenu.setEnabled(enabled);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        return new DataReference(getValue());
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data == null) {
            setValue(null);
        }
        if (data instanceof DataReference dataReference) {
            setValue(dataReference.getValue());
        }
    }

}
