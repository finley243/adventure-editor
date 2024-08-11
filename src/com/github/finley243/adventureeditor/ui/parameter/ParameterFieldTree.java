package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.*;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.EditorFrame;
import com.github.finley243.adventureeditor.ui.ObjectTree;
import com.github.finley243.adventureeditor.ui.ObjectTreeNode;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterFieldTree extends ParameterField {

    private static final String BLANK_PANEL_KEY = "EMPTY";

    private final Main main;
    private final Template template;
    private final EditorFrame editorFrame;
    private final String treeID;
    private final ObjectTree treePanel;
    private final CardLayout cardLayout;
    private final Map<String, ParameterFieldObject> objectFields;
    private final Map<String, ObjectTreeNode> nodes;
    private final JPanel objectPanel;

    public ParameterFieldTree(EditorFrame editorFrame, boolean optional, String name, ParameterField parentField, Template template, String treeID, Main main) {
        super(editorFrame, optional, name, parentField);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.main = main;
        this.template = template;
        this.editorFrame = editorFrame;
        this.treeID = treeID;
        this.objectFields = new HashMap<>();
        this.nodes = new HashMap<>();
        this.objectPanel = new JPanel();
        this.cardLayout = new CardLayout();
        objectPanel.setLayout(cardLayout);
        objectPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.treePanel = new ObjectTree(this);
        JScrollPane treeScrollPane = new JScrollPane(treePanel);
        treeScrollPane.setViewportView(treePanel);
        treeScrollPane.setPreferredSize(new Dimension(200, 200));
        treePanel.setPreferredSize(new Dimension(200, 200));
        getInnerPanel().setLayout(new GridBagLayout());
        GridBagConstraints optionalConstraints = new GridBagConstraints();
        optionalConstraints.gridx = 0;
        optionalConstraints.gridy = 0;
        GridBagConstraints treeConstraints = new GridBagConstraints();
        treeConstraints.gridx = 0;
        treeConstraints.gridy = optional ? 1 : 0;
        treeConstraints.weightx = 1;
        treeConstraints.weighty = 1;
        treeConstraints.fill = GridBagConstraints.BOTH;
        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 1;
        panelConstraints.gridy = 0;
        panelConstraints.weightx = 1;
        panelConstraints.weighty = 1;
        panelConstraints.fill = GridBagConstraints.BOTH;
        if (optional) {
            getInnerPanel().add(getOptionalCheckbox(), optionalConstraints);
        }
        getInnerPanel().add(treeScrollPane, treeConstraints);
        getInnerPanel().add(objectPanel, panelConstraints);
        if (optional) {
            setEnabledState(false);
        }
        addBlankPanel();
        setSelectedNode(null);
    }

    public void setSelectedNode(ObjectTreeNode node) {
        cardLayout.show(objectPanel, node == null ? BLANK_PANEL_KEY : node.getUniqueID());
    }

    public void addNode(ObjectTreeNode parentNode, ObjectTreeNode node) {
        treePanel.addNode(parentNode, node);
        addCardForNode(node);
        expandAllNodes();
    }

    public void duplicateNode(ObjectTreeNode node) {
        Data dataCopy = node.getData().createCopy();
        ObjectTreeNode newNode = new ObjectTreeNode(dataCopy.toString(), dataCopy, null);
        treePanel.addNode((ObjectTreeNode) node.getParent(), newNode);
        addCardForNode(newNode);
        expandAllNodes();
    }

    public void deleteNode(ObjectTreeNode node) {
        treePanel.deleteNode(node);
        if (node.getParent() != null) {
            ObjectTreeNode parentNode = (ObjectTreeNode) node.getParent();
            parentNode.remove(node);
        }
        removeCardForNode(node);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        treePanel.setEnabled(enabled);
        for (ParameterFieldObject objectField : objectFields.values()) {
            objectField.setEnabledState(enabled);
        }
    }

    @Override
    public Data getData() {
        updateNodeDataFromFields();
        if (!isOptionalEnabled()) {
            return null;
        }
        List<Data> topNodes = new ArrayList<>();
        for (ObjectTreeNode node : treePanel.getTopNodes()) {
            Data data = node.getData();
            topNodes.add(data);
        }
        return new DataTree(topNodes);
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        treePanel.clearNodes();
        clearCards();
        if (data instanceof DataTree dataTree) {
            for (Data topNodeData : dataTree.getValue()) {
                setDataForNode(topNodeData, null);
            }
        }
        expandAllNodes();
    }

    @Override
    public void onFieldUpdated() {
        // TODO - Improve efficiency (no need to process every node every time, only the active node)
        updateNodeDataFromFields();
        super.onFieldUpdated();
    }

    private void addBlankPanel() {
        ParameterFieldObject blankObjectField = new ParameterFieldObject(editorFrame, false, null, this, template, main, false, false);
        blankObjectField.setEnabledFromParent(false);
        objectPanel.add(blankObjectField, BLANK_PANEL_KEY);
    }

    private void expandAllNodes() {
        for (int i = 0; i < treePanel.getRowCount(); i++) {
            treePanel.expandRow(i);
        }
    }

    private void updateNodeDataFromFields() {
        List<ObjectTreeNode> nodesToRemoveChildren = new ArrayList<>();
        for (Map.Entry<String, ParameterFieldObject> entry : objectFields.entrySet()) {
            ObjectTreeNode node = nodes.get(entry.getKey());
            if (node != null) {
                node.setData(entry.getValue().getData());
                if (!node.getAllowsChildren()) {
                    nodesToRemoveChildren.add(node);
                }
            }
        }
        for (ObjectTreeNode node : nodesToRemoveChildren) {
            removeChildrenRecursive(node);
            //((DefaultTreeModel) treePanel.getModel()).nodeStructureChanged(node);
        }
        ObjectTreeNode selectedNode = (ObjectTreeNode) treePanel.getLastSelectedPathComponent();
        for (ObjectTreeNode node : nodes.values()) {
            ((DefaultTreeModel) treePanel.getModel()).nodeStructureChanged(node);
        }
        if (selectedNode != null) {
            treePanel.setSelectionPath(new TreePath(selectedNode.getPath()));
        }
        expandAllNodes();
        treePanel.revalidate();
        treePanel.repaint();
    }

    private void setDataForNode(Data data, ObjectTreeNode parentNode) {
        DataObject dataObject = (DataObject) data;
        NodeDataContainer nextBranch = findNextTreeBranch(dataObject);
        ObjectTreeNode currentNode = new ObjectTreeNode(nextBranch.dataUpToBranch().toString(), nextBranch.dataUpToBranch(), nextBranch.branchPoint());
        treePanel.addNode(parentNode, currentNode);
        addCardForNode(currentNode);
        if (nextBranch.branchPoint() != null) {
            DataTreeBranch treeBranchData = nextBranch.branchPoint();
            for (Data childNodeData : treeBranchData.getValue()) {
                setDataForNode(childNodeData, currentNode);
            }
        }
    }

    private void clearCards() {
        objectPanel.removeAll();
        objectPanel.revalidate();
        objectPanel.repaint();
        objectFields.clear();
        nodes.clear();
        addBlankPanel();
    }

    private void addCardForNode(ObjectTreeNode node) {
        ParameterFieldObject objectField = new ParameterFieldObject(editorFrame, false, null, this, template, main, false, false);
        Data nodeData = node.getData();
        objectField.setData(nodeData);
        node.setData(objectField.getData());
        objectPanel.add(objectField, node.getUniqueID());
        objectFields.put(node.getUniqueID(), objectField);
        nodes.put(node.getUniqueID(), node);
    }

    private void removeCardForNode(ObjectTreeNode node) {
        objectPanel.remove(objectFields.get(node.getUniqueID()));
        objectFields.remove(node.getUniqueID());
        removeChildrenRecursive(node);
        nodes.remove(node.getUniqueID());
        objectPanel.revalidate();
        objectPanel.repaint();
    }

    private void removeChildrenRecursive(ObjectTreeNode node) {
        List<ObjectTreeNode> childNodes = new ArrayList<>(node.getObjectTreeChildren());
        for (ObjectTreeNode childNode : childNodes) {
            objectPanel.remove(objectFields.get(childNode.getUniqueID()));
            objectFields.remove(childNode.getUniqueID());
            nodes.remove(childNode.getUniqueID());
            node.remove(childNode);
            removeChildrenRecursive(childNode);
        }
    }

    private NodeDataContainer findNextTreeBranch(Data data) {
        switch (data) {
            case DataTreeBranch treeBranch -> {
                return new NodeDataContainer(treeBranch, new DataTreeBranch(new ArrayList<>()));
            }
            case DataObject dataObject -> {
                for (Map.Entry<String, Data> entry : dataObject.getValue().entrySet()) {
                    NodeDataContainer nextBranch = findNextTreeBranch(entry.getValue());
                    if (nextBranch.branchPoint() != null) {
                        Map<String, Data> modifiedObjectData = new HashMap<>(dataObject.getValue());
                        modifiedObjectData.put(entry.getKey(), nextBranch.dataUpToBranch());
                        Data dataUpToBranch = new DataObject(dataObject.getTemplate(), modifiedObjectData);
                        return new NodeDataContainer(nextBranch.branchPoint(), dataUpToBranch);
                    }
                }
                return new NodeDataContainer(null, data);
            }
            case DataComponent dataComponent -> {
                NodeDataContainer nextBranch = findNextTreeBranch(dataComponent.getObjectData());
                Data dataUpToBranch = new DataComponent(dataComponent.getType(), nextBranch.dataUpToBranch(), dataComponent.getNameOverride());
                return new NodeDataContainer(nextBranch.branchPoint(), dataUpToBranch);
            }
            case null, default -> {
                return new NodeDataContainer(null, data);
            }
        }
    }

    private record NodeDataContainer(DataTreeBranch branchPoint, Data dataUpToBranch) {}

}
