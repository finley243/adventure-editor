package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.*;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.EditorFrame;
import com.github.finley243.adventureeditor.ui.ObjectTree;
import com.github.finley243.adventureeditor.ui.ObjectTreeNode;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterFieldTree extends ParameterField {

    private final Main main;
    private final Template template;
    private final EditorFrame editorFrame;
    private final String treeID;
    private final ObjectTree treePanel;
    private final CardLayout cardLayout;
    private final Map<String, ParameterFieldObject> objectFields;
    private final Map<String, ObjectTreeNode> nodes;
    private final JPanel objectPanel;

    public ParameterFieldTree(EditorFrame editorFrame, boolean optional, String name, Template template, String treeID, Main main) {
        super(editorFrame, optional, name);
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
        this.treePanel = new ObjectTree(main, this, editorFrame);
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
        getInnerPanel().add(treePanel, treeConstraints);
        getInnerPanel().add(objectPanel, panelConstraints);
        if (optional) {
            setEnabledState(false);
        }
        setSelectedNode(null);
    }

    public void setSelectedNode(ObjectTreeNode node) {
        cardLayout.show(objectPanel, node == null ? null : node.getUniqueID());
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

    private void updateNodeDataFromFields() {
        for (Map.Entry<String, ParameterFieldObject> entry : objectFields.entrySet()) {
            ObjectTreeNode node = nodes.get(entry.getKey());
            if (node != null) {
                node.setData(entry.getValue().getData());
            }
        }
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
    }

    private void setDataForNode(Data data, ObjectTreeNode parentNode) {
        DataObject dataObject = (DataObject) data;
        BranchDataContainer nextBranch = findNextTreeBranch(dataObject);
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
    }

    private void addCardForNode(ObjectTreeNode node) {
        ParameterFieldObject objectField = new ParameterFieldObject(editorFrame, false, null, template, main, false, false);
        objectField.setData(node.getData());
        objectPanel.add(objectField, node.getUniqueID());
        objectFields.put(node.getUniqueID(), objectField);
        nodes.put(node.getUniqueID(), node);
        cardLayout.show(objectPanel, node.getUniqueID());
    }

    private BranchDataContainer findNextTreeBranch(Data data) {
        switch (data) {
            case DataTreeBranch treeBranch -> {
                return new BranchDataContainer(treeBranch, new DataTreeBranch(new ArrayList<>()));
            }
            case DataObject dataObject -> {
                for (Map.Entry<String, Data> entry : dataObject.getValue().entrySet()) {
                    BranchDataContainer nextBranch = findNextTreeBranch(entry.getValue());
                    if (nextBranch.branchPoint() != null) {
                        Map<String, Data> modifiedObjectData = new HashMap<>(dataObject.getValue());
                        modifiedObjectData.put(entry.getKey(), nextBranch.dataUpToBranch());
                        Data dataUpToBranch = new DataObject(dataObject.getTemplate(), modifiedObjectData);
                        return new BranchDataContainer(nextBranch.branchPoint(), dataUpToBranch);
                    }
                }
                return new BranchDataContainer(null, data);
            }
            case DataComponent dataComponent -> {
                BranchDataContainer nextBranch = findNextTreeBranch(dataComponent.getObjectData());
                Data dataUpToBranch = new DataComponent(dataComponent.getType(), nextBranch.dataUpToBranch(), dataComponent.getNameOverride());
                return new BranchDataContainer(nextBranch.branchPoint(), dataUpToBranch);
            }
            case null, default -> {
                return new BranchDataContainer(null, data);
            }
        }
    }

    private record BranchDataContainer(DataTreeBranch branchPoint, Data dataUpToBranch) {}

}
