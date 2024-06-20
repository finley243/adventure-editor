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

}
