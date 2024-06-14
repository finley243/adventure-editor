package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataFloat;
import com.github.finley243.adventureeditor.data.DataInteger;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class ParameterFieldFloat extends EditorElement {

    private JLabel label;
    private JSpinner spinner;

    public ParameterFieldFloat(String name) {
        setLayout(new GridBagLayout());
        this.label = new JLabel(name);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.000d, null, null, 0.001d);
        this.spinner = new JSpinner(spinnerModel);
        spinner.setPreferredSize(new Dimension(150, 20));
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinner.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(3);
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

    public float getValue() {
        return (float) ((double) spinner.getValue());
    }

    public void setValue(float value) {
        spinner.setValue((double) value);
    }

    @Override
    public Data getData() {
        return new DataFloat(getValue());
    }

    @Override
    public void setData(Data data) {
        if (data instanceof DataFloat dataFloat) {
            setValue(dataFloat.getValue());
        }
    }

}
