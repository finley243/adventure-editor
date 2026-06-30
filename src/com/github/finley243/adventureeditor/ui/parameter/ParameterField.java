package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

import javax.swing.*;
import java.awt.*;

public abstract class ParameterField extends JPanel {

    protected final EditorFrame parentFrame;
    private final ParameterField parentField;
    private final JPanel innerPanel;
    private final JCheckBox optionalCheckbox;
    private final boolean isOptional;
    private boolean isOptionalEnabled;

    private boolean isUpdatingData;

    public ParameterField(EditorFrame parentFrame, boolean isOptional, String name, ParameterField parentField) {
        this.parentFrame = parentFrame;
        this.parentField = parentField;
        this.isOptional = isOptional;
        this.innerPanel = new JPanel();
        setLayout(new BorderLayout());
        add(innerPanel, BorderLayout.CENTER);
        if (isOptional) {
            this.optionalCheckbox = new JCheckBox(name);
            optionalCheckbox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            optionalCheckbox.addActionListener(e -> {
                boolean isEnabled = optionalCheckbox.isSelected();
                this.isOptionalEnabled = isEnabled;
                setEnabledState(isEnabled);
                if (!isUpdatingData) {
                    parentFrame.onEditorElementUpdated();
                }
            });
            optionalCheckbox.setVerticalTextPosition(SwingConstants.TOP);
            this.isOptionalEnabled = false;
        } else {
            this.optionalCheckbox = null;
            this.isOptionalEnabled = true;
        }
    }

    public void onFieldUpdated() {
        if (isUpdatingData) return;
        if (parentField != null) {
            parentField.onFieldUpdated();
        } else {
            parentFrame.onEditorElementUpdated();
        }
    }

    public JCheckBox getOptionalCheckbox() {
        return optionalCheckbox;
    }

    public JPanel getInnerPanel() {
        return innerPanel;
    }

    public boolean isOptionalEnabled() {
        return isOptionalEnabled;
    }

    public void setOptionalEnabled(boolean enabled) {
        if (isOptional) {
            isOptionalEnabled = enabled;
            if (optionalCheckbox != null) {
                optionalCheckbox.setSelected(enabled);
            }
            setEnabledState(enabled);
        }
    }

    public void setEnabledFromParent(boolean enabled) {
        if (optionalCheckbox != null) {
            optionalCheckbox.setEnabled(enabled);
        }
        if (enabled) {
            setEnabledState(isOptionalEnabled);
        } else {
            setEnabledState(false);
        }
    }

    public boolean requestClose() {
        return true;
    }

    public abstract void setEnabledState(boolean enabled);

    public abstract Data getData();

    public void setData(Data data) {
        this.isUpdatingData = true;
        setDataInternal(data);
        this.isUpdatingData = false;
    }

    protected abstract void setDataInternal(Data data);

    protected boolean isUpdatingData() {
        return isUpdatingData;
    }

}
