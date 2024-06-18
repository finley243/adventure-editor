package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;

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
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    onRightClick(e.getPoint());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    onDoubleClick(e.getPoint());
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
                    main.newObject(categoryNode.getCategoryID());
                }/* else if (node instanceof BrowserObjectNode objectNode) {
                    main.newObject(objectNode.getCategoryID());
                }*/
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
                    main.editObject(objectNode.getCategoryID(), objectNode.getObjectID());
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
                    main.duplicateObject(objectNode.getCategoryID(), objectNode.getObjectID());
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
                    main.deleteObject(objectNode.getCategoryID(), objectNode.getObjectID());
                }
            }
        };

        ActionMap actionMap = getActionMap();
        actionMap.put("newGameObject", newAction);
        actionMap.put("editGameObject", editAction);
        actionMap.put("duplicateGameObject", duplicateAction);
        actionMap.put("deleteGameObject", deleteAction);

        InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newGameObject");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editGameObject");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "duplicateGameObject");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteGameObject");
    }

    public void addCategory(String categoryID, String name) {
        BrowserCategoryNode node = new BrowserCategoryNode(main, categoryID, name);
        categoryNodes.put(categoryID, node);
        treeRoot.add(node);
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
        for (BrowserCategoryNode node : categoryNodes.values()) {
            treeModel.removeNodeFromParent(node);
        }
        categoryNodes.clear();
    }

    public void setSelectedNode(String categoryID, String objectID) {
        if (objectID == null) {
            this.setSelectionPath(new TreePath(treeModel.getPathToRoot(categoryNodes.get(categoryID))));
        } else {
            this.setSelectionPath(new TreePath(treeModel.getPathToRoot(categoryNodes.get(categoryID).getObjectNode(objectID))));
        }
    }

    private void onDoubleClick(Point mousePos) {
        TreePath path = this.getPathForLocation(mousePos.x, mousePos.y);
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (node instanceof BrowserObjectNode objectNode) {
                main.editObject(objectNode.getCategoryID(), objectNode.getObjectID());
            }
        }
    }

    private void onRightClick(Point mousePos) {
        TreePath path = this.getPathForLocation(mousePos.x, mousePos.y);
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            this.setSelectionPath(path);
            if (node instanceof BrowserObjectNode objectNode) {
                objectNode.openContextMenu(this, mousePos);
            } else if (node instanceof BrowserCategoryNode categoryNode) {
                categoryNode.openContextMenu(this, mousePos);
            }
        }
    }

}
