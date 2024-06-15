package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;

import javax.swing.*;

public class EditorFrame extends JFrame {

    public EditorFrame(Main main, Template template, Data objectData, boolean isTopLevel) {
        super(template.name());
        EditorElement editorElement = new ParameterFieldObject(template.name(), template, main, isTopLevel);
        if (objectData != null) {
            editorElement.setData(objectData);
        }
        JScrollPane scrollPane = new JScrollPane(editorElement);
        //this.getContentPane().add(editorElement);
        this.getContentPane().add(scrollPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

}
