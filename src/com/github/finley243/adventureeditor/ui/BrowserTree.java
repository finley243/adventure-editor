package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class BrowserTree extends JTree {

    private final DefaultMutableTreeNode treeRoot;
    private final Map<String, BrowserCategoryNode> categoryNodes;
    private final Main main;

    public BrowserTree(Main main) {
        this.treeRoot = new BrowserRootNode();
        this.categoryNodes = new HashMap<>();
        this.main = main;
        DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot, false);
        this.setModel(treeModel);
        //this.expandRow(0);
        BrowserTree thisTree = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = thisTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if (node instanceof BrowserObjectNode) {
                            BrowserObjectNode objectNode = (BrowserObjectNode) node;
                            objectNode.openInEditor();
                        }
                    }
                }
            }
        });
    }

    public void addCategory(String categoryID, String name) {
        BrowserCategoryNode node = new BrowserCategoryNode(main, categoryID, name);
        categoryNodes.put(categoryID, node);
        treeRoot.add(node);
        //this.expandRow(0);
    }

    public void addGameObject(String category, String objectID) {
        if (categoryNodes.containsKey(category)) {
            categoryNodes.get(category).addGameObject(objectID);
        }
        this.expandRow(0);
    }

}
