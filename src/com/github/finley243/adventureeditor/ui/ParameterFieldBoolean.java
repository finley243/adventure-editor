package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataBoolean;

import javax.swing.*;
import java.awt.*;

public class ParameterFieldBoolean extends EditorElement {

    private final JCheckBox checkBox;

    public ParameterFieldBoolean(String name) {
        setLayout(new GridBagLayout());
        this.checkBox = new JCheckBox(name);
        checkBox.setPreferredSize(new Dimension(150, 20));
        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = 0;
        valueConstraints.gridy = 0;
        add(checkBox, valueConstraints);
    }

    public boolean getValue() {
        return checkBox.isSelected();
    }

    public void setValue(boolean value) {
        checkBox.setSelected(value);
    }

    @Override
    public Data getData() {
        return new DataBoolean(getValue());
    }

    @Override
    public void setData(Data data) {
        if (data instanceof DataBoolean dataBoolean) {
            setValue(dataBoolean.getValue());
        }
    }

}
