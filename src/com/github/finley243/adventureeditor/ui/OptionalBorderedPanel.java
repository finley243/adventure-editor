package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class OptionalBorderedPanel extends JPanel {

    public OptionalBorderedPanel(String name, JPanel contentPanel, JCheckBox checkBox) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.setAlignmentX(LEFT_ALIGNMENT);
        if (checkBox != null) {
            labelPanel.add(checkBox);
        }
        //JLabel label = new JLabel(name);
        //label.setAlignmentY(CENTER_ALIGNMENT);
        //labelPanel.add(label);
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        upperPanel.add(labelPanel);
        JSeparator separator = new JSeparator();
        upperPanel.add(separator);
        add(upperPanel, BorderLayout.PAGE_START);
        add(contentPanel, BorderLayout.CENTER);
    }

}
