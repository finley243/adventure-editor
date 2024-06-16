package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataBoolean;

import javax.swing.*;
import java.awt.*;

public class ParameterFieldBoolean extends EditorElement {

    private final JCheckBox checkBox;

    public ParameterFieldBoolean(EditorFrame editorFrame, boolean optional, String name) {
        super(editorFrame, optional, name);
        this.checkBox = new JCheckBox(name);
        checkBox.setPreferredSize(new Dimension(140, 20));
        checkBox.setVerticalTextPosition(SwingConstants.TOP);
        checkBox.addActionListener(e -> editorFrame.onEditorElementUpdated());
        getInnerPanel().add(checkBox);
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
