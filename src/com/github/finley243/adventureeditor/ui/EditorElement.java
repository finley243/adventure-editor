package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;

import javax.swing.*;

public abstract class EditorElement extends JPanel {

    protected final EditorFrame editorFrame;

    public EditorElement(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
    }

    public abstract Data getData();

    public abstract void setData(Data data);

}
