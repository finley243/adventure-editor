package com.github.finley243.adventureeditor.data;

import java.util.List;

public class DataTree extends Data {

    private final List<Data> topNodes;

    public DataTree(List<Data> topNodes) {
        this.topNodes = topNodes;
    }

    public List<Data> getValue() {
        return topNodes;
    }

    @Override
    public Data createCopy() {
        return null;
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        return false;
    }

}
