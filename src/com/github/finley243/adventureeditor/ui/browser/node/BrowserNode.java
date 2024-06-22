package com.github.finley243.adventureeditor.ui.browser.node;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class BrowserNode extends DefaultMutableTreeNode {

    public BrowserNode(String name) {
        super(name);
    }

    public abstract JPopupMenu getContextMenu();

}
