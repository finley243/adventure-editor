package com.github.finley243.adventureeditor.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataTreeBranch extends Data {

    private final List<Data> topNodes;

    public DataTreeBranch(List<Data> topNodes) {
        this.topNodes = topNodes;
    }

    public List<Data> getValue() {
        return topNodes;
    }

    @Override
    public Data createCopy() {
        List<Data> copyList = new ArrayList<>();
        for (Data data : topNodes) {
            copyList.add(data.createCopy());
        }
        return new DataTreeBranch(copyList);
    }

    @Override
    public String toString() {
        return topNodes.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataTreeBranch dataTree && Objects.equals(topNodes, dataTree.topNodes);
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        if (!(data instanceof DataTreeBranch dataTree)) {
            return false;
        }
        if (topNodes.isEmpty()) {
            return false;
        }
        //Template template = ((DataObject) value.getFirst()).getTemplate();
        for (Data innerData : topNodes) {
            boolean hasDuplicate = false;
            for (Data otherInnerData : dataTree.topNodes) {
                if (innerData.isDuplicateValue(otherInnerData)) {
                    hasDuplicate = true;
                    break;
                }
            }
            if (!hasDuplicate) {
                return false;
            }
        }
        return true;
    }

}
