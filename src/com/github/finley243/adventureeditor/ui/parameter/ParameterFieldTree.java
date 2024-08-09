package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.*;
import com.github.finley243.adventureeditor.template.ComponentOption;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.ui.EditorFrame;
import com.github.finley243.adventureeditor.ui.ObjectTree;
import com.github.finley243.adventureeditor.ui.ObjectTreeNode;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterFieldTree extends ParameterField {

    private final Map<String, ParameterField> editorElements;
    private final Main main;
    private final Template template;
    private final String treeID;
    private final ObjectTree treePanel;
    private final JPanel objectPanel;
    private final ParameterFieldObject objectField;
    private ObjectTreeNode currentNode;

    public ParameterFieldTree(EditorFrame editorFrame, boolean optional, String name, Template template, String treeID, Main main) {
        super(editorFrame, optional, name);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.editorElements = new HashMap<>();
        this.main = main;
        this.template = template;
        this.treeID = treeID;
        this.objectPanel = new JPanel();
        objectPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.treePanel = new ObjectTree(main, this);
        this.objectField = new ParameterFieldObject(editorFrame, false, name, template, main, false, false);
        objectPanel.add(objectField);
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
        saveCurrentNode();
        if (node == null || node.getData() == null) {
            objectField.setData(null);
            objectField.setEnabledState(false);
            currentNode = null;
        } else {
            objectField.setEnabledState(true);
            objectField.setData(node.getData());
            currentNode = node;
        }
    }

    @Override
    public void setEnabledState(boolean enabled) {
        for (ParameterField element : editorElements.values()) {
            element.setEnabledFromParent(enabled);
        }
        //dropdownMenu.setEnabled(enabled);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        List<Data> topNodes = new ArrayList<>();
        for (ObjectTreeNode node : treePanel.getTopNodes()) {
            Data data = node.getData();
            if (data != null) {
                topNodes.add(data);
            }
        }
        // TODO - Needs to properly save child nodes as DataTreeBranch objects, while maintaining surrounding structure (components, child objects, etc.)
        return new DataTree(topNodes);
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        treePanel.clearNodes();
        if (data instanceof DataTree dataTree) {
            for (Data topNodeData : dataTree.getValue()) {
                DataObject nodeObjectData = (DataObject) topNodeData;
                // TODO - Generate node name through the object template (should adapt when node data changes)
                ObjectTreeNode node = new ObjectTreeNode("temp_name", nodeObjectData);
                treePanel.addNode(null, node);
                setDataForNode(nodeObjectData, node);
            }
        }
    }

    private void setDataForNode(Data data, ObjectTreeNode parentNode) {
        DataObject dataObject = (DataObject) data;
        Data treeIDData = dataObject.getValue().get(treeID);
        if (!(treeIDData instanceof DataTreeBranch treeBranchData)) {
            for (Data parameterData : dataObject.getValue().values()) {
                if (parameterData instanceof DataComponent parameterDataComponent) {
                    setDataForNode(parameterDataComponent.getObjectData(), parentNode);
                }
            }
            return;
        }
        for (Data topNodeData : treeBranchData.getValue()) {
            DataObject nodeObjectData = (DataObject) topNodeData;
            // TODO - Generate node name through the object template (should adapt when node data changes)
            ObjectTreeNode node = new ObjectTreeNode("temp_name", nodeObjectData);
            treePanel.addNode(parentNode, node);
            setDataForNode(nodeObjectData, node);
        }
    }

    private void saveCurrentNode() {
        if (currentNode != null) {
            currentNode.setData(objectField.getData());
        }
    }

}
