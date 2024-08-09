package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataComponent;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataTreeBranch;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class ObjectTreeNode extends DefaultMutableTreeNode {

    private Data data;
    // A reference to the DataTreeBranch in the provided Data, to which data from this node's children will be appended
    private DataTreeBranch childBranchPoint;
    private final String uniqueID;

    public ObjectTreeNode(String name, Data data, DataTreeBranch childBranchPoint) {
        super(name);
        this.data = data;
        this.childBranchPoint = childBranchPoint;
        this.uniqueID = UUID.randomUUID().toString();
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public Data getData() {
        if (data == null) {
            return null;
        }
        if (childBranchPoint == null) {
            return data.createCopy();
        }
        List<Data> childData = new ArrayList<>();
        for (ObjectTreeNode child : getObjectTreeChildren()) {
            childData.add(child.getData());
        }
        Data dataCopy = data.createCopy();
        DataTreeBranch branchPointCopy = findNextTreeBranch(dataCopy);
        branchPointCopy.setValue(childData);
        //childBranchPoint.setValue(childData);
        return dataCopy;
    }

    public void setData(Data newData) {
        //System.out.println("ObjectTreeNode setData()");
        //System.out.println("Old Data: " + data.getDebugString());
        //System.out.println("New Data: " + newData.getDebugString());
        this.data = newData;
        this.childBranchPoint = findNextTreeBranch(data);
        // TODO - Update name whenever value is updated, not only on changing node selection
        setUserObject(data.toString());
    }

    public List<ObjectTreeNode> getObjectTreeChildren() {
        List<ObjectTreeNode> children = new ArrayList<>();
        this.children().asIterator().forEachRemaining(e -> children.add((ObjectTreeNode) e));
        return children;
    }

    private DataTreeBranch findNextTreeBranch(Data data) {
        switch (data) {
            case DataTreeBranch treeBranch -> {
                return treeBranch;
            }
            case DataObject dataObject -> {
                for (Map.Entry<String, Data> entry : dataObject.getValue().entrySet()) {
                    DataTreeBranch nextBranch = findNextTreeBranch(entry.getValue());
                    if (nextBranch != null) {
                        return nextBranch;
                    }
                }
                return null;
            }
            case DataComponent dataComponent -> {
                return findNextTreeBranch(dataComponent.getObjectData());
            }
            case null, default -> {
                return null;
            }
        }
    }

}
