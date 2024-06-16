package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataScript;
import com.github.finley243.adventureeditor.data.DataString;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ParameterFieldScript extends EditorElement {

    private final JTextPane textPane;

    public ParameterFieldScript(EditorFrame editorFrame, boolean optional, String name) {
        super(editorFrame, optional, name);
        getInnerPanel().setLayout(new GridBagLayout());
        JLabel label = new JLabel(name);
        this.textPane = new JTextPane();
        JPanel sizeLimiterPanel = new JPanel(new BorderLayout());
        sizeLimiterPanel.add(textPane);
        JScrollPane scrollPane = new JScrollPane(sizeLimiterPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        //textPane.setPreferredSize(new Dimension(200, 150));
        //sizeLimiterPanel.setPreferredSize(new Dimension(200, 150));
        scrollPane.setPreferredSize(new Dimension(200, 150));
        //textPane.addActionListener(e -> editorFrame.onEditorElementUpdated());
        textPane.getDocument().addDocumentListener(new DocumentListener() {
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
        getInnerPanel().add(label, labelConstraints);
        //getInnerPanel().add(textPane, valueConstraints);
        getInnerPanel().add(scrollPane, valueConstraints);
    }

    public String getValue() {
        return textPane.getText();
    }

    public void setValue(String value) {
        textPane.setText(value);
        System.out.println("Is text modified by pane?: " + value.equals(textPane.getText()));
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
        return new DataString(getValue());
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
