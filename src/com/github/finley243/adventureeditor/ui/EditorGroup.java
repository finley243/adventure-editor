package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class EditorGroup extends JPanel {

    private final String id;
    private final String name;

    public EditorGroup(String id, String name) {
        this.id = id;
        this.name = name;
        this.setLayout(new GridBagLayout());
        if (name != null) {
            this.setBorder(BorderFactory.createTitledBorder(name));
        } else {
            this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
    }

    public String getID() {
        return id;
    }

}
