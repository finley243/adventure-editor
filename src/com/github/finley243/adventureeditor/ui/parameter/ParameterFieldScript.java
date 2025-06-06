package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataScript;
import com.github.finley243.adventureeditor.ui.EditorFrame;
import com.github.finley243.adventureeditor.ui.ScriptPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ParameterFieldScript extends ParameterField {

    private final ScriptPane textPane;

    public ParameterFieldScript(EditorFrame editorFrame, boolean optional, String name, ParameterField parentField) {
        super(editorFrame, optional, name, parentField);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        getInnerPanel().setLayout(new GridBagLayout());
        JComponent label;
        if (optional) {
            label = getOptionalCheckbox();
        } else {
            label = new JLabel(name);
        }
        this.textPane = new ScriptPane();
        JPanel sizeLimiterPanel = new JPanel(new BorderLayout());
        sizeLimiterPanel.add(textPane);
        JScrollPane scrollPane = new JScrollPane(sizeLimiterPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(300, 250));
        textPane.getDocument().addDocumentListener(new DocumentListener() {
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
                //onFieldUpdated();
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
        valueConstraints.weighty = 1;
        valueConstraints.fill = GridBagConstraints.BOTH;
        getInnerPanel().add(label, labelConstraints);
        getInnerPanel().add(scrollPane, valueConstraints);
        if (optional) {
            setEnabledState(false);
        }
    }

    public String getValue() {
        return textPane.getText();
    }

    public void setValue(String value) {
        textPane.setText(value);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        textPane.setEnabled(enabled);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        return new DataScript(getValue());
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataScript dataScript) {
            setValue(dataScript.getValue());
        }
    }

    @Override
    public String toString() {
        return getValue();
    }

}
