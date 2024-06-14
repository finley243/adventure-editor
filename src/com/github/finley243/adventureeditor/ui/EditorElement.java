package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;

import javax.swing.*;

public abstract class EditorElement extends JPanel {

    public abstract Data getData();

    public abstract void setData(Data data);

}
