package com.github.finley243.adventureeditor.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataObjectSet extends Data {

    private final List<Data> value;

    public DataObjectSet(List<Data> value) {
        this.value = value;
    }

    public List<Data> getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        List<Data> copyList = new ArrayList<>();
        for (Data data : value) {
            copyList.add(data.createCopy());
        }
        return new DataObjectSet(copyList);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataObjectSet dataObjectSet && Objects.equals(value, dataObjectSet.value);
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        if (!(data instanceof DataObjectSet dataObjectSet)) {
            return false;
        }
        if (value.isEmpty()) {
            return false;
        }
        //Template template = ((DataObject) value.getFirst()).getTemplate();
        for (Data innerData : value) {
            boolean hasDuplicate = false;
            for (Data otherInnerData : dataObjectSet.value) {
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
