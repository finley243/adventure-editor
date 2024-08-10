package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserCategoryNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserObjectNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserRootNode;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFieldTree;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectTree extends JTree {

    private final ObjectTreeNode treeRoot;
    private final DefaultTreeModel treeModel;
    private final Main main;
    private final EditorFrame editorFrame;

    public ObjectTree(Main main, ParameterFieldTree parameterFieldTree, EditorFrame editorFrame) {
        this.treeRoot = new ObjectTreeNode("Root", null, null);
        this.main = main;
        this.editorFrame = editorFrame;
        this.treeModel = new DefaultTreeModel(treeRoot, false);
        this.setModel(treeModel);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.setDragEnabled(true);
        this.setDropMode(DropMode.ON_OR_INSERT);
        this.setTransferHandler(new ObjectTreeTransferHandler(treeModel));
        treeModel.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                editorFrame.onEditorElementUpdated();
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
                editorFrame.onEditorElementUpdated();
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                editorFrame.onEditorElementUpdated();
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                editorFrame.onEditorElementUpdated();
            }
        });
        this.addTreeSelectionListener(e -> {
            ObjectTreeNode node = (ObjectTreeNode) this.getLastSelectedPathComponent();
            if (node != null) {
                parameterFieldTree.setSelectedNode(node);
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    onRightClick(e.getPoint());
                }
            }
        });
        /*Action newAction = new AbstractAction() {
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

    private void onRightClick(Point mousePos) {
        TreePath path = this.getPathForLocation(mousePos.x, mousePos.y);
        if (path != null) {
            ObjectTreeNode node = (ObjectTreeNode) path.getLastPathComponent();
            this.setSelectionPath(path);
            // TODO - Open right-click context menu for node
        }
    }

    private static class ObjectTreeTransferHandler extends TransferHandler {
        private final DataFlavor nodesFlavor;
        private final DefaultTreeModel treeModel;
        private DefaultMutableTreeNode[] nodesToTransfer;

        public ObjectTreeTransferHandler(DefaultTreeModel treeModel) {
            this.treeModel = treeModel;
            nodesFlavor = new DataFlavor(DefaultMutableTreeNode[].class, "Array of DefaultMutableTreeNode");
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null) {
                nodesToTransfer = new DefaultMutableTreeNode[paths.length];
                for (int i = 0; i < paths.length; i++) {
                    nodesToTransfer[i] = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                }
                return new NodesTransferable(nodesToTransfer);
            }
            return null;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            if (!support.isDrop() || !support.isDataFlavorSupported(nodesFlavor)) {
                return false;
            }
            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            if (dropLocation == null) {
                return false;
            }
            TreePath dropPath = dropLocation.getPath();
            if (dropPath == null) {
                return false;
            }
            DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
            if (!targetNode.getAllowsChildren()) {
                return false;
            }
            for (DefaultMutableTreeNode node : nodesToTransfer) {
                if (node.isNodeDescendant(targetNode)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            if (dropLocation == null) {
                return false;
            }
            TreePath dropPath = dropLocation.getPath();
            if (dropPath == null) {
                return false;
            }
            DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
            for (DefaultMutableTreeNode node : nodesToTransfer) {
                treeModel.removeNodeFromParent(node);
                treeModel.insertNodeInto(node, targetNode, targetNode.getChildCount());
            }
            return true;
        }

        private static class NodesTransferable implements Transferable {
            private final DefaultMutableTreeNode[] nodes;

            public NodesTransferable(DefaultMutableTreeNode[] nodes) {
                this.nodes = nodes;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{new DataFlavor(DefaultMutableTreeNode[].class, "Array of DefaultMutableTreeNode")};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(new DataFlavor(DefaultMutableTreeNode[].class, "Array of DefaultMutableTreeNode"));
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return nodes;
            }
        }
    }

}
