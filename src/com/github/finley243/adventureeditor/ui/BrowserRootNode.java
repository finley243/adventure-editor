package com.github.finley243.adventureeditor.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Comparator;

public class BrowserRootNode extends DefaultMutableTreeNode {

    public BrowserRootNode() {
        super("Game Data");
    }

    @Override
    public void add(MutableTreeNode newChild) {
        super.add(newChild);
        this.children.sort(Comparator.comparing(o -> ((BrowserCategoryNode) o).getCategoryID()));
    }

}
