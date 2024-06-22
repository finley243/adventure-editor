package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import java.util.Comparator;

public class BrowserRootNode extends BrowserNode {

    private static final String ROOT_NAME = "Game Data";

    public BrowserRootNode() {
        super(ROOT_NAME);
    }

    @Override
    public void add(MutableTreeNode newChild) {
        if (!(newChild instanceof BrowserCategoryNode)) {
            throw new IllegalArgumentException("Root node can only contain category nodes");
        }
        super.add(newChild);
        this.children.sort(Comparator.comparing(o -> ((BrowserCategoryNode) o).getCategoryID()));
    }

    @Override
    protected JPopupMenu getContextMenu() {
        return null;
    }

}
