package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataString;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ParameterFieldString extends EditorElement {

    private final JLabel label;
    private final JTextField textField;

    public ParameterFieldString(EditorFrame editorFrame, String name) {
        super(editorFrame);
        setLayout(new GridBagLayout());
        this.label = new JLabel(name);
        this.textField = new JTextField();
        textField.setPreferredSize(new Dimension(150, 20));
        textField.addActionListener(e -> editorFrame.onEditorElementUpdated());
        textField.getDocument().addDocumentListener(new DocumentListener() {
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
        });
        GridBagConstraints labelConstraints = new GridBagConstraints();
        GridBagConstraints valueConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        labelConstraints.insets = new Insets(2, 2, 2, 2);
        valueConstraints.gridx = 0;
        valueConstraints.gridy = 1;
        add(label, labelConstraints);
        add(textField, valueConstraints);
    }

    public String getValue() {
        return textField.getText();
    }

    public void setValue(String value) {
        textField.setText(value);
    }

    @Override
    public Data getData() {
        return new DataString(getValue());
    }

    @Override
    public void setData(Data data) {
        if (data instanceof DataString dataString) {
            setValue(dataString.getValue());
        }
    }

    @Override
    public String toString() {
        return getValue();
    }

}
