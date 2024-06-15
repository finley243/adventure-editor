package com.github.finley243.adventureeditor.data;

import java.util.List;
import java.util.Objects;

public class DataStringSet extends Data {

    private final List<String> value;

    public DataStringSet(List<String> value) {
        this.value = value;
    }

    public List<String> getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        return new DataStringSet(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataStringSet dataStringSet && Objects.equals(dataStringSet.value, value);
    }

}
