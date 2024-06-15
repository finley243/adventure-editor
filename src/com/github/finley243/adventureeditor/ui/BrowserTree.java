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
    private final DefaultTreeModel treeModel;
    private final Map<String, BrowserCategoryNode> categoryNodes;
    private final Main main;

    public BrowserTree(Main main) {
        this.treeRoot = new BrowserRootNode();
        this.categoryNodes = new HashMap<>();
        this.main = main;
        this.treeModel = new DefaultTreeModel(treeRoot, false);
        this.setModel(treeModel);
        BrowserTree thisTree = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    TreePath path = thisTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if (node instanceof BrowserObjectNode objectNode) {
                            main.editObject(objectNode.getCategoryID(), objectNode.getObjectID());
                        }
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    TreePath path = thisTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        thisTree.setSelectionPath(path);
                        if (node instanceof BrowserObjectNode objectNode) {
                            objectNode.openContextMenu(e.getComponent(), e.getPoint());
                        } else if (node instanceof BrowserCategoryNode categoryNode) {
                            categoryNode.openContextMenu(e.getComponent(), e.getPoint());
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
    }

    public void updateCategory(String categoryID) {
        treeModel.nodeStructureChanged(categoryNodes.get(categoryID));
    }

    public void addGameObject(String category, String objectID) {
        if (categoryNodes.containsKey(category)) {
            categoryNodes.get(category).addGameObject(objectID);
            updateCategory(category);
        }
        this.expandRow(0);
    }

    public void removeGameObject(String category, String objectID) {
        treeModel.removeNodeFromParent(categoryNodes.get(category).getObjectNode(objectID));
        categoryNodes.get(category).removeGameObject(objectID);
    }

}
