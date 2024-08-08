package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;

import javax.swing.tree.DefaultMutableTreeNode;

public class ObjectTreeNode extends DefaultMutableTreeNode {

    private Data data;

    public ObjectTreeNode(String name, Data data) {
        super(name);
        this.data = data;
    }

    public Data getData() {
        return data;
    }

}
