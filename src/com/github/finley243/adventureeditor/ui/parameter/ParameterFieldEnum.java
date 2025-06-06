package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataEnum;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

import javax.swing.*;
import java.awt.*;

public class ParameterFieldEnum extends ParameterField {

    private final JComboBox<String> dropdownMenu;

    public ParameterFieldEnum(EditorFrame editorFrame, boolean optional, String name, ParameterField parentField, String[] values) {
        super(editorFrame, optional, name, parentField);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        getInnerPanel().setLayout(new GridBagLayout());
        JComponent label;
        if (optional) {
            label = getOptionalCheckbox();
        } else {
            label = new JLabel(name);
        }
        this.dropdownMenu = new JComboBox<>(values);
        dropdownMenu.setPreferredSize(new Dimension(150, 20));
        dropdownMenu.setEditable(false);
        dropdownMenu.addActionListener(e -> onFieldUpdated());
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
        getInnerPanel().add(dropdownMenu, valueConstraints);
        if (optional) {
            setEnabledState(false);
        }
    }

    public String getValue() {
        return (String) dropdownMenu.getSelectedItem();
    }

    public void setValue(String value) {
        dropdownMenu.setSelectedItem(value);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        dropdownMenu.setEnabled(enabled);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        return new DataEnum(getValue());
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataEnum dataEnum) {
            setValue(dataEnum.getValue());
        }
    }

}
