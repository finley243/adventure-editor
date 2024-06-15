package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataInteger;
import com.github.finley243.adventureeditor.data.DataObjectSet;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ParameterFieldInteger extends EditorElement {

    private final JLabel label;
    private final JSpinner spinner;

    public ParameterFieldInteger(EditorFrame editorFrame, String name) {
        super(editorFrame);
        setLayout(new GridBagLayout());
        this.label = new JLabel(name);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, null, null, 1);
        this.spinner = new JSpinner(spinnerModel);
        spinner.setPreferredSize(new Dimension(150, 20));
        spinner.addChangeListener(e -> editorFrame.onEditorElementUpdated());
        GridBagConstraints labelConstraints = new GridBagConstraints();
        GridBagConstraints valueConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        labelConstraints.insets = new Insets(2, 2, 2, 2);
        valueConstraints.gridx = 0;
        valueConstraints.gridy = 1;
        add(label, labelConstraints);
        add(spinner, valueConstraints);
    }

    public int getValue() {
        return (int) spinner.getValue();
    }

    public void setValue(int value) {
        spinner.setValue(value);
    }

    @Override
    public Data getData() {
        return new DataInteger(getValue());
    }

    @Override
    public void setData(Data data) {
        if (data instanceof DataInteger dataInteger) {
            setValue(dataInteger.getValue());
        }
    }

}
