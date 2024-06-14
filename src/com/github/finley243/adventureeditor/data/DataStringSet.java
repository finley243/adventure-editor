package com.github.finley243.adventureeditor.data;

import java.util.List;

public class DataStringSet extends Data {

    private final List<String> value;

    public DataStringSet(List<String> value) {
        this.value = value;
    }

    public List<String> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
