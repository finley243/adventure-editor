package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.ui.EditorFrame;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ParameterFieldString extends ParameterField {

    private final JTextField textField;

    public ParameterFieldString(EditorFrame editorFrame, boolean optional, String name, ParameterField parentField) {
        super(editorFrame, optional, name, parentField);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        getInnerPanel().setLayout(new GridBagLayout());
        JComponent label;
        if (optional) {
            label = getOptionalCheckbox();
        } else {
            label = new JLabel(name);
        }
        this.textField = new JTextField();
        textField.setPreferredSize(new Dimension(150, 20));
        textField.addActionListener(e -> onFieldUpdated());
        textField.getDocument().addDocumentListener(new DocumentListener() {
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
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        labelConstraints.insets = new Insets(2, 2, 2, 2);
        valueConstraints.gridx = 0;
        valueConstraints.gridy = 1;
        valueConstraints.weightx = 1;
        valueConstraints.weighty = 0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        getInnerPanel().add(label, labelConstraints);
        getInnerPanel().add(textField, valueConstraints);
        if (optional) {
            setEnabledState(false);
        }
    }

    public String getValue() {
        return textField.getText();
    }

    public void setValue(String value) {
        textField.setText(value);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        textField.setEnabled(enabled);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        return new DataString(getValue());
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataString dataString) {
            setValue(dataString.getValue());
        }
    }

    @Override
    public String toString() {
        return getValue();
    }

}
