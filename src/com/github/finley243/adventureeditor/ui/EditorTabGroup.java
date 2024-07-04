package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class EditorTabGroup extends JPanel {

    private final String id;
    private final String name;
    private final JTabbedPane tabbedPane;

    public EditorTabGroup(String id, String name) {
        this.id = id;
        this.name = name;
        this.tabbedPane = new JTabbedPane();
        //this.setLayout(new GridBagLayout());
        if (name != null) {
            this.setBorder(BorderFactory.createTitledBorder(name));
        }/* else {
            this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }*/
        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    public String getID() {
        return id;
    }

    public void addGroupTab(EditorGroup group) {
        tabbedPane.addTab(group.getName(), group);
        group.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }

}
