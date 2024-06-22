package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataBoolean;
import com.github.finley243.adventureeditor.ui.EditorFrame;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class ParameterFieldBoolean extends ParameterField {

    private final JCheckBox checkBox;

    public ParameterFieldBoolean(EditorFrame editorFrame, boolean optional, String name) {
        super(editorFrame, optional, name);
        this.checkBox = new JCheckBox(name);
        //checkBox.setPreferredSize(new Dimension(140, 20));
        //checkBox.setHorizontalAlignment(SwingConstants.LEFT);
        checkBox.setVerticalTextPosition(SwingConstants.TOP);
        checkBox.addActionListener(e -> editorFrame.onEditorElementUpdated());
        JPanel checkBoxPanel = new JPanel();
        //checkBoxPanel.setAlignmentX(LEFT_ALIGNMENT);
        getInnerPanel().setLayout(new GridBagLayout());
        checkBoxPanel.add(checkBox);
        if (optional) {
            GridBagConstraints optionalConstraints = new GridBagConstraints();
            optionalConstraints.gridx = 0;
            optionalConstraints.gridy = 0;
            checkBoxPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            getInnerPanel().add(getOptionalCheckbox(), optionalConstraints);
            getOptionalCheckbox().setText(null);
        }
        //getInnerPanel().add(checkBox);
        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = optional ? 1 : 0;
        valueConstraints.gridy = 0;
        valueConstraints.weightx = 1;
        valueConstraints.weighty = 0;
        //valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        valueConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        getInnerPanel().add(checkBoxPanel, valueConstraints);
        if (optional) {
            setEnabledState(false);
        }
    }

    public boolean getValue() {
        return checkBox.isSelected();
    }

    public void setValue(boolean value) {
        checkBox.setSelected(value);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        checkBox.setEnabled(enabled);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        return new DataBoolean(getValue());
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataBoolean dataBoolean) {
            setValue(dataBoolean.getValue());
        }
    }

}
