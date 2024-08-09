package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserCategoryNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserObjectNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserRootNode;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFieldTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectTree extends JTree {

    private final ObjectTreeNode treeRoot;
    private final DefaultTreeModel treeModel;
    private final Main main;

    public ObjectTree(Main main, ParameterFieldTree parameterFieldTree) {
        this.treeRoot = new ObjectTreeNode("Root", null);
        this.main = main;
        this.treeModel = new DefaultTreeModel(treeRoot, false);
        this.setModel(treeModel);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.addTreeSelectionListener(e -> {
            ObjectTreeNode node = (ObjectTreeNode) this.getLastSelectedPathComponent();
            if (node != null) {
                parameterFieldTree.setSelectedNode(node);
            }
        });
        /*this.addMouseListener(new MouseAdapter() {
            private DefaultMutableTreeNode lastClickedNode;

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    onRightClick(e.getPoint());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                TreePath path = ObjectTree.this.getPathForLocation(e.getX(), e.getY());
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
        inputMapFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteGameObject");*/
    }

    @Override
    public void collapsePath(TreePath path) {
        if (path.equals(new TreePath(getModel().getRoot()))) {
            return;
        }
        super.collapsePath(path);
    }

    public List<ObjectTreeNode> getTopNodes() {
        return treeRoot.getObjectTreeChildren();
    }

    public void addNode(ObjectTreeNode parentNode, ObjectTreeNode node) {
        if (parentNode == null) {
            treeRoot.add(node);
        } else {
            parentNode.add(node);
        }
        treeModel.nodeStructureChanged(treeRoot);
        this.scrollPathToVisible(new TreePath(treeModel.getPathToRoot(node)));
    }

    public void clearNodes() {
        treeRoot.removeAllChildren();
        treeModel.nodeStructureChanged(treeRoot);
    }

    public void setSelectedNode(ObjectTreeNode node) {
        if (node != null) {
            this.setSelectionPath(new TreePath(treeModel.getPathToRoot(node)));
        }
    }

    private void onDoubleClick(DefaultMutableTreeNode node) {
        /*if (node instanceof BrowserObjectNode objectNode) {
            main.getDataManager().editObject(objectNode.getCategoryID(), objectNode.getObjectID());
        }*/
    }

    private void onRightClick(Point mousePos) {
        TreePath path = this.getPathForLocation(mousePos.x, mousePos.y);
        /*if (path != null) {
            BrowserNode node = (BrowserNode) path.getLastPathComponent();
            this.setSelectionPath(path);
            JPopupMenu contextMenu = node.getContextMenu();
            if (contextMenu != null) {
                contextMenu.show(this, mousePos.x, mousePos.y);
            }
        }*/
    }

}
