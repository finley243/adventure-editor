package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public abstract class EditorElement extends JPanel {

    protected final EditorFrame parentFrame;
    private final JPanel innerPanel;
    private final JCheckBox optionalCheckbox;
    private final boolean isOptional;
    private boolean isOptionalEnabled;

    public EditorElement(EditorFrame parentFrame, boolean isOptional, String name) {
        this.parentFrame = parentFrame;
        this.isOptional = isOptional;
        this.innerPanel = new JPanel();
        setLayout(new BorderLayout());
        add(innerPanel, BorderLayout.CENTER);
        if (isOptional) {
            setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            this.optionalCheckbox = new JCheckBox("Enable " + name);
            //optionalCheckbox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            optionalCheckbox.addActionListener(e -> {
                boolean isEnabled = optionalCheckbox.isSelected();
                this.isOptionalEnabled = isEnabled;
                setEnabledState(isEnabled);
            });
            optionalCheckbox.setVerticalTextPosition(SwingConstants.TOP);
            add(optionalCheckbox, BorderLayout.PAGE_START);
            this.isOptionalEnabled = false;
        } else {
            this.optionalCheckbox = null;
            this.isOptionalEnabled = true;
        }
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

    public abstract void setEnabledState(boolean enabled);

    public abstract Data getData();

    public abstract void setData(Data data);

}
