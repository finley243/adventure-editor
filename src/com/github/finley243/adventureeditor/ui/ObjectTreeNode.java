package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

public class ObjectTreeNode extends DefaultMutableTreeNode {

    private Data data;

    public ObjectTreeNode(String name, Data data) {
        super(name);
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<ObjectTreeNode> getObjectTreeChildren() {
        List<ObjectTreeNode> children = new ArrayList<>();
        this.children().asIterator().forEachRemaining(e -> children.add((ObjectTreeNode) e));
        return children;
    }

}
