package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataEnum;
import com.github.finley243.adventureeditor.data.DataInteger;
import com.github.finley243.adventureeditor.data.DataReference;

import javax.swing.*;
import java.awt.*;

public class ParameterFieldEnum extends EditorElement {

    private final JComboBox<String> dropdownMenu;

    public ParameterFieldEnum(EditorFrame editorFrame, String name, String[] values) {
        super(editorFrame);
        setLayout(new GridBagLayout());
        JLabel label = new JLabel(name);
        this.dropdownMenu = new JComboBox<>(values);
        dropdownMenu.setPreferredSize(new Dimension(150, 20));
        dropdownMenu.setEditable(false);
        dropdownMenu.addActionListener(e -> editorFrame.onEditorElementUpdated());
        GridBagConstraints labelConstraints = new GridBagConstraints();
        GridBagConstraints valueConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        labelConstraints.insets = new Insets(2, 2, 2, 2);
        valueConstraints.gridx = 0;
        valueConstraints.gridy = 1;
        add(label, labelConstraints);
        add(dropdownMenu, valueConstraints);
    }

    public String getValue() {
        return (String) dropdownMenu.getSelectedItem();
    }

    public void setValue(String value) {
        dropdownMenu.setSelectedItem(value);
    }

    @Override
    public Data getData() {
        return new DataEnum(getValue());
    }

    @Override
    public void setData(Data data) {
        if (data instanceof DataEnum dataEnum) {
            setValue(dataEnum.getValue());
        }
    }

}
