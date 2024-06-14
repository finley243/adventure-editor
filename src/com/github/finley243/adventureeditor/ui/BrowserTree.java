package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.HashMap;
import java.util.Map;

public class BrowserTree extends JTree {

    private final DefaultMutableTreeNode treeRoot;
    private final Map<String, BrowserCategoryNode> categoryNodes;

    public BrowserTree() {
        this.treeRoot = new DefaultMutableTreeNode("Game Data");
        this.categoryNodes = new HashMap<>();
        DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot, false);
        this.setModel(treeModel);
        this.expandRow(0);
    }

    public void addCategory(String categoryID, String name) {
        BrowserCategoryNode node = new BrowserCategoryNode(categoryID, name);
        categoryNodes.put(categoryID, node);
        treeRoot.add(node);
        //this.expandRow(0);
    }

    public void addGameObject(String category, String objectID) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            BrowserCategoryNode node = (BrowserCategoryNode) root.getChildAt(i);
            if (node.getCategoryID().equals(category)) {
                node.addGameObject(objectID);
                break;
            }
        }
        this.expandRow(0);
    }

}
