package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataInteger;
import com.github.finley243.adventureeditor.ui.EditorFrame;

import javax.swing.*;
import java.awt.*;

public class ParameterFieldInteger extends ParameterField {

    private final JSpinner spinner;

    public ParameterFieldInteger(EditorFrame editorFrame, boolean optional, String name) {
        super(editorFrame, optional, name);
        getInnerPanel().setLayout(new GridBagLayout());
        JComponent label;
        if (optional) {
            label = getOptionalCheckbox();
        } else {
            label = new JLabel(name);
        }
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
        valueConstraints.weightx = 1;
        valueConstraints.weighty = 0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        getInnerPanel().add(label, labelConstraints);
        getInnerPanel().add(spinner, valueConstraints);
        if (optional) {
            setEnabledState(false);
        }
    }

    public int getValue() {
        return (int) spinner.getValue();
    }

    public void setValue(int value) {
        spinner.setValue(value);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        spinner.setEnabled(enabled);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        return new DataInteger(getValue());
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataInteger dataInteger) {
            setValue(dataInteger.getValue());
        }
    }

}
