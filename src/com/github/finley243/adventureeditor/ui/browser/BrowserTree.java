package com.github.finley243.adventureeditor.ui.browser;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserCategoryNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserObjectNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserRootNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
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
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.addMouseListener(new MouseAdapter() {
            private DefaultMutableTreeNode lastClickedNode;

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    onRightClick(e.getPoint());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                TreePath path = BrowserTree.this.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    lastClickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && lastClickedNode != null) {
                    onDoubleClick(lastClickedNode);
                }
            }
        });
        Action newAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = getSelectionPath();
                if (path == null) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node instanceof BrowserCategoryNode categoryNode) {
                    main.getDataManager().newObject(categoryNode.getCategoryID());
                } else if (node instanceof BrowserObjectNode objectNode) {
                    main.getDataManager().newObject(objectNode.getCategoryID());
                }
            }
        };
        Action editAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = getSelectionPath();
                if (path == null) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node instanceof BrowserObjectNode objectNode) {
                    main.getDataManager().editObject(objectNode.getCategoryID(), objectNode.getObjectID());
                }
            }
        };
        Action duplicateAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = getSelectionPath();
                if (path == null) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node instanceof BrowserObjectNode objectNode) {
                    main.getDataManager().duplicateObject(objectNode.getCategoryID(), objectNode.getObjectID());
                }
            }
        };
        Action deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = getSelectionPath();
                if (path == null) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node instanceof BrowserObjectNode objectNode) {
                    main.getDataManager().deleteObject(objectNode.getCategoryID(), objectNode.getObjectID());
                }
            }
        };

        ActionMap actionMap = getActionMap();
        actionMap.put("newGameObject", newAction);
        actionMap.put("editGameObject", editAction);
        actionMap.put("duplicateGameObject", duplicateAction);
        actionMap.put("deleteGameObject", deleteAction);

        InputMap inputMapFocused = getInputMap(JComponent.WHEN_FOCUSED);
        inputMapFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newGameObject");
        inputMapFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editGameObject");
        inputMapFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "duplicateGameObject");
        inputMapFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteGameObject");
    }

    @Override
    public void collapsePath(TreePath path) {
        if (path.equals(new TreePath(getModel().getRoot()))) {
            return;
        }
        super.collapsePath(path);
    }

    public void addCategory(String categoryID, String name) {
        BrowserCategoryNode node = new BrowserCategoryNode(main, categoryID, name);
        categoryNodes.put(categoryID, node);
        treeRoot.add(node);
        treeModel.nodeStructureChanged(treeRoot);
        this.scrollPathToVisible(new TreePath(treeModel.getPathToRoot(node)));
    }

    public void updateCategory(String categoryID) {
        treeModel.nodeStructureChanged(categoryNodes.get(categoryID));
    }

    public void addGameObject(String categoryID, String objectID, boolean selectAfterAdding) {
        if (categoryNodes.containsKey(categoryID)) {
            categoryNodes.get(categoryID).addGameObject(objectID);
            updateCategory(categoryID);
            if (selectAfterAdding) {
                this.setSelectionPath(new TreePath(treeModel.getPathToRoot(categoryNodes.get(categoryID).getObjectNode(objectID))));
            }
        }
        this.expandRow(0);
    }

    public void removeGameObject(String categoryID, String objectID) {
        TreePath currentPath = getSelectionPath();
        TreePath newSelectionPath = null;
        if (currentPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentPath.getLastPathComponent();
            if (node instanceof BrowserObjectNode objectNode && objectNode == categoryNodes.get(categoryID).getObjectNode(objectID)) {
                BrowserCategoryNode categoryNode = categoryNodes.get(categoryID);
                DefaultMutableTreeNode siblingPrevious = categoryNode.getObjectNode(objectID).getPreviousSibling();
                DefaultMutableTreeNode siblingNext = categoryNode.getObjectNode(objectID).getNextSibling();
                if (siblingNext instanceof BrowserObjectNode) {
                    newSelectionPath = new TreePath(treeModel.getPathToRoot(siblingNext));
                } else if (siblingPrevious != null) {
                    newSelectionPath = new TreePath(treeModel.getPathToRoot(siblingPrevious));
                } else {
                    newSelectionPath = new TreePath(treeModel.getPathToRoot(categoryNode));
                }
            }
        }
        treeModel.removeNodeFromParent(categoryNodes.get(categoryID).getObjectNode(objectID));
        categoryNodes.get(categoryID).removeGameObject(objectID);
        if (newSelectionPath != null) {
            this.setSelectionPath(newSelectionPath);
        }
    }

    public void clearData() {
        /*for (BrowserCategoryNode node : categoryNodes.values()) {
            treeModel.removeNodeFromParent(node);
        }*/
        treeRoot.removeAllChildren();
        treeModel.nodeStructureChanged(treeRoot);
        categoryNodes.clear();
    }

    public void setSelectedNode(String categoryID, String objectID) {
        if (objectID == null) {
            this.setSelectionPath(new TreePath(treeModel.getPathToRoot(categoryNodes.get(categoryID))));
        } else {
            this.setSelectionPath(new TreePath(treeModel.getPathToRoot(categoryNodes.get(categoryID).getObjectNode(objectID))));
        }
    }

    private void onDoubleClick(DefaultMutableTreeNode node) {
        if (node instanceof BrowserObjectNode objectNode) {
            main.getDataManager().editObject(objectNode.getCategoryID(), objectNode.getObjectID());
        }
    }

    private void onRightClick(Point mousePos) {
        TreePath path = this.getPathForLocation(mousePos.x, mousePos.y);
        if (path == null) {
            return;
        }
        BrowserNode node = (BrowserNode) path.getLastPathComponent();
        this.setSelectionPath(path);
        JPopupMenu contextMenu = node.getContextMenu();
        if (contextMenu != null) {
            contextMenu.show(this, mousePos.x, mousePos.y);
        }
    }

}
