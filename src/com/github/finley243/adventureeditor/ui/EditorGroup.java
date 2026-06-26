package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class EditorGroup extends JPanel {

    private final String id;
    private final String name;

    public EditorGroup(String id, String name) {
        this.id = id;
        this.name = name;
        this.setLayout(new GridBagLayout());
        if (name != null) {
            TitledBorder border = BorderFactory.createTitledBorder(name);
            border.setTitleFont(UIManager.getFont("Label.font"));
            this.setBorder(border);
        } else {
            this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

}
