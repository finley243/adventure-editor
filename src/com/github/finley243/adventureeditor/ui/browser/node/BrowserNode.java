package com.github.finley243.adventureeditor.ui.browser.node;

import com.github.finley243.adventureeditor.ui.browser.BrowserFrame;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class BrowserNode extends DefaultMutableTreeNode {

    public BrowserNode(String name) {
        super(name);
    }

    public abstract JPopupMenu getContextMenu(BrowserFrame browserFrame);

}
