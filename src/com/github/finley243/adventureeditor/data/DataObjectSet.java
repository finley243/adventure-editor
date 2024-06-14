package com.github.finley243.adventureeditor.data;

import java.util.List;

public class DataObjectSet extends Data {

    private final List<Data> value;

    public DataObjectSet(List<Data> value) {
        this.value = value;
    }

    public List<Data> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
