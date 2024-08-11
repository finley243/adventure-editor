package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataReference;
import com.github.finley243.adventureeditor.ui.EditorFrame;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Arrays;

public class ParameterFieldReference extends ParameterField {

    private final JComboBox<String> dropdownMenu;
    private final JButton openReferenceButton;

    public ParameterFieldReference(EditorFrame editorFrame, boolean optional, String name, ParameterField parentField, Main main, String categoryID) {
        super(editorFrame, optional, name, parentField);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        getInnerPanel().setLayout(new GridBagLayout());
        JComponent label;
        if (optional) {
            label = getOptionalCheckbox();
        } else {
            label = new JLabel(name);
        }
        String[] values = main.getDataManager().getIDsForCategoryArray(categoryID);
        Arrays.sort(values);
        this.dropdownMenu = new JComboBox<>(values);
        dropdownMenu.setPreferredSize(new Dimension(150, 20));
        dropdownMenu.setEditable(true);
        dropdownMenu.addActionListener(e -> onFieldUpdated());
        this.openReferenceButton = new JButton("...");
        openReferenceButton.setPreferredSize(new Dimension(20, 20));
        openReferenceButton.addActionListener(e -> {
            String value = (String) dropdownMenu.getSelectedItem();
            if (value != null && main.getDataManager().getIDsForCategory(categoryID).contains(value)) {
                main.getDataManager().editObject(categoryID, value);
            }
        });
        ((JTextField) dropdownMenu.getEditor().getEditorComponent()).getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onFieldUpdated();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                onFieldUpdated();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                onFieldUpdated();
            }
        });
        GridBagConstraints labelConstraints = new GridBagConstraints();
        GridBagConstraints valueConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.gridwidth = 2;
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        labelConstraints.insets = new Insets(2, 2, 2, 2);
        valueConstraints.gridx = 0;
        valueConstraints.gridy = 1;
        valueConstraints.weightx = 1;
        valueConstraints.weighty = 0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 1;
        buttonConstraints.gridy = 1;
        getInnerPanel().add(label, labelConstraints);
        getInnerPanel().add(dropdownMenu, valueConstraints);
        getInnerPanel().add(openReferenceButton, buttonConstraints);
        if (optional) {
            setEnabledState(false);
        }
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
        openReferenceButton.setEnabled(enabled);
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
